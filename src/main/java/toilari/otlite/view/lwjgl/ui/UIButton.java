package toilari.otlite.view.lwjgl.ui;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;

/**
 * Painike käyttöliittymässä.
 */
public class UIButton {
    private final StatisticsManager statistics;
    private final Profile profile;

    @NonNull private final String text;
    @NonNull private final UIPanel panel;

    @NonNull private final Action onClick;

    @NonNull private final Color hoverColor;
    @NonNull private final Color idleColor;

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
        this.panel = new UIPanel(width, height, size, texture);

        this.text = text;

        this.idleColor = idleColor;
        this.hoverColor = hoverColor;

        this.onClick = onClick;
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

        this.panel.draw(camera, x, y, this.color);

        val textX = x + (this.panel.getWidth() / 2f - (this.text.length() / 2.0f) * fontSize);
        val textY = y + (this.panel.getHeight() / 2f - fontSize / 2f);
        textRenderer.draw(camera, textX, textY, Color.WHITE, fontSize, this.text);
    }

    private void updateMouse(@NonNull LWJGLCamera camera, float x, float y) {
        val mouseX = (int) Math.floor(camera.getX() + Input.getHandler().mouseX() / camera.getPixelsPerUnit());
        val mouseY = (int) Math.floor(camera.getY() + Input.getHandler().mouseY() / camera.getPixelsPerUnit());

        if (mouseX >= x && mouseX <= x + this.panel.getWidth() && mouseY >= y && mouseY <= y + this.panel.getHeight()) {
            this.color = this.hoverColor;
        } else {
            this.color = this.idleColor;
        }

        if (Input.getHandler().isMousePressed(0)) {
            click(x, y, mouseX, mouseY);
        }
    }

    private void click(float x, float y, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x + this.panel.getWidth() && mouseY >= y && mouseY <= y + this.panel.getHeight()) {
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
