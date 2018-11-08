package toilari.otlite;

import java.nio.file.Paths;
import java.util.Scanner;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.io.dao.TileDAO;
import toilari.otlite.world.Level;
import toilari.otlite.world.TileMapping;
import toilari.otlite.world.World;

/**
 * Pelin varsinainen pelillinen osuus.
 */
@Slf4j
public class PlayGameState extends GameState {
    @NonNull private final World world;
    
    private Scanner scanner;

    /**
     * Luo uuden pelitila-instanssin.
     */
    public PlayGameState() {
        super("Game");
        this.world = new World();
    }

    @Override
    public void init() {
        LOG.info("Initializing gameplay...{}", Paths.get(".").toAbsolutePath());

        LOG.info("Loading assets...");
        val tileDao = new TileDAO("content/tiles/");
        tileDao.discoverAndLoadAll();

        val tileMappings = new TileMapping(tileDao);
        this.world.changeLevel(createLevel(tileMappings));

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
        val level = this.world.getCurrentLevel();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                val tile = level.getTileAt(x, y);
                System.out.printf("%c%c", tile.getSymbol(), (x == level.getWidth() - 1 ? '\n' : ' '));
            }
        }
    }

    @Override
    public void destroy() {
        this.scanner.close();
    }
}
