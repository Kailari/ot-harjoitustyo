package toilari.otlite;

import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.Game;
import toilari.otlite.game.MainMenuGameState;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.ProfileSelectGameState;
import toilari.otlite.view.lwjgl.LWJGLGameRunner;
import toilari.otlite.view.lwjgl.renderer.MainMenuGameStateRenderer;
import toilari.otlite.view.lwjgl.renderer.PlayGameStateRenderer;
import toilari.otlite.view.lwjgl.renderer.ProfileSelectGameStateRenderer;
import toilari.otlite.view.renderer.IGameStateRenderer;

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
        val textureDao = new TextureDAO("content/textures/");

        val stateRenderers = new HashMap<Class, IGameStateRenderer>();
        stateRenderers.put(PlayGameState.class, new PlayGameStateRenderer(textureDao));
        stateRenderers.put(ProfileSelectGameState.class, new ProfileSelectGameStateRenderer(textureDao));
        stateRenderers.put(MainMenuGameState.class, new MainMenuGameStateRenderer(textureDao));


        val app = new LWJGLGameRunner(
            new Game(new ProfileSelectGameState(), "data/"),
            stateRenderers
        );

        app.run();
    }
}
