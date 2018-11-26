package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Hahmon attribuutit. Hahmokohtaiset perusmääreet jotka määrittävät hahmon kyvyt.
 */
@RequiredArgsConstructor
public class CharacterAttributes {
    private static final float FEAR_RESISTANCE_CAP = 0.95f;
    private static final float KNOCKBACK_RESISTANCE_CAP = 0.975f;
    private static final float EVASION_CAP = 0.9f;

    /**
     * Laskee hahmon maksimiterveyspisteet. Laskeminen tapahtuu kaavalla<br/>
     * <code>maxHealth = (baseHealth + level * healthGain) * modifier</code><br/>
     * johon modifier-osa saadaan laskemalla<br/>
     * <code>modifier = (1.0 + enduranceModifier + vitalityModifier)</code><br/>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon maksimiterveyspisteet, kaikki määreet huomioonottaen
     */
    public float getMaxHealth(@NonNull CharacterLevels levels) {
        val base = this.baseHealth + levels.getXpLevel() * this.healthGain;
        val enduranceModifier = Attribute.Endurance.getHealthModifier(levels.getAttributeLevel(Attribute.ENDURANCE));
        val vitalityModifier = Attribute.Vitality.getHealthModifier(levels.getAttributeLevel(Attribute.VITALITY));
        return base * (1.0f + enduranceModifier + vitalityModifier);
    }

    /**
     * Laskee hahmon terveyspisteiden palautumisnopeuden. Laskeminen tapahtuu kaavalla<br/>
     * <code>regen = (baseRegen + level * regenGain) * modifier</code><br/>
     * johon modifier-osa saadaan laskemalla<br/>
     * <code>modifier = (1.0 + vitalityModifier)</code><br/>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon terveyspisteiden palautumisnopeus, kaikki määreet huomioonottaen
     */
    public float getHealthRegen(@NonNull CharacterLevels levels) {
        val base = this.baseHealthRegen + levels.getXpLevel() * this.healthRegenGain;
        val vitalityModifier = Attribute.Vitality.getHealthRegenModifier(levels.getAttributeLevel(Attribute.VITALITY));
        return base * (1.0f + vitalityModifier);
    }

    /**
     * Hakee hahmon terveyspisteiden palautumisen viiveen. Palautettu luku kertoo montako vuoroa
     * kestää ennen kuin hahmon terveyspisteet alkavat palautua.
     *
     * @param levels hahmon attribuuttien tasot
     * @return viive kauanko vielä kestää ennen kuin hahmon terveyspisteet alkavat palautua
     */
    public int getHealthRegenDelay(@NonNull CharacterLevels levels) {
        val vitalityPart = Attribute.Vitality.getHealthRegenDelay(levels.getAttributeLevel(Attribute.VITALITY));
        return this.baseHealthRegenDelay + vitalityPart;
    }

    /**
     * Laskee hahmon "panssarin" eli vahingonvähennyksen määrän. Laskeminen tapahtuu kaavalla<br/>
     * <code>armor = (baseArmor + level * armorGain) * modifier</code><br/>
     * johon modifier-osa saadaan laskemalla<br/>
     * <code>modifier = (1.0 + enduranceModifier)</code><br/>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon vahingonvähennyksen määrä, kaikki määreet huomioiden
     */
    public float getArmor(@NonNull CharacterLevels levels) {
        val base = this.baseArmor + levels.getXpLevel() * this.armorGain;
        val enduranceBonus = Attribute.Endurance.getArmorModifier(levels.getAttributeLevel(Attribute.ENDURANCE));
        return base * (1.0f * enduranceBonus);
    }

    /**
     * Hahmon kyky välttää osumia. Todennäköisyys ettei isku osu hahmoon lainkaan.
     *
     * @param levels hahmon attribuuttien tilat
     * @return todennäköisyys ettei hahmoon osuta
     */
    public float getEvasion(@NonNull CharacterLevels levels) {
        val base = this.baseEvasion + levels.getXpLevel() * evasionGain;
        val luckBonus = Attribute.Luck.getEvasion(levels.getAttributeLevel(Attribute.LUCK));
        return Math.min(
            CharacterAttributes.EVASION_CAP,
            base + luckBonus);
    }

