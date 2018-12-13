package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.util.Color;
import toilari.otlite.game.util.MathUtil;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Piirtää "pop-up" tekstejä kuten vahinkopisteindikaattoreita yms.
 */
public class PopupTextRenderer {
    private static final float HEALTH_LABEL_START_OFFSET_X = 2.0f;
    private static final float HEALTH_LABEL_START_OFFSET_Y = 2.0f;
    private static final float HEALTH_LABEL_TARGET_OFFSET_X = 2.0f;
    private static final float HEALTH_LABEL_TARGET_OFFSET_Y = -6.0f;
    private static final int HEALTH_LABEL_DURATION = 1500;
    private static final int HEALTH_LABEL_FONTSIZE = 2;
    private static final Color DAMAGE_LABEL_COLOR = Color.RED.shade(0.15f);
    private static final Color DAMAGE_LABEL_COLOR_CRITICAL = new Color(0.9f, 0.8f, 0.1f);
    private static final Color HEAL_LABEL_COLOR = Color.GREEN.shade(0.15f);

    private static final float MISS_LABEL_START_OFFSET_X = 2.0f;
    private static final float MISS_LABEL_START_OFFSET_Y = 2.0f;
    private static final float MISS_LABEL_TARGET_OFFSET_X = 2.0f;
    private static final float MISS_LABEL_TARGET_OFFSET_Y = -6.0f;
    private static final int MISS_LABEL_DURATION = 2000;
    private static final int MISS_LABEL_FONTSIZE = 3;
    private static final Color MISS_LABEL_COLOR = new Color(0.1f, 0.8f, 0.9f);

    private static final float DEATH_LABEL_START_OFFSET_X = 2.0f;
    private static final float DEATH_LABEL_START_OFFSET_Y = 2.0f;
    private static final float DEATH_LABEL_TARGET_OFFSET_X = 2.0f;
    private static final float DEATH_LABEL_TARGET_OFFSET_Y = -6.0f;
    private static final int DEATH_LABEL_DURATION = 1500;
    private static final int DEATH_LABEL_FONTSIZE = 2;
    private static final Color DEATH_LABEL_COLOR = Color.RED.shade(0.15f);
    private static final String[] DEATH_MESSAGES = {
        "REKT",
        "pwnd",
        "rip",
        "-snip-"
    };


    private List<PopupText> popupTexts, popupTextsSwap;
    private Random random = new Random();

    /**
     * Alustaa piirtäjän.
     *
     * @param state pelitila jonka viestejä kuunnellaan
     */
    public void init(@NonNull PlayGameState state) {
        this.popupTexts = new ArrayList<>();
        this.popupTextsSwap = new ArrayList<>();
        state.getEventSystem().subscribeTo(CharacterEvent.Damage.class, (e) -> onCharacterHealthChange(e.getTarget(), -e.getAmount(), e.isCritical()));
        state.getEventSystem().subscribeTo(CharacterEvent.Heal.class, (e) -> onCharacterHealthChange(e.getCharacter(), e.getAmount(), false));
        state.getEventSystem().subscribeTo(CharacterEvent.MissedAttack.class, this::onAttackMiss);
        state.getEventSystem().subscribeTo(CharacterEvent.Death.class, this::onCharacterDeath);
    }

