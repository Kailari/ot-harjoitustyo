package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.Level;
import toilari.otlite.game.world.Tile;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.Texture;

/**
 * Piirtää pelin kartan.
 */
public class LevelRenderer implements ILWJGLRenderer<Level> {
    @NonNull private final TextureDAO textureDAO;
    @NonNull private final String textureFilename;

    private final int tilesetRows;
    private final int tilesetColumns;

    private Texture tileset;
    private Sprite[] tileSprites;

    /**
     * Luo uuden karttapiirtäjän.
     *
     * @param textureDAO      DAO-jolla tekstuuri ladataan
     * @param textureFilename tileset-tekstuurin tiedostonimi
     * @param tilesetRows     montako riviä atlaksessa on
     * @param tilesetColumns  montako saraketta atlaksessa on
     * @throws NullPointerException jos tiedostopolku tai DAO on null
     */
    LevelRenderer(@NonNull TextureDAO textureDAO, @NonNull String textureFilename, int tilesetRows, int tilesetColumns) {
        this.textureFilename = textureFilename;
        this.textureDAO = textureDAO;
        this.tilesetRows = tilesetRows;
        this.tilesetColumns = tilesetColumns;
    }

    @Override
    public boolean init() {
        this.tileset = this.textureDAO.load(this.textureFilename);
        int tileWidth = this.tileset.getWidth() / tilesetColumns;
        int tileHeight = this.tileset.getHeight() / tilesetRows;

        this.tileSprites = new Sprite[this.tilesetRows * this.tilesetColumns];
        for (int y = 0; y < tilesetRows; y++) {
            for (int x = 0; x < tilesetColumns; x++) {
                this.tileSprites[y * tilesetColumns + x] = new Sprite(
                    this.tileset,
                    tileWidth * x,
                    tileHeight * y,
                    tileWidth,
                    tileHeight,
                    Tile.SIZE_IN_WORLD,
                    Tile.SIZE_IN_WORLD);
            }
        }

        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull Level level) {
        this.tileset.bind();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                val tile = level.getTileAt(x, y);
                val index = tile.getTileIndex();

                this.tileSprites[index].draw(camera, x * Tile.SIZE_IN_WORLD, y * Tile.SIZE_IN_WORLD, 1.0f, 1.0f, 1.0f);
            }
        }

        this.tileset.release();
    }

    @Override
    public void destroy(@NonNull Level level) {
        this.tileset.destroy();
    }
}
