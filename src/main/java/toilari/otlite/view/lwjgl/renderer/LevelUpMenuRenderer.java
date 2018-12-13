package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetDAO;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.PlayEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.util.Color;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.ui.UIButton;
import toilari.otlite.view.lwjgl.ui.UIPanel;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Piirtäjä attribuuttivalikon piirtämiseen.
 */
public class LevelUpMenuRenderer {
    private static final int LABEL_FONT_SIZE = 4;
    private static final int LEVEL_UP_BUTTON_TEXTURE_SIZE = 2;
    private static final int BACKGROUND_TEXTURE_SIZE = 2;

    private static final int TOP_MARGIN = 4;
    private static final int BOTTOM_MARGIN = 2;
    private static final int SIDE_MARGIN = 4;

    private static final int LEVEL_UP_BUTTON_MARGIN = 2;

    private static final int ATTRIBUTE_FIELD_MARGIN = 3;
    private static final int ATTRIBUTE_FIELD_HEIGHT = LABEL_FONT_SIZE;

    private static final int LEVEL_UP_BUTTON_SIZE = ATTRIBUTE_FIELD_HEIGHT + 2;
    private static final int LEVEL_UP_BUTTON_FONT_SIZE = 3;
    private static final int LEVEL_UP_BUTTON_OFFSET = -1;
    private static final Color LEVEL_UP_BUTTON_COLOR_HOVER = Color.WHITE;
    private static final Color LEVEL_UP_BUTTON_COLOR_IDLE = Color.WHITE.shade(0.35f);
    private static final Color LEVEL_UP_BUTTON_COLOR_DISABLED = Color.WHITE.shade(0.5f);

    private static final int CLOSE_BUTTON_TOP_MARGIN = 4;
    private static final int CLOSE_BUTTON_HEIGHT = 6;
    private static final int CLOSE_BUTTON_FONT_SIZE = 2;
    private static final int CLOSE_BUTTON_TEXTURE_SIZE = 2;
    private static final Color CLOSE_BUTTON_COLOR_IDLE = Color.WHITE.shade(0.35f);
    private static final Color CLOSE_BUTTON_COLOR_HOVER = Color.WHITE;

    private static final int LONGEST_ATTRIBUTE_NAME_LENGTH = Arrays.stream(Attribute.values()).map(Enum::name).max(Comparator.comparingInt(String::length)).orElse("ATTRIBUTE").length();
    private static final int CHARS_IN_ATTRIBUTE_LABEL = LONGEST_ATTRIBUTE_NAME_LENGTH + ": XX".length();

    private static final int MENU_WIDTH = SIDE_MARGIN * 2 + LEVEL_UP_BUTTON_MARGIN + LEVEL_UP_BUTTON_SIZE + CHARS_IN_ATTRIBUTE_LABEL * LABEL_FONT_SIZE;
    private static final int MENU_HEIGHT = TOP_MARGIN + (Attribute.MAX.ordinal() * (ATTRIBUTE_FIELD_HEIGHT + ATTRIBUTE_FIELD_MARGIN)) - ATTRIBUTE_FIELD_MARGIN
        + CLOSE_BUTTON_TOP_MARGIN + CLOSE_BUTTON_HEIGHT + BOTTOM_MARGIN;

    private static final int CLOSE_BUTTON_WIDTH = MENU_WIDTH / 2;

    private static final Color BACKGROUND_COLOR = Color.WHITE.shade(0.3f);
    private static final Color ATTRIBUTE_FIELD_COLOR = Color.WHITE.shade(0.2f);

    @NonNull private final UIPanel background;
    @NonNull private final Texture uiTexture;
    @NonNull private final UIButton closeButton;
    private UIButton[] buttons;

    /**
     * Luo uuden piirtäjän.
     *
     * @param textures dao jolla tekstuurit saadaan ladattua
     * @param state    pelitila
     */
    public LevelUpMenuRenderer(@NonNull IGetDAO<Texture, String> textures, @NonNull PlayGameState state) {
        this.uiTexture = textures.get("ui.png");
        this.background = new UIPanel(MENU_WIDTH, MENU_HEIGHT, BACKGROUND_TEXTURE_SIZE, this.uiTexture);
        this.closeButton = new UIButton(state.getGame().getStatistics(), state.getGame().getActiveProfile(),
            CLOSE_BUTTON_WIDTH, CLOSE_BUTTON_HEIGHT,
            CLOSE_BUTTON_TEXTURE_SIZE, "Close", this.uiTexture,
            CLOSE_BUTTON_COLOR_IDLE, CLOSE_BUTTON_COLOR_HOVER,
            () -> {
                state.getEventSystem().fire(new PlayEvent.CloseMenu());
                this.buttons = null;
            });
    }

