package toilari.otlite.view.lwjgl;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.TextureDAO;

import java.util.HashMap;
import java.util.Map;

public class TextRenderer {
    private static final String AVAILABLE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVXYZ.!?-+";
    private static final Map<Character, Integer> CHAR_TO_FRAME = new HashMap<>();

    static {
        for (int i = 0; i < AVAILABLE_CHARS.length(); i++) {
            CHAR_TO_FRAME.put(AVAILABLE_CHARS.charAt(i), i);
        }
    }


    private final int maxFontSize;
    private final int minFontSize;

    private final AnimatedSprite[] font;

    public TextRenderer(@NonNull TextureDAO textures, int minFontSize, int maxFontSize) {
        this.maxFontSize = maxFontSize;
        this.minFontSize = minFontSize;

        val texture = textures.load("font.png");
        int size = minFontSize;

        this.font = new AnimatedSprite[maxFontSize - minFontSize + 1];
        for (int i = 0; i < this.font.length; i++, size++) {
            this.font[i] = new AnimatedSprite(texture, 40, size, size);
        }
    }

    public void draw(@NonNull LWJGLCamera camera, int x, int y, float r, float g, float b, int size, @NonNull String string) {
        size = Math.max(this.minFontSize, Math.min(this.maxFontSize, size));
        string = string.toUpperCase();

        var destX = x;
        var destY = y;
        for (int i = 0; i < string.length(); i++) {
            val c = string.charAt(i);

            if (c == '\n') {
                destX = x;
                destY += size;
                continue;
            } else if (!CHAR_TO_FRAME.containsKey(c)) {
                continue;
            }

            this.font[size - 1].draw(camera, destX, destY, CHAR_TO_FRAME.get(c), r, g, b);
            destX += size;
        }
    }
}
