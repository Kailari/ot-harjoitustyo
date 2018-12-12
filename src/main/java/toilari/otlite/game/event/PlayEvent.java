package toilari.otlite.game.event;

import lombok.NoArgsConstructor;
import toilari.otlite.game.PlayGameState;

/**
 * {@link PlayGameState}-tilan viestit.
 */
public abstract class PlayEvent implements IEvent {
    /**
     * Viesti joka lähetetään kun halutaan palata valikkoon pelaajan hävittyä peli.
     */
    @NoArgsConstructor
    public static class ReturnToMenuAfterLoss extends PlayEvent {
    }

    @NoArgsConstructor
    public static class NextFloor extends PlayEvent {
    }
}
