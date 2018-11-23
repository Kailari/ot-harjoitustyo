package toilari.otlite.game.input;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputTest {
    @BeforeAll
    static void beforeAll() {
        Input.init(new TestInputHandler());
    }

    @Test
    void inputGetHandlerWorks() {
        assertNotNull(Input.getHandler());
    }

    @Test
    void tryingToReinitializeHandlerThrows() {
        assertThrows(IllegalStateException.class, () -> Input.init(new TestInputHandler()));
    }

    @Test
    void keyDownReturnsTrueForKeysThatAreDown() {
        assertTrue(Input.getHandler().isKeyDown(Key.SPACE));
    }

    @Test
    void keyDownReturnsFalseForKeysThatAreUp() {
        assertFalse(Input.getHandler().isKeyDown(Key.ESCAPE));
    }

    @Test
    void keyUpReturnsTrueForKeysThatAreUp() {
        assertTrue(Input.getHandler().isKeyUp(Key.ESCAPE));
    }

    @Test
    void keyUpReturnsFalseForKeysThatAreDown() {
        assertFalse(Input.getHandler().isKeyUp(Key.SPACE));
    }

    private static class TestInputHandler implements IInputHandler {
        @Override
        public boolean isKeyDown(Key key) {
            switch (key) {
                case SPACE:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean isKeyUp(Key key) {
            switch (key) {
                case SPACE:
                    return false;
                default:
                    return true;
            }
        }

        @Override
        public int mouseX() {
            return 10;
        }

        @Override
        public int mouseY() {
            return 20;
        }

        @Override
        public boolean isMouseDown(int button) {
            return false;
        }
    }
}
