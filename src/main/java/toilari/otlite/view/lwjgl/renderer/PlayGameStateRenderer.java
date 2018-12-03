package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.RendererDAO;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.TargetSelectorControllerComponent;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;

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
    // TODO: mapping class for these to get rid of unchecked code
    private final TextureDAO textureDao;
    private final RendererDAO renderers;

    private List<DamageInstance> damageInstances, damageInstancesSwap;

    private Texture abilityBackgroundTexture;

    private LevelRenderer levelRenderer;
    private TextRenderer textRenderer;
    private Sprite abilityBackground;

    /**
     * Luo uuden pelitilapiirtäjän.
     *
     * @param textureDao dao jolla tekstuurit ladataan
     */
    public PlayGameStateRenderer(@NonNull TextureDAO textureDao) {
        this.textureDao = textureDao;
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

        this.damageInstances = new ArrayList<>();
        this.damageInstancesSwap = new ArrayList<>();
        this.textRenderer = new TextRenderer(this.textureDao, 1, 16);
        state.getEventSystem().subscribeTo(CharacterEvent.Damage.class, this::onCharacterDamage);

        this.abilityBackgroundTexture = this.textureDao.get("ability_background.png");
        this.abilityBackground = new Sprite(this.abilityBackgroundTexture, 0, 0, 16, 16, 16, 16);

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
        val cameraX = player.getX() - camera.getViewportWidth() / 16;
        val cameraY = player.getY() - camera.getViewportHeight() / 16;
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
        int x = Math.round(camera.getPosition().x);
        int y = Math.round(camera.getPosition().y);

        drawTurnStatus(camera, state, x, y);
        drawAbilityBar(camera, state, x, y);

        if (state.getPlayer().isDead()) {
            drawDeathMessage(camera, state, x, y);
        }

    }

    private void drawTurnStatus(@NonNull LWJGLCamera camera, @NonNull PlayGameState state, int x, int y) {
        val world = state.getWorld();
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

    private void drawAbilityBar(LWJGLCamera camera, @NonNull PlayGameState state, int x, int y) {
        val abilities = state.getPlayer().getAbilities().getAbilitiesSortedByPriority();
        int i = 0;
        for (val ability : abilities) {
            if (!state.getPlayer().getAbilities().getComponent(ability.getClass()).isHidden()) {
                val ts = state.getPlayer().getAbilities().getComponent(TargetSelectorAbility.class);
                drawAbility(camera, ability, ts, i++, x, y);
            }
        }
    }

    private void drawAbility(LWJGLCamera camera, IAbility ability, TargetSelectorControllerComponent ts, int index, int x, int y) {
        val xx = 2 + x + index * (16 + 2);
        val yy = y + (int) (camera.getViewportHeight() / camera.getPixelsPerUnit()) - 16 - 2;

        val r = ability.isOnCooldown() ? 0.85f : (ts.isActive(ability) ? 0.15f : 1.0f);
        val g = ability.isOnCooldown() ? 0.15f : (ts.isActive(ability) ? 0.85f : 1.0f);
        val b = ability.isOnCooldown() ? 0.15f : (ts.isActive(ability) ? 0.15f : 1.0f);
        this.abilityBackground.draw(camera, xx, yy, r, g, b);

        this.textRenderer.draw(camera, xx, yy - (2.75f + ability.getName().chars().filter(c -> c == '\n').count() * 2.75f), 1.0f, 1.0f, 1.0f, 2, ability.getName());

        this.textRenderer.draw(camera, xx + 1, yy + 16 - 5.5f, 0.5f, 0.5f, 0.5f, 4, String.valueOf(index + 1));

        if (ability.isOnCooldown()) {
            this.textRenderer.draw(camera, xx, yy, 0.85f, 0.85f, 0.85f, 16, String.valueOf(ability.getRemainingCooldown()));
        }
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
        this.abilityBackgroundTexture.destroy();
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
