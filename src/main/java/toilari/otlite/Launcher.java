package toilari.otlite;

import toilari.otlite.rendering.GameStateRenderer;
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
        Game app = new Game(new PlayGameState(new GameStateRenderer(), new ObjectManager()));
        app.run();
    }
}