    /**
     * Piirtää valikon.
     *
     * @param camera       kamera jonka näkökulmasta piiretään
     * @param state        pelitila
     * @param textRenderer piirtäjä jolla merkkijonot piiretään
     * @param batch        sarjapiirtäjä jonka jonoon piirtokomennot asetetaan
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, @NonNull TextRenderer textRenderer, SpriteBatch batch) {
        if (this.buttons == null) {
            refreshButtons(state.getManager().getPlayer(), state.getGame().getStatistics(), state.getGame().getActiveProfile());
        }

        val topLeftX = camera.getX() + camera.getViewportWidth() / 2.0f - MENU_WIDTH / 2.0f;
        val topLeftY = camera.getY() + camera.getViewportHeight() / 2.0f - MENU_HEIGHT / 2.0f;

        this.background.draw(camera, batch, topLeftX, topLeftY, BACKGROUND_COLOR);

        for (int i = 0; i < Attribute.MAX.ordinal(); i++) {
            drawAttributeField(camera, state, textRenderer, topLeftX, topLeftY, i, Attribute.values()[i], batch);
        }

        this.closeButton.draw(camera, batch, textRenderer, CLOSE_BUTTON_FONT_SIZE,
            topLeftX + MENU_WIDTH / 2.0f - CLOSE_BUTTON_WIDTH / 2.0f,
            topLeftY + TOP_MARGIN + (Attribute.MAX.ordinal() * (ATTRIBUTE_FIELD_HEIGHT + ATTRIBUTE_FIELD_MARGIN)) - ATTRIBUTE_FIELD_MARGIN + CLOSE_BUTTON_TOP_MARGIN);
    }

    private void drawAttributeField(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, @NonNull TextRenderer textRenderer, float topLeftX, float topLeftY, int i, Attribute attribute, SpriteBatch batch) {
        val x = topLeftX + SIDE_MARGIN;
        val y = topLeftY + TOP_MARGIN + (i * (ATTRIBUTE_FIELD_HEIGHT + ATTRIBUTE_FIELD_MARGIN));

        val level = state.getManager().getPlayer().getLevels().getAttributeLevel(attribute);
        val label = String.format("%" + LONGEST_ATTRIBUTE_NAME_LENGTH + "s: %s%d", attribute.name(), (level < 10 ? "0" : ""), level);
        textRenderer.draw(camera, batch, x, y, ATTRIBUTE_FIELD_COLOR, LABEL_FONT_SIZE, label);

        this.buttons[i].draw(camera, batch, textRenderer, LEVEL_UP_BUTTON_FONT_SIZE, x + label.length() * LABEL_FONT_SIZE + LEVEL_UP_BUTTON_MARGIN + LEVEL_UP_BUTTON_OFFSET, y + LEVEL_UP_BUTTON_OFFSET);
    }

    private void refreshButtons(CharacterObject player, StatisticsManager statistics, Profile activeProfile) {
        this.buttons = new UIButton[Attribute.MAX.ordinal()];

        for (int i = 0; i < Attribute.MAX.ordinal(); i++) {
            val attribute = Attribute.values()[i];

            val disabled = player.getLevels().getAttributeLevel(attribute) == 10 || player.getLevels().calculateMaxAttributePoints() - player.getLevels().calculateAttributePointsInUse() <= 0;
            this.buttons[i] = new UIButton(statistics, activeProfile,
                LEVEL_UP_BUTTON_SIZE, LEVEL_UP_BUTTON_SIZE,
                LEVEL_UP_BUTTON_TEXTURE_SIZE, "+", this.uiTexture,
                disabled ? LEVEL_UP_BUTTON_COLOR_DISABLED : LEVEL_UP_BUTTON_COLOR_IDLE,
                disabled ? LEVEL_UP_BUTTON_COLOR_DISABLED : LEVEL_UP_BUTTON_COLOR_HOVER,
                disabled ? () -> { } : () -> {
                    player.getLevels().levelUpAttribute(attribute);
                    refreshButtons(player, statistics, activeProfile);
                });
        }
    }
}
