package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.joml.Matrix4f;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.MainMenuGameState;
import toilari.otlite.game.event.EventSystem;
import toilari.otlite.game.event.IEvent;
import toilari.otlite.game.event.MainMenuEvent;
import toilari.otlite.game.event.MenuEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.ui.UIButton;

import java.util.ArrayList;
import java.util.List;

public class MainMenuGameStateRenderer implements ILWJGLGameStateRenderer<MainMenuGameState> {
    private static final int BUTTON_WIDTH = 64;
    private static final int BUTTON_HEIGHT = 8;
    private static final int BUTTON_TEXTURE_SIZE = 2;
    private static final int BUTTON_FONT_SIZE = 2;
    private static final int BUTTON_MARGIN = 1;

    private static final Color BUTTON_COLOR_IDLE = Color.WHITE.shade(0.35f);
    private static final Color BUTTON_COLOR_HOVER = Color.WHITE;
    private static final Color TITLE_COLOR = Color.WHITE.shade(0.15f);

    private static final int TITLE_FONT_SIZE = 16;
    private static final String TITLE_STRING = "OT-LITE";


    @NonNull private final TextureDAO textures;
    @NonNull private final TextRenderer textRenderer;
    @NonNull private final SpriteBatch batch;

    private Texture uiTexture;

    private List<UIButton> buttons;
    private StatisticsManager statisticsManager;
    private Profile profile;

    /**
     * Luo uuden päävalikon piirtäjän.
     *
     * @param textures tekstuuri-dao jolla tekstuurit ladataan
     */
    public MainMenuGameStateRenderer(@NonNull TextureDAO textures) {
        this.textures = textures;
        this.textRenderer = new TextRenderer(this.textures);
        this.batch = new SpriteBatch();
    }

    @Override
    public boolean init(@NonNull MainMenuGameState state) {
        this.batch.init();
        this.statisticsManager = state.getGame().getStatistics();
        this.profile = state.getGame().getActiveProfile();
        this.textRenderer.init();
        this.uiTexture = this.textures.get("ui.png");

        this.buttons = new ArrayList<>();
        createButtons(state);

        return false;
    }

    private void createButtons(MainMenuGameState state) {
        addDisabledButton(state.getEventSystem(), "Continue", new MainMenuEvent.Continue());
        addButton(state.getEventSystem(), "New Game", new MainMenuEvent.NewGame());
        addButton(state.getEventSystem(), "Bestiary", new MainMenuEvent.Bestiary());
        addDisabledButton(state.getEventSystem(), "Settings", new MainMenuEvent.Settings());
        addButton(state.getEventSystem(), "Quit Game", new MenuEvent.Quit());
    }

    private void addDisabledButton(EventSystem es, String label, IEvent onClick) {
        val button = new UIButton(this.statisticsManager, this.profile,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            BUTTON_TEXTURE_SIZE,
            label,
            this.uiTexture,
            BUTTON_COLOR_IDLE.shade(0.5f),
            BUTTON_COLOR_IDLE.shade(0.5f),
            () -> { }
        );

        this.buttons.add(button);
    }

    private void addButton(EventSystem es, String label, IEvent onClick) {
        val button = new UIButton(this.statisticsManager, this.profile,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            BUTTON_TEXTURE_SIZE,
            label,
            this.uiTexture,
            BUTTON_COLOR_IDLE,
            BUTTON_COLOR_HOVER,
            () -> es.fire(onClick)
        );

        this.buttons.add(button);
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull MainMenuGameState state) {
        camera.setPosition(0.0f, 0.0f);

        val x = (camera.getViewportWidth() / 2f) - (TITLE_STRING.length() / 2.0f) * TITLE_FONT_SIZE;
        val y = 4f;
        this.batch.begin();
        this.textRenderer.draw(camera, this.batch, x, y, TITLE_COLOR, TITLE_FONT_SIZE, TITLE_STRING);

        drawButtons(camera);
        this.batch.end(camera);
    }

    private void drawButtons(@NonNull LWJGLCamera camera) {
        val x = (camera.getViewportWidth() - BUTTON_WIDTH) / 2.0f;
        var y = (camera.getViewportHeight() - BUTTON_HEIGHT * this.buttons.size()) / 2.0f;

        for (int i = 0; i < this.buttons.size(); i++) {
            val button = this.buttons.get(i);
            button.draw(camera, this.batch, this.textRenderer, BUTTON_FONT_SIZE, x, y);
            y += BUTTON_HEIGHT + BUTTON_MARGIN;
        }
    }

    @Override
    public void destroy(@NonNull MainMenuGameState state) {
        this.uiTexture.destroy();
    }
}
