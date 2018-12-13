package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.ProfileSelectGameState;
import toilari.otlite.game.event.ProfileMenuEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.ui.UIButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProfileSelectGameStateRenderer implements ILWJGLGameStateRenderer<ProfileSelectGameState> {
    private static final int ADD_BUTTON_WIDTH = 31;
    private static final int ADD_BUTTON_HEIGHT = 8;
    private static final int ADD_BUTTON_SIZE = 2;
    private static final int ADD_BUTTON_FONT_SIZE = 2;
    private static final Color ADD_BUTTON_COLOR_IDLE = Color.WHITE.shade(0.35f);
    private static final Color ADD_BUTTON_COLOR_HOVER = Color.WHITE;


    private static final int LARGE_BUTTON_WIDTH = 64;
    private static final int LARGE_BUTTON_HEIGHT = 8;
    private static final int LARGE_BUTTON_SIZE = 2;
    private static final int LARGE_BUTTON_FONT_SIZE = 2;
    private static final Color LARGE_BUTTON_COLOR_IDLE = Color.WHITE.shade(0.35f);
    private static final Color LARGE_BUTTON_COLOR_HOVER = Color.WHITE;

    private static final int BUTTON_MARGIN = 2;
    private static final int BUTTON_START_Y = 16;

    private static final int TITLE_FONTSIZE = 5;
    private static final int TITLE_Y = 4;
    private static final String TITLE_STRING = "Select/Create a profile";

    private static final Color PROFILE_REMOVE_BUTTON_COLOR_IDLE = new Color(0.85f, 0.4f, 0.4f);
    private static final Color PROFILE_REMOVE_BUTTON_COLOR_HOVER = new Color(0.95f, 0.7f, 0.7f);

    private static final Color TITLE_COLOR = Color.WHITE.shade(0.15f);

    @NonNull private final TextureDAO textureDAO;
    @NonNull private final TextRenderer textRenderer;
    @NonNull private final SpriteBatch batch;

    private Texture uiTexture;
    private UIButton createProfileButton;
    private List<UIButton> profileButtons;
    private List<UIButton> profileRemoveButtons;
    private List<Integer> profileIds;

    private ProfileSelectGameState state;

    /**
     * Luo uuden profiilivalikon piirtäjän.
     *
     * @param textureDAO dao jolla tarvittavat tekstuurit voidaan ladata
     */
    public ProfileSelectGameStateRenderer(@NonNull TextureDAO textureDAO) {
        this.textureDAO = textureDAO;
        this.textRenderer = new TextRenderer(this.textureDAO);
        this.batch = new SpriteBatch();
    }

    @Override
    public boolean init(@NonNull ProfileSelectGameState state) {
        this.batch.init();
        this.state = state;
        this.textRenderer.init();
        this.uiTexture = this.textureDAO.get("ui.png");

        if (refreshProfileList(state)) {
            return true;
        }

        this.createProfileButton = new UIButton(state.getGame().getStatistics(), state.getGame().getActiveProfile(),
            ADD_BUTTON_WIDTH, ADD_BUTTON_HEIGHT,
            ADD_BUTTON_SIZE,
            "Add", this.uiTexture,
            ADD_BUTTON_COLOR_IDLE,
            ADD_BUTTON_COLOR_HOVER,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Add("Player #" + this.profileButtons.size())));

        state.getEventSystem().subscribeTo(ProfileMenuEvent.Added.class, this::onAdded);
        state.getEventSystem().subscribeTo(ProfileMenuEvent.Removed.class, this::onRemoved);

        return false;
    }

    private void onAdded(@NonNull ProfileMenuEvent.Added event) {
        createProfileButton(this.state, event.getProfile());
    }

    private void onRemoved(@NonNull ProfileMenuEvent.Removed event) {
        int index = this.profileIds.indexOf(event.getProfile().getId());
        if (index >= 0) {
            this.profileButtons.remove(index);
            this.profileRemoveButtons.remove(index);
            this.profileIds.remove(index);
        }
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull ProfileSelectGameState state) {
        this.batch.begin();
        drawTitle(camera);
        drawButtons(camera);
        this.batch.end(camera);
    }

    private void drawTitle(@NonNull LWJGLCamera camera) {
        val centerX = camera.getViewportWidth() / 2;
        val x = centerX - TITLE_STRING.length() * (TITLE_FONTSIZE / 2.0f);

        this.textRenderer.draw(camera, this.batch, x, TITLE_Y, TITLE_COLOR, TITLE_FONTSIZE, TITLE_STRING);
    }

    private void drawButtons(@NonNull LWJGLCamera camera) {
        val centerX = camera.getViewportWidth() / 2;
        val x = Math.round(centerX - LARGE_BUTTON_WIDTH / 2.0f);

        for (int i = 0; i < this.profileButtons.size(); i++) {
            val button = this.profileButtons.get(i);
            val y = BUTTON_START_Y + ADD_BUTTON_HEIGHT + BUTTON_MARGIN + (i * (LARGE_BUTTON_HEIGHT + BUTTON_MARGIN));
            button.draw(camera, this.batch, this.textRenderer, LARGE_BUTTON_FONT_SIZE, x, y);

            val removeButton = this.profileRemoveButtons.get(i);
            val removeButtonX = x + LARGE_BUTTON_WIDTH - LARGE_BUTTON_HEIGHT;
            removeButton.draw(camera, this.batch, this.textRenderer, LARGE_BUTTON_FONT_SIZE, removeButtonX, y);
        }

        this.createProfileButton.draw(camera, this.batch, this.textRenderer, ADD_BUTTON_FONT_SIZE, x, BUTTON_START_Y);
    }

    private boolean refreshProfileList(@NonNull ProfileSelectGameState state) {
        this.profileButtons = new ArrayList<>();
        this.profileRemoveButtons = new ArrayList<>();
        this.profileIds = new ArrayList<>();
        List<Profile> profiles;
        try {
            profiles = state.getGame().getProfiles().findAll();
        } catch (SQLException e) {
            LOG.error("Could not fetch profiles.");
            return true;
        }

        for (val profile : profiles) {
            createProfileButton(state, profile);
        }

        return false;
    }

    private void createProfileButton(@NonNull ProfileSelectGameState state, @NonNull Profile profile) {
        this.profileButtons.add(new UIButton(state.getGame().getStatistics(), state.getGame().getActiveProfile(),
            LARGE_BUTTON_WIDTH - (LARGE_BUTTON_HEIGHT + BUTTON_MARGIN), LARGE_BUTTON_HEIGHT,
            LARGE_BUTTON_SIZE,
            profile.getName(),
            this.uiTexture,
            LARGE_BUTTON_COLOR_IDLE, LARGE_BUTTON_COLOR_HOVER,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Select(profile))));

        this.profileRemoveButtons.add(new UIButton(state.getGame().getStatistics(), state.getGame().getActiveProfile(),
            LARGE_BUTTON_HEIGHT, LARGE_BUTTON_HEIGHT,
            LARGE_BUTTON_SIZE,
            "X",
            this.uiTexture,
            PROFILE_REMOVE_BUTTON_COLOR_IDLE,
            PROFILE_REMOVE_BUTTON_COLOR_HOVER,
            () -> state.getEventSystem().fire(new ProfileMenuEvent.Remove(profile))));

        this.profileIds.add(profile.getId());
    }

    @Override
    public void destroy(@NonNull ProfileSelectGameState state) {
        this.uiTexture.destroy();
    }
}
