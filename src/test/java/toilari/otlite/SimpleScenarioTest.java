package toilari.otlite;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.PlayerStatisticDAO;
import toilari.otlite.dao.ProfileDAO;
import toilari.otlite.dao.SettingsDAO;
import toilari.otlite.dao.database.Database;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.dao.util.FileHelper;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeGameRunner;
import toilari.otlite.fake.FakeInputHandler;
import toilari.otlite.game.*;
import toilari.otlite.game.event.*;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AIMoveTowardsPlayerMoveControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.PerformIfNothingElseToDoEndTurnControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerEndTurnControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerMoveControllerComponent;
import toilari.otlite.game.world.level.*;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class SimpleScenarioTest {
    private Game game;
    private FakeGameRunner runner;
    private FakeInputHandler input;

    @BeforeEach
    void beforeEach() throws SQLException {
        val tiles = new TileMapping(createTileMappings());

        input = new FakeInputHandler() {
            @Override
            public void update() {
            }
        };
        game = new TestGame(
            new ProfileSelectGameState(),
            "test",
            createTileMappings(),
            createCharacters()::get,
            createLevels(tiles)::get,
            createProfileDAO(),
            createStatisticsManager(),
            TurnObjectManager::new);

        runner = FakeGameRunner.create(game, input);
        runner.init();
    }

    @AfterEach
    void afterEach() {
        runner.destroy();
        FileHelper.deleteDirectoryAndChildren(Paths.get("target/test-temp"));
    }

    @Test
    void openBestiary10000Times() {
        // Create and immediately select a profile
        assertTrue(game.getCurrentGameState() instanceof ProfileSelectGameState);
        { //
            ProfileSelectGameState state = (ProfileSelectGameState) game.getCurrentGameState();
            state.getEventSystem().subscribeTo(ProfileMenuEvent.Added.class, (e) -> {
                state.getEventSystem().fire(new ProfileMenuEvent.Select(e.getProfile()));
            });
            state.getEventSystem().fire(new ProfileMenuEvent.Add("TestProfile"));
        }

        for (int i = 0; i < 10000; i++) {
            // Open bestiary
            assertTrue(game.getCurrentGameState() instanceof MainMenuGameState);
            { //
                MainMenuGameState state = (MainMenuGameState) game.getCurrentGameState();

                state.getEventSystem().fire(new MainMenuEvent.Bestiary());
            }

            // Close bestiary
            assertTrue(game.getCurrentGameState() instanceof BestiaryGameState);
            { //
                BestiaryGameState state = (BestiaryGameState) game.getCurrentGameState();

                state.getEventSystem().fire(new BestiaryEvent.Return());
            }
        }

        // Quit game
        assertTrue(game.getCurrentGameState() instanceof MainMenuGameState);
        { //
            MainMenuGameState state = (MainMenuGameState) game.getCurrentGameState();

            state.getEventSystem().fire(new MenuEvent.Quit());
        }
    }

    @Test
    void playerWalksToAPit() {
        // Create and immediately select a profile
        assertTrue(game.getCurrentGameState() instanceof ProfileSelectGameState);
        { //
            ProfileSelectGameState state = (ProfileSelectGameState) game.getCurrentGameState();
            state.getEventSystem().subscribeTo(ProfileMenuEvent.Added.class, (e) -> {
                state.getEventSystem().fire(new ProfileMenuEvent.Select(e.getProfile()));
            });
            state.getEventSystem().fire(new ProfileMenuEvent.Add("TestProfile"));
        }

        // Start a new game
        assertTrue(game.getCurrentGameState() instanceof MainMenuGameState);
        { //
            MainMenuGameState state = (MainMenuGameState) game.getCurrentGameState();

            state.getEventSystem().fire(new MainMenuEvent.NewGame());
        }

        // Simulate game flow
        assertTrue(game.getCurrentGameState() instanceof PlayGameState);
        { //
            PlayGameState state = (PlayGameState) game.getCurrentGameState();
            val player = state.getManager().getPlayer();

            assertNotNull(player);
            assertTrue(state.getManager().isCharactersTurn(player));
            assertEquals(2, player.getTileX());
            assertEquals(2, player.getTileY());

            // Move right once and end turn
            input.setPressedKeys(Key.RIGHT);
            runner.runTick(0.02f);
            input.setPressedKeys(Key.SPACE);
            runner.runTick(0.02f);
            assertEquals(3, player.getTileX());
            assertEquals(2, player.getTileY());
            assertFalse(state.getManager().isCharactersTurn(player));

            assertTimeout(Duration.ofSeconds(1L), () -> {
                while (!state.getManager().isCharactersTurn(player)) {
                    runner.runTick(0.02f);
                }
            });

            // Move down twice
            assertTrue(state.getManager().isCharactersTurn(player));
            input.setPressedKeys(Key.DOWN);
            runner.runTick(0.02f);
            input.setPressedKeys(Key.DOWN);
            runner.runTick(0.02f);
            assertEquals(3, player.getTileX());
            assertEquals(4, player.getTileY());

            // Player *should* have ended up in a pit
            assertTrue(state.getManager().isCharactersTurn(player));
            assertTrue(player.isDead());
            assertTrue(player.isRemoved());

            // Dead players don't get turns
            runner.runTick(0.02f);
            assertFalse(state.getManager().isCharactersTurn(player));

            // Return to menu
            state.getEventSystem().fire(new PlayEvent.ReturnToMenuAfterLoss());
        }

        // Quit game
        assertTrue(game.getCurrentGameState() instanceof MainMenuGameState);
        { //
            MainMenuGameState state = (MainMenuGameState) game.getCurrentGameState();

            state.getEventSystem().fire(new MenuEvent.Quit());
        }
    }

    private StatisticsManager createStatisticsManager() throws SQLException {
        val db = new Database("target/test-temp/profiles.db");
        return new StatisticsManager(new PlayerStatisticDAO(db));
    }

    private ProfileDAO createProfileDAO() throws SQLException {
        val db = new Database("target/test-temp/profiles.db");
        return new ProfileDAO(db, new SettingsDAO("target/test-temp/"));
    }

    private Map<String, LevelData> createLevels(TileMapping tiles) {
        val mapping = new HashMap<String, LevelData>();
        mapping.put("test", createTestLevelData(tiles));
        return mapping;
    }

    private Map<String, CharacterObject> createCharacters() {
        val mapping = new HashMap<String, CharacterObject>();

        mapping.put("player", FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new MoveAbility(), new PlayerMoveControllerComponent()),
            new AbilityEntry<>(99, new EndTurnAbility(), new PlayerEndTurnControllerComponent())
        ));

        mapping.put("enemy", FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new MoveAbility(), new AIMoveTowardsPlayerMoveControllerComponent(3, 1337)),
            new AbilityEntry<>(99, new EndTurnAbility(), new PerformIfNothingElseToDoEndTurnControllerComponent())
        ));
        return mapping;
    }

    private IGetAllDAO<Tile> createTileMappings() {
        return () -> Arrays.asList(
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 0, "floor"),
            new KillTile(false, true, 0, "hole")
        );
    }

    private class TestGame extends Game {
        public TestGame(
            @NonNull GameState defaultState,
            @NonNull String initialLevelId,
            @NonNull IGetAllDAO<Tile> tiles,
            @NonNull IGetByIDDao<CharacterObject> characters,
            @NonNull IGetByIDDao<LevelData> levels,
            @NonNull ProfileDAO profiles,
            @NonNull StatisticsManager statistics,
            @NonNull Supplier<TurnObjectManager> managerSupplier
        ) {
            super(defaultState, initialLevelId, tiles, characters, levels, profiles, statistics, managerSupplier);
        }
    }

    private LevelData createTestLevelData(TileMapping tiles) {
        return new LevelData(
            8, 8,
            tiles.getMapping(),
            new byte[]{
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 1, 1, 1, 1, 1, 1, 0,
                0, 1, 1, 1, 2, 1, 1, 0,
                0, 1, 1, 1, 2, 1, 1, 0,
                0, 1, 0, 2, 1, 1, 1, 2,
                0, 0, 1, 1, 1, 2, 2, 2,
                0, 1, 0, 1, 2, 2, 1, 2,
                0, 0, 0, 0, 0, 2, 2, 2,
            },
            "__null",
            Arrays.asList(
                new LevelData.CharacterEntry(2, 2, "player"),
                new LevelData.CharacterEntry(2, 5, "enemy"),
                new LevelData.CharacterEntry(6, 6, "enemy")
            )
        );
    }
}
