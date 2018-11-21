package toilari.otlite.io;

import org.junit.jupiter.api.Test;
import toilari.otlite.game.input.Key;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testaa näppäinkoodienumeraatotion toimintaa.
 */
class KeyTest {
    @Test
    void keyUnknownIsDefinedCorrectly() {
        assertEquals(-1, Key.UNKNOWN.getKeycode());
    }

    @Test
    void keyLastIsDefinedCorrectly() {
        assertEquals(348, Key.LAST.getKeycode());
    }
}
