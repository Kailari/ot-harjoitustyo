package toilari.otlite.game;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.PlayerStatisticDAO;
import toilari.otlite.dao.ProfileDAO;
import toilari.otlite.dao.SettingsDAO;
import toilari.otlite.dao.database.Database;
import toilari.otlite.dao.util.FileHelper;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.world.entities.TurnObjectManager;

import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    @Test
    void gameConstructorThrowsWhenGivenNullParameters() {
        assertThrows(NullPointerException.class, () -> createGame(null));
    }

    @Test
    void changeStateWorksWithNullCallback() {
        val game = createGame(new TestGameState());
        game.setStateChangeCallback(null);

        assertDoesNotThrow(() -> game.changeState(new TestGameState()));
    }

    @Test
    void changeStateTriggersCallback() {
        val state = new TestGameState();
        val game = createGame(new TestGameState());
        game.setStateChangeCallback(state::callback);

        game.changeState(state);
        assertTrue(state.callback);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void changeStateThrowsIfNewStateIsNull() {
        val game = createGame(new TestGameState());
        assertThrows(NullPointerException.class, () -> game.changeState(null));
    }

    @Test
    void getCurrentStateReturnsCorrectStateAfterInit() {
        val stateA = new TestGameState();
        val game = createGame(stateA);

        game.init();
        assertEquals(stateA, game.getCurrentGameState());
    }

    @Test
    void currentGameStateReturnsCorrectStateAfterStateChange() {
        val stateA = new TestGameState();
        val stateB = new TestGameState();
        val game = createGame(stateA);

        game.changeState(stateB);
        assertEquals(stateB, game.getCurrentGameState());
    }

    @Test
    void currentGameStateReturnsCorrectStateAfterInitAndStateChange() {
        val stateA = new TestGameState();
        val stateB = new TestGameState();
        val game = createGame(stateA);

        game.init();
        game.changeState(stateB);
        assertEquals(stateB, game.getCurrentGameState());
    }

    @Test
    void initDoesNotOverrideManuallySetState() {
        val state = new TestGameState();
        val game = createGame(new TestGameState());

        game.changeState(state);
        game.init();
        assertEquals(state, game.getCurrentGameState());
    }

    @Test
    void changeStateSetsGameInstanceForActiveState() {
        val state = new TestGameState();
        val game = createGame(new TestGameState());

        game.changeState(state);
        assertEquals(game, state.getGame());
    }

    @Test
    void initSetsGameInstanceForActiveState() {
        val state = new TestGameState();
        val game = createGame(state);

        game.init();
        assertEquals(game, state.getGame());
    }

    @Test
    void changeStateClearsGameInstanceForOldState() {
        val state = new TestGameState();
        val game = createGame(state);

        game.init();
        game.changeState(new TestGameState());
        assertNull(state.getGame());
    }

    @Test
    void initCallsInitForNewState() {
        val state = new TestGameState();
        val game = createGame(state);

        game.init();
        assertTrue(state.init);
    }

    @Test
    void changeStateCallsInitForNewState() {
        val state = new TestGameState();
        val game = createGame(new TestGameState());

        game.changeState(state);
        assertTrue(state.init);
    }

    @Test
    void changeStateCallsDestroyForOldState() {
        val state = new TestGameState();
        val game = createGame(state);

        game.init();
        game.changeState(new TestGameState());
        assertTrue(state.destroy);
    }

    @Test
    void gameIsRunningAfterInit() {
        val game = createGame(new TestGameState());
        game.init();

        assertTrue(game.isRunning());
    }

    @Test
    void updateCallsGameStateUpdate() {
        val state = new TestGameState();
        val game = createGame(state);

        game.init();
        game.update(1.0f);
        assertTrue(state.update);
    }

    @Test
    void updateThrowsIfNotInitialized() {
        val game = createGame(new TestGameState());

        assertThrows(IllegalStateException.class, () -> game.update(1.0f));
    }

    @Test
    void updateThrowsIfNotInitializedEvenIfStateIsSetManually() {
        val game = createGame(new TestGameState());

        game.changeState(new TestGameState());
        assertThrows(IllegalStateException.class, () -> game.update(1.0f));
    }

    @Test
    void destroyCallsGameStateDestroy() {
        val state = new TestGameState();
        val game = createGame(state);

        game.init();
        game.destroy();
        assertTrue(state.destroy);
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(Paths.get("target/test-temp"));
    }

    private static Game createGame(GameState state) {
        try {
            val database = new Database("src/test/resources/test.db");
            return new Game(state, "",
                () -> null,
                id -> null,
                id -> null,
                new ProfileDAO(database, new SettingsDAO("")),
                new StatisticsManager(new PlayerStatisticDAO(database)),
                TurnObjectManager::new);
        } catch (SQLException ignored) {
            throw new IllegalStateException("Invalid database path");
        }
    }

    private static class TestGameState extends GameState {
        boolean init, update, destroy, callback;

        @Override
        public boolean init() {
            this.init = true;
            return false;
        }

        @Override
        public void update(float delta) {
            this.update = true;
        }

        @Override
        public void destroy() {
            this.destroy = true;
        }

        void callback(GameState a, GameState b) {
            this.callback = true;
        }
    }
}
