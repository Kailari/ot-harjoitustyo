package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.MainMenuGameState;
import toilari.otlite.game.event.EventSystem;
import toilari.otlite.game.event.IEvent;
import toilari.otlite.game.event.MainMenuEvent;
import toilari.otlite.game.event.MenuEvent;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.ui.UIButton;

import java.util.ArrayList;
import java.util.List;

public class MainMenuGameStateRenderer implements ILWJGLGameStateRenderer<MainMenuGameState> {
    private static final int BUTTON_WIDTH = 64;
    private static final int BUTTON_HEIGHT = 8;
    private static final int BUTTON_TEXTURE_SIZE = 2;
    private static final int BUTTON_FONT_SIZE = 2;
    private static final int BUTTON_MARGIN = 1;

    private static final float BUTTON_IDLE_R = 0.65f;
    private static final float BUTTON_IDLE_G = 0.65f;
    private static final float BUTTON_IDLE_B = 0.65f;

    private static final float BUTTON_HOVER_R = 1.0f;
    private static final float BUTTON_HOVER_G = 1.0f;
    private static final float BUTTON_HOVER_B = 1.0f;

    private static final int TITLE_FONT_SIZE = 16;
    private static final String TITLE_STRING = "OT-LITE";


    @NonNull private final TextureDAO textures;
    @NonNull private final TextRenderer textRenderer;

    private Texture uiTexture;

    private List<UIButton> buttons;

    /**
     * Luo uuden päävalikon piirtäjän.
     *
     * @param textures tekstuuri-dao jolla tekstuurit ladataan
     */
    public MainMenuGameStateRenderer(@NonNull TextureDAO textures) {
        this.textures = textures;
        this.textRenderer = new TextRenderer(this.textures, 1, 16);
    }

    @Override
    public boolean init(@NonNull MainMenuGameState state) {
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
        val button = new UIButton(
            BUTTON_WIDTH, BUTTON_HEIGHT,
            BUTTON_TEXTURE_SIZE,
            label,
            this.uiTexture,
            BUTTON_IDLE_R * 0.5f, BUTTON_IDLE_G * 0.5f, BUTTON_IDLE_B * 0.5f,
            BUTTON_IDLE_R * 0.5f, BUTTON_IDLE_G * 0.5f, BUTTON_IDLE_B * 0.5f,
            () -> { }
        );

        this.buttons.add(button);
    }

    private void addButton(EventSystem es, String label, IEvent onClick) {
        val button = new UIButton(
            BUTTON_WIDTH, BUTTON_HEIGHT,
            BUTTON_TEXTURE_SIZE,
            label,
            this.uiTexture,
            BUTTON_IDLE_R, BUTTON_IDLE_G, BUTTON_IDLE_B,
            BUTTON_HOVER_R, BUTTON_HOVER_G, BUTTON_HOVER_B,
            () -> es.fire(onClick)
        );

        this.buttons.add(button);
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull MainMenuGameState state) {
        val x = (camera.getViewportWidth() / 2f) - (TITLE_STRING.length() / 2.0f) * TITLE_FONT_SIZE;
        val y = 4f;

        this.textRenderer.draw(camera, x, y, 1.0f, 1.0f, 1.0f, TITLE_FONT_SIZE, TITLE_STRING);

        drawButtons(camera);
    }

    private void drawButtons(@NonNull LWJGLCamera camera) {
        val x = (camera.getViewportWidth() - BUTTON_WIDTH) / 2.0f;
        var y = (camera.getViewportHeight() - BUTTON_HEIGHT * this.buttons.size()) / 2.0f;

        for (int i = 0; i < this.buttons.size(); i++) {
            val button = this.buttons.get(i);
            button.draw(camera, this.textRenderer, BUTTON_FONT_SIZE, x, y);
            y += BUTTON_HEIGHT + BUTTON_MARGIN;
        }
    }

    @Override
    public void destroy(@NonNull MainMenuGameState state) {
        this.uiTexture.destroy();
    }
}
