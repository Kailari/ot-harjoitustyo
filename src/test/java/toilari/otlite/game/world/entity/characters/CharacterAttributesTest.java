package toilari.otlite.game.world.entity.characters;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterLevels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterAttributesTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void getMethodsThrowIfLevelsIsNull() {
        val attrs = new CharacterAttributes();
        assertThrows(NullPointerException.class, () -> attrs.getMaxHealth(null));
        assertThrows(NullPointerException.class, () -> attrs.getHealthRegen(null));
        assertThrows(NullPointerException.class, () -> attrs.getHealthRegenDelay(null));
        assertThrows(NullPointerException.class, () -> attrs.getArmor(null));
        assertThrows(NullPointerException.class, () -> attrs.getEvasion(null));
        assertThrows(NullPointerException.class, () -> attrs.getKnockbackResistance(null));
        assertThrows(NullPointerException.class, () -> attrs.getFearResistance(null));
        assertThrows(NullPointerException.class, () -> attrs.getActionPoints(null));
        assertThrows(NullPointerException.class, () -> attrs.getAttackDamage(null));
        assertThrows(NullPointerException.class, () -> attrs.getCriticalHitDamage(null));
        assertThrows(NullPointerException.class, () -> attrs.getCriticalHitChance(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void copyConstructorThrowsIfTemplateIsNull() {
        assertThrows(NullPointerException.class, () -> new CharacterAttributes(null));
    }

    @Test
    void copyConstructorCopiesValues() {
        val attrs = new CharacterAttributes(10, 0, 20, 0, 10, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0);
        val copy = new CharacterAttributes(attrs);
        assertEquals(10, copy.getMoveCost());
        assertEquals(20, copy.getAttackCost());
        assertEquals(10, copy.getHealthRegen(new CharacterLevels()));
    }
}
