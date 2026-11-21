package co.com.equipo.certificacion.automationexercise.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;

import java.util.Collection;

public final class DisplayedText {
    private DisplayedText() {}

    public static Question<String> of(Target target) {
        return Question.about(target.getName())
                .answeredBy(actor -> Text.of(target).answeredBy(actor).replaceAll("[\\s\\u00a0]+", " ").trim());
    }

    public static Question<Collection<String>> ofAll(Target target) {
        return Text.ofEach(target);
    }
}
