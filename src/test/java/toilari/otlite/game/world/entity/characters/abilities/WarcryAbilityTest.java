package toilari.otlite.game.world.entity.characters.abilities;

import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.abilities.WarcryAbility;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WarcryAbilityTest {
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10})
    void getCooldownLengthMatchesCharacterAttributes(int charismaLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.CHARISMA, charismaLevel);
        val ability = new WarcryAbility();
        ability.init(character);
        assertEquals(Attribute.Charisma.getWarcryCooldown(character.getLevels()), ability.getCooldownLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10})
    void getCostMatchesCharacterAttributes(int charismaLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.CHARISMA, charismaLevel);
        val ability = new WarcryAbility();
        ability.init(character);
        assertEquals(Attribute.Charisma.getWarcryCost(character.getLevels()), ability.getCost());
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10})
    void getExtentMatchesCharacterAttributes(int charismaLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.CHARISMA, charismaLevel);
        val ability = new WarcryAbility();
        ability.init(character);
        assertEquals(Attribute.Charisma.getWarcryRange(character.getLevels()), ability.getAreaExtent());
    }
}
