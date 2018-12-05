package toilari.otlite.view.lwjgl;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * Piirtää tekstiä ruudulle.
 */
public class TextRenderer {
    private static final String AVAILABLE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.:!?-+/\\()[]<>";
    private static final Map<Character, Integer> CHAR_TO_FRAME = new HashMap<>();

    static {
        for (int i = 0; i < AVAILABLE_CHARS.length(); i++) {
            CHAR_TO_FRAME.put(AVAILABLE_CHARS.charAt(i), i);
        }
    }

    @NonNull private final TextureDAO textures;
    private final int maxFontSize;
    private final int minFontSize;

    private Texture fontTexture;
    private AnimatedSprite[] font;

    /**
     * Luo uuden piirtäjän.
     *
     * @param textures    DAO jolla tarvittavat tekstuurit ladataan
     * @param minFontSize minimifonttikoko
     * @param maxFontSize maksimifonttikoko
     * @throws NullPointerException jos dao on <code>null</code>
     */
    public TextRenderer(@NonNull TextureDAO textures, int minFontSize, int maxFontSize) {
        this.textures = textures;
        this.maxFontSize = maxFontSize;
        this.minFontSize = minFontSize;
    }


    /**
     * Alustaa piirtäjän ja lataa tarvittavat resurssit.
     */
    public void init() {
        this.fontTexture = this.textures.get("font.png");
        int size = this.minFontSize;

        this.font = new AnimatedSprite[this.maxFontSize - this.minFontSize + 1];
        for (int i = 0; i < this.font.length; i++, size++) {
            this.font[i] = new AnimatedSprite(this.fontTexture, AVAILABLE_CHARS.length(), size, size);
        }
    }

    /**
     * Piirtää merkkijonon ruudulle.
     *
     * @param camera kamera jonka näkökulmasta piiretään
     * @param x      tekstin x-koordinaatti
     * @param y      tekstin y-koordinaatti
     * @param r      värisävyn punainen komponentti
     * @param g      värisävyn vihreä komponentti
     * @param b      värisävyn sininen komponentti
     * @param size   fonttikoko
     * @param string piirrettävä merkkijono
     */
    public void draw(@NonNull LWJGLCamera camera, float x, float y, float r, float g, float b, int size, @NonNull String string) {
        size = Math.max(this.minFontSize, Math.min(this.maxFontSize, size));
        string = string.toUpperCase();
        float destX = x, destY = y;
        for (int i = 0; i < string.length(); i++) {
            val c = string.charAt(i);

            if (c == ' ') {
                destX += size;
                continue;
            } else if (c == '\n') {
                destX = x;
                destY += size;
                continue;
            } else if (!TextRenderer.CHAR_TO_FRAME.containsKey(c)) {
                continue;
            }

            this.font[size - 1].draw(camera, destX, destY, TextRenderer.CHAR_TO_FRAME.get(c), r, g, b);
            destX += size;
        }
    }

    /**
     * Vapauttaa piirtäjälle varatut resurssit.
     */
    public void destroy() {
        this.fontTexture.destroy();
        for (val as : this.font) {
            as.destroy();
        }
    }
}
