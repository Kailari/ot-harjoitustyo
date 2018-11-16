package toilari.otlite.rendering;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.rendering.lwjgl.Sprite;
import toilari.otlite.rendering.lwjgl.Texture;
import toilari.otlite.world.Level;
import toilari.otlite.world.Tile;

/**
 * Piirtää pelin kartan.
 */
public class LevelRenderer implements IRenderer<Level> {
    private final Texture tileset;
    private final Sprite[] tileSprites;

    /**
     * Luo uuden karttapiirtäjän.
     *
     * @param tileset        tekstuuriatlas josta ruutujen tekstuurit löytyvät
     * @param tilesetRows    montako riviä atlaksessa on
     * @param tilesetColumns montako saraketta atlaksessa on
     * @throws NullPointerException jos tekstuuri on null
     */
    public LevelRenderer(@NonNull Texture tileset, int tilesetRows, int tilesetColumns) {
        this.tileset = tileset;
        int tileWidth = this.tileset.getWidth() / tilesetColumns;
        int tileHeight = this.tileset.getHeight() / tilesetRows;

        this.tileSprites = new Sprite[tilesetRows * tilesetColumns];
        for (int y = 0; y < tilesetRows; y++) {
            for (int x = 0; x < tilesetColumns; x++) {
                this.tileSprites[y * tilesetColumns + x] = new Sprite(
                    tileset,
                    tileWidth * x,
                    tileHeight * y,
                    tileWidth,
                    tileHeight,
                    Tile.SIZE_IN_WORLD,
                    Tile.SIZE_IN_WORLD);
            }
        }
    }

    @Override
    public void draw(@NonNull Camera camera, @NonNull Level level) {
        this.tileset.bind();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                val tile = level.getTileAt(x, y);
                val index = tile.getTileIndex();

                this.tileSprites[index].draw(camera, x * Tile.SIZE_IN_WORLD, y * Tile.SIZE_IN_WORLD);
            }
        }

        this.tileset.release();
    }

    @Override
    public void destroy(@NonNull Level level) {
        this.tileset.destroy();
    }
}
