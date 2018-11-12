package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.world.Level;

public class LevelRenderer implements IRenderer<Level> {
    private final Texture tileset;
    private final int tileWidth;
    private final int tileHeight;
    private final int tilesetRows;
    private final int tilesetColumns;

    public LevelRenderer(Texture tileset, int tilesetRows, int tilesetColumns) {
        this.tileset = tileset;
        this.tilesetRows = tilesetRows;
        this.tilesetColumns = tilesetColumns;

        this.tileWidth = this.tileset.getWidth() / tilesetColumns;
        this.tileHeight = this.tileset.getHeight() / tilesetRows;
    }

    @Override
    public void draw(Level level) {
        this.tileset.bind();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                val tile = level.getTileAt(x, y);
                val index = tile.getTileIndex();

                
            }
        }

        this.tileset.release();
    }
}
