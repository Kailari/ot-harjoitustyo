package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.ProfileSelectState;
import toilari.otlite.game.event.ProfileMenuEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.UIButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProfileSelectStateRenderer implements ILWJGLRenderer<ProfileSelectState> {
    private static final int SMALL_BUTTON_WIDTH = 31;
    private static final int SMALL_BUTTON_HEIGHT = 8;
    private static final int SMALL_BUTTON_SIZE = 2;
    private static final int SMALL_BUTTON_FONT_SIZE = 2;

    private static final float SMALL_BUTTON_IDLE_R = 0.65f;
    private static final float SMALL_BUTTON_IDLE_G = 0.65f;
    private static final float SMALL_BUTTON_IDLE_B = 0.65f;

    private static final float SMALL_BUTTON_HOVER_R = 1.0f;
    private static final float SMALL_BUTTON_HOVER_G = 1.0f;
    private static final float SMALL_BUTTON_HOVER_B = 1.0f;


    private static final int LARGE_BUTTON_WIDTH = 64;
    private static final int LARGE_BUTTON_HEIGHT = 8;
    private static final int LARGE_BUTTON_SIZE = 2;
    private static final int LARGE_BUTTON_FONT_SIZE = 2;

    private static final float LARGE_BUTTON_IDLE_R = 0.65f;
    private static final float LARGE_BUTTON_IDLE_G = 0.65f;
    private static final float LARGE_BUTTON_IDLE_B = 0.65f;

    private static final float LARGE_BUTTON_HOVER_R = 1.0f;
    private static final float LARGE_BUTTON_HOVER_G = 1.0f;
    private static final float LARGE_BUTTON_HOVER_B = 1.0f;

    private static final int BUTTON_MARGIN = 2;
    private static final int BUTTON_START_Y = 8;

    @NonNull private final TextureDAO textureDAO;

    private Texture uiTexture;
    private UIButton createProfileButton;
    private UIButton removeProfileButton;
    private List<UIButton> profileButtons;
    private TextRenderer textRenderer;

    public ProfileSelectStateRenderer(@NonNull TextureDAO textureDAO) {
        this.textureDAO = textureDAO;
    }

    @Override
    public boolean init() {
        this.uiTexture = this.textureDAO.load("ui.png");
        this.textRenderer = new TextRenderer(this.textureDAO, 1, 16);

        this.createProfileButton = new UIButton(
            SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT, SMALL_BUTTON_SIZE,
            "Add", this.uiTexture,
            SMALL_BUTTON_IDLE_R, SMALL_BUTTON_IDLE_G, SMALL_BUTTON_IDLE_B,
            SMALL_BUTTON_HOVER_R, SMALL_BUTTON_HOVER_G, SMALL_BUTTON_HOVER_B,
            () -> System.out.println("Create clicked!"));

        this.removeProfileButton = new UIButton(
            SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT, SMALL_BUTTON_SIZE,
            "Remove", this.uiTexture,
            SMALL_BUTTON_IDLE_R, SMALL_BUTTON_IDLE_G, SMALL_BUTTON_IDLE_B,
            SMALL_BUTTON_HOVER_R, SMALL_BUTTON_HOVER_G, SMALL_BUTTON_HOVER_B,
            () -> System.out.println("Remove clicked!"));

        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull ProfileSelectState state) {
        if (this.profileButtons == null) {
            if (refreshProfileList(state)) {
                return;
            }
        }

        drawButtons(camera);
    }

    private void drawButtons(@NonNull LWJGLCamera camera) {
        val centerX = camera.getViewportWidth() / camera.getPixelsPerUnit() / 2;
        val x = Math.round(centerX - LARGE_BUTTON_WIDTH / 2.0f);
        int i = 0;
        for (val button : this.profileButtons) {
            val y = BUTTON_START_Y + SMALL_BUTTON_HEIGHT + BUTTON_MARGIN + (i++ * (LARGE_BUTTON_HEIGHT + BUTTON_MARGIN));
            button.draw(camera, this.textRenderer, LARGE_BUTTON_FONT_SIZE, x, y);
        }

        this.createProfileButton.draw(camera, this.textRenderer, SMALL_BUTTON_FONT_SIZE, x, BUTTON_START_Y);
        this.removeProfileButton.draw(camera, this.textRenderer, SMALL_BUTTON_FONT_SIZE, x + SMALL_BUTTON_WIDTH + BUTTON_MARGIN, BUTTON_START_Y);
    }

    private boolean refreshProfileList(@NonNull ProfileSelectState state) {
        this.profileButtons = new ArrayList<>();
        List<Profile> profiles;
        try {
            profiles = state.getGame().getProfileDao().findAll();
        } catch (SQLException e) {
            LOG.error("Could not fetch profiles.");
            return true;
        }

        for (val profile : profiles) {
            this.profileButtons.add(new UIButton(
                LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, LARGE_BUTTON_SIZE,
                profile.getName(), this.uiTexture,
                LARGE_BUTTON_IDLE_R, LARGE_BUTTON_IDLE_G, LARGE_BUTTON_IDLE_B,
                LARGE_BUTTON_HOVER_R, LARGE_BUTTON_HOVER_G, LARGE_BUTTON_HOVER_B,
                () -> state.getEventSystem().fire(new ProfileMenuEvent.Select(profile.getId()))));
        }

        return false;
    }

    @Override
    public void destroy() {
        this.uiTexture.destroy();
    }
}
