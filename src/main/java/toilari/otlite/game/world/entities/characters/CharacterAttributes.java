package toilari.otlite.game.world.entities.characters;

import lombok.*;

/**
 * Hahmon attribuutit. Hahmokohtaiset perusmääreet jotka määrittävät hahmon kyvyt.
 */
@NoArgsConstructor
@AllArgsConstructor
public class CharacterAttributes {
    private static final float FEAR_RESISTANCE_CAP = 0.95f;
    private static final float KNOCKBACK_RESISTANCE_CAP = 0.975f;
    private static final float EVASION_CAP = 0.9f;

    /**
     * Laskee hahmon maksimiterveyspisteet. Laskeminen tapahtuu kaavalla<br>
     * <code>maxHealth = (baseHealth + level * healthGain) * modifier</code><br>
     * johon modifier-osa saadaan laskemalla<br>
     * <code>modifier = (1.0 + enduranceModifier + vitalityModifier)</code><br>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon maksimiterveyspisteet, kaikki määreet huomioonottaen
     */
    public float getMaxHealth(@NonNull CharacterLevels levels) {
        val base = this.baseHealth + levels.getXpLevel() * this.healthGain;
        val enduranceModifier = Attribute.Endurance.getHealthModifier(levels);
        val vitalityModifier = Attribute.Vitality.getHealthModifier(levels);
        return base * (1.0f + enduranceModifier + vitalityModifier);
    }

    /**
     * Laskee hahmon terveyspisteiden palautumisnopeuden. Laskeminen tapahtuu kaavalla<br>
     * <code>regen = (baseRegen + level * regenGain) * modifier</code><br>
     * johon modifier-osa saadaan laskemalla<br>
     * <code>modifier = (1.0 + vitalityModifier)</code><br>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon terveyspisteiden palautumisnopeus, kaikki määreet huomioonottaen
     */
    public float getHealthRegen(@NonNull CharacterLevels levels) {
        val base = this.baseHealthRegen + levels.getXpLevel() * this.healthRegenGain;
        val vitalityModifier = Attribute.Vitality.getHealthRegenModifier(levels);
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
        val vitalityPart = Attribute.Vitality.getHealthRegenDelay(levels);
        return this.baseHealthRegenDelay + vitalityPart;
    }

    /**
     * Laskee hahmon "panssarin" eli vahingonvähennyksen määrän. Laskeminen tapahtuu kaavalla<br>
     * <code>armor = (baseArmor + level * armorGain) * modifier</code><br>
     * johon modifier-osa saadaan laskemalla<br>
     * <code>modifier = (1.0 + enduranceModifier)</code><br>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @param levels hahmon attribuuttien tasot
     * @return hahmon vahingonvähennyksen määrä, kaikki määreet huomioiden
     */
    public float getArmor(@NonNull CharacterLevels levels) {
        val base = this.baseArmor + levels.getXpLevel() * this.armorGain;
        val enduranceBonus = Attribute.Endurance.getArmorModifier(levels);
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
        val luckBonus = Attribute.Luck.getEvasion(levels);
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
        val strengthModifier = Attribute.Strength.getKnockbackResistance(levels);
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
        val charismaModifier = Attribute.Charisma.getFearResistanceModifier(levels);
        val wisdomModifier = Attribute.Wisdom.getFearResistance(levels);
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
        val base = this.baseActionPoints + Attribute.Dexterity.getActionPoints(levels);
        val bonus = Attribute.Wisdom.getActionPoints(levels);
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
        val strengthModifier = Attribute.Strength.getDamageModifier(levels);
        return base * (1.0f + strengthModifier);
    }

    /**
     * Laskee kriittisen osuman todennäköisyyden.
     *
     * @param levels hahmon attribuuttien tasot
     * @return todennäköisyys että hahmo tekee kriittisen osuman
     */
    public float getCriticalHitChance(@NonNull CharacterLevels levels) {
        val luckBonus = Attribute.Luck.getCriticalHitChance(levels);
        return this.baseCriticalHitChance + luckBonus;
    }

    /**
     * Laskee kriittisen osuman vahinkokertoimen.
     *
     * @param levels hahmon attribuuttien tasot
     * @return todennäköisyys että hahmo tekee kriittisen osuman
     */
    public float getCriticalHitDamage(@NonNull CharacterLevels levels) {
        val wisdomModifier = Attribute.Wisdom.getCriticalHitDamageModifier(levels);
        return this.baseCriticalHitDamage + (1.0f + wisdomModifier);
    }

    @Getter private int moveCost = 1;

    @Getter private int moveCooldown = 0;
    @Getter private int attackCost = 1;
    @Getter private int attackCooldown = 0;
    @Getter private int xpReward = 1;

    private int baseActionPoints = 0;

    private int baseHealthRegenDelay = 0;
    private int baseArmor = 0;

    private float armorGain = 0.05f;
    private float baseEvasion = 0.01f;
    private float evasionGain = 0.001f;
    private float baseKnockbackResistance = 0.0f;

    private float baseFearResistance = 0.0f;
    private float baseAttackDamage = 1.0f;

    private float attackDamageGain = 0.01f;
    private float baseCriticalHitChance = 0.01f;
    private float baseCriticalHitDamage = 0.30f;
    private float baseHealth = 10.0f;

    private float healthGain = 0.01f;
    private float baseHealthRegen = 0.50f;
    private float healthRegenGain = 0.01f;

    /**
     * Kopioi attribuutit uuteen instanssiin.
     *
     * @param attributes templaatti josta kopioidaan
     */
    public CharacterAttributes(@NonNull CharacterAttributes attributes) {
        this.moveCost = attributes.moveCost;
        this.moveCooldown = attributes.moveCooldown;
        this.attackCost = attributes.attackCost;
        this.attackCooldown = attributes.attackCooldown;
        this.xpReward = attributes.xpReward;
        this.baseActionPoints = attributes.baseActionPoints;
        this.baseHealthRegenDelay = attributes.baseHealthRegenDelay;
        this.baseArmor = attributes.baseArmor;
        this.armorGain = attributes.armorGain;
        this.baseEvasion = attributes.baseEvasion;
        this.evasionGain = attributes.evasionGain;
        this.baseKnockbackResistance = attributes.baseKnockbackResistance;
        this.baseFearResistance = attributes.baseFearResistance;
        this.baseAttackDamage = attributes.baseAttackDamage;
        this.attackDamageGain = attributes.attackDamageGain;
        this.baseCriticalHitChance = attributes.baseCriticalHitChance;
        this.baseCriticalHitDamage = attributes.baseCriticalHitDamage;
        this.baseHealth = attributes.baseHealth;
        this.healthGain = attributes.healthGain;
        this.baseHealthRegen = attributes.baseHealthRegen;
        this.healthRegenGain = attributes.healthRegenGain;
    }
}
