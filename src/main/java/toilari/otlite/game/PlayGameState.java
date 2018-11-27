package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.CharacterDAO;
import toilari.otlite.dao.TileDAO;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.PlayerCharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.TileMapping;

/**
 * Pelin varsinainen pelillinen osuus.
 */
@Slf4j
public class PlayGameState extends GameState {
    @Getter @NonNull private final World world;
    @Getter private CharacterObject player;

    /**
     * Luo uuden pelitila-instanssin.
     *
     * @param objectManager vuoro/peliobjektimanageri
     * @throws NullPointerException jos piirtäjä tai objektimanageri on <code>null</code>
     */
    public PlayGameState(@NonNull TurnObjectManager objectManager) {
        objectManager.setGameState(this);
        this.world = new World(objectManager);
    }

    @Override
    public boolean init() {
        LOG.info("Initializing PlayGameState...");
        loadAssets();
        initSystems();

        LOG.info("Initialization finished.");

        val dao = new CharacterDAO("content/characters/");
        this.player = dao.get("player.json");

        this.world.getObjectManager().spawn(this.player);
        this.player.setTilePos(5, 3);

        createSheep(5, 1);
        createSheep(8, 1);
        createSheep(11, 2);

        return false;
    }

    private void createSheep(int x, int y) {
        var sheep = new CharacterObject(new CharacterAttributes(0, 2, 0,
            0.1f, 0.1f, 0.001f, 0.0f, 0.0f,
            0.1f, 0.01f, 0.0f, 0.1f,
            5.0f, 0.1f, 0.5f, 0.001f));
        this.world.getObjectManager().spawn(sheep);
        val ability = new MoveAbility();
        val component = new MoveControllerComponent.AI();
        ability.init(sheep, 0);
        component.init(sheep);
        sheep.addAbility(ability, component);

        val ability2 = new EndTurnAbility();
        val component2 = new EndTurnControllerComponent.AI();
        ability2.init(sheep, 99);
        component2.init(sheep);
        sheep.addAbility(ability2, component2);
        sheep.setTilePos(x, y);
    }

    private void loadAssets() {
        LOG.info("Loading assets...");

        val tileDao = new TileDAO("content/tiles/");
        tileDao.discoverAndLoadAll();

        val tileMappings = new TileMapping(tileDao);
        this.world.changeLevel(createLevel(tileMappings));
    }

    private void initSystems() {
        this.world.init();
    }

    private Level createLevel(TileMapping tileMappings) {
        final byte w = tileMappings.getIndex("wall");
        final byte f = tileMappings.getIndex("floor");
        final byte g = tileMappings.getIndex("grass");
        final byte h = tileMappings.getIndex("hole");
        final byte[] indices = new byte[]{
            w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w,
            w, g, g, g, w, f, f, w, f, w, h, h, h, f, f, w,
            w, g, g, f, w, f, w, w, w, f, h, f, h, f, f, w,
            w, h, h, f, f, f, f, w, f, f, h, h, h, f, f, w,
            w, h, h, f, f, f, f, f, f, f, f, f, f, f, f, w,
            w, f, f, f, h, f, f, w, f, f, f, f, f, g, g, w,
            w, f, f, g, g, f, f, h, f, g, g, f, g, g, g, w,
            w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w,
        };

        return new Level(16, 8, tileMappings, indices);
    }

    @Override
    public void update() {
        this.world.update();
    }

    @Override
    public void destroy() {
    }
}
