package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;

public class CharacterLevels {
    @Getter private final int xpLevel;
    private final int[] attributeLevels = new int[]{1, 1, 1, 1, 1, 1, 1, 1};

    public int getAttributeLevel(@NonNull Attribute attribute) {
        if (attribute == Attribute.MAX) {
            throw new IllegalArgumentException("MAX is not a valid Attribute!");
        }

        return this.attributeLevels[attribute.ordinal()];
    }

    public void setAttributeLevel(@NonNull Attribute attribute, int level) {
        if (attribute == Attribute.MAX) {
            throw new IllegalArgumentException("MAX is not a valid Attribute!");
        }

        if (level < 1 || level > 10) {
            throw new IllegalArgumentException("Level must be within range 1..10");
        }

        this.attributeLevels[attribute.ordinal()] = level;
    }

    public void levelUpAttribute(@NonNull Attribute attribute) {
        setAttributeLevel(attribute, getAttributeLevel(attribute) + 1);
    }

    public CharacterLevels() {
        this.xpLevel = 0;

        setAttributeLevel(Attribute.STRENGTH, 1);
        setAttributeLevel(Attribute.ENDURANCE, 1);
        setAttributeLevel(Attribute.VITALITY, 1);
        setAttributeLevel(Attribute.DEXTERITY, 1);
        setAttributeLevel(Attribute.CHARISMA, 1);
        setAttributeLevel(Attribute.INTELLIGENCE, 1);
        setAttributeLevel(Attribute.WISDOM, 1);
        setAttributeLevel(Attribute.LUCK, 1);
    }
}
