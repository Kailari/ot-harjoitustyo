package toilari.otlite.game.world.entity.characters.abilities;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttackAbilityTest {
    @Test
    void getCooldownLengthMatchesCharacterAttributes() {
        val character = FakeCharacterObject.create();
        val ability = new AttackAbility();
        ability.init(character);
        assertEquals(character.getAttributes().getAttackCooldown(), ability.getCooldownLength());
    }

    @Test
    void getCostMatchesCharacterAttributes() {
        val character = FakeCharacterObject.create();
        val ability = new AttackAbility();
        ability.init(character);
        assertEquals(character.getAttributes().getAttackCost(), ability.getCost());
    }
}
