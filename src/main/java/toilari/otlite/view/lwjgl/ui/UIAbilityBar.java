package toilari.otlite.view.lwjgl.ui;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetDAO;
import toilari.otlite.game.util.Color;
import toilari.otlite.game.world.entities.characters.CharacterAbilities;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.TargetSelectorControllerComponent;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;

/**
 * Käyttöliittymän kykypalkki josta löytyvät kaikki pelihahmon kyvyt.
 */
public class UIAbilityBar {
    private static final int ABILITY_LABEL_FONTSIZE = 2;
    private static final Color ABILITY_COLOR_ON_COOLDOWN = new Color(0.85f, 0.15f, 0.15f);
    private static final Color ABILITY_COLOR_WHILE_ACTIVE = new Color(0.15f, 0.85f, 0.15f);
    private static final Color ABILITY_COLOR_IDLE = Color.WHITE;

    private static final Color ABILITY_LABEL_COLOR = Color.WHITE;
    private static final Color ABILITY_INDEX_COLOR = Color.WHITE.shade(0.45f);
    private static final Color ABILITY_COST_COLOR_CAN_AFFORD = new Color(0.15f, 0.85f, 0.15f);
    private static final Color ABILITY_COST_COLOR_CANNOT_AFFORD = new Color(0.85f, 0.15f, 0.15f);
    private static final Color ABILITY_COOLDOWN_COLOR = Color.WHITE.shade(0.15f);

    private Texture abilityBackgroundTexture;


    private final Sprite abilityBackground;
    private final TextRenderer textRenderer;

    /**
     * Luo uuden kykypalkin.
     *
     * @param textures     dao jolla tarvittavat tekstuurit ladataan
     * @param textRenderer tekstipiirtäjä jolla tarvittavat tekstit piiretään
     */
    public UIAbilityBar(@NonNull IGetDAO<Texture, String> textures, @NonNull TextRenderer textRenderer) {
        this.textRenderer = textRenderer;

        this.abilityBackgroundTexture = textures.get("ability_background.png");
        this.abilityBackground = new Sprite(this.abilityBackgroundTexture, 0, 0, 16, 16, 16, 16);
    }

    /**
     * Piirtää palkin.
     *
     * @param camera      kamera jonka näkökulmasta piiretään
     * @param abilities   pelihahmon kyvyt joista näytettävät kyvyt valitaan
     * @param x           palkin x-koordinaatti
     * @param y           palkin y-koordinaatti
     * @param remainingAp hahmon jäljelläolevat toimintopisteet
     */
    public void draw(LWJGLCamera camera, @NonNull CharacterAbilities abilities, float x, float y, int remainingAp) {
        int i = 0;
        for (val ability : abilities.getAbilitiesSortedByPriority()) {
            if (!abilities.getComponent(ability.getClass()).isHidden() && ability.getCost() != -1) {
                val ts = abilities.getComponent(TargetSelectorAbility.class);
                drawAbility(camera, ability, ts, i++, x, y, remainingAp);
            }
        }
    }

    private void drawAbility(LWJGLCamera camera, IAbility ability, TargetSelectorControllerComponent ts, int index, float x, float y, int remainingAp) {
        val xx = 2 + x + index * (16 + 2);

        val color = ability.isOnCooldown() ? ABILITY_COLOR_ON_COOLDOWN : (ts.isActive(ability) ? ABILITY_COLOR_WHILE_ACTIVE : ABILITY_COLOR_IDLE);
        this.abilityBackground.draw(camera, xx, y, color);

        // Ability name (above the icon)
        this.textRenderer.draw(camera, xx, y - (2.5f + ability.getName().chars().filter(c -> c == '\n').count() * ABILITY_LABEL_FONTSIZE), ABILITY_LABEL_COLOR, ABILITY_LABEL_FONTSIZE, ability.getName());

        // Ability index (left bottom)
        this.textRenderer.draw(camera, xx + 1, y + 16 - 5.5f, ABILITY_INDEX_COLOR, 4, String.valueOf(index + 1));

        // AP cost (right bottom)
        val apColor = ability.getCost() > remainingAp ? ABILITY_COST_COLOR_CANNOT_AFFORD : ABILITY_COST_COLOR_CAN_AFFORD;
        this.textRenderer.draw(camera, xx + 11, y + 16 - 5.5f, apColor, 4, String.valueOf(ability.getCost()));

        // Cooldown (overdraw the whole thing)
        if (ability.isOnCooldown()) {
            this.textRenderer.draw(camera, xx + 1, y + 1, ABILITY_COOLDOWN_COLOR, 14, String.valueOf(ability.getRemainingCooldown()));
        }
    }

    /**
     * Vapauttaa piirtäjälle varatut resurssit.
     */
    public void destroy() {
        this.abilityBackgroundTexture.destroy();
    }
}
