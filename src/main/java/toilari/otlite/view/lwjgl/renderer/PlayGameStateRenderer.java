package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.IGetDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.PlayEvent;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Color;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.ui.UIAbilityBar;

/**
 * Piirtäjä pelitilan piirtämiseen. Vastaa maailman
 */
public class PlayGameStateRenderer<R extends IGetAllDAO<ILWJGLRenderer> & IGetByIDDao<ILWJGLRenderer>> implements ILWJGLGameStateRenderer<PlayGameState> {
    private static final Color DEATH_MESSAGE_COLOR = new Color(0.65f, 0.25f, 0.25f);
    private static final Color RETURN_TO_MENU_MESSAGE_COLOR = Color.WHITE.shade(0.15f);
    private static final Color GAME_INFO_COLOR = new Color(0.25f, 0.65f, 0.25f);
    private static final Color ACTION_LABEL_COLOR = new Color(0.65f, 0.25f, 0.25f);
    @NonNull private final R renderers;
    @NonNull private final IGetDAO<Texture, String> textureDao;
    @NonNull private final TextRenderer textRenderer;
    @NonNull private final PopupTextRenderer popupTextRenderer;

    private PlayGameState state;

    private LevelRenderer levelRenderer;
    private UIAbilityBar abilityBar;
    private LevelUpMenuRenderer levelUpMenuRenderer;
    private SpriteBatch batch;

    /**
     * Luo uuden pelitilapiirtäjän.
     *
     * @param renderers  dao jolla piirtäjät ladataan
     * @param textureDao dao jolla tekstuurit ladataan
     */
    public PlayGameStateRenderer(@NonNull R renderers, @NonNull IGetDAO<Texture, String> textureDao) {
        this.textureDao = textureDao;
        this.textRenderer = new TextRenderer(this.textureDao);
        this.levelRenderer = new LevelRenderer(this.textureDao, "tileset.png", 8, 8);

        this.renderers = renderers;
        this.batch = new SpriteBatch();

        this.popupTextRenderer = new PopupTextRenderer();
    }

    @Override
    public boolean init(@NonNull PlayGameState state) {
        this.batch.init();
        this.state = state;

        for (val renderer : this.renderers.getAll()) {
            if (renderer.init()) {
                return true;
            }
        }

        this.textRenderer.init();
        this.levelUpMenuRenderer = new LevelUpMenuRenderer(this.textureDao, state);
        this.abilityBar = new UIAbilityBar(this.textureDao, this.textRenderer);
        this.popupTextRenderer.init(state);

        return this.levelRenderer.init();
    }

    @Override
    public void destroy(@NonNull PlayGameState state) {
        this.levelRenderer.destroy();
        this.abilityBar.destroy();
        this.state = null;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        makeCameraFollowPlayer(camera, state);

        this.batch.begin();
        drawWorld(camera, state);
        this.batch.end(camera);

        this.batch.begin();
        postDrawWorld(camera, state);
        this.batch.end(camera);

        this.batch.begin();
        this.popupTextRenderer.draw(camera, this.textRenderer, batch);
        this.batch.end(camera);

        this.batch.begin();
        if (!state.isMenuOpen()) {
            drawUI(camera, state);
        } else {
            this.levelUpMenuRenderer.draw(camera, state, this.textRenderer, batch);
        }
        this.batch.end(camera);
    }

    private void makeCameraFollowPlayer(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val player = state.getManager().getPlayer();
        val cameraX = player.getX() - camera.getViewportWidth() / 2;
        val cameraY = player.getY() - camera.getViewportHeight() / 2;
        camera.setPosition(cameraX, cameraY);
    }

