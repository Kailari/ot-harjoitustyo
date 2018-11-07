package toilari.otlite;

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

    /**
     * Luo uuden pelitila-instanssin.
     */
    public PlayGameState() {
        super("Game");
        this.world = new World();
    }

    @Override
    public void init() {
        LOG.info("Initializing gameplay...");

        LOG.debug("Loading assets...");
        val tileMappings = new TileMapping(new TileDAO("content/tiles/"));
        this.world.changeLevel(createLevel(tileMappings));
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

    }

    @Override
    public void draw() {
        /*for (int y = 0; y < this.world.get; y++) {

        }*/
    }

    @Override
    public void destroy() {

    }
}
