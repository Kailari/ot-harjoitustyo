package toilari.otlite.game;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testaa että pelin runko toimii oikein.
 */
class GameTest {
    /**
     * Testaa että konstruktori ei hyväksy virheellisiä parametreja.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void gameConstructorThrowsWhenGivenNullDefaultState() {
        assertThrows(NullPointerException.class, () -> new Game(null));
    }

    /**
     * Testaa että {@link Game#changeState(GameState)} toimii vaikkei tilanvaihdon takaisinkutsua olisi asetettu.
     */
    @Test
    void changeStateWorksWithNullCallback() {
        val game = new Game(new TestGameState());
        game.setStateChangeCallback(null);

        assertDoesNotThrow(() -> game.changeState(new TestGameState()));
    }

    /**
     * Testaa että {@link Game#changeState(GameState)} kutsuu tilanvaihdon takaisinkutsua.
     */
    @Test
    void changeStateTriggersCallback() {
        val state = new TestGameState();
        val game = new Game(new TestGameState());
        game.setStateChangeCallback(state::callback);

        game.changeState(state);
        assertTrue(state.callback);
    }

    /**
     * Testaa että {@link Game#changeState(GameState)} ei hyväksy epäkelpoja parametreja.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void changeStateThrowsIfNewStateIsNull() {
        val game = new Game(new TestGameState());
        assertThrows(NullPointerException.class, () -> game.changeState(null));
    }

    /**
     * Tarkistaa että {@link Game#getCurrentGameState()} palauttaa oikean pelitilan {@link Game#init()} kutsumisen
     * jälkeen.
     */
    @Test
    void getCurrentStateReturnsCorrectStateAfterInit() {
        val stateA = new TestGameState();
        val game = new Game(stateA);

        game.init();
        assertEquals(stateA, game.getCurrentGameState());
    }

    /**
     * Tarkistaa että {@link Game#getCurrentGameState()} palauttaa oikean pelitilan {@link Game#changeState(GameState)}
     * kutsumisen jälkeen.
     */
    @Test
    void currentGameStateReturnsCorrectStateAfterStateChange() {
        val stateA = new TestGameState();
        val stateB = new TestGameState();
        val game = new Game(stateA);

        game.changeState(stateB);
        assertEquals(stateB, game.getCurrentGameState());
    }

    /**
     * Tarkistaa että {@link Game#getCurrentGameState()} palauttaa oikean pelitilan {@link Game#init()} ja
     * {@link Game#changeState(GameState)} kutsumisen jälkeen.
     */
    @Test
    void currentGameStateReturnsCorrectStateAfterInitAndStateChange() {
        val stateA = new TestGameState();
        val stateB = new TestGameState();
        val game = new Game(stateA);

        game.init();
        game.changeState(stateB);
        assertEquals(stateB, game.getCurrentGameState());
    }

    /**
     * Testaa ettei oletuspelitilaa aseteta jos aloituspelitila on asetettu kutsumalla
     * {@link Game#changeState(GameState)} ennen {@link Game#init()}.
     */
    @Test
    void initDoesNotOverrideManuallySetState() {
        val state = new TestGameState();
        val game = new Game(new TestGameState());

        game.changeState(state);
        game.init();
        assertEquals(state, game.getCurrentGameState());
    }

    /**
     * Testaa että {@link Game#changeState(GameState)} asettaa uuden pelitilan peli-instanssin.
     */
    @Test
    void changeStateSetsGameInstanceForActiveState() {
        val state = new TestGameState();
        val game = new Game(new TestGameState());

        game.changeState(state);
        assertEquals(game, state.getGame());
    }

    /**
     * Testaa että {@link Game#init()} asettaa uuden pelitilan peli-instanssin.
     */
    @Test
    void initSetsGameInstanceForActiveState() {
        val state = new TestGameState();
        val game = new Game(state);

        game.init();
        assertEquals(game, state.getGame());
    }

    /**
     * Testaa että {@link Game#changeState(GameState)} tyhjentää vanhan pelitilan peli-instanssiviitteen.
     */
    @Test
    void changeStateClearsGameInstanceForOldState() {
        val state = new TestGameState();
        val game = new Game(state);

        game.init();
        game.changeState(new TestGameState());
        assertNull(state.getGame());
    }

    /**
     * Tarkistaa että {@link Game#init()} kutsuu {@link GameState#init()} uudelle pelitilalle.
     */
    @Test
    void initCallsInitForNewState() {
        val state = new TestGameState();
        val game = new Game(state);

        game.init();
        assertTrue(state.init);
    }

    /**
     * Tarkistaa että {@link Game#changeState(GameState)} kutsuu {@link GameState#init()} uudelle pelitilalle.
     */
    @Test
    void changeStateCallsInitForNewState() {
        val state = new TestGameState();
        val game = new Game(new TestGameState());

        game.changeState(state);
        assertTrue(state.init);
    }

    /**
     * Tarkistaa että {@link Game#changeState(GameState)} kutsuu {@link GameState#destroy()} vanhalle pelitilalle.
     */
    @Test
    void changeStateCallsDestroyForOldState() {
        val state = new TestGameState();
        val game = new Game(state);

        game.init();
        game.changeState(new TestGameState());
        assertTrue(state.destroy);
    }

    /**
     * Tarkistaa että {@link Game#isRunning()} palauttaa <code>true</code> kun {@link Game#init()} on kutsuttu.
     */
    @Test
    void gameIsRunningAfterInit() {
        val game = new Game(new TestGameState());
        game.init();

        assertTrue(game.isRunning());
    }

    /**
     * Tarkistaa että {@link Game#update()} kutsuu {@link GameState#update()} aktiiviselle pelitilalle.
     */
    @Test
    void updateCallsGameStateUpdate() {
        val state = new TestGameState();
        val game = new Game(state);

        game.init();
        game.update();
        assertTrue(state.update);
    }

    /**
     * Testaa että {@link Game#update()} aiheuttaa keskeytyksen jos {@link Game#init()} ei ole kutsuttu.
     */
    @Test
    void updateThrowsIfNotInitialized() {
        val game = new Game(new TestGameState());

        assertThrows(IllegalStateException.class, game::update);
    }

    /**
     * Testaa että {@link Game#update()} aiheuttaa keskeytyksen jos {@link Game#init()} ei ole kutsuttu,
     * vaikka pelitila olisi asetettu kutsumalla {@link Game#changeState(GameState)}.
     */
    @Test
    void updateThrowsIfNotInitializedEvenIfStateIsSetManually() {
        val game = new Game(new TestGameState());

        game.changeState(new TestGameState());
        assertThrows(IllegalStateException.class, game::update);
    }

    /**
     * Testaa että {@link Game#destroy()} kutsuu {@link GameState#destroy()} aktiiviselle pelitilalle.
     */
    @Test
    void destroyCallsGameStateDestroy() {
        val state = new TestGameState();
        val game = new Game(state);

        game.init();
        game.destroy();
        assertTrue(state.destroy);
    }


    private static class TestGameState extends GameState {
        boolean init, update, destroy, callback;

        @Override
        public void init() {
            this.init = true;
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
