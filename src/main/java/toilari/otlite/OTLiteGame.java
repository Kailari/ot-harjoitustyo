package toilari.otlite;

import toilari.otlite.rendering.IRenderer;

/**
 * Pelin runko.
 */
public class OTLiteGame extends Game {
    /**
     * Luo uuden peli-instanssin.
     */
    public OTLiteGame(IRenderer<PlayGameState> renderer) {
        super(() -> new PlayGameState(renderer));
    }
}
