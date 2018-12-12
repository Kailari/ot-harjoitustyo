package toilari.otlite.game.world.entity.characters;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterLevels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterAttributesTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void copyConstructorThrowsIfTemplateIsNull() {
        assertThrows(NullPointerException.class, () -> new CharacterAttributes(null));
    }

    @Test
    void copyConstructorCopiesValues() {
        val character = FakeCharacterObject.create();
        val attrs = new CharacterAttributes(character, 10, 0, 20, 0, 10, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0);
        val copy = new CharacterAttributes(attrs);
        character.init();
        copy.init(character);
        assertEquals(10, copy.getMoveCost());
        assertEquals(20, copy.getAttackCost());
        assertEquals(10, copy.getHealthRegen());
    }
}
