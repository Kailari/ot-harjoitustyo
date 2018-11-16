package toilari.otlite.rendering;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.Game;
import toilari.otlite.game.GameState;

/**
 * Piirtäjä pelin piirtämiseen. Käärii pelin logiikan ja käyttää sitä ajamaan käyttöliittymää.
 */
public abstract class AbstractGameRenderer {
    @Getter private final Game game;

    protected AbstractGameRenderer(Game game) {
        this.game = game;
        this.game.setStateChangeCallback(this::onStateChange);
    }

    /**
     * Takaisinkutsu joka ajetaan kun käärityn pelin pelitila vaihtuu.
     *
     * @param gameState uusi pelitila
     */
    protected abstract void onStateChange(@NonNull GameState gameState);

    /**
     * Alustaa pelin piirtämiseen tarvittavat resurssit.
     *
     * @return <code>true</code> jos alustus onnistuu, <code>false</code> jos ilmentyy virhe
     */
    protected abstract boolean init();

    /**
     * Piirtää pelin.
     */
    protected abstract void draw();

    /**
     * Vapauttaa pelin piirtämiseen varatut resurssit.
     */
    protected abstract void destroy();

    /**
     * Aloittaa pelin suorittamisen.
     */
    public void run() {
        init();
        this.game.init();
        while (this.game.isRunning()) {
            this.game.update();
            draw();
        }
        this.game.destroy();
        destroy();
    }
}
