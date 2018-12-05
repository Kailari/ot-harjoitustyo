package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.RendererDAO;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.ui.UIAbilityBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Piirtäjä pelitilan piirtämiseen. Vastaa maailman
 */
public class PlayGameStateRenderer implements ILWJGLGameStateRenderer<PlayGameState> {
    private static final int DAMAGE_LABEL_DURATION = 1500;
    private static final int DAMAGE_LABEL_OFFSET_X = 2;
    private static final int DAMAGE_LABEL_OFFSET_Y = 2;
    private static final int DAMAGE_LABEL_DISTANCE = 8;

    @NonNull private final TextureDAO textureDao; // TODO: mapping class for these to get rid of unchecked code
    @NonNull private final RendererDAO renderers;
    @NonNull private final TextRenderer textRenderer;

    private List<DamageInstance> damageInstances, damageInstancesSwap;

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
    }

    @Override
    public boolean init(@NonNull PlayGameState state) {
        for (val renderer : this.renderers.getAll()) {
            if (renderer.init()) {
                return true;
            }
        }

        this.textRenderer.init();
        this.damageInstances = new ArrayList<>();
        this.damageInstancesSwap = new ArrayList<>();
        state.getEventSystem().subscribeTo(CharacterEvent.Damage.class, this::onCharacterDamage);

        this.abilityBar = new UIAbilityBar(this.textureDao, this.textRenderer);

        return this.levelRenderer.init();
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        makeCameraFollowPlayer(camera, state);

        drawWorld(camera, state);
        postDrawWorld(camera, state);

        drawPopupTexts(camera);
        drawUI(camera, state);
    }

    private void makeCameraFollowPlayer(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val player = state.getPlayer();
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
        this.abilityBar.draw(camera, state.getPlayer().getAbilities(), screenTopLeftX, screenTopLeftY + camera.getViewportHeight() - 18);

        if (state.getPlayer().isDead()) {
            drawDeathMessage(camera, state, screenTopLeftX, screenTopLeftY);
        }

    }

    private void drawTurnStatus(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        drawCurrentTurn(camera, state, x + 2, y + 2);
        drawActionLabel(camera, state, x + 2, y + 10);
    }

    private void drawCurrentTurn(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        val str = state.getGame().getActiveProfile().getName()
            + "\nTurn: " + state.getPlayer().getTurnsTaken();
        this.textRenderer.draw(camera, x, y, 0.25f, 0.65f, 0.25f, 4, str);
    }

    private void drawActionLabel(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, float x, float y) {
        String apStr = resolveActionLabel(state);
        this.textRenderer.draw(camera, x, y, 0.65f, 0.25f, 0.25f, 2, apStr);
    }

    private String resolveActionLabel(@NonNull PlayGameState state) {
        val world = state.getWorld();

        String apStr = "Waiting...";
        if (world.getObjectManager().isCharactersTurn(state.getPlayer())) {
            val remaining = world.getObjectManager().getRemainingActionPoints();
            val total = state.getPlayer().getAttributes().getActionPoints(state.getPlayer().getLevels());
            if (remaining == 0) {
                apStr = "Press <SPACE> to end turn";
            } else {
                val targetSelector = state.getPlayer().getAbilities().getComponent(TargetSelectorAbility.class);
                if (targetSelector != null && targetSelector.getTarget() != null) {
                    apStr = "Press <SPACE> to attack!";
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

        val dt = Math.min(1, (System.currentTimeMillis() - state.getPlayer().getDeathTime()) / 5000.0f);
        this.textRenderer.draw(
            camera,
            x + (w / 2f) - (len * size) / 2f,
            y + (h / 2f) - (size / 2f) + ((h / 2f + size) - dt * (h / 2f + size)),
            0.65f, 0.25f, 0.25f,
            size,
            ded);
    }

    private void drawPopupTexts(@NonNull LWJGLCamera camera) {
        this.damageInstancesSwap.clear();
        for (val instance : this.damageInstances) {
            if (System.currentTimeMillis() > instance.timestamp + instance.duration) {
                continue;
            }

            this.damageInstancesSwap.add(instance);
            drawPopupText(camera, instance);
        }

        val tmp = this.damageInstances;
        this.damageInstances = this.damageInstancesSwap;
        this.damageInstancesSwap = tmp;
    }

    private void drawPopupText(@NonNull LWJGLCamera camera, DamageInstance instance) {
        int size = 4;
        float dt = (System.currentTimeMillis() - instance.timestamp) / (float) instance.duration;
        var offsetY = -dt * PlayGameStateRenderer.DAMAGE_LABEL_DISTANCE;
        var offsetX = 0f;

        String msg;
        if (instance.killingBlow) {
            if (instance.amount > 99999f) {
                msg = "AAaAaa!";
                size = 3;
                offsetX -= 9;
            } else {
                msg = "rekt";
                offsetX -= 6;
            }
        } else {
            msg = String.valueOf(Math.round(instance.amount));
        }

        this.textRenderer.draw(camera, instance.x + offsetX, instance.y + offsetY, 0.8f, 0.1f, 0.1f, size, msg);
    }

    private void onCharacterDamage(@NonNull CharacterEvent.Damage event) {
        val x = event.getTarget().getX() + PlayGameStateRenderer.DAMAGE_LABEL_OFFSET_X;
        val y = event.getTarget().getY() + PlayGameStateRenderer.DAMAGE_LABEL_OFFSET_Y;
        val killingBlow = event.getTarget() instanceof IHealthHandler && ((IHealthHandler) event.getTarget()).isDead();
        this.damageInstances.add(new DamageInstance(event.getAmount(), x, y, System.currentTimeMillis(), PlayGameStateRenderer.DAMAGE_LABEL_DURATION, killingBlow));
    }


    @Override
    public void destroy(@NonNull PlayGameState state) {
        this.levelRenderer.destroy();
        this.abilityBar.destroy();
    }

    private static class DamageInstance {
        private final long timestamp;
        private final long duration;
        private final float amount;
        private final boolean killingBlow;
        private final int x;
        private final int y;

        DamageInstance(float amount, int x, int y, long timestamp, int duration, boolean killingBlow) {
            this.amount = amount;
            this.x = x;
            this.y = y;
            this.timestamp = timestamp;
            this.duration = duration;
            this.killingBlow = killingBlow;
        }
    }
}
