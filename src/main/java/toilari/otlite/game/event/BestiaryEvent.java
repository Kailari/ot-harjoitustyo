package toilari.otlite.game.event;

/**
 * Bestiary-tilan tapahtumat.
 */
public class BestiaryEvent implements IEvent {
    /**
     * Viesti joka lähetetään kun halutaan palata takaisin päävalikkoon.
     */
    public static class Return extends BestiaryEvent {
    }
}
