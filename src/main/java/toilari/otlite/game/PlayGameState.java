package toilari.otlite;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.io.dao.TileDAO;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.world.Level;
import toilari.otlite.world.TileMapping;
import toilari.otlite.world.World;
import toilari.otlite.world.entities.ObjectManager;

import java.util.Scanner;

/**
 * Pelin varsinainen pelillinen osuus.
 */
@Slf4j
public class PlayGameState extends GameState {
    @Getter @NonNull private final World world;
    private final IRenderer<PlayGameState> renderer;

    private Scanner scanner;

    /**
     * Luo uuden pelitila-instanssin.
     *
     * @param renderer piirtäjä jota käytetään instannsin näyttämiseen
     * @param objectManager peliobjektimanageri
     */
    public PlayGameState(@NonNull IRenderer<PlayGameState> renderer, @NonNull ObjectManager objectManager) {
        this.world = new World(objectManager);
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
    }

    private void initSystems() {
        this.renderer.init(this);
        this.scanner = new Scanner(System.in);
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
    public void draw() {
        this.renderer.draw(this);
    }

    @Override
    public void destroy() {
        //this.scanner.close();
        this.renderer.destroy(this);
    }
}
