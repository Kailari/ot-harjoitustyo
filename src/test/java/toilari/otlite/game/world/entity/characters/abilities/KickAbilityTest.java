package toilari.otlite.game.world.entity.characters.abilities;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class KickAbilityTest {
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getCooldownLengthMatchesCharacterAttributes(int strengthLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, strengthLevel);
        val ability = new KickAbility();
        ability.init(character);
        assertEquals(Attribute.Strength.getKickCooldown(character.getLevels()), ability.getCooldownLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getCostMatchesCharacterAttributes(int strengthLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, strengthLevel);
        val ability = new KickAbility();
        ability.init(character);
        assertEquals(Attribute.Strength.getKickCost(character.getLevels()), ability.getCost());
    }

    //@Test
    void notYetImplemented() {
        //fail("not implemented");
    }
}
