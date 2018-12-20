package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.BestiaryGameState;
import toilari.otlite.game.event.BestiaryEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.util.Color;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.ui.UIButton;
import toilari.otlite.view.lwjgl.ui.UICharacterEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Piirtäjä bestiaarin piirtämiseen.
 *
 * @param <R> piirtäjä-DAOn tyyppi
 */
public class BestiaryGameStateRenderer<R extends IGetAllDAO<ILWJGLRenderer> & IGetByIDDao<ILWJGLRenderer>> implements ILWJGLGameStateRenderer<BestiaryGameState> {
    private static final int RETURN_BUTTON_WIDTH = 24;
    private static final int RETURN_BUTTON_HEIGHT = 8;
    private static final int RETURN_BUTTON_TEXTURE_SIZE = 2;
    private static final int RETURN_BUTTON_FONT_SIZE = 2;
    private static final String RETURN_BUTTON_LABEL = "Return";

    private static final Color RETURN_BUTTON_IDLE_COLOR = new Color(0.65f, 0.65f, 0.65f);
    private static final Color RETURN_BUTTON_HOVER_COLOR = new Color(1.00f, 1.00f, 1.00f);

    private static final int SELECT_BUTTON_WIDTH = 48;
    private static final int SELECT_BUTTON_HEIGHT = 4;
    private static final int SELECT_BUTTON_TEXTURE_SIZE = 1;
    private static final int SELECT_BUTTON_FONT_SIZE = 2;

    private static final Color SELECT_BUTTON_IDLE_COLOR = new Color(0.65f, 0.65f, 0.65f);
    private static final Color SELECT_BUTTON_HOVER_COLOR = new Color(1.00f, 1.00f, 1.00f);

    private static final int SELECT_BUTTON_MARGIN = 1;
    private static final int SELECT_BUTTON_HORIZONTAL_MARGIN = 2;

    private static final Color ATTRIBUTE_TITLE_COLOR = Color.WHITE;
    private static final Color ATTRIBUTE_ENTRY_COLOR = Color.WHITE.shade(0.1f);

    private static final Color STATS_TITLE_COLOR = Color.WHITE;
    private static final Color STATS_ENTRY_COLOR = Color.WHITE.shade(0.1f);

    @NonNull private final TextureDAO textures;
    @NonNull private final IGetAllDAO<CharacterObject> characters;
    @NonNull private final TextRenderer textRenderer;
    @NonNull private final R renderers;
    @NonNull private final SpriteBatch batch;

    private World previewWorld;
    private Texture uiTexture;
    private UIButton returnButton;

    private StatisticsManager statisticsManager;
    private Profile profile;

    private UICharacterEntry activeEntry;
    private List<UIButton> characterButtons = new ArrayList<>();
    private List<UICharacterEntry> characterEntries = new ArrayList<>();

    /**
     * Luo uuden piirtäjän.
     *
     * @param characters dao jolla hahmot ladataan
     * @param renderers  dao jolla piirtäjät ladataan
     * @param textures   dao jolla tekstuurit ladataan
     */
    public BestiaryGameStateRenderer(@NonNull IGetAllDAO<CharacterObject> characters, @NonNull R renderers, @NonNull TextureDAO textures) {
        this.textures = textures;
        this.characters = characters;
        this.renderers = renderers;
        this.textRenderer = new TextRenderer(this.textures);
        this.batch = new SpriteBatch();
    }

    @Override
    public boolean init(@NonNull BestiaryGameState state) {
        this.batch.init();

        this.statisticsManager = state.getGame().getStatistics();
        this.profile = state.getGame().getActiveProfile();

        this.previewWorld = new World(new TurnObjectManager() {
            @Override
            public boolean isCharactersTurn(CharacterObject character) {
                return false;
            }
        }, state.getGame().getTiles(), state.getGame().getLevels(), state.getGame().getCharacters());

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
        createCharacterButtons(state);
    }

    private void createStaticButtons(@NonNull BestiaryGameState state) {
        this.returnButton = new UIButton(this.statisticsManager, this.profile,
            RETURN_BUTTON_WIDTH, RETURN_BUTTON_HEIGHT,
            RETURN_BUTTON_TEXTURE_SIZE,
            RETURN_BUTTON_LABEL,
            this.uiTexture,
            RETURN_BUTTON_IDLE_COLOR, RETURN_BUTTON_HOVER_COLOR,
            () -> state.getEventSystem().fire(new BestiaryEvent.Return()));
    }

    private void createCharacterButtons(@NonNull BestiaryGameState state) {
        for (val entry : this.characterEntries) {
            createCharacterButton(entry, state);
        }
    }

