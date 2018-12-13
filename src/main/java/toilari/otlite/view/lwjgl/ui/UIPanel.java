package toilari.otlite.view.lwjgl.ui;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

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

        this.topLeft = new Sprite(texture, 0, 0, 4, 4);
        this.top = new Sprite(texture, 3, 0, 2, 4);
        this.topRight = new Sprite(texture, 4, 0, 4, 4);

        this.left = new Sprite(texture, 0, 3, 4, 2);
        this.fill = new Sprite(texture, 3, 3, 2, 2);
        this.right = new Sprite(texture, 4, 3, 4, 2);

        this.botLeft = new Sprite(texture, 0, 4, 4, 4);
        this.bot = new Sprite(texture, 3, 4, 2, 4);
        this.botRight = new Sprite(texture, 4, 4, 4, 4);
    }

    /**
     * Piirtää paneelin.
     *
     * @param camera kamera jonka näkökulmasta piiretään
     * @param batch  sarjapiirtäjä jonka jonoon piirtokomennot asetetaan
     * @param x      x-koordinaatti johon piiretään
     * @param y      y-koordinaatti johon piiretään
     * @param color  värisävy
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull SpriteBatch batch, float x, float y, @NonNull Color color) {
        this.topLeft.draw(camera, batch, x, y, this.size, this.size, color);
        this.top.draw(camera, batch, x + this.size, y, this.width - 2 * this.size, this.size, color);
        this.topRight.draw(camera, batch, x + this.width - this.size, y, this.size, this.size, color);

        this.left.draw(camera, batch, x, y + this.size, size, height - 2 * size, color);
        this.fill.draw(camera, batch, x + this.size, y + this.size, width - 2 * size, height - 2 * size, color);
        this.right.draw(camera, batch, x + this.width - this.size, y + this.size, size, height - 2 * size, color);

        this.botLeft.draw(camera, batch, x, y + this.height - this.size, size, size, color);
        this.bot.draw(camera, batch, x + this.size, y + this.height - this.size, width - 2 * size, size, color);
        this.botRight.draw(camera, batch, x + this.width - this.size, y + this.height - this.size, size, size, color);
    }
}
