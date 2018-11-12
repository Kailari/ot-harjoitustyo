package toilari.otlite;

import toilari.otlite.game.Game;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.rendering.PlayGameStateRenderer;
import toilari.otlite.rendering.lwjgl.LWJGLGameRenderer;
import toilari.otlite.world.entities.ObjectManager;

/**
 * Vastaa sovelluksen käynnistämisestä ja komentoriviparametrien parsimisesta.
 */
public class Launcher {
    /**
     * Main-metodi, parsii komentoriviparametrit ja käynnistää pelin.
     *
     * @param args Raa'at, parsimattomat kometoriviparametrit
     */
    public static void main(String[] args) {
        Game app = new Game(
            new PlayGameState(new PlayGameStateRenderer(), new ObjectManager()),
            new LWJGLGameRenderer()
        );
        app.run();
    }
}
