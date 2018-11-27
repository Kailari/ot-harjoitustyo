package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;

/**
 * Apuluokka hahmon attribuuttien käsittelyyn.
 */
public class CharacterLevels {
    @Getter private final int xpLevel;
    private final int[] attributeLevels = new int[]{1, 1, 1, 1, 1, 1, 1, 1};

    /**
     * Kopio tasot uuteen olioon.
     *
     * @param template templaatti josta kopioidaan
     */
    public CharacterLevels(CharacterLevels template) {
        this.xpLevel = template.xpLevel;
        System.arraycopy(template.attributeLevels, 0, this.attributeLevels, 0, 8);
    }

    /**
     * Hakee attribuutin tason.
     *
     * @param attribute attribuutti joka taso haetaan
     * @return attribuutin taso
     * @throws NullPointerException     jos attribuutti on <code>null</code>
     * @throws IllegalArgumentException jos attribuutti on {@link Attribute#MAX}
     */
    public int getAttributeLevel(@NonNull Attribute attribute) {
        if (attribute == Attribute.MAX) {
            throw new IllegalArgumentException("MAX is not a valid Attribute!");
        }

        return this.attributeLevels[attribute.ordinal()];
    }

    /**
     * Asettaa attribuutin tason.
     *
     * @param attribute attribuutti joka taso asetetaan
     * @param level     attribuutin uusi taso
     * @throws NullPointerException     jos attribuutti on <code>null</code>
     * @throws IllegalArgumentException jos taso ei ole välillä [1..10]
     * @throws IllegalArgumentException jos attribuutti on {@link Attribute#MAX}
     */
    public void setAttributeLevel(@NonNull Attribute attribute, int level) {
        if (attribute == Attribute.MAX) {
            throw new IllegalArgumentException("MAX is not a valid Attribute!");
        }

        if (level < 1 || level > 10) {
            throw new IllegalArgumentException("Level must be within range 1..10");
        }

        this.attributeLevels[attribute.ordinal()] = level;
    }


    /**
     * Kasvattaa attribuutin tasoa yhdellä.
     *
     * @param attribute attribuutti joka tasoa kasvatetaan
     * @throws NullPointerException     jos attribuutti on <code>null</code>
     * @throws IllegalArgumentException jos taso ei ole välillä [1..10]
     * @throws IllegalArgumentException jos attribuutti on {@link Attribute#MAX}
     */
    public void levelUpAttribute(@NonNull Attribute attribute) {
        setAttributeLevel(attribute, getAttributeLevel(attribute) + 1);
    }

    /**
     * Luo uuden hahmon attribuuttiolion ja asettaa kaikkien attribuuttien tasoksi 1.
     */
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
