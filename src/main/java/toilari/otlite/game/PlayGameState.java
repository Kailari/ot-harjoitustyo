package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.io.dao.TextureDAO;
import toilari.otlite.io.dao.TileDAO;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.rendering.PlayerRenderer;
import toilari.otlite.world.Level;
import toilari.otlite.world.Tile;
import toilari.otlite.world.TileMapping;
import toilari.otlite.world.World;
import toilari.otlite.world.entities.ObjectManager;
import toilari.otlite.world.entities.characters.PlayerCharacter;

/**
 * Pelin varsinainen pelillinen osuus.
 */
@Slf4j
public class PlayGameState extends GameState {
    @Getter @NonNull private final World world;
    private final IRenderer<PlayGameState> renderer;

    private final PlayerCharacter player;

    /**
     * Luo uuden pelitila-instanssin.
     *
     * @param renderer      piirtäjä jota käytetään instannsin näyttämiseen
     * @param objectManager peliobjektimanageri
     */
    public PlayGameState(@NonNull IRenderer<PlayGameState> renderer, @NonNull ObjectManager objectManager) {
        this.world = new World(objectManager);
        this.player = new PlayerCharacter();
        this.renderer = renderer;
    }

    @Override
    public void init() {
        LOG.info("Initializing PlayGameState...");
        loadAssets();
        initSystems();

        LOG.info("Initialization finished.");
    }

    private void loadAssets() {
        LOG.info("Loading assets...");

        val tileDao = new TileDAO("content/tiles/");
        tileDao.discoverAndLoadAll();

        val tileMappings = new TileMapping(tileDao);
        this.world.changeLevel(createLevel(tileMappings));
        this.world.getObjectManager().spawn(this.player);
        this.player.setX(2 * Tile.SIZE_IN_WORLD);
        this.player.setY(2 * Tile.SIZE_IN_WORLD);
    }

    private void initSystems() {
        this.renderer.init(this);
    }

    private Level createLevel(TileMapping tileMappings) {
        final byte w = tileMappings.getIndex("wall");
        final byte f = tileMappings.getIndex("floor");
        final byte h = tileMappings.getIndex("hole");
        final byte[] indices = new byte[]{
            w, w, w, w, w, w, w, w,
            w, f, f, f, w, f, f, w,
            w, f, f, f, w, f, w, w,
            w, h, h, f, f, f, f, w,
            w, h, h, f, f, f, f, w,
            w, f, f, f, h, f, f, w,
            w, f, f, f, f, f, f, w,
            w, w, w, w, w, w, w, w,
        };

        return new Level(8, 8, tileMappings, indices);
    }

    @Override
    public void update() {
        this.world.update();
    }

    @Override
    public void draw(Camera camera) {
        this.renderer.draw(camera, this);
    }

    @Override
    public void destroy() {
        //this.scanner.close();
        this.renderer.destroy(this);
    }
}
