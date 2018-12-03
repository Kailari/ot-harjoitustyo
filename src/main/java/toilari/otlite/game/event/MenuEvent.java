package toilari.otlite.game.event;

import lombok.NoArgsConstructor;

/**
 * Genneeriset valikkoviestit joita lähetetään kaikkista valikoista.
 */
public class MenuEvent implements IEvent {
    /**
     * Viesti joka lähetetään kun ohjelma halutaan sulkea.
     */
    @NoArgsConstructor
    public static class Quit extends MenuEvent {
    }
}