    private void createCharacterButton(@NonNull UICharacterEntry entry, @NonNull BestiaryGameState state) {
        this.characterButtons.add(new UIButton(this.statisticsManager, this.profile,
            SELECT_BUTTON_WIDTH, SELECT_BUTTON_HEIGHT,
            SELECT_BUTTON_TEXTURE_SIZE,
            entry.getCharacter().getInfo().getName(),
            this.uiTexture,
            SELECT_BUTTON_IDLE_COLOR, SELECT_BUTTON_HOVER_COLOR,
            () -> {
                state.setActiveCharacter(entry.getCharacter());
                this.activeEntry = entry;
            }));

        if (this.activeEntry == null) {
            this.activeEntry = entry;
        }
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull BestiaryGameState state) {
        val screenTopLeftX = camera.getX();
        val screenTopLeftY = camera.getY();

        this.batch.begin();
        this.returnButton.draw(camera, this.batch, this.textRenderer, RETURN_BUTTON_FONT_SIZE,
            screenTopLeftX + camera.getViewportWidth() - 2 - RETURN_BUTTON_WIDTH,
            screenTopLeftY + camera.getViewportHeight() - 2 - RETURN_BUTTON_HEIGHT);

        if (this.activeEntry != null) {
            drawActiveEntry(camera, screenTopLeftX, screenTopLeftY, batch);
        }

        drawSelectButtons(camera, screenTopLeftX, screenTopLeftY);
        this.batch.end(camera);
    }

    private void drawActiveEntry(@NonNull LWJGLCamera camera, float screenTopLeftX, float screenTopLeftY, @NonNull SpriteBatch batch) {
        val portraitSize = 8;
        drawActiveEntryPortrait(camera,
            screenTopLeftX + SELECT_BUTTON_WIDTH / 2.0f + SELECT_BUTTON_HORIZONTAL_MARGIN - portraitSize / 2.0f,
            screenTopLeftY + camera.getViewportHeight() / 4.0f - portraitSize / 2.0f);
        drawActiveEntryStats(camera,
            screenTopLeftX + SELECT_BUTTON_HORIZONTAL_MARGIN * 2 + SELECT_BUTTON_WIDTH,
            screenTopLeftY + 2, batch);
    }

    private void drawSelectButtons(@NonNull LWJGLCamera camera, float screenTopLeftX, float screenTopLeftY) {
        for (int i = 0; i < this.characterButtons.size(); i++) {
            this.characterButtons.get(i).draw(camera, this.batch, this.textRenderer, SELECT_BUTTON_FONT_SIZE,
                screenTopLeftX + 2,
                screenTopLeftY + camera.getViewportHeight() / 2.0f + 2 + i * (SELECT_BUTTON_HEIGHT + SELECT_BUTTON_MARGIN));
        }
    }

    private void drawActiveEntryStats(@NonNull LWJGLCamera camera, float x, float y, @NonNull SpriteBatch batch) {
        val info = this.activeEntry.getCharacter().getInfo();
        val attr = this.activeEntry.getCharacter().getAttributes();

        this.textRenderer.draw(camera, batch, x, y, ATTRIBUTE_TITLE_COLOR, 4, info.getName());

        val rowHeight = 3.5f;
        int i = 0;
        drawStatEntry(camera, x + 1, y + 6 + (i++ * rowHeight), 3, "Health", String.format("%.1f", attr.getMaxHealth()), ATTRIBUTE_ENTRY_COLOR);
        drawStatEntry(camera, x + 1, y + 6 + (i++ * rowHeight), 3, "Attack Damage", String.format("%.1f", attr.getAttackDamage()), ATTRIBUTE_ENTRY_COLOR);
        drawStatEntry(camera, x + 1, y + 6 + (i++ * rowHeight), 3, "Armor", String.format("%.1f", attr.getArmor()), ATTRIBUTE_ENTRY_COLOR);
        drawStatEntry(camera, x + 1, y + 6 + (i++ * rowHeight), 3, "AP", String.format("%d", attr.getActionPoints()), ATTRIBUTE_ENTRY_COLOR);

        if (this.activeEntry.getCharacter().getInfo().getName().equals("Hero (you!)")) {
            this.textRenderer.draw(camera, batch, x, y + 10 + (i * rowHeight), STATS_TITLE_COLOR, 4, "Statistics");

            for (val statistic : Statistics.values()) {
                val value = this.statisticsManager.getDouble(statistic, this.profile.getId());
                drawStatEntry(camera, x + 1, y + 16 + (i++ * rowHeight), 3, statistic.getName(), String.format("%.1f", value), STATS_ENTRY_COLOR);
            }
        }
    }

    private void drawStatEntry(@NonNull LWJGLCamera camera, float x, float y, int fontsize, String title, String value, Color color) {
        val maxNumberOfChars = (int) Math.ceil((camera.getViewportWidth() - x - 3) / fontsize);
        val titleNumberOfChars = title.length() + 2;
        val numberOfSpaces = maxNumberOfChars - (titleNumberOfChars);

        val format = "%s: %" + numberOfSpaces + "s";
        this.textRenderer.draw(camera, batch, x, y, color, fontsize, String.format(format, title, value));
    }

    private void drawActiveEntryPortrait(@NonNull LWJGLCamera camera, float x, float y) {
        this.activeEntry.drawPortrait(camera, this.batch, x, y);
    }

    @Override
    public void destroy(@NonNull BestiaryGameState state) {
        this.statisticsManager = null;
        this.profile = null;
        this.characterEntries.clear();
        this.characterButtons.clear();
        this.activeEntry = null;
        this.uiTexture.destroy();
        this.textRenderer.destroy();
    }
}
