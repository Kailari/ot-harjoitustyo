package toilari.otlite;

/**
 * Pelin runko.
 */
public class OTLiteGame extends Game {
    /**
     * Luo uuden peli-instanssin.
     */
    public OTLiteGame() {
        super(PlayGameState::new);
    }
}
