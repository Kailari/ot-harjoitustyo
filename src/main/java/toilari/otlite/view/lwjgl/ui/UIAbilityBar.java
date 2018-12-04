package toilari.otlite.view.lwjgl.ui;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
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

    private Texture abilityBackgroundTexture;


    private final Sprite abilityBackground;
    private final TextRenderer textRenderer;

    public UIAbilityBar(@NonNull TextureDAO textures, @NonNull TextRenderer textRenderer) {
        this.textRenderer = textRenderer;

        this.abilityBackgroundTexture = textures.get("ability_background.png");
        this.abilityBackground = new Sprite(this.abilityBackgroundTexture, 0, 0, 16, 16, 16, 16);
    }

    public void draw(LWJGLCamera camera, @NonNull CharacterAbilities abilities, float x, float y) {
        int i = 0;
        for (val ability : abilities.getAbilitiesSortedByPriority()) {
            if (!abilities.getComponent(ability.getClass()).isHidden()) {
                val ts = abilities.getComponent(TargetSelectorAbility.class);
                drawAbility(camera, ability, ts, i++, x, y);
            }
        }
    }

    private void drawAbility(LWJGLCamera camera, IAbility ability, TargetSelectorControllerComponent ts, int index, float x, float y) {
        val xx = 2 + x + index * (16 + 2);

        val r = ability.isOnCooldown() ? 0.85f : (ts.isActive(ability) ? 0.15f : 1.0f);
        val g = ability.isOnCooldown() ? 0.15f : (ts.isActive(ability) ? 0.85f : 1.0f);
        val b = ability.isOnCooldown() ? 0.15f : (ts.isActive(ability) ? 0.15f : 1.0f);
        this.abilityBackground.draw(camera, xx, y, r, g, b);

        this.textRenderer.draw(camera, xx, y - (2.5f + ability.getName().chars().filter(c -> c == '\n').count() * ABILITY_LABEL_FONTSIZE), 1.0f, 1.0f, 1.0f, ABILITY_LABEL_FONTSIZE, ability.getName());

        this.textRenderer.draw(camera, xx + 1, y + 16 - 5.5f, 0.5f, 0.5f, 0.5f, 4, String.valueOf(index + 1));

        if (ability.isOnCooldown()) {
            this.textRenderer.draw(camera, xx, y, 0.85f, 0.85f, 0.85f, 16, String.valueOf(ability.getRemainingCooldown()));
        }
    }

    public void destroy() {
        this.abilityBackgroundTexture.destroy();
    }
}
