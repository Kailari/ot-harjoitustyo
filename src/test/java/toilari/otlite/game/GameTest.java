package toilari.otlite.game;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.util.FileHelper;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void gameConstructorThrowsWhenGivenNullParameters() {
        assertThrows(NullPointerException.class, () -> new Game(null, null));
        assertThrows(NullPointerException.class, () -> new Game(new TestGameState(), null));
        assertThrows(NullPointerException.class, () -> new Game(null, "target/test-temp/profiles.db"));
    }

    @Test
    void changeStateWorksWithNullCallback() {
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");
        game.setStateChangeCallback(null);

        assertDoesNotThrow(() -> game.changeState(new TestGameState()));
    }

    @Test
    void changeStateTriggersCallback() {
        val state = new TestGameState();
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");
        game.setStateChangeCallback(state::callback);

        game.changeState(state);
        assertTrue(state.callback);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void changeStateThrowsIfNewStateIsNull() {
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");
        assertThrows(NullPointerException.class, () -> game.changeState(null));
    }

    @Test
    void getCurrentStateReturnsCorrectStateAfterInit() {
        val stateA = new TestGameState();
        val game = new Game(stateA, "target/test-temp/profiles.db");

        game.init();
        assertEquals(stateA, game.getCurrentGameState());
    }

    @Test
    void currentGameStateReturnsCorrectStateAfterStateChange() {
        val stateA = new TestGameState();
        val stateB = new TestGameState();
        val game = new Game(stateA, "target/test-temp/profiles.db");

        game.changeState(stateB);
        assertEquals(stateB, game.getCurrentGameState());
    }

    @Test
    void currentGameStateReturnsCorrectStateAfterInitAndStateChange() {
        val stateA = new TestGameState();
        val stateB = new TestGameState();
        val game = new Game(stateA, "target/test-temp/profiles.db");

        game.init();
        game.changeState(stateB);
        assertEquals(stateB, game.getCurrentGameState());
    }

    @Test
    void initDoesNotOverrideManuallySetState() {
        val state = new TestGameState();
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");

        game.changeState(state);
        game.init();
        assertEquals(state, game.getCurrentGameState());
    }

    @Test
    void changeStateSetsGameInstanceForActiveState() {
        val state = new TestGameState();
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");

        game.changeState(state);
        assertEquals(game, state.getGame());
    }

    @Test
    void initSetsGameInstanceForActiveState() {
        val state = new TestGameState();
        val game = new Game(state, "target/test-temp/profiles.db");

        game.init();
        assertEquals(game, state.getGame());
    }

    @Test
    void changeStateClearsGameInstanceForOldState() {
        val state = new TestGameState();
        val game = new Game(state, "target/test-temp/profiles.db");

        game.init();
        game.changeState(new TestGameState());
        assertNull(state.getGame());
    }

    @Test
    void initCallsInitForNewState() {
        val state = new TestGameState();
        val game = new Game(state, "target/test-temp/profiles.db");

        game.init();
        assertTrue(state.init);
    }

    @Test
    void changeStateCallsInitForNewState() {
        val state = new TestGameState();
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");

        game.changeState(state);
        assertTrue(state.init);
    }

    @Test
    void changeStateCallsDestroyForOldState() {
        val state = new TestGameState();
        val game = new Game(state, "target/test-temp/profiles.db");

        game.init();
        game.changeState(new TestGameState());
        assertTrue(state.destroy);
    }

    @Test
    void gameIsRunningAfterInit() {
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");
        game.init();

        assertTrue(game.isRunning());
    }

    @Test
    void updateCallsGameStateUpdate() {
        val state = new TestGameState();
        val game = new Game(state, "target/test-temp/profiles.db");

        game.init();
        game.update();
        assertTrue(state.update);
    }

    @Test
    void updateThrowsIfNotInitialized() {
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");

        assertThrows(IllegalStateException.class, game::update);
    }

    @Test
    void updateThrowsIfNotInitializedEvenIfStateIsSetManually() {
        val game = new Game(new TestGameState(), "target/test-temp/profiles.db");

        game.changeState(new TestGameState());
        assertThrows(IllegalStateException.class, game::update);
    }

    @Test
    void destroyCallsGameStateDestroy() {
        val state = new TestGameState();
        val game = new Game(state, "target/test-temp/profiles.db");

        game.init();
        game.destroy();
        assertTrue(state.destroy);
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(Paths.get("target/test-temp"));
    }

    private static class TestGameState extends GameState {
        boolean init, update, destroy, callback;

        @Override
        public boolean init() {
            this.init = true;
            return false;
        }

        @Override
        public void update() {
            this.update = true;
        }

        @Override
        public void destroy() {
            this.destroy = true;
        }

        void callback(GameState state) {
            this.callback = true;
        }
    }
}
