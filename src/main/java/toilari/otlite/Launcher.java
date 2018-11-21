package toilari.otlite;

import lombok.val;
import toilari.otlite.game.Game;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.ProfileSelectState;
import toilari.otlite.view.renderer.IRenderer;
import toilari.otlite.view.renderer.lwjgl.PlayGameStateRenderer;
import toilari.otlite.view.renderer.ProfileSelectStateRenderer;
import toilari.otlite.view.lwjgl.LWJGLGameRunner;

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
        val stateRenderers = new HashMap<Class, IRenderer>();
        stateRenderers.put(PlayGameState.class, new PlayGameStateRenderer());
        stateRenderers.put(ProfileSelectState.class, new ProfileSelectStateRenderer());

        val app = new LWJGLGameRunner(
            new Game(new ProfileSelectState("data/profiles.db")),
            stateRenderers
        );

        app.run();
    }
}