    /**
     * Hahmon kyky vastustaa tönäisyjä.
     *
     * @param levels hahmon attribuuttien tilat
     * @return montako prosenttia tönäisystä tulee jättää huomiotta
     */
    public float getKnockbackResistance(@NonNull CharacterLevels levels) {
        val strengthModifier = Attribute.Strength.getKnockbackResistance(levels.getAttributeLevel(Attribute.STRENGTH));
        return Math.min(
            CharacterAttributes.KNOCKBACK_RESISTANCE_CAP,
            this.baseKnockbackResistance * (1.0f + strengthModifier));
    }

    /**
     * Hahmon kyky vastustaa "pelko"-statusefektiä.
     *
     * @param levels hahmon attribuuttien tilat
     * @return montako prosenttia efektin todennäköisyydestä tulee jättää huomiotta
     */
    public float getFearResistance(@NonNull CharacterLevels levels) {
        val charismaModifier = Attribute.Charisma.getFearResistanceModifier(levels.getAttributeLevel(Attribute.CHARISMA));
        val wisdomModifier = Attribute.Wisdom.getFearResistance(levels.getAttributeLevel(Attribute.WISDOM));
        return Math.min(
            CharacterAttributes.FEAR_RESISTANCE_CAP,
            (this.baseFearResistance + wisdomModifier) * (1.0f + charismaModifier));
    }


    /**
     * Hakee hahmon toimintopisteiden määrän. Määrä riippuu hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon toimintopisteiden määrä, kaikki määreet huomioiden
     */
    public int getActionPoints(@NonNull CharacterLevels levels) {
        val base = this.baseActionPoints + Attribute.Dexterity.getActionPoints(levels.getAttributeLevel(Attribute.DEXTERITY));
        val bonus = Attribute.Wisdom.getActionPoints(levels.getAttributeLevel(Attribute.WISDOM));
        return base + (bonus > 0 ? bonus : 0);
    }

    /**
     * Laskee hahmon vahinkopisteiden määrän.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon vahinkopisteiden määrä, kaikki määreet huomioiden
     */
    public float getAttackDamage(@NonNull CharacterLevels levels) {
        val base = this.baseAttackDamage + levels.getXpLevel() * this.attackDamageGain;
        val strengthModifier = Attribute.Strength.getDamageModifier(levels.getAttributeLevel(Attribute.STRENGTH));
        return base * (1.0f + strengthModifier);
    }

    /**
     * Laskee kriittisen osuman todennäköisyyden.
     *
     * @param levels hahmon attribuuttien tasot
     * @return todennäköisyys että hahmo tekee kriittisen osuman
     */
    public float getCriticalHitChance(@NonNull CharacterLevels levels) {
        val luckBonus = Attribute.Luck.getCriticalHitChance(levels.getAttributeLevel(Attribute.LUCK));
        return this.baseCriticalHitChance + luckBonus;
    }

    /**
     * Laskee kriittisen osuman vahinkokertoimen.
     *
     * @param levels hahmon attribuuttien tasot
     * @return todennäköisyys että hahmo tekee kriittisen osuman
     */
    public float getCriticalHitDamage(@NonNull CharacterLevels levels) {
        val wisdomModifier = Attribute.Wisdom.getCriticalHitDamageModifier(levels.getAttributeLevel(Attribute.WISDOM));
        return this.baseCriticalHitDamage + (1.0f + wisdomModifier);
    }

    @Getter private final int moveCost = 1;
    @Getter private final int moveCooldown = 0;
    @Getter private final int attackCost = 1;
    private final int baseActionPoints;
    private final int baseHealthRegenDelay;

    private final int baseArmor;
    private final float armorGain;
    private final float baseEvasion;
    private final float evasionGain;

    private final float baseKnockbackResistance;
    private final float baseFearResistance;

    private final float baseAttackDamage;
    private final float attackDamageGain;
    private final float baseCriticalHitChance;
    private final float baseCriticalHitDamage;

    private final float baseHealth;
    private final float healthGain;
    private final float baseHealthRegen;
    private final float healthRegenGain;
}
