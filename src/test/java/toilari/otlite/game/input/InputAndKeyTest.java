package toilari.otlite.game.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testaa näppäinkoodienumeraatotion toimintaa.
 */
class InputAndKeyTest {
    @Test
    void keyUnknownIsDefinedCorrectly() {
        assertEquals(-1, Key.UNKNOWN.getKeycode());
    }

    @Test
    void keyLastIsDefinedCorrectly() {
        assertEquals(348, Key.LAST.getKeycode());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void inputSingletonInitDoesNotAcceptNullHandler() {
        assertThrows(NullPointerException.class, () -> Input.init(null));
    }
}