    /**
     * Piirtää kaikki indikaattorit.
     *
     * @param camera       kamera jonka näkökulmasta piirretään
     * @param textRenderer tekstipiirtäjä jolla luvut piirretään
     * @param batch        sarjapiirtäjä jonka jonoon piirtokomennot asetetaan
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull TextRenderer textRenderer, @NonNull SpriteBatch batch) {
        this.popupTextsSwap.clear();
        for (val instance : this.popupTexts) {
            if (System.currentTimeMillis() > instance.timestamp + instance.duration) {
                continue;
            }

            this.popupTextsSwap.add(instance);
            drawPopupText(camera, textRenderer, instance, batch);
        }

        val tmp = this.popupTexts;
        this.popupTexts = this.popupTextsSwap;
        this.popupTextsSwap = tmp;
    }

    private void drawPopupText(@NonNull LWJGLCamera camera, @NonNull TextRenderer textRenderer, PopupText instance, @NonNull SpriteBatch batch) {

        float dt = (System.currentTimeMillis() - instance.timestamp) / (float) instance.duration;
        var x = MathUtil.lerp(instance.startX, instance.targetX, dt);
        var y = MathUtil.lerp(instance.startY, instance.targetY, dt);

        var offsetY = 0.0f;
        var offsetX = instance.centered ? ((instance.text.length() - 1) * instance.fontsize) / -2.0f : 0.0f;
        textRenderer.draw(camera, batch, x + offsetX, y + offsetY, instance.color, instance.fontsize, instance.text);
    }

    private void onCharacterHealthChange(@NonNull GameObject target, float amount, boolean critical) {
        if (target instanceof IHealthHandler && ((IHealthHandler) target).isDead()) {
            return;
        }

        var color = amount < 0 ? (critical ? DAMAGE_LABEL_COLOR_CRITICAL : DAMAGE_LABEL_COLOR) : HEAL_LABEL_COLOR;
        var prefix = amount > 0 ? "+" : "";

        this.popupTexts.add(new PopupText(
            target.getX() + HEALTH_LABEL_START_OFFSET_X,
            target.getY() + HEALTH_LABEL_START_OFFSET_Y,
            target.getX() + HEALTH_LABEL_TARGET_OFFSET_X,
            target.getY() + HEALTH_LABEL_TARGET_OFFSET_Y,
            HEALTH_LABEL_DURATION,
            String.format("%s%.1f", prefix, amount),
            color, HEALTH_LABEL_FONTSIZE + (critical ? 1 : 0), true));
    }

    private void onAttackMiss(@NonNull CharacterEvent.MissedAttack event) {
        val target = event.getTarget();
        if (((IHealthHandler) target).isDead()) {
            return;
        }

        this.popupTexts.add(new PopupText(
            target.getX() + MISS_LABEL_START_OFFSET_X,
            target.getY() + MISS_LABEL_START_OFFSET_Y,
            target.getX() + MISS_LABEL_TARGET_OFFSET_X,
            target.getY() + MISS_LABEL_TARGET_OFFSET_Y,
            MISS_LABEL_DURATION,
            "MISSED",
            MISS_LABEL_COLOR, MISS_LABEL_FONTSIZE, true));
    }

    private void onCharacterDeath(@NonNull CharacterEvent.Death event) {
        val target = event.getCharacter();
        this.popupTexts.add(new PopupText(
            target.getX() + DEATH_LABEL_START_OFFSET_X,
            target.getY() + DEATH_LABEL_START_OFFSET_Y,
            target.getX() + DEATH_LABEL_TARGET_OFFSET_X,
            target.getY() + DEATH_LABEL_TARGET_OFFSET_Y,
            DEATH_LABEL_DURATION,
            resolveDeathMessage(target, event.getCause()),
            DEATH_LABEL_COLOR, DEATH_LABEL_FONTSIZE, true));
    }

    private String resolveDeathMessage(CharacterObject target, CharacterEvent.Death.Cause cause) {
        if (cause == CharacterEvent.Death.Cause.FALL) {
            return target.getRendererID().equals("sheep") ? "BAAAAAA!" : "AAAAAA!";
        }

        return DEATH_MESSAGES[this.random.nextInt(DEATH_MESSAGES.length)];
    }

    private static class PopupText {
        @NonNull private final String text;
        @NonNull private final Color color;
        private final long timestamp, duration;
        private final float startX, startY;
        private final float targetX, targetY;
        private final int fontsize;
        private final boolean centered;

        PopupText(float startX, float startY, float targetX, float targetY, int duration, @NonNull String text, @NonNull Color color, int fontsize, boolean centered) {
            this.startX = startX;
            this.startY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.timestamp = System.currentTimeMillis();
            this.duration = duration;
            this.text = text;
            this.color = color;
            this.fontsize = fontsize;
            this.centered = centered;
        }
    }
}
