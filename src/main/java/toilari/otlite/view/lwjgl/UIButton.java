package toilari.otlite.view.lwjgl;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;

public class UIButton {
    @Getter private final int width;
    @Getter private final int height;
    @Getter private final int size;

    private final String text;
    private final Sprite topLeft;
    private final Sprite top;
    private final Sprite topRight;

    private final Sprite left;
    private final Sprite fill;
    private final Sprite right;

    private final Sprite botLeft;
    private final Sprite bot;
    private final Sprite botRight;

    private final Action onClick;

    private boolean mouseWasDown;

    public UIButton(int width, int height, int size, @NonNull String text, @NonNull Texture texture, Action onClick) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.text = text;
        this.onClick = onClick;

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

    public void draw(@NonNull LWJGLCamera camera, @NonNull TextRenderer textRenderer, int fontSize, int x, int y, float r, float g, float b) {
        updateMouse(camera, x, y);

        this.topLeft.draw(camera, x, y, r, g, b);
        this.top.draw(camera, x + this.size, y, r, g, b);
        this.topRight.draw(camera, x + this.width - this.size, y, r, g, b);

        this.left.draw(camera, x, y + this.size, r, g, b);
        this.fill.draw(camera, x + this.size, y + this.size, r, g, b);
        this.right.draw(camera, x + this.width - this.size, y + this.size, r, g, b);

        this.botLeft.draw(camera, x, y + this.height - this.size, r, g, b);
        this.bot.draw(camera, x + this.size, y + this.height - this.size, r, g, b);
        this.botRight.draw(camera, x + this.width - this.size, y + this.height - this.size, r, g, b);

        val textX = x + Math.round(this.width / 2f - (this.text.length() / 2.0f) * fontSize);
        val textY = y + Math.round(this.height / 2f - fontSize / 2f);
        textRenderer.draw(camera, textX, textY, 1.0f, 1.0f, 1.0f, fontSize, this.text);
    }

    private void updateMouse(@NonNull LWJGLCamera camera, int x, int y) {
        if (this.mouseWasDown) {
            this.mouseWasDown = Input.getHandler().isMouseDown(0);
        } else if (Input.getHandler().isMouseDown(0)) {
            this.mouseWasDown = true;
            click(x, y,
                (int) Math.floor(Input.getHandler().mouseX() / camera.getPixelsPerUnit()),
                (int) Math.floor(Input.getHandler().mouseY() / camera.getPixelsPerUnit()));
        }
    }

    private void click(int x, int y, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            this.onClick.perform();
        }
    }

    public interface Action {
        void perform();
    }
}
