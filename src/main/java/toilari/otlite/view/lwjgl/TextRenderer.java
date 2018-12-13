package toilari.otlite.view.lwjgl;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetDAO;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

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

    @NonNull private final IGetDAO<Texture, String> textures;

    private Texture fontTexture;
    private AnimatedSprite font;

    /**
     * Luo uuden piirtäjän.
     *
     * @param textures DAO jolla tarvittavat tekstuurit ladataan
     *
     * @throws NullPointerException jos dao on <code>null</code>
     */
    public TextRenderer(@NonNull IGetDAO<Texture, String> textures) {
        this.textures = textures;
    }


    /**
     * Alustaa piirtäjän ja lataa tarvittavat resurssit.
     */
    public void init() {
        this.fontTexture = this.textures.get("font.png");

        this.font = new AnimatedSprite(this.fontTexture, AVAILABLE_CHARS.length());
    }

    /**
     * Piirtää merkkijonon ruudulle.
     *
     * @param camera kamera jonka näkökulmasta piiretään
     * @param batch  sarjapiirtä
     * @param x      tekstin x-koordinaatti
     * @param y      tekstin y-koordinaatti
     * @param color  väri
     * @param size   fonttikoko
     * @param string piirrettävä merkkijono
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull SpriteBatch batch, float x, float y, @NonNull Color color, float size, @NonNull String string) {
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

            this.font.draw(camera, batch, destX, destY, size, size, TextRenderer.CHAR_TO_FRAME.get(c), color);
            destX += size;
        }
    }

    /**
     * Vapauttaa piirtäjälle varatut resurssit.
     */
    public void destroy() {
        this.fontTexture.destroy();
    }
}
