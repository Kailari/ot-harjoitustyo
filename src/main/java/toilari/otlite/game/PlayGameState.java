package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.event.PlayEvent;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.level.LevelData;
import toilari.otlite.game.world.level.Tile;

/**
 * Pelin varsinainen pelillinen osuus.
 */
@Slf4j
public class PlayGameState extends GameState {
    @Getter @NonNull private final World world;
    @Getter @NonNull private final TurnObjectManager manager;
    @NonNull private final IGetByIDDao<CharacterObject> characters;
    @NonNull private final IGetByIDDao<LevelData> levels;
    @NonNull private final IGetAllDAO<Tile> tiles;

    /**
     * Luo uuden pelitila-instanssin.
     *
     * @param manager    vuoro/peliobjektimanageri
     * @param tiles      dao jolla ruututyypit saadaan valittua
     * @param characters dao jolla hahmot ladataan
     * @param levels     dao jolla kartat saadaan ladattua
     * @throws NullPointerException jos piirtäjä tai objektimanageri on <code>null</code>
     */
    public PlayGameState(@NonNull TurnObjectManager manager, @NonNull IGetAllDAO<Tile> tiles, @NonNull IGetByIDDao<CharacterObject> characters, @NonNull IGetByIDDao<LevelData> levels) {
        this.manager = manager;
        this.tiles = tiles;
        this.characters = characters;
        this.levels = levels;

        this.manager.setGameState(this);
        this.world = new World(manager);
    }

    @Override
    public boolean init() {
        LOG.info("Initializing PlayGameState...");
        this.world.init();

        val player = this.characters.getByID("player");
        this.manager.spawn(player);
        this.manager.setPlayer(player);
        val levelId = "1";
        changeLevel(levelId);

        LOG.info("Initialization finished.");

        getEventSystem().subscribeTo(PlayEvent.ReturnToMenuAfterLoss.class, (e) -> getGame().changeState(new MainMenuGameState()));
        return false;
    }

    private void changeLevel(@NonNull String levelId) {
        val level = this.levels.getByID(levelId);
        this.world.changeLevel(level.asLevel(this.tiles));
        level.spawn(this.characters, this.manager);
    }

    @Override
    public void update(float delta) {
        this.world.update(delta);
    }

    @Override
    public void destroy() {
    }
}
