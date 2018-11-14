package toilari.otlite.game;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.GameRenderer;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.world.entities.ObjectManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayGameAndRendererTest {
    @Test
    void hasValidWorldAfterInit() {
        val state = new PlayGameState((c, r) -> { }, new ObjectManager()) {
            @Override
            public void update() {
                assertNotNull(getWorld());
                getGame().setRunning(false);
            }
        };

        val game = new Game(state, new GameRenderer());

        game.run();
    }

    @Test
    void rendererMethodsGetCalled() {
        val renderer = new TestRenderer();

        val state = new PlayGameState(renderer, new ObjectManager()) {
            @Override
            public void update() {
                assertFalse(renderer.destroy);
                getGame().setRunning(false);
            }

            @Override
            public void draw(Camera camera) {
                super.draw(camera);
                assertTrue(renderer.draw);
            }
        };

        val game = new Game(state, new GameRenderer());
        game.run();
        assertTrue(renderer.destroy);
    }

    private static class TestRenderer implements IRenderer<PlayGameState> {
        boolean draw, init, destroy;

        @Override
        public void draw(Camera camera, PlayGameState s) {
            this.draw = true;
        }

        @Override
        public boolean init(PlayGameState s) {
            this.init = true;
            return true;
        }

        @Override
        public void destroy(PlayGameState s) {
            this.destroy = true;
        }
    }
}
