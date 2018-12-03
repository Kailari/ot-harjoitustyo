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
public class ProfileSelectStateRenderer implements ILWJGLGameStateRenderer<ProfileSelectState> {
    private static final int ADD_BUTTON_WIDTH = 31;
    private static final int ADD_BUTTON_HEIGHT = 8;
    private static final int ADD_BUTTON_SIZE = 2;
    private static final int ADD_BUTTON_FONT_SIZE = 2;

    private static final float ADD_BUTTON_IDLE_R = 0.65f;
    private static final float ADD_BUTTON_IDLE_G = 0.65f;
    private static final float ADD_BUTTON_IDLE_B = 0.65f;

    private static final float ADD_BUTTON_HOVER_R = 1.0f;
    private static final float ADD_BUTTON_HOVER_G = 1.0f;
    private static final float ADD_BUTTON_HOVER_B = 1.0f;


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
    private static final int BUTTON_START_Y = 16;

    private static final int TITLE_FONTSIZE = 5;
    private static final int TITLE_Y = 4;
    private static final String TITLE_STRING = "Select/Create a profile";

    @NonNull private final TextureDAO textureDAO;

    private Texture uiTexture;
    private UIButton createProfileButton;
    private List<UIButton> profileButtons;
    private List<UIButton> profileRemoveButtons;
    private List<Integer> profileIds;
    private TextRenderer textRenderer;

    private ProfileSelectState state;

    /**
     * Luo uuden profiilivalikon piirtäjän.
     *
     * @param textureDAO dao jolla tarvittavat tekstuurit voidaan ladata
     */
    public ProfileSelectStateRenderer(@NonNull TextureDAO textureDAO) {
        this.textureDAO = textureDAO;
    }

    @Override
    public boolean init(@NonNull ProfileSelectState state) {
        this.state = state;
        this.uiTexture = this.textureDAO.get("ui.png");
        this.textRenderer = new TextRenderer(this.textureDAO, 1, 16);

        if (refreshProfileList(state)) {
            return true;
        }

        this.createProfileButton = new UIButton(ADD_BUTTON_WIDTH, ADD_BUTTON_HEIGHT, ADD_BUTTON_SIZE, "Add", this.uiTexture,
            ADD_BUTTON_IDLE_R, ADD_BUTTON_IDLE_G, ADD_BUTTON_IDLE_B, ADD_BUTTON_HOVER_R, ADD_BUTTON_HOVER_G, ADD_BUTTON_HOVER_B,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Add("Player #" + this.profileButtons.size())));

        state.getEventSystem().subscribeTo(ProfileMenuEvent.Added.class, this::onAdded);
        state.getEventSystem().subscribeTo(ProfileMenuEvent.Removed.class, this::onRemoved);

        return false;
    }

    private void onAdded(@NonNull ProfileMenuEvent.Added event) {
        createProfileButton(this.state, event.getProfile());
    }

    private void onRemoved(@NonNull ProfileMenuEvent.Removed event) {
        int index = this.profileIds.indexOf(event.getProfileId());
        if (index >= 0) {
            this.profileButtons.remove(index);
            this.profileRemoveButtons.remove(index);
            this.profileIds.remove(index);
        }
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull ProfileSelectState state) {
        drawTitle(camera);
        drawButtons(camera);
    }

    private void drawTitle(@NonNull LWJGLCamera camera) {
        val centerX = camera.getViewportWidth() / camera.getPixelsPerUnit() / 2;
        val x = centerX - TITLE_STRING.length() * (TITLE_FONTSIZE / 2.0f);

        this.textRenderer.draw(camera, x, TITLE_Y, 1.0f, 1.0f, 1.0f, TITLE_FONTSIZE, TITLE_STRING);
    }

    private void drawButtons(@NonNull LWJGLCamera camera) {
        val centerX = camera.getViewportWidth() / camera.getPixelsPerUnit() / 2;
        val x = Math.round(centerX - LARGE_BUTTON_WIDTH / 2.0f);

        for (int i = 0; i < this.profileButtons.size(); i++) {
            val button = this.profileButtons.get(i);
            val y = BUTTON_START_Y + ADD_BUTTON_HEIGHT + BUTTON_MARGIN + (i * (LARGE_BUTTON_HEIGHT + BUTTON_MARGIN));
            button.draw(camera, this.textRenderer, LARGE_BUTTON_FONT_SIZE, x, y);

            val removeButton = this.profileRemoveButtons.get(i);
            val removeButtonX = x + LARGE_BUTTON_WIDTH - LARGE_BUTTON_HEIGHT;
            removeButton.draw(camera, this.textRenderer, LARGE_BUTTON_FONT_SIZE, removeButtonX, y);
        }

        this.createProfileButton.draw(camera, this.textRenderer, ADD_BUTTON_FONT_SIZE, x, BUTTON_START_Y);
    }

    private boolean refreshProfileList(@NonNull ProfileSelectState state) {
        this.profileButtons = new ArrayList<>();
        this.profileRemoveButtons = new ArrayList<>();
        this.profileIds = new ArrayList<>();
        List<Profile> profiles;
        try {
            profiles = state.getGame().getProfileDao().findAll();
        } catch (SQLException e) {
            LOG.error("Could not fetch profiles.");
            return true;
        }

        for (val profile : profiles) {
            createProfileButton(state, profile);
        }

        return false;
    }

    private void createProfileButton(@NonNull ProfileSelectState state, @NonNull Profile profile) {
        this.profileButtons.add(new UIButton(
            LARGE_BUTTON_WIDTH - (LARGE_BUTTON_HEIGHT + BUTTON_MARGIN), LARGE_BUTTON_HEIGHT,
            LARGE_BUTTON_SIZE,
            profile.getName(),
            this.uiTexture,
            LARGE_BUTTON_IDLE_R, LARGE_BUTTON_IDLE_G, LARGE_BUTTON_IDLE_B,
            LARGE_BUTTON_HOVER_R, LARGE_BUTTON_HOVER_G, LARGE_BUTTON_HOVER_B,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Select(profile.getId()))));

        this.profileRemoveButtons.add(new UIButton(
            LARGE_BUTTON_HEIGHT, LARGE_BUTTON_HEIGHT,
            LARGE_BUTTON_SIZE,
            "X",
            this.uiTexture,
            0.85f, 0.4f, 0.4f,
            0.95f, 0.7f, 0.7f,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Remove(profile.getId()))));

        this.profileIds.add(profile.getId());
    }

    @Override
    public void destroy(@NonNull ProfileSelectState state) {
        this.uiTexture.destroy();
    }
}
