package toilari.otlite.game;

import lombok.NonNull;
import toilari.otlite.game.GameState;

/**
 * Takaisinkutsu jolla käärivä käyttöliittymämoottori voi reagoida pelitilan muutoksiin.
 */
public interface StateChangeCallback {
    /**
     * Kutsutaan kun pelitila vaihtuu.
     *
     * @param oldState wanha pelitila
     * @param newState uusi pelitila
     */
    void onStateChange(GameState oldState, @NonNull GameState newState);
}
