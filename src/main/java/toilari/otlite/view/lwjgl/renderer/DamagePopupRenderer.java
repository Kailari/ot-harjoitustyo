package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Piirtää vahinkopisteindikaattoreita.
 */
public class DamagePopupRenderer {
    private static final int DAMAGE_LABEL_DURATION = 1500;
    private static final int DAMAGE_LABEL_OFFSET_X = 2;
    private static final int DAMAGE_LABEL_OFFSET_Y = 2;
    private static final int DAMAGE_LABEL_DISTANCE = 8;

    private List<DamageInstance> damageInstances, damageInstancesSwap;

    /**
     * Alustaa piirtäjän.
     *
     * @param state pelitila jonka viestejä kuunnellaan
     */
    public void init(@NonNull PlayGameState state) {
        this.damageInstances = new ArrayList<>();
        this.damageInstancesSwap = new ArrayList<>();
        state.getEventSystem().subscribeTo(CharacterEvent.Damage.class, this::onCharacterDamage);
    }

    /**
     * Piirtää kaikki indikaattorit.
     *
     * @param camera       kamera jonka näkökulmasta piirretään
     * @param textRenderer tekstipiirtäjä jolla luvut piirretään
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull TextRenderer textRenderer) {
        this.damageInstancesSwap.clear();
        for (val instance : this.damageInstances) {
            if (System.currentTimeMillis() > instance.timestamp + instance.duration) {
                continue;
            }

            this.damageInstancesSwap.add(instance);
            drawPopupText(camera, textRenderer, instance);
        }

        val tmp = this.damageInstances;
        this.damageInstances = this.damageInstancesSwap;
        this.damageInstancesSwap = tmp;
    }

    private void drawPopupText(@NonNull LWJGLCamera camera, @NonNull TextRenderer textRenderer, DamageInstance instance) {
        int size = 4;
        float dt = (System.currentTimeMillis() - instance.timestamp) / (float) instance.duration;
        var offsetY = -dt * DAMAGE_LABEL_DISTANCE;
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

        textRenderer.draw(camera, instance.x + offsetX, instance.y + offsetY, 0.8f, 0.1f, 0.1f, size, msg);
    }

    private void onCharacterDamage(@NonNull CharacterEvent.Damage event) {
        val x = event.getTarget().getX() + DAMAGE_LABEL_OFFSET_X;
        val y = event.getTarget().getY() + DAMAGE_LABEL_OFFSET_Y;
        val killingBlow = event.getTarget() instanceof IHealthHandler && ((IHealthHandler) event.getTarget()).isDead();
        this.damageInstances.add(new DamageInstance(event.getAmount(), x, y, System.currentTimeMillis(), DAMAGE_LABEL_DURATION, killingBlow));
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
