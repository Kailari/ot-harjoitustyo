package toilari.otlite;

import lombok.val;
import toilari.otlite.game.Game;
import toilari.otlite.game.GameState;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.rendering.PlayGameStateRenderer;
import toilari.otlite.rendering.lwjgl.LWJGLGameRenderer;
import toilari.otlite.world.entities.TurnObjectManager;

import java.util.HashMap;

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
        val rendererMapping = new HashMap<Class, IRenderer>();
        rendererMapping.put(PlayGameState.class, new PlayGameStateRenderer());

        val app = new LWJGLGameRenderer(
            new Game(new PlayGameState(new TurnObjectManager())),
            rendererMapping
        );

        app.run();
    }
}
