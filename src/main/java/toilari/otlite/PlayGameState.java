package toilari.otlite;

import java.nio.file.Paths;
import java.util.Scanner;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.io.dao.TileDAO;
import toilari.otlite.rendering.GameStateRenderer;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.world.Level;
import toilari.otlite.world.TileMapping;
import toilari.otlite.world.World;

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
     */
    public PlayGameState(IRenderer<PlayGameState> renderer) {
        super("Game");
        this.world = new World();
        this.renderer = renderer;
    }

    @Override
    public void init() {
        LOG.info("Initializing gameplay...{}", Paths.get(".").toAbsolutePath());

        LOG.info("Loading assets...");
        val tileDao = new TileDAO("content/tiles/");
        tileDao.discoverAndLoadAll();

        val tileMappings = new TileMapping(tileDao);
        this.world.changeLevel(createLevel(tileMappings));


        this.renderer.init(this);
        this.scanner = new Scanner(System.in);

        LOG.info("Initialization finished.");
    }

    private Level createLevel(TileMapping tileMappings) {
        final byte w = tileMappings.getIndex("wall");
        final byte f = tileMappings.getIndex("floor");
        final byte h = tileMappings.getIndex("hole");
        final byte[] indices = new byte[] {
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
        System.out.println("Vuoro pelattu, paina <enter>");
        this.scanner.nextLine();
    }

    @Override
    public void draw() {
        this.renderer.draw(this);
    }

    @Override
    public void destroy() {
        this.scanner.close();
        this.renderer.destroy(this);
    }
}
