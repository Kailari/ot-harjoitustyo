package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.renderer.IRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Piirtäjä pelitilan piirtämiseen. Vastaa maailman
 */
public class PlayGameStateRenderer implements ILWJGLGameStateRenderer<PlayGameState> {
    private static final int DAMAGE_LABEL_DURATION = 1500;
    private static final int DAMAGE_LABEL_OFFSET_X = 2;
    private static final int DAMAGE_LABEL_OFFSET_Y = 2;
    private static final int DAMAGE_LABEL_DISTANCE = 8;
    // TODO: mapping class for these to get rid of unchecked code
    @NonNull private final Map<String, IRenderer> rendererMappings = new HashMap<>();
    private final TextureDAO textureDao;

    private List<DamageInstance> damageInstances;
    private List<DamageInstance> damageInstancesSwap;

    private LevelRenderer levelRenderer;
    private TextRenderer textRenderer;

    /**
     * Luo uuden pelitilapiirtäjän.
     *
     * @param textureDao dao jolla tekstuurit ladataan
     */
    public PlayGameStateRenderer(@NonNull TextureDAO textureDao) {
        this.textureDao = textureDao;
        this.levelRenderer = new LevelRenderer(this.textureDao, "tileset.png", 8, 8);
        this.rendererMappings.put("player", new PlayerRenderer(this.textureDao));
        this.rendererMappings.put("character", new CharacterRenderer(this.textureDao, "sheep.png", 1));
    }

    @Override
    public boolean init(@NonNull PlayGameState state) {
        for (val renderer : this.rendererMappings.values()) {
            if (renderer.init()) {
                return true;
            }
        }

        this.damageInstances = new ArrayList<>();
        this.damageInstancesSwap = new ArrayList<>();
        this.textRenderer = new TextRenderer(this.textureDao, 1, 8);
        state.getEventSystem().subscribeTo(CharacterEvent.Damage.class, this::onCharacterDamage);

        return this.levelRenderer.init();
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        makeCameraFollowPlayer(camera, state);

        drawWorld(camera, state);
        postDrawWorld(camera, state);

        drawUI(camera, state);
        drawPopupTexts(camera);
    }

    private void makeCameraFollowPlayer(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val player = state.getPlayer();
        val cameraX = player.getX() - camera.getViewportWidth() / 16;
        val cameraY = player.getY() - camera.getViewportHeight() / 16;
        camera.setPosition(cameraX, cameraY);
    }

    private void drawWorld(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.draw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.rendererMappings.get(object.getRendererID());
            if (renderer != null) {
                renderer.draw(camera, object);
            }
        }
    }

    private void postDrawWorld(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.postDraw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.rendererMappings.get(object.getRendererID());
            if (renderer != null) {
                renderer.postDraw(camera, object);
            }
        }
    }

    private void drawUI(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        int x = Math.round(camera.getPosition().x);
        int y = Math.round(camera.getPosition().y);

        drawTurnStatus(camera, state, world, x, y);

        if (state.getPlayer().isDead()) {
            drawDeathMessage(camera, state, x, y);
        }
    }

    private void drawTurnStatus(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, @NonNull World world, int x, int y) {
        val str = state.getGame().getActiveProfile().getName()
            + "\nTurn: " + state.getPlayer().getTurnsTaken();
        this.textRenderer.draw(camera, x + 2, y + 2, 0.25f, 0.65f, 0.25f, 4, str);

        String apStr = "Waiting...";
        if (world.getObjectManager().isCharactersTurn(state.getPlayer())) {
            val remaining = world.getObjectManager().getRemainingActionPoints();
            val total = state.getPlayer().getAttributes().getActionPoints(state.getPlayer().getLevels());
            if (remaining == 0) {
                apStr = "Press <SPACE> to end turn";
            } else {
                apStr = "AP: " + remaining + "/" + total;
            }
        }

        this.textRenderer.draw(camera, x + 2, y + 2 + 8, 0.65f, 0.25f, 0.25f, 2, apStr);
    }

    private void drawDeathMessage(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, int x, int y) {
        val ded = "You are dead";
        val len = ded.length();
        val size = 8;

        val w = (int) Math.ceil(camera.getViewportWidth() / camera.getPixelsPerUnit());
        val h = (int) Math.ceil(camera.getViewportHeight() / camera.getPixelsPerUnit());

        val dt = Math.min(1, (System.currentTimeMillis() - state.getPlayer().getDeathTime()) / 5000.0f);
        this.textRenderer.draw(
            camera,
            x + (w / 2) - (len * size) / 2,
            y + (h / 2) - (size / 2) + (int) Math.floor((h / 2f + size) - dt * (h / 2f + size)),
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
        float dt = (System.currentTimeMillis() - instance.timestamp) / (float) instance.duration;
        int offsetY = -Math.round(dt * PlayGameStateRenderer.DAMAGE_LABEL_DISTANCE);
        int offsetX = 0;

        String msg;
        if (instance.killingBlow) {
            msg = "rekt";
            offsetX -= 6;
        } else {
            msg = String.valueOf(Math.round(instance.amount));
        }

        this.textRenderer.draw(camera, instance.x + offsetX, instance.y + offsetY, 0.8f, 0.1f, 0.1f, 4, msg);
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
