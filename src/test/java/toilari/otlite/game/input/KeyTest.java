package toilari.otlite.game.input;

import org.junit.jupiter.api.Test;

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
