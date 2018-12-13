package toilari.otlite.game.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.world.entities.characters.Attribute;

/**
 * {@link PlayGameState}-tilan viestit.
 */
public abstract class PlayEvent implements IEvent {
    /**
     * Viesti joka lähetetään kun halutaan palata valikkoon pelaajan hävittyä peli.
     */
    public static class ReturnToMenuAfterLoss extends PlayEvent {
    }

    /**
     * Viesti joka lähetetään kun pelaaja siirtyy seuraavaan kerrokseen.
     */
    public static class NextFloor extends PlayEvent {
    }

    /**
     * Viesti joka lähetetään kun aktiivinen valikko suljetaan.
     */
    public static class CloseMenu extends PlayEvent {
    }

    /**
     * Viesti joka lähetetään kun pelaaja haluaa käyttää attribuuttipisteitään attribuutin tason kasvattamiseen.
     */
    @RequiredArgsConstructor
    public static class LevelUpAttribute extends PlayEvent {
        @Getter @NonNull private final Attribute attribute;
    }
}
