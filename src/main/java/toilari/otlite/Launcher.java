package toilari.otlite;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.*;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.*;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.view.lwjgl.LWJGLGameRunner;
import toilari.otlite.view.lwjgl.renderer.BestiaryGameStateRenderer;
import toilari.otlite.view.lwjgl.renderer.MainMenuGameStateRenderer;
import toilari.otlite.view.lwjgl.renderer.PlayGameStateRenderer;
import toilari.otlite.view.lwjgl.renderer.ProfileSelectGameStateRenderer;
import toilari.otlite.view.renderer.IGameStateRenderer;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Vastaa sovelluksen käynnistämisestä ja komentoriviparametrien parsimisesta.
 */
@Slf4j
public class Launcher {
    /**
     * Main-metodi, parsii komentoriviparametrit ja käynnistää pelin.
     *
     * @param args Raa'at, parsimattomat kometoriviparametrit
     */
    public static void main(String[] args) {
        val app = createRunner("data/", "1");

        if (app != null) {
            app.run();
        }
    }

    private static AbstractGameRunner createRunner(@NonNull String savePath, @NonNull String initialLevelId) {
        ProfileDAO profiles;
        PlayerStatisticDAO statisticsDao;
        try {
            val database = new Database(savePath + "profiles.db");
            profiles = new ProfileDAO(database, new SettingsDAO(savePath));
            statisticsDao = new PlayerStatisticDAO(database);
        } catch (SQLException e) {
            LOG.error("Could not initialize statistics. Shutting down.");
            LOG.error("Cause: {}", e.getMessage());
            return null;
        }

        val statistics = new StatisticsManager(statisticsDao);
        val characterDao = new CharacterDAO("content/characters/");
        val tileDao = new TileDAO("content/tiles/");
        val levelDao = new LevelDAO("content/levels/");

        val stateRenderers = createStateRenderers(characterDao);
        val game = new Game(new ProfileSelectGameState(), initialLevelId, tileDao, characterDao, levelDao, profiles, statistics, TurnObjectManager::new);

        return new LWJGLGameRunner(game, stateRenderers);
    }

    private static HashMap<Class, IGameStateRenderer> createStateRenderers(CharacterDAO characterDao) {
        val textureDao = new TextureDAO("content/textures/");
        val rendererDao = new RendererDAO("content/renderers/", textureDao);

        val stateRenderers = new HashMap<Class, IGameStateRenderer>();
        stateRenderers.put(ProfileSelectGameState.class, new ProfileSelectGameStateRenderer(textureDao));
        stateRenderers.put(MainMenuGameState.class, new MainMenuGameStateRenderer(textureDao));
        stateRenderers.put(PlayGameState.class, new PlayGameStateRenderer<>(rendererDao, textureDao));
        stateRenderers.put(BestiaryGameState.class, new BestiaryGameStateRenderer<>(characterDao, rendererDao, textureDao));
        return stateRenderers;
    }
}
