package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.event.EventSystem;

import java.util.Arrays;

/**
 * Apuluokka hahmon attribuuttien käsittelyyn.
 */
@Slf4j
public class CharacterLevels {
    private transient EventSystem eventSystem;
    private transient CharacterObject character;

    @Getter private int experience;
    @Getter @Setter private int experiencePerFloor = 0;
    private int[] attributeLevels = new int[]{1, 1, 1, 1, 1, 1, 1, 1};


    /**
     * Laskee paljonko attribuuttipisteitä hahmolla on käytettävissä yhteensä.
     *
     * @return attribuuttipisteiden maksimimäärä
     */
    public int calculateMaxAttributePoints() {
        return Attribute.MAX.ordinal() + this.getXpLevel();
    }

    /**
     * Laskee montako attribuuttipistettä on jo käytössä.
     *
     * @return käytössä olevien attribuuttipisteiden määrä
     */
    public int calculateAttributePointsInUse() {
        return Arrays.stream(this.attributeLevels).sum();
    }

    /**
     * Laskee hahmon kokemustason.
     *
     * @return hahmon kokemustaso
     */
    public int getXpLevel() {
        if (this.experience == 0) {
            return 0;
        }
        int level = 0;
        while ((this.experience - experienceRequiredForLevel(level + 1)) >= 0) {
            level++;
        }

        return level;
    }

    /**
     * Laskee tasoon tarvittavan kokemuspistemäärän.
     *
     * @param level taso jonka "hinta" kokemuspisteinä halutaan tietää
     *
     * @return tarvittava kokemuspisteiden määrä
     *
     * @throws IllegalArgumentException jos taso on negatiivinen
     */
    public int experienceRequiredForLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Level cannot be nagative!");
        }

        return (100 + Math.max(0, level - 1) * 5) * level;
    }

    /**
     * Kopio tasot uuteen olioon.
     *
     * @param template templaatti josta kopioidaan
     */
    public CharacterLevels(CharacterLevels template) {
        this.experience = template.experience;
        this.experiencePerFloor = template.experiencePerFloor;

        this.attributeLevels = new int[Attribute.MAX.ordinal()];
        for (int i = 0; i < this.attributeLevels.length; i++) {
            this.attributeLevels[i] = template.attributeLevels[i] > 0 ? template.attributeLevels[i] : 1;
        }
    }

    /**
     * Antaa hahmolle kokemuspisteitä.
     *
     * @param amount annettavien pisteiden määrä
     */
    public void rewardExperience(int amount) {
        val levelBefore = getXpLevel();
        val multiplier = 1.0f + Attribute.Intelligence.getXpBonus(this);
        this.experience += amount * multiplier;

        val levelAfter = getXpLevel();
        if (levelAfter > levelBefore) {
            this.eventSystem.fire(new CharacterEvent.LevelUp(this.character, levelAfter));
        }
    }

    /**
     * Hakee attribuutin tason.
     *
     * @param attribute attribuutti joka taso haetaan
     *
     * @return attribuutin taso
     *
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
     *
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

        val spentPointsBeforehand = calculateAttributePointsInUse();
        val spentPointsAfterwards = spentPointsBeforehand - this.attributeLevels[attribute.ordinal()] + level;
        if (calculateMaxAttributePoints() < spentPointsAfterwards) {
            LOG.error("Tried to set attribute level but there are no enough points available");
            return;
        }

        this.attributeLevels[attribute.ordinal()] = level;
    }


    /**
     * Kasvattaa attribuutin tasoa yhdellä.
     *
     * @param attribute attribuutti joka tasoa kasvatetaan
     *
     * @throws NullPointerException     jos attribuutti on <code>null</code>
     * @throws IllegalArgumentException jos taso ei ole välillä [1..10]
     * @throws IllegalArgumentException jos attribuutti on {@link Attribute#MAX}
     */
    public void levelUpAttribute(@NonNull Attribute attribute) {
        if (calculateMaxAttributePoints() == 0) {
            LOG.warn("Tried to level up attribute without enough attribute points!");
            return;
        }
        setAttributeLevel(attribute, getAttributeLevel(attribute) + 1);
    }

    /**
     * Luo uuden hahmon attribuuttiolion ja asettaa kaikkien attribuuttien tasoksi 1.
     */
    public CharacterLevels() {
        this.experience = 0;

        setAttributeLevel(Attribute.STRENGTH, 1);
        setAttributeLevel(Attribute.ENDURANCE, 1);
        setAttributeLevel(Attribute.VITALITY, 1);
        setAttributeLevel(Attribute.DEXTERITY, 1);
        setAttributeLevel(Attribute.CHARISMA, 1);
        setAttributeLevel(Attribute.INTELLIGENCE, 1);
        setAttributeLevel(Attribute.WISDOM, 1);
        setAttributeLevel(Attribute.LUCK, 1);
    }

    public void init(@NonNull CharacterObject character) {
        if (character.getWorld() != null) {
            this.eventSystem = character.getWorld().getObjectManager().getEventSystem();
            this.experience += this.experiencePerFloor * character.getWorld().getFloor();
        }
        this.character = character;

    }
}
