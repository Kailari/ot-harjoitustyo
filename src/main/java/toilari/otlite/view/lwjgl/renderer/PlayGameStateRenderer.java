package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.RendererDAO;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.PlayEvent;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.ui.UIAbilityBar;

/**
 * Piirtäjä pelitilan piirtämiseen. Vastaa maailman
 */
public class PlayGameStateRenderer implements ILWJGLGameStateRenderer<PlayGameState> {
    @NonNull private final TextureDAO textureDao;
    @NonNull private final RendererDAO renderers;
    @NonNull private final TextRenderer textRenderer;
    @NonNull private final DamagePopupRenderer damagePopupRenderer;

    private PlayGameState state;

    private LevelRenderer levelRenderer;
    private UIAbilityBar abilityBar;

    /**
     * Luo uuden pelitilapiirtäjän.
     *
     * @param textureDao dao jolla tekstuurit ladataan
     */
    public PlayGameStateRenderer(@NonNull TextureDAO textureDao) {
        this.textureDao = textureDao;
        this.textRenderer = new TextRenderer(this.textureDao, 1, 16);
        this.levelRenderer = new LevelRenderer(this.textureDao, "tileset.png", 8, 8);

        this.renderers = new RendererDAO("content/renderers/", this.textureDao);
        this.renderers.discoverAndLoadAll();

        this.damagePopupRenderer = new DamagePopupRenderer();
    }

    @Override
    public boolean init(@NonNull PlayGameState state) {
        this.state = state;

        for (val renderer : this.renderers.getAll()) {
            if (renderer.init()) {
                return true;
            }
        }

        this.textRenderer.init();
        this.damagePopupRenderer.init(state);
        this.abilityBar = new UIAbilityBar(this.textureDao, this.textRenderer);

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

        drawWorld(camera, state);
        postDrawWorld(camera, state);

        this.damagePopupRenderer.draw(camera, this.textRenderer);
        drawUI(camera, state);
    }

    private void makeCameraFollowPlayer(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val player = state.getManager().getPlayer();
        val cameraX = player.getX() - camera.getViewportWidth() / 2;
        val cameraY = player.getY() - camera.getViewportHeight() / 2;
        camera.setPosition(cameraX, cameraY);
    }

    private void drawWorld(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.draw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.renderers.get(object.getRendererID());
            if (renderer != null) {
                renderer.draw(camera, object);
            }
        }
    }

    private void postDrawWorld(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.postDraw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.renderers.get(object.getRendererID());
            if (renderer != null) {
                renderer.postDraw(camera, object);
            }
        }
    }

    private void drawUI(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val screenTopLeftX = camera.getX();
        val screenTopLeftY = camera.getY();

        drawTurnStatus(camera, state, screenTopLeftX, screenTopLeftY);
        val remainingAP = state.getManager().isCharactersTurn(state.getManager().getPlayer()) ? state.getManager().getRemainingActionPoints() : 0;
        this.abilityBar.draw(camera, state.getManager().getPlayer().getAbilities(), screenTopLeftX, screenTopLeftY + camera.getViewportHeight() - 18, remainingAP);

        if (state.getManager().getPlayer().isDead()) {
            drawDeathMessage(camera, state, screenTopLeftX, screenTopLeftY);
        }

    }

    private void drawTurnStatus(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        drawCurrentTurn(camera, state, x + 2, y + 2);
        drawActionLabel(camera, state, x + 2, y + 10);
    }

    private void drawCurrentTurn(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        val str = state.getGame().getActiveProfile().getName()
            + "\nTurn: " + state.getManager().getPlayer().getTurnsTaken();
        this.textRenderer.draw(camera, x, y, 0.25f, 0.65f, 0.25f, 4, str);
    }

    private void drawActionLabel(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        String apStr = resolveActionLabel(state);
        this.textRenderer.draw(camera, x, y, 0.65f, 0.25f, 0.25f, 2, apStr);
    }

    private String resolveActionLabel(@NonNull PlayGameState state) {
        val world = state.getWorld();
        val player = state.getManager().getPlayer();

        String apStr = "Waiting...";
        if (world.getObjectManager().isCharactersTurn(player)) {
            val remaining = world.getObjectManager().getRemainingActionPoints();
            val total = player.getAttributes().getActionPoints(player.getLevels());
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
            x + (w / 2f) - (len * size) / 2f,
            y + (h / 2f) - (size / 2f) + ((h / 2f + size) - dt * (h / 2f + size)),
            0.65f, 0.25f, 0.25f,
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
            x + (w / 2.0f) - (continueLen * continueSize) / 2.0f,
            y + (h / 2.0f) + (size / 2.0f) + 0.5f,
            0.85f, 0.85f, 0.85f,
            continueSize, continueStr
        );

        if (Input.getHandler().isKeyPressed(Key.SPACE)) {
            this.state.getEventSystem().fire(new PlayEvent.ReturnToMenuAfterLoss());
        }
    }
}