    private void drawWorld(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.draw(camera, world.getCurrentLevel(), batch);

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.renderers.getByID(object.getRendererID());
            if (renderer != null) {
                renderer.draw(camera, object, batch);
            }
        }
    }

    private void postDrawWorld(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.postDraw(camera, world.getCurrentLevel(), batch);

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.renderers.getByID(object.getRendererID());
            if (renderer != null) {
                renderer.postDraw(camera, object, batch);
            }
        }
    }

    private void drawUI(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val screenTopLeftX = camera.getX();
        val screenTopLeftY = camera.getY();

        drawTurnStatus(camera, state, screenTopLeftX, screenTopLeftY);
        val remainingAP = state.getManager().isCharactersTurn(state.getManager().getPlayer()) ? state.getManager().getRemainingActionPoints() : 0;
        this.abilityBar.draw(camera, batch, state.getManager().getPlayer().getAbilities(), screenTopLeftX, screenTopLeftY + camera.getViewportHeight() - 18, remainingAP);

        if (state.getManager().getPlayer().isDead()) {
            drawDeathMessage(camera, state, screenTopLeftX, screenTopLeftY);
        }

    }

    private void drawTurnStatus(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        drawCurrentTurn(camera, state, x + 2, y + 2);
        drawXPStatus(camera, state, x + 2, y + 10);
        drawActionLabel(camera, state, x + 2, y + 17);
    }

    private void drawCurrentTurn(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        val str = String.format("Floor: %d Turn: %d", state.getWorld().getFloor(), state.getManager().getPlayer().getTurnsTaken());
        this.textRenderer.draw(camera, batch, x, y, GAME_INFO_COLOR, 3, str);
    }

    private void drawXPStatus(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        val levels = state.getManager().getPlayer().getLevels();

        val currentLevel = levels.getXpLevel();

        val requiredForCurrent = levels.experienceRequiredForLevel(currentLevel);
        val requiredForNext = levels.experienceRequiredForLevel(currentLevel + 1);
        val actualRequiredExperience = requiredForNext - requiredForCurrent;

        val currentExperience = levels.getExperience();
        val progressTowardsNextLevel = currentExperience - requiredForCurrent;

        val str = String.format("Level: %d\nXP: %d/%d", currentLevel, progressTowardsNextLevel, actualRequiredExperience);
        this.textRenderer.draw(camera, batch, x, y, GAME_INFO_COLOR, 3, str);
    }

    private void drawActionLabel(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        String apStr = resolveActionLabel(state);
        this.textRenderer.draw(camera, batch, x, y, ACTION_LABEL_COLOR, 2, apStr);
    }

    private String resolveActionLabel(@NonNull PlayGameState state) {
        val world = state.getWorld();
        val player = state.getManager().getPlayer();

        String apStr = "Waiting...";
        if (world.getObjectManager().isCharactersTurn(player)) {
            val remaining = world.getObjectManager().getRemainingActionPoints();
            val total = player.getAttributes().getActionPoints();
            if (remaining == 0) {
                apStr = "Press <SPACE> to end turn";
            } else {
                val targetSelector = player.getAbilities().getComponent(TargetSelectorAbility.class);
                if (targetSelector != null && targetSelector.getTarget() != null) {
                    apStr = "Press <SPACE> to attack\nPress <ESC> to cancel";
                } else {
                    apStr = "AP: " + remaining + "/" + total;
                }
            }
        }
        return apStr;
    }

    private void drawDeathMessage(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        val ded = "You are dead";
        val len = ded.length();
        val size = 8;

        val w = camera.getViewportWidth();
        val h = camera.getViewportHeight();

        val dt = Math.min(1, (System.currentTimeMillis() - state.getManager().getPlayer().getDeathTime()) / 5000.0f);
        this.textRenderer.draw(camera,
            batch, x + (w / 2f) - (len * size) / 2f,
            y + (h / 2f) - (size / 2f) + ((h / 2f + size) - dt * (h / 2f + size)),
            DEATH_MESSAGE_COLOR,
            size, ded);

        if (1.0f - dt < 0.001f) {
            handleReturnToMenu(camera, x, y, size, w, h);
        }
    }

    private void handleReturnToMenu(@NonNull LWJGLCamera camera, float x, float y, int size, float w, float h) {
        val continueStr = "Press <SPACE> to return to menu";
        val continueLen = continueStr.length();
        val continueSize = 3;
        this.textRenderer.draw(camera,
            batch, x + (w / 2.0f) - (continueLen * continueSize) / 2.0f,
            y + (h / 2.0f) + (size / 2.0f) + 0.5f,
            RETURN_TO_MENU_MESSAGE_COLOR,
            continueSize, continueStr
        );

        if (Input.getHandler().isKeyPressed(Key.SPACE)) {
            this.state.getEventSystem().fire(new PlayEvent.ReturnToMenuAfterLoss());
        }
    }
}
