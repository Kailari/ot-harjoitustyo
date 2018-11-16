package toilari.otlite;

import lombok.val;
import toilari.otlite.game.Game;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.io.dao.TextureDAO;
import toilari.otlite.rendering.CharacterRenderer;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.rendering.PlayGameStateRenderer;
import toilari.otlite.rendering.lwjgl.LWJGLGameRenderer;
import toilari.otlite.world.entities.TurnObjectManager;
import toilari.otlite.world.entities.characters.AnimalCharacter;
import toilari.otlite.world.entities.characters.PlayerCharacter;

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

        val app = new LWJGLGameRenderer(
            new Game(new PlayGameState(new TurnObjectManager())),
            stateRenderers
        );

        app.run();
    }
}
