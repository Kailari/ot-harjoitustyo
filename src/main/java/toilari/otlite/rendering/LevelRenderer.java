package toilari.otlite.rendering;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.rendering.lwjgl.Sprite;
import toilari.otlite.world.Level;

public class LevelRenderer implements IRenderer<Level> {
    private final Texture tileset;
    private final Sprite[] tileSprites;
    private final int tileWidth;
    private final int tileHeight;


    public LevelRenderer(@NonNull Texture tileset, int tilesetRows, int tilesetColumns) {
        this.tileset = tileset;
        this.tileWidth = this.tileset.getWidth() / tilesetColumns;
        this.tileHeight = this.tileset.getHeight() / tilesetRows;

        this.tileSprites = new Sprite[tilesetRows * tilesetColumns];
        for (int y = 0; y < tilesetRows; y++) {
            for (int x = 0; x < tilesetColumns; x++) {
                this.tileSprites[(tilesetRows - (y + 1)) * tilesetColumns + x] = new Sprite(
                    tileset,
                    this.tileWidth * x,
                    this.tileHeight * y,
                    this.tileWidth,
                    this.tileHeight);
            }
        }
    }

    @Override
    public void draw(Level level) {
        this.tileset.bind();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                val tile = level.getTileAt(x, y);
                val index = tile.getTileIndex();

                this.tileSprites[index].draw(x * this.tileWidth, y * this.tileHeight);
            }
        }

        this.tileset.release();
    }
}
