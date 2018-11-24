package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.TileDAO;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.AnimalCharacter;
import toilari.otlite.game.world.entities.characters.PlayerCharacter;
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
    @Getter private PlayerCharacter player;

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

        this.player = new PlayerCharacter();
        this.player.addAbility(new MoveAbility(this.player, 0), new MoveControllerComponent.Player(this.player));
        this.player.addAbility(new EndTurnAbility(this.player, 99), new EndTurnControllerComponent.Player(this.player, getGame().getActiveProfile().getSettings().isAutoEndTurn()));
        this.world.getObjectManager().spawn(this.player);
        this.player.setTilePos(5, 3);

        var sheep = new AnimalCharacter();
        this.world.getObjectManager().spawn(sheep);
        sheep.addAbility(new MoveAbility(sheep, 0), new MoveControllerComponent.AI(sheep));
        sheep.addAbility(new EndTurnAbility(sheep, 99), new EndTurnControllerComponent.AI(sheep));
        sheep.setTilePos(5, 1);

        sheep = new AnimalCharacter();
        this.world.getObjectManager().spawn(sheep);
        sheep.addAbility(new MoveAbility(sheep, 0), new MoveControllerComponent.AI(sheep));
        sheep.addAbility(new EndTurnAbility(sheep, 99), new EndTurnControllerComponent.AI(sheep));
        sheep.setTilePos(8, 1);

        sheep = new AnimalCharacter();
        this.world.getObjectManager().spawn(sheep);
        sheep.addAbility(new MoveAbility(sheep, 0), new MoveControllerComponent.AI(sheep));
        sheep.addAbility(new EndTurnAbility(sheep, 99), new EndTurnControllerComponent.AI(sheep));
        sheep.setTilePos(11, 2);

        return false;
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
        final byte h = tileMappings.getIndex("hole");
        final byte[] indices = new byte[]{
            w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w,
            w, f, f, f, w, f, f, w, f, w, h, h, h, f, f, w,
            w, f, f, f, w, f, w, w, w, f, h, f, h, f, f, w,
            w, h, h, f, f, f, f, w, f, f, h, h, h, f, f, w,
            w, h, h, f, f, f, f, f, f, f, f, f, f, f, f, w,
            w, f, f, f, h, f, f, w, f, f, f, f, f, f, f, w,
            w, f, f, f, f, f, f, h, f, f, f, f, f, f, f, w,
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
