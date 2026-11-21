package co.com.equipo.certificacion.automationexercise.utils;

import net.serenitybdd.screenplay.Actor;

/**
 * Narra en consola las acciones ejecutadas por el actor en tiempo real.
 */
public final class Narrator {
    private Narrator() {}

    /**
     * Imprime una linea describiendo la accion actual del actor cuando progress.actions esta activo.
     */
    public static void says(Actor actor, String action) {
        if (!ConsoleOutput.enabled("progress.actions", true) || action == null || action.isBlank()) {
            return;
        }
        String actorName = actor == null ? "Actor" : actor.getName();
        ConsoleOutput.line(ConsoleOutput.colorize("      . " + actorName + " " + action, ConsoleOutput.GRAY));
    }
}

