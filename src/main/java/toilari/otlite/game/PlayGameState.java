package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.event.CharacterEvent;
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

    @Getter private int currentFloor = 0;

    @Getter private boolean menuOpen = false;

    /**
     * Luo uuden pelitila-instanssin.
     *
     * @param manager    vuoro/peliobjektimanageri
     * @param tiles      dao jolla ruututyypit saadaan valittua
     * @param characters dao jolla hahmot ladataan
     * @param levels     dao jolla kartat saadaan ladattua
     *
     * @throws NullPointerException jos piirtäjä tai objektimanageri on <code>null</code>
     */
    public PlayGameState(@NonNull TurnObjectManager manager, @NonNull IGetAllDAO<Tile> tiles, @NonNull IGetByIDDao<CharacterObject> characters, @NonNull IGetByIDDao<LevelData> levels) {
        this.manager = manager;
        this.characters = characters;

        this.manager.setGameState(this);
        this.world = new World(manager, tiles, levels, characters);
    }

    @Override
    public boolean init() {
        LOG.info("Initializing PlayGameState...");
        this.world.init();
        getEventSystem().subscribeTo(PlayEvent.NextFloor.class, (e) -> this.currentFloor++);

        val player = this.manager.spawnTemplate(this.characters.getByID("player"));
        this.manager.setPlayer(player);
        val levelId = getGame().getInitialLevelId();
        this.world.changeLevel(levelId);

        LOG.info("Initialization finished.");

        getEventSystem().subscribeTo(PlayEvent.ReturnToMenuAfterLoss.class, (e) -> getGame().changeState(new MainMenuGameState()));
        getEventSystem().subscribeTo(PlayEvent.CloseMenu.class, (e) -> this.menuOpen = false);
        getEventSystem().subscribeTo(CharacterEvent.LevelUp.class, this::onCharacterLevelUp);

        getEventSystem().subscribeTo(PlayEvent.LevelUpAttribute.class, e -> {
            val levels = this.manager.getPlayer().getLevels();
            if (levels.calculateMaxAttributePoints() - levels.calculateAttributePointsInUse() > 0) {
                levels.levelUpAttribute(e.getAttribute());
            }
        });
        return false;
    }

    private void onCharacterLevelUp(@NonNull CharacterEvent.LevelUp event) {
        if (event.getCharacter().equals(this.manager.getPlayer())) {
            this.menuOpen = true;
        }
    }

    @Override
    public void update(float delta) {
        if (!this.menuOpen) {
            this.world.update(delta);
        }
    }

    @Override
    public void destroy() {
    }
}
