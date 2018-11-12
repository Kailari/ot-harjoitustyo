package toilari.otlite;

import org.junit.jupiter.api.Test;
import toilari.otlite.rendering.GameRenderer;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testaa että {@link Game} ja {@link GameState} toimivat oikein.
 */
class GameTest {
    /**
     * Testaa että pelitilalla on validi viite peli-instanssiin {@link GameState#init()} kutsuttaessa.
     */
    @Test
    void gameInitCallsSetGame() {
        Game game = new TestGame(new InstantlyClosingGameState() {
            @Override
            public void init() {
                super.init();
                assertNotNull(getGame());
            }
        });

        game.run();
    }

    /**
     * Testaa että changeState tuhoaa vanhan pelitila-instanssin.
     */
    @Test
    void gameChangeStateWorksBeforeInit() {
        Game game = new TestGame();
        TestGameState a = new TestGameState();
        GameState b = new TestGameState();
        game.changeState(a);
        game.changeState(b);
        assertTrue(a.destroyCalled);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void gameChangeStateThrowsWhenCalledWithNullState() {
        TestGame game = new TestGame();
        assertThrows(NullPointerException.class, () -> game.changeState(null));
    }


    /**
     * Testaa että pelitila on tuhottu kun suoritus päättyy.
     */
    @Test
    void gameDestroyCalledAfterFinishedExecution() {
        TestGame game = new InstantlyClosingGame();
        game.run();
        assertTrue(game.isDestroyCalled());
    }

    /**
     * Testaa että pelin ajaminen lopetetaan kun {@link Game#setRunning(boolean)} asetetaan arvoon <code>false</code>.
     */
    @Test
    void gameExitsWhenRunningSetToFalse() {
        assertTimeoutPreemptively(Duration.ofSeconds(5L), () -> {
            Game game = new InstantlyClosingGame();
            game.run();
        });
    }

    /**
     * Testaa että {@link GameState#init()}, {@link GameState#update()}, {@link GameState#draw()}, ja
     * {@link GameState#destroy()} kutsutaan oikeisiin aikoihin.
     */
    @Test
    void gameCallingRunCallsExpectedMethods() {
        assertTimeoutPreemptively(Duration.ofSeconds(2L), () -> {
            TestGame game = new TestGame();
            Thread thread = new Thread(game::run);
            thread.start();

            Thread.sleep(50L);
            assertTrue(game.isInitCalled());
            assertTrue(game.isUpdateCalled());
            assertTrue(game.isDrawCalled());
            assertFalse(game.isDestroyCalled());

            game.setRunning(false);

            Thread.sleep(50L);

            assertTrue(game.isDestroyCalled());
        });
    }

    private static class TestGame extends Game {
        boolean isInitCalled() {
            return ((TestGameState) getCurrentGameState()).initCalled;
        }

        boolean isUpdateCalled() {
            return ((TestGameState) getCurrentGameState()).updatedCalled;
        }

        boolean isDrawCalled() {
            return ((TestGameState) getCurrentGameState()).drawCalled;
        }

        boolean isDestroyCalled() {
            return ((TestGameState) getCurrentGameState()).destroyCalled;
        }


        TestGame() {
            super(new TestGameState(), new GameRenderer());
        }

        TestGame(GameState state) {
            super(state, new GameRenderer());
        }
    }

    private static class InstantlyClosingGame extends TestGame {
        InstantlyClosingGame() {
            super(new InstantlyClosingGameState());
        }
    }

    private static class InstantlyClosingGameState extends TestGameState {
        @Override
        public void update() {
            super.update();
            getGame().setRunning(false);
        }
    }

    private static class TestGameState extends GameState {
        private boolean initCalled;
        private boolean updatedCalled;
        private boolean drawCalled;
        private boolean destroyCalled;


        @Override
        public void init() {
            this.initCalled = true;
        }

        @Override
        public void update() {
            this.updatedCalled = true;
        }

        @Override
        public void draw() {
            this.drawCalled = true;
        }

        @Override
        public void destroy() {
            this.destroyCalled = true;
        }
    }
}
