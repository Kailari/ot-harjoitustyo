package toilari.otlite.game.world.entity.characters.abilities;

import org.junit.jupiter.api.Test;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EndTurnAbilityTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void performThrowsWhenGivenNullArgument() {
        assertThrows(NullPointerException.class, () -> new EndTurnAbility().perform(null));
    }

    @Test
    void hasNoCooldown() {
        assertEquals(0, new EndTurnAbility().getCooldownLength());
    }

    @Test
    void hasNoCost() {
        assertEquals(0, new EndTurnAbility().getCost());
    }
}
