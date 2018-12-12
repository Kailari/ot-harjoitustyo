package toilari.otlite.view.lwjgl.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;

/**
 * Painike käyttöliittymässä.
 */
public class UIButton {
    private final StatisticsManager statistics;
    private final Profile profile;
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

    private final Color hoverColor;
    private final Color idleColor;

    private Color color;

    /**
     * Luo uuden painikkeen.
     *
     * @param statistics statistiikkamanageri
     * @param profile    nykyinen aktiivinen profiili
     * @param width      painikkeen leveys
     * @param height     painikkeen korkeus
     * @param size       painikkeen tekstuurin skaalaus
     * @param text       painikkeen teksti
     * @param texture    painikkeen tekstuuri
     * @param idleColor  painikkeen väri
     * @param hoverColor väri kursorin ollessa painikkeen päällä
     * @param onClick    takaisinkutsu jota kutsutaan kun painiketta painetaan
     */
    public UIButton(StatisticsManager statistics, Profile profile, int width, int height, int size, @NonNull String text, @NonNull Texture texture, @NonNull Color idleColor, @NonNull Color hoverColor, Action onClick) {
        this.statistics = statistics;
        this.profile = profile;
        this.width = width;
        this.height = height;
        this.size = size;
        this.text = text;

        this.idleColor = idleColor;
        this.hoverColor = hoverColor;

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

    /**
     * Piirtää painikkeen.
     *
     * @param camera       kamera jonka näkökulmasta piiretään
     * @param textRenderer tekstipiirtäjä jota käytetään tekstin piirtämiseen
     * @param fontSize     fonttikoko
     * @param x            painikkeen x-koordinaatti
     * @param y            painikkeen y-koordinaatti
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull TextRenderer textRenderer, int fontSize, float x, float y) {
        updateMouse(camera, x, y);

        this.topLeft.draw(camera, x, y, this.color);
        this.top.draw(camera, x + this.size, y, this.color);
        this.topRight.draw(camera, x + this.width - this.size, y, this.color);

        this.left.draw(camera, x, y + this.size, this.color);
        this.fill.draw(camera, x + this.size, y + this.size, this.color);
        this.right.draw(camera, x + this.width - this.size, y + this.size, this.color);

        this.botLeft.draw(camera, x, y + this.height - this.size, this.color);
        this.bot.draw(camera, x + this.size, y + this.height - this.size, this.color);
        this.botRight.draw(camera, x + this.width - this.size, y + this.height - this.size, this.color);

        val textX = x + (this.width / 2f - (this.text.length() / 2.0f) * fontSize);
        val textY = y + (this.height / 2f - fontSize / 2f);
        textRenderer.draw(camera, textX, textY, Color.WHITE, fontSize, this.text);
    }

    private void updateMouse(@NonNull LWJGLCamera camera, float x, float y) {
        val mouseX = (int) Math.floor(Input.getHandler().mouseX() / camera.getPixelsPerUnit());
        val mouseY = (int) Math.floor(Input.getHandler().mouseY() / camera.getPixelsPerUnit());

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            this.color = this.hoverColor;
        } else {
            this.color = this.idleColor;
        }

        if (Input.getHandler().isMousePressed(0)) {
            click(x, y, mouseX, mouseY);
        }
    }

    private void click(float x, float y, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            this.onClick.perform();
            if (this.statistics != null && this.profile != null) {
                this.statistics.increment(Statistics.BUTTONS_CLICKED, this.profile.getId());
            }
        }
    }

    /**
     * Takaisinkutsu joka kutsutaan kun painiketta painetaan.
     */
    public interface Action {
        /**
         * Mitä tapahtuu kun painiketta painetaan painamalla painiketta.
         */
        void perform();
    }
}
