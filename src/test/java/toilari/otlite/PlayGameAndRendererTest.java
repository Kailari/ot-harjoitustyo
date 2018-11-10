package toilari.otlite;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.rendering.IRenderer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayGameAndRendererTest {
    @Test
    void hasValidWorldAfterInit() {
        val state = new PlayGameState((r) -> {
        }) {
            @Override
            public void update() {
                assertNotNull(getWorld());
                getGame().setRunning(false);
            }
        };

        val game = new Game(state);

        game.run();
    }

    @Test
    void rendererMethodsGetCalled() {
        val renderer = new TestRenderer();

        val state = new PlayGameState(renderer) {
            @Override
            public void update() {
                assertFalse(renderer.destroy);
                getGame().setRunning(false);
            }

            @Override
            public void draw() {
                super.draw();
                assertTrue(renderer.draw);
            }
        };

        val game = new Game(state);
        game.run();
        assertTrue(renderer.destroy);
    }

    private static class TestRenderer implements IRenderer<PlayGameState> {
        boolean draw, init, destroy;

        @Override
        public void draw(PlayGameState s) {
            this.draw = true;
        }

        @Override
        public void init(PlayGameState s) {
            this.init = true;
        }

        @Override
        public void destroy(PlayGameState s) {
            this.destroy = true;
        }
    }
}