package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.CharacterDAO;
import toilari.otlite.dao.RendererDAO;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.BestiaryGameState;
import toilari.otlite.game.event.BestiaryEvent;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.ui.UIButton;
import toilari.otlite.view.lwjgl.ui.UICharacterEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Piirtäjä "bestiary"-valikon piirtämiseen.
 */
public class BestiaryGameStateRenderer implements ILWJGLGameStateRenderer<BestiaryGameState> {
    private static final int RETURN_BUTTON_WIDTH = 24;
    private static final int RETURN_BUTTON_HEIGHT = 8;
    private static final int RETURN_BUTTON_TEXTURE_SIZE = 2;
    private static final int RETURN_BUTTON_FONT_SIZE = 2;
    private static final String RETURN_BUTTON_LABEL = "Return";

    private static final float RETURN_BUTTON_IDLE_R = 0.65f;
    private static final float RETURN_BUTTON_IDLE_G = 0.65f;
    private static final float RETURN_BUTTON_IDLE_B = 0.65f;

    private static final float RETURN_BUTTON_HOVER_R = 1.0f;
    private static final float RETURN_BUTTON_HOVER_G = 1.0f;
    private static final float RETURN_BUTTON_HOVER_B = 1.0f;

    private static final int SELECT_BUTTON_WIDTH = 48;
    private static final int SELECT_BUTTON_HEIGHT = 4;
    private static final int SELECT_BUTTON_TEXTURE_SIZE = 1;
    private static final int SELECT_BUTTON_FONT_SIZE = 2;

    private static final float SELECT_BUTTON_IDLE_R = 0.65f;
    private static final float SELECT_BUTTON_IDLE_G = 0.65f;
    private static final float SELECT_BUTTON_IDLE_B = 0.65f;

    private static final float SELECT_BUTTON_HOVER_R = 1.0f;
    private static final float SELECT_BUTTON_HOVER_G = 1.0f;
    private static final float SELECT_BUTTON_HOVER_B = 1.0f;

    private static final int SELECT_BUTTON_MARGIN = 1;

    @NonNull private final TextureDAO textures;
    @NonNull private final CharacterDAO characters;
    @NonNull private final TextRenderer textRenderer;
    @NonNull private final RendererDAO renderers;
    @NonNull private final World previewWorld;

    private Texture uiTexture;
    private UIButton returnButton;

    private UICharacterEntry activeEntry;
    private List<UIButton> characterButtons = new ArrayList<>();
    private List<UICharacterEntry> characterEntries = new ArrayList<>();

    public BestiaryGameStateRenderer(@NonNull TextureDAO textures) {
        this.textures = textures;
        this.characters = new CharacterDAO("content/characters/");
        this.renderers = new RendererDAO("content/renderers/", textures);
        this.renderers.discoverAndLoadAll();
        this.textRenderer = new TextRenderer(this.textures, 1, 16);
        this.previewWorld = new World(new TurnObjectManager() {
            @Override
            public boolean isCharactersTurn(CharacterObject character) {
                return false;
            }
        });
    }

    @Override
    public boolean init(@NonNull BestiaryGameState state) {
        if (initResources()) {
            return true;
        }

        loadCharacters();
        createButtons(state);

        return false;
    }

    private boolean initResources() {
        this.textRenderer.init();
        this.uiTexture = this.textures.get("ui.png");

        for (val renderer : this.renderers.getAll()) {
            if (renderer.init()) {
                return true;
            }
        }

        return false;
    }

    private void loadCharacters() {
        this.previewWorld.init();

        for (val character : this.characters.getAll()) {
            this.characterEntries.add(new UICharacterEntry(this.renderers, this.previewWorld, character));
        }
    }

    private void createButtons(@NonNull BestiaryGameState state) {
        createStaticButtons(state);
        createCharacterButtons();
    }

    private void createStaticButtons(@NonNull BestiaryGameState state) {
        this.returnButton = new UIButton(
            RETURN_BUTTON_WIDTH, RETURN_BUTTON_HEIGHT,
            RETURN_BUTTON_TEXTURE_SIZE,
            RETURN_BUTTON_LABEL,
            this.uiTexture,
            RETURN_BUTTON_IDLE_R, RETURN_BUTTON_IDLE_G, RETURN_BUTTON_IDLE_B,
            RETURN_BUTTON_HOVER_R, RETURN_BUTTON_HOVER_G, RETURN_BUTTON_HOVER_B,
            () -> state.getEventSystem().fire(new BestiaryEvent.Return()));
    }

    private void createCharacterButtons() {
        for (val entry : this.characterEntries) {
            createCharacterButton(entry);
        }
    }

    private void createCharacterButton(@NonNull UICharacterEntry entry) {
        this.characterButtons.add(new UIButton(
            SELECT_BUTTON_WIDTH, SELECT_BUTTON_HEIGHT,
            SELECT_BUTTON_TEXTURE_SIZE,
            entry.getName(),
            this.uiTexture,
            SELECT_BUTTON_IDLE_R, SELECT_BUTTON_IDLE_G, SELECT_BUTTON_IDLE_B,
            SELECT_BUTTON_HOVER_R, SELECT_BUTTON_HOVER_G, SELECT_BUTTON_HOVER_B,
            () -> this.activeEntry = entry));
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull BestiaryGameState state) {
        val screenTopLeftX = camera.getX();
        val screenTopLeftY = camera.getY();

        this.returnButton.draw(camera, this.textRenderer, RETURN_BUTTON_FONT_SIZE,
            screenTopLeftX + camera.getViewportWidth() - 2 - RETURN_BUTTON_WIDTH,
            screenTopLeftY + camera.getViewportHeight() - 2 - RETURN_BUTTON_HEIGHT);

        if (this.activeEntry != null) {
            val x = screenTopLeftX + camera.getViewportWidth() / 2.0f - 4;
            val y = screenTopLeftY + camera.getViewportHeight() / 2.0f - 4;
            this.activeEntry.drawPortrait(camera, x, y);
        }

        for (int i = 0; i < this.characterButtons.size(); i++) {
            this.characterButtons.get(i).draw(camera, this.textRenderer, SELECT_BUTTON_FONT_SIZE, screenTopLeftX + 2, screenTopLeftY + camera.getViewportHeight() / 2.0f + 2 + i * (SELECT_BUTTON_HEIGHT + SELECT_BUTTON_MARGIN));
        }
    }

    @Override
    public void destroy(@NonNull BestiaryGameState state) {
        this.uiTexture.destroy();
        this.textRenderer.destroy();
    }
}
