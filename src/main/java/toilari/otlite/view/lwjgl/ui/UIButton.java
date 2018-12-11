package toilari.otlite.view.lwjgl.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.profile.statistics.StatisticsManager;
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

    private final float hoverR;
    private final float hoverG;
    private final float hoverB;

    private final float idleR;
    private final float idleG;
    private final float idleB;

    private float r, g, b;

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
     * @param idleR      värisävyn punainen komponentti
     * @param idleG      värisävyn vihreä komponentti
     * @param idleB      värisävyn sininen komponentti
     * @param hoverR     värisävyn punainen komponentti kursorin ollessa painikkeen päällä
     * @param hoverG     värisävyn vihreä komponentti kursorin ollessa painikkeen päällä
     * @param hoverB     värisävyn sininen komponentti kursorin ollessa painikkeen päällä
     * @param onClick    takaisinkutsu jota kutsutaan kun painiketta painetaan
     */
    public UIButton(StatisticsManager statistics, Profile profile, int width, int height, int size, @NonNull String text, @NonNull Texture texture, float idleR, float idleG, float idleB, float hoverR, float hoverG, float hoverB, Action onClick) {
        this.statistics = statistics;
        this.profile = profile;
        this.width = width;
        this.height = height;
        this.size = size;
        this.text = text;

        this.idleR = idleR;
        this.idleG = idleG;
        this.idleB = idleB;

        this.hoverR = hoverR;
        this.hoverG = hoverG;
        this.hoverB = hoverB;

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

        this.topLeft.draw(camera, x, y, this.r, this.g, this.b);
        this.top.draw(camera, x + this.size, y, this.r, this.g, this.b);
        this.topRight.draw(camera, x + this.width - this.size, y, this.r, this.g, this.b);

        this.left.draw(camera, x, y + this.size, this.r, this.g, this.b);
        this.fill.draw(camera, x + this.size, y + this.size, this.r, this.g, this.b);
        this.right.draw(camera, x + this.width - this.size, y + this.size, this.r, this.g, this.b);

        this.botLeft.draw(camera, x, y + this.height - this.size, this.r, this.g, this.b);
        this.bot.draw(camera, x + this.size, y + this.height - this.size, this.r, this.g, this.b);
        this.botRight.draw(camera, x + this.width - this.size, y + this.height - this.size, this.r, this.g, this.b);

        val textX = x + (this.width / 2f - (this.text.length() / 2.0f) * fontSize);
        val textY = y + (this.height / 2f - fontSize / 2f);
        textRenderer.draw(camera, textX, textY, 1.0f, 1.0f, 1.0f, fontSize, this.text);
    }

    private void updateMouse(@NonNull LWJGLCamera camera, float x, float y) {
        val mouseX = (int) Math.floor(Input.getHandler().mouseX() / camera.getPixelsPerUnit());
        val mouseY = (int) Math.floor(Input.getHandler().mouseY() / camera.getPixelsPerUnit());

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            this.r = this.hoverR;
            this.g = this.hoverG;
            this.b = this.hoverB;
        } else {
            this.r = this.idleR;
            this.g = this.idleG;
            this.b = this.idleB;
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
