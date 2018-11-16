package toilari.otlite.game;

import lombok.NonNull;

/**
 * Takaisinkutsu jolla käärivä käyttöliittymämoottori voi reagoida pelitilan muutoksiin.
 */
public interface StateChangeCallback {
    /**
     * Kutsutaan kun pelitila vaihtuu.
     *
     * @param newState uusi pelitila
     */
    void onStateChange(@NonNull GameState newState);
}
