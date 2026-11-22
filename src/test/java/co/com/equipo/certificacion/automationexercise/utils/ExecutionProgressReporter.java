package co.com.equipo.certificacion.automationexercise.utils;

import co.com.equipo.certificacion.automationexercise.questions.TheSite;
import co.com.equipo.certificacion.automationexercise.utils.FailureDiagnosis.Diagnosis;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookTestStep;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Plugin de Cucumber que muestra el avance por escenario, paso y diagnostico de fallos en tiempo real.
 */
public final class ExecutionProgressReporter implements ConcurrentEventListener {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Pattern AC_TAG_PATTERN = Pattern.compile("^@AC\\d+$");

    private final Map<URI, String> featureNames = new ConcurrentHashMap<>();
    private final Map<UUID, ScenarioState> activeScenarios = new ConcurrentHashMap<>();
    private final List<FailedScenario> failedScenarios = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger scenarioCounter = new AtomicInteger();
    private final AtomicInteger passedCount = new AtomicInteger();
    private final AtomicInteger failedCount = new AtomicInteger();
    private final AtomicInteger skippedCount = new AtomicInteger();

    private volatile Instant runStartedAt = Instant.now();

    public ExecutionProgressReporter() {}

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, event -> safely("TestSourceRead", () -> onTestSourceRead(event)));
        publisher.registerHandlerFor(TestRunStarted.class, event -> safely("TestRunStarted", this::onTestRunStarted));
        publisher.registerHandlerFor(TestCaseStarted.class, event -> safely("TestCaseStarted", () -> onTestCaseStarted(event)));
        publisher.registerHandlerFor(TestStepStarted.class, event -> safely("TestStepStarted", () -> onTestStepStarted(event)));
        publisher.registerHandlerFor(TestStepFinished.class, event -> safely("TestStepFinished", () -> onTestStepFinished(event)));
        publisher.registerHandlerFor(TestCaseFinished.class, event -> safely("TestCaseFinished", () -> onTestCaseFinished(event)));
        publisher.registerHandlerFor(TestRunFinished.class, event -> safely("TestRunFinished", this::onTestRunFinished));
    }

    private void safely(String eventName, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException error) {
            System.err.println("[ExecutionProgressReporter] Error en "
                    + eventName + " (" + error.getClass().getSimpleName() + "): " + error.getMessage());
        }
    }

    private void onTestSourceRead(TestSourceRead event) {
        String featureName = extractFeatureName(event.getSource(), event.getUri());
        featureNames.put(event.getUri(), featureName);
    }

    private void onTestRunStarted() {
        runStartedAt = Instant.now();
        scenarioCounter.set(0);
        passedCount.set(0);
        failedCount.set(0);
        skippedCount.set(0);
        activeScenarios.clear();
        failedScenarios.clear();

        String time = LocalTime.now().format(TIME_FORMATTER);
        ConsoleOutput.line(ConsoleOutput.colorize(
                "==== Automation Exercise | ejecucion iniciada " + time + " ====",
                ConsoleOutput.CYAN));
    }

    private void onTestCaseStarted(TestCaseStarted event) {
        TestCase testCase = event.getTestCase();
        int scenarioIndex = scenarioCounter.incrementAndGet();
        int totalSteps = countPickleSteps(testCase.getTestSteps());
        activeScenarios.put(testCase.getId(), new ScenarioState(scenarioIndex, totalSteps));

        String featureName = featureNames.getOrDefault(testCase.getUri(), fileNameOf(testCase.getUri()));
        String tags = String.join(" ", testCase.getTags());
        String header = tags.isEmpty()
                ? ">> [" + scenarioIndex + "] " + featureName + " > " + testCase.getName()
                : ">> [" + scenarioIndex + "] " + featureName + " > " + testCase.getName() + "  " + tags;
        ConsoleOutput.line(ConsoleOutput.colorize(header, ConsoleOutput.CYAN));
    }

    private void onTestStepStarted(TestStepStarted event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep pickleStep)) {
            return;
        }
        ScenarioState state = activeScenarios.get(event.getTestCase().getId());
        if (state == null) {
            return;
        }
        int currentStep = state.nextStep();
        String stepText = formatStep(pickleStep.getStep());
        ConsoleOutput.line("   " + currentStep + "/" + state.totalSteps() + " .. " + stepText);
    }

    private void onTestStepFinished(TestStepFinished event) {
        ScenarioState state = activeScenarios.get(event.getTestCase().getId());
        Result result = event.getResult();
        Status status = result.getStatus();
        String duration = formatSeconds(result.getDuration());

        if (event.getTestStep() instanceof PickleStepTestStep pickleStep) {
            int currentStep = state == null ? 0 : state.currentStep();
            int totalSteps = state == null ? 0 : state.totalSteps();
            String label = statusLabel(status);
            String coloredLabel = ConsoleOutput.colorize(label, colorForStatus(status));
            String stepText = formatStep(pickleStep.getStep());

            ConsoleOutput.line("   " + currentStep + "/" + totalSteps + " " + coloredLabel + " " + stepText + "  " + duration);

            if (status == Status.FAILED) {
                Diagnosis diagnosis = FailureDiagnosis.of(result.getError(), readOverloadNoticeSafely());
                if (state != null) {
                    state.recordDiagnosisIfAbsent(diagnosis);
                }
                String location = fileNameOf(pickleStep.getUri()) + ":" + pickleStep.getStep().getLine();
                printDiagnosis(diagnosis, location);
            }
            return;
        }

        if (event.getTestStep() instanceof HookTestStep hookStep && status == Status.FAILED) {
            Diagnosis diagnosis = FailureDiagnosis.of(result.getError(), readOverloadNoticeSafely());
            if (state != null) {
                state.recordDiagnosisIfAbsent(diagnosis);
            }
            String hookLabel = ConsoleOutput.colorize("FALLO", ConsoleOutput.RED);
            ConsoleOutput.line("   [hook] " + hookLabel + " " + hookStep.getHookType() + "  " + duration);
            String location = hookStep.getCodeLocation() != null
                    ? hookStep.getCodeLocation()
                    : fileNameOf(event.getTestCase().getUri()) + ":" + event.getTestCase().getLocation().getLine();
            printDiagnosis(diagnosis, location);
        }
    }

    private static String readOverloadNoticeSafely() {
        if (!OnStage.theStageIsSet() || !ThucydidesWebDriverSupport.isDriverInstantiated()) {
            return "";
        }
        try {
            Actor actor = OnStage.theActorInTheSpotlight();
            if (actor == null || actor.abilityTo(BrowseTheWeb.class) == null) {
                return "";
            }
            return TheSite.overloadNotice().answeredBy(actor);
        } catch (RuntimeException error) {
            System.err.println("[ExecutionProgressReporter] Error leyendo pagina ("
                    + error.getClass().getSimpleName() + "): " + error.getMessage());
            return "";
        }
    }

    private void onTestCaseFinished(TestCaseFinished event) {
        TestCase testCase = event.getTestCase();
        ScenarioState state = activeScenarios.remove(testCase.getId());
        int scenarioIndex = state == null ? 0 : state.index();
        Result result = event.getResult();
        Status status = result.getStatus();

        String outcomeLabel;
        String outcomeColor;
        if (status == Status.PASSED) {
            passedCount.incrementAndGet();
            outcomeLabel = "PASO";
            outcomeColor = ConsoleOutput.GREEN;
        } else if (status == Status.SKIPPED) {
            skippedCount.incrementAndGet();
            outcomeLabel = "OMITIDO";
            outcomeColor = ConsoleOutput.YELLOW;
        } else {
            failedCount.incrementAndGet();
            outcomeLabel = "FALLO";
            outcomeColor = ConsoleOutput.RED;
            Diagnosis diagnosis = resolveScenarioDiagnosis(state, result);
            failedScenarios.add(new FailedScenario(acTagsOf(testCase.getTags()), testCase.getName(), diagnosis));
        }

        String line = "<< " + ConsoleOutput.colorize(outcomeLabel, outcomeColor)
                + " [" + scenarioIndex + "] " + testCase.getName() + " " + formatSeconds(result.getDuration());
        ConsoleOutput.line(line);
    }

    private void onTestRunFinished() {
        long totalSeconds = Math.max(0L, Duration.between(runStartedAt, Instant.now()).toSeconds());
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        boolean isDryRun = ConsoleOutput.enabled("cucumber.execution.dry-run", false);
        String passedLabel = isDryRun ? "%d validados (dry-run)" : "%d pasaron";
        String summary = String.format(
                Locale.US,
                "==== Resumen: " + passedLabel + " | %d fallaron | %d omitidos | %d min %d s ====",
                passedCount.get(),
                failedCount.get(),
                skippedCount.get(),
                minutes,
                seconds);
        String summaryColor = failedCount.get() > 0 ? ConsoleOutput.RED : ConsoleOutput.GREEN;
        ConsoleOutput.line(ConsoleOutput.colorize(summary, summaryColor));

        synchronized (failedScenarios) {
            for (FailedScenario failed : failedScenarios) {
                String prefix = failed.acTags().isEmpty() ? "FALLO " : "FALLO " + failed.acTags() + " ";
                String detailLine = prefix + failed.scenarioName() + " -> "
                        + failed.diagnosis().category() + ": " + failed.diagnosis().probableCause();
                ConsoleOutput.line(ConsoleOutput.colorize(detailLine, ConsoleOutput.RED));
            }
        }

        ConsoleOutput.line("Reporte: target/site/serenity/index.html");
    }

    private static void printDiagnosis(Diagnosis diagnosis, String location) {
        String indent = "         ";
        ConsoleOutput.line(indent + "Categoria:      " + diagnosis.category());
        ConsoleOutput.line(indent + "Causa probable: " + diagnosis.probableCause());
        ConsoleOutput.line(indent + "Que revisar:    " + diagnosis.whatToCheck());
        ConsoleOutput.line(indent + "Detalle:        " + diagnosis.detail());
        ConsoleOutput.line(indent + "Ubicacion:      " + location);
    }

    private static Diagnosis resolveScenarioDiagnosis(ScenarioState state, Result result) {
        if (state != null && state.firstDiagnosis() != null) {
            return state.firstDiagnosis();
        }
        Diagnosis fromResult = FailureDiagnosis.of(result.getError());
        if (!"SIN_ERROR".equals(fromResult.category())) {
            return fromResult;
        }
        if (result.getStatus() == Status.UNDEFINED) {
            return new Diagnosis(
                    "PASO_INDEFINIDO",
                    "Hay pasos sin definicion en Cucumber",
                    "Implementar los pasos faltantes en stepdefinitions",
                    "");
        }
        return fromResult;
    }

    private static int countPickleSteps(List<TestStep> steps) {
        int count = 0;
        for (TestStep step : steps) {
            if (step instanceof PickleStepTestStep) {
                count++;
            }
        }
        return count;
    }

    private static String formatStep(Step step) {
        String keyword = step.getKeyword() == null ? "" : step.getKeyword();
        String text = step.getText() == null ? "" : step.getText().trim();
        if (keyword.isEmpty()) {
            return text;
        }
        return keyword.endsWith(" ") ? keyword + text : keyword + " " + text;
    }

    private static String statusLabel(Status status) {
        return switch (status) {
            case PASSED -> "OK";
            case FAILED -> "FALLO";
            case SKIPPED, UNUSED -> "OMITIDO";
            case UNDEFINED -> "INDEFINIDO";
            case PENDING -> "PENDIENTE";
            case AMBIGUOUS -> "AMBIGUO";
        };
    }

    private static String colorForStatus(Status status) {
        return switch (status) {
            case PASSED -> ConsoleOutput.GREEN;
            case FAILED, AMBIGUOUS -> ConsoleOutput.RED;
            case SKIPPED, UNUSED, UNDEFINED, PENDING -> ConsoleOutput.YELLOW;
        };
    }

    private static String formatSeconds(Duration duration) {
        double seconds = duration == null ? 0.0 : duration.toNanos() / 1_000_000_000.0;
        return String.format(Locale.US, "(%.1f s)", seconds);
    }

    private static String extractFeatureName(String source, URI uri) {
        if (source != null) {
            String[] lines = source.split("\\R");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("Feature:")) {
                    String title = trimmed.substring("Feature:".length()).trim();
                    if (!title.isEmpty()) {
                        return title;
                    }
                }
            }
        }
        return fileNameOf(uri);
    }

    private static String fileNameOf(URI uri) {
        if (uri == null) {
            return "desconocido";
        }
        String raw = uri.getSchemeSpecificPart();
        if (raw == null || raw.isBlank()) {
            raw = uri.toString();
        }
        int slash = Math.max(raw.lastIndexOf('/'), raw.lastIndexOf('\\'));
        return slash >= 0 && slash + 1 < raw.length() ? raw.substring(slash + 1) : raw;
    }

    private static String acTagsOf(List<String> tags) {
        List<String> acTags = new ArrayList<>();
        for (String tag : tags) {
            if (AC_TAG_PATTERN.matcher(tag).matches()) {
                acTags.add(tag);
            }
        }
        return String.join(" ", acTags);
    }

    private static final class ScenarioState {
        private final int index;
        private final int totalSteps;
        private final AtomicInteger currentStep = new AtomicInteger();
        private volatile Diagnosis firstDiagnosis;

        ScenarioState(int index, int totalSteps) {
            this.index = index;
            this.totalSteps = totalSteps;
        }

        int index() {
            return index;
        }

        int totalSteps() {
            return totalSteps;
        }

        int nextStep() {
            return currentStep.incrementAndGet();
        }

        int currentStep() {
            return currentStep.get();
        }

        void recordDiagnosisIfAbsent(Diagnosis diagnosis) {
            if (this.firstDiagnosis == null) {
                this.firstDiagnosis = diagnosis;
            }
        }

        Diagnosis firstDiagnosis() {
            return firstDiagnosis;
        }
    }

    private record FailedScenario(String acTags, String scenarioName, Diagnosis diagnosis) {}
}

