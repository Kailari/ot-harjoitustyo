package toilari.otlite.view.lwjgl.ui;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.Texture;

/**
 * Käyttöliittymän "paneeli" eli rajattu alue.
 */
public class UIPanel {
    @Getter private final int width;
    @Getter private final int height;
    @Getter private final int size;

    private final Sprite topLeft;
    private final Sprite top;
    private final Sprite topRight;

    private final Sprite left;
    private final Sprite fill;
    private final Sprite right;

    private final Sprite botLeft;
    private final Sprite bot;
    private final Sprite botRight;

    /**
     * Luo uuden paneelin.
     *
     * @param width   paneelin leveys
     * @param height  paneelin korkeus
     * @param size    tekstuurin skaalaus
     * @param texture tekstuuri
     */
    public UIPanel(int width, int height, int size, @NonNull Texture texture) {
        this.width = width;
        this.height = height;
        this.size = size;

        this.topLeft = new Sprite(texture, 0, 0, 4, 4, size, size);
        this.top = new Sprite(texture, 3, 0, 2, 4, width - 2 * size, size);
        this.topRight = new Sprite(texture, 4, 0, 4, 4, size, size);

        this.left = new Sprite(texture, 0, 3, 4, 2, size, height - 2 * size);
        this.fill = new Sprite(texture, 3, 3, 2, 2, width - 2 * size, height - 2 * size);
        this.right = new Sprite(texture, 4, 3, 4, 2, size, height - 2 * size);

        this.botLeft = new Sprite(texture, 0, 4, 4, 4, size, size);
        this.bot = new Sprite(texture, 3, 4, 2, 4, width - 2 * size, size);
        this.botRight = new Sprite(texture, 4, 4, 4, 4, size, size);
    }

    /**
     * Piirtää paneelin.
     *
     * @param camera kamera jonka näkökulmasta piiretään
     * @param x      x-koordinaatti johon piiretään
     * @param y      y-koordinaatti johon piiretään
     * @param color  värisävy
     */
    public void draw(@NonNull LWJGLCamera camera, float x, float y, @NonNull Color color) {
        this.topLeft.draw(camera, x, y, color);
        this.top.draw(camera, x + this.size, y, color);
        this.topRight.draw(camera, x + this.width - this.size, y, color);

        this.left.draw(camera, x, y + this.size, color);
        this.fill.draw(camera, x + this.size, y + this.size, color);
        this.right.draw(camera, x + this.width - this.size, y + this.size, color);

        this.botLeft.draw(camera, x, y + this.height - this.size, color);
        this.bot.draw(camera, x + this.size, y + this.height - this.size, color);
        this.botRight.draw(camera, x + this.width - this.size, y + this.height - this.size, color);
    }
}
