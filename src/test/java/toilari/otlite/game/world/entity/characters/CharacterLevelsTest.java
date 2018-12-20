package toilari.otlite.game.world.entity.characters;

import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterLevels;

import static org.junit.jupiter.api.Assertions.*;

class CharacterLevelsTest {
    @Test
    void attributeLevelsAreAtOneWithDefaultConstructor() {
        val levels = new CharacterLevels();
        for (val attribute : Attribute.values()) {
            if (attribute == Attribute.MAX) {
                continue;
            }
            assertEquals(1, levels.getAttributeLevel(attribute));
        }
    }

    @Test
    void experienceIsAtZeroWithDefaultConstructor() {
        val levels = new CharacterLevels();
        assertEquals(0, levels.getExperience());
    }

    @Test
    void getXpLevelIsConsistentWithAmountOfExperience() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        for (var i = 0; i <= 100000; i++) {
            val levels = new CharacterLevels();
            levels.init(character);
            levels.rewardExperience(i);

            val required = levels.experienceRequiredForLevel(levels.getXpLevel());
            val requiredForNext = levels.experienceRequiredForLevel(levels.getXpLevel() + 1);
            assertTrue(levels.getExperience() >= required);
            assertTrue(levels.getExperience() < requiredForNext);
        }
    }

    @Test
    void experienceRequiredForLevelThrowsIfLevelIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new CharacterLevels().experienceRequiredForLevel(-1));
    }

    @Test
    void getAttributeLevelThrowsIfAttributeIsMAX() {
        assertThrows(IllegalArgumentException.class, () -> new CharacterLevels().getAttributeLevel(Attribute.MAX));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getAttributeLevelThrowsIfAttributeIsNull() {
        assertThrows(NullPointerException.class, () -> new CharacterLevels().getAttributeLevel(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setAttributeLevelThrowsIfAttributeIsNull() {
        assertThrows(NullPointerException.class, () -> new CharacterLevels().setAttributeLevel(null, 1));
    }

    @Test
    void setAttributeLevelThrowsIfAttributeIsMAX() {
        assertThrows(IllegalArgumentException.class, () -> new CharacterLevels().setAttributeLevel(Attribute.MAX, 1));
    }

    @Test
    void setAttributeLevelThrowsIfAttributeIsOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> new CharacterLevels().setAttributeLevel(Attribute.STRENGTH, 0));
        assertThrows(IllegalArgumentException.class, () -> new CharacterLevels().setAttributeLevel(Attribute.STRENGTH, 11));
    }

    @Test
    void copyConstructorCopiesValues() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        val template = new CharacterLevels();
        template.init(character);
        template.setAttributeLevel(Attribute.STRENGTH, 5);
        template.setAttributeLevel(Attribute.ENDURANCE, 3);
        template.setAttributeLevel(Attribute.VITALITY, 4);
        template.setAttributeLevel(Attribute.DEXTERITY, 7);
        template.setAttributeLevel(Attribute.CHARISMA, 2);
        template.setAttributeLevel(Attribute.INTELLIGENCE, 2);
        template.setAttributeLevel(Attribute.WISDOM, 2);
        template.setAttributeLevel(Attribute.LUCK, 6);
        template.rewardExperience(12345);
        template.setExperiencePerFloor(321);

        val copy = new CharacterLevels(template);
        for (val attribute : Attribute.values()) {
            if (attribute == Attribute.MAX) {
                continue;
            }
            assertEquals(template.getAttributeLevel(attribute), copy.getAttributeLevel(attribute));
        }
        assertEquals(template.getExperience(), copy.getExperience());
        assertEquals(template.getXpLevel(), copy.getXpLevel());
        assertEquals(template.getExperiencePerFloor(), copy.getExperiencePerFloor());
    }
}
