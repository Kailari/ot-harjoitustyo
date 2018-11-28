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

        this.createProfileButton = new UIButton(ProfileSelectStateRenderer.SMALL_BUTTON_WIDTH, ProfileSelectStateRenderer.SMALL_BUTTON_HEIGHT, ProfileSelectStateRenderer.SMALL_BUTTON_SIZE, "Add", this.uiTexture,
            ProfileSelectStateRenderer.SMALL_BUTTON_IDLE_R, ProfileSelectStateRenderer.SMALL_BUTTON_IDLE_G, ProfileSelectStateRenderer.SMALL_BUTTON_IDLE_B, ProfileSelectStateRenderer.SMALL_BUTTON_HOVER_R, ProfileSelectStateRenderer.SMALL_BUTTON_HOVER_G, ProfileSelectStateRenderer.SMALL_BUTTON_HOVER_B,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Add("Player #" + this.profileButtons.size())));

        this.removeProfileButton = new UIButton(ProfileSelectStateRenderer.SMALL_BUTTON_WIDTH, ProfileSelectStateRenderer.SMALL_BUTTON_HEIGHT, ProfileSelectStateRenderer.SMALL_BUTTON_SIZE, "Remove", this.uiTexture,
            ProfileSelectStateRenderer.SMALL_BUTTON_IDLE_R, ProfileSelectStateRenderer.SMALL_BUTTON_IDLE_G, ProfileSelectStateRenderer.SMALL_BUTTON_IDLE_B, ProfileSelectStateRenderer.SMALL_BUTTON_HOVER_R, ProfileSelectStateRenderer.SMALL_BUTTON_HOVER_G, ProfileSelectStateRenderer.SMALL_BUTTON_HOVER_B,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Remove(this.profileIds.get(this.profileIds.size() - 1))));

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
            this.profileIds.remove(index);
        }
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull ProfileSelectState state) {
        drawButtons(camera);
    }

    private void drawButtons(@NonNull LWJGLCamera camera) {
        val centerX = camera.getViewportWidth() / camera.getPixelsPerUnit() / 2;
        val x = Math.round(centerX - ProfileSelectStateRenderer.LARGE_BUTTON_WIDTH / 2.0f);
        int i = 0;
        for (val button : this.profileButtons) {
            val y = ProfileSelectStateRenderer.BUTTON_START_Y + ProfileSelectStateRenderer.SMALL_BUTTON_HEIGHT + ProfileSelectStateRenderer.BUTTON_MARGIN + (i++ * (ProfileSelectStateRenderer.LARGE_BUTTON_HEIGHT + ProfileSelectStateRenderer.BUTTON_MARGIN));
            button.draw(camera, this.textRenderer, ProfileSelectStateRenderer.LARGE_BUTTON_FONT_SIZE, x, y);
        }

        this.createProfileButton.draw(camera, this.textRenderer, ProfileSelectStateRenderer.SMALL_BUTTON_FONT_SIZE, x, ProfileSelectStateRenderer.BUTTON_START_Y);
        this.removeProfileButton.draw(camera, this.textRenderer, ProfileSelectStateRenderer.SMALL_BUTTON_FONT_SIZE, x + ProfileSelectStateRenderer.SMALL_BUTTON_WIDTH + ProfileSelectStateRenderer.BUTTON_MARGIN, ProfileSelectStateRenderer.BUTTON_START_Y);
    }

    private boolean refreshProfileList(@NonNull ProfileSelectState state) {
        this.profileButtons = new ArrayList<>();
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
        this.profileButtons.add(new UIButton(ProfileSelectStateRenderer.LARGE_BUTTON_WIDTH, ProfileSelectStateRenderer.LARGE_BUTTON_HEIGHT, ProfileSelectStateRenderer.LARGE_BUTTON_SIZE, profile.getName(), this.uiTexture,
            ProfileSelectStateRenderer.LARGE_BUTTON_IDLE_R, ProfileSelectStateRenderer.LARGE_BUTTON_IDLE_G, ProfileSelectStateRenderer.LARGE_BUTTON_IDLE_B, ProfileSelectStateRenderer.LARGE_BUTTON_HOVER_R, ProfileSelectStateRenderer.LARGE_BUTTON_HOVER_G, ProfileSelectStateRenderer.LARGE_BUTTON_HOVER_B,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Select(profile.getId()))));
        this.profileIds.add(profile.getId());
    }

    @Override
    public void destroy(@NonNull ProfileSelectState state) {
        this.uiTexture.destroy();
    }
}
