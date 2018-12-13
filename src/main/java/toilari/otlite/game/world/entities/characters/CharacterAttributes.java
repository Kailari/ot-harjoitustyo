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
    private static final float MAX_ARMOR_REDUCTION_PERCENTAGE = 0.95f;

    /**
     * Laskee hahmon maksimiterveyspisteet. Laskeminen tapahtuu kaavalla<br>
     * <code>maxHealth = (baseHealth + level * healthGain) * modifier</code><br>
     * johon modifier-osa saadaan laskemalla<br>
     * <code>modifier = (1.0 + enduranceModifier + vitalityModifier)</code><br>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @return hahmon maksimiterveyspisteet, kaikki määreet huomioonottaen
     */
    public float getMaxHealth() {
        val levels = this.character.getLevels();
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
     * @return hahmon terveyspisteiden palautumisnopeus, kaikki määreet huomioonottaen
     */
    public float getHealthRegen() {
        val levels = this.character.getLevels();
        val base = this.baseHealthRegen + levels.getXpLevel() * this.healthRegenGain;
        val vitalityModifier = Attribute.Vitality.getHealthRegenModifier(levels);
        return base * (1.0f + vitalityModifier);
    }

    /**
     * Laskee hahmon "panssarin" eli vahingonvähennyksen määrän. Laskeminen tapahtuu kaavalla<br>
     * <code>armor = (baseArmor + level * armorGain) * modifier</code><br>
     * johon modifier-osa saadaan laskemalla<br>
     * <code>modifier = (1.0 + enduranceModifier)</code><br>
     * Attribuuttikohtaiset osat riippuvat hahmon attribuuteista.
     *
     * @return hahmon vahingonvähennyksen määrä, kaikki määreet huomioiden
     */
    public float getArmor() {
        val levels = this.character.getLevels();
        val base = this.baseArmor + levels.getXpLevel() * this.armorGain;
        val enduranceBonus = Attribute.Endurance.getArmorModifier(levels);
        return base * (1.0f * enduranceBonus);
    }

    /**
     * Hahmon kyky välttää osumia. Todennäköisyys ettei isku osu hahmoon lainkaan.
     *
     * @return todennäköisyys ettei hahmoon osuta
     */
    public float getEvasion() {
        val levels = this.character.getLevels();
        val base = this.baseEvasion + levels.getXpLevel() * evasionGain;
        val luckBonus = Attribute.Luck.getEvasion(levels);
        return Math.min(
            CharacterAttributes.EVASION_CAP,
            base + luckBonus);
    }

    /**
     * Hahmon kyky vastustaa tönäisyjä.
     *
     * @return montako prosenttia tönäisystä tulee jättää huomiotta
     */
    public float getKnockbackResistance() {
        val levels = this.character.getLevels();
        val strengthModifier = Attribute.Strength.getKnockbackResistance(levels);
        return Math.min(
            CharacterAttributes.KNOCKBACK_RESISTANCE_CAP,
            this.baseKnockbackResistance * (1.0f + strengthModifier));
    }

    /**
     * Hahmon kyky vastustaa "pelko"-statusefektiä.
     *
     * @return montako prosenttia efektin todennäköisyydestä tulee jättää huomiotta
     */
    public float getFearResistance() {
        val levels = this.character.getLevels();
        val charismaModifier = Attribute.Charisma.getFearResistanceModifier(levels);
        val wisdomModifier = Attribute.Wisdom.getFearResistance(levels);
        return Math.min(
            CharacterAttributes.FEAR_RESISTANCE_CAP,
            (this.baseFearResistance + wisdomModifier) * (1.0f + charismaModifier));
    }


    /**
     * Hakee hahmon toimintopisteiden määrän. Määrä riippuu hahmon attribuuteista.
     *
     * @return hahmon toimintopisteiden määrä, kaikki määreet huomioiden
     */
    public int getActionPoints() {
        val levels = this.character.getLevels();
        val base = this.baseActionPoints + Attribute.Dexterity.getActionPoints(levels);
        val bonus = Attribute.Wisdom.getActionPoints(levels);
        return base + (bonus > 0 ? bonus : 0);
    }

    /**
     * Laskee hahmon vahinkopisteiden määrän.
     *
     * @return hahmon vahinkopisteiden määrä, kaikki määreet huomioiden
     */
    public float getAttackDamage() {
        val levels = this.character.getLevels();
        val base = this.baseAttackDamage + levels.getXpLevel() * this.attackDamageGain;
        val strengthModifier = Attribute.Strength.getDamageModifier(levels);
        return base * (1.0f + strengthModifier);
    }

    /**
     * Laskee kriittisen osuman todennäköisyyden.
     *
     * @return todennäköisyys että hahmo tekee kriittisen osuman
     */
    public float getCriticalHitChance() {
        val levels = this.character.getLevels();
        val luckBonus = Attribute.Luck.getCriticalHitChance(levels);
        return this.baseCriticalHitChance + luckBonus;
    }

    /**
     * Laskee kriittisen osuman vahinkokertoimen.
     *
     * @return todennäköisyys että hahmo tekee kriittisen osuman
     */
    public float getCriticalHitDamage() {
        val levels = this.character.getLevels();
        val wisdomModifier = Attribute.Wisdom.getCriticalHitDamageModifier(levels);
        return this.baseCriticalHitDamage + (1.0f + wisdomModifier);
    }

    private transient CharacterObject character;

    @Getter private int moveCost = 1;
    @Getter private int moveCooldown = 0;
    @Getter private int attackCost = 1;
    @Getter private int attackCooldown = 0;
    @Getter private int xpReward = 10;
    private int baseActionPoints;

    private int baseArmor;
    private float armorGain;
    private float baseEvasion;
    private float evasionGain;
    private float baseKnockbackResistance;
    private float baseFearResistance;

    private float baseAttackDamage;
    private float attackDamageGain;
    private float baseCriticalHitChance;
    private float baseCriticalHitDamage;

    private float baseHealth;
    private float healthGain;
    private float baseHealthRegen;
    private float healthRegenGain;

    /**
     * Laskee panssarista aiheutuvan vahinkopisteiden vähennyksen.
     *
     * @param amount vahinkopisteet ennen vähennyksiä
     *
     * @return vähettävien vahinkopisteiden määrä
     */
    public float calculateDamageReduction(float amount) {
        return Math.min(amount * MAX_ARMOR_REDUCTION_PERCENTAGE, getArmor());
    }

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

    /**
     * Alustaa hahmon attribuuttisäilön.
     *
     * @param character hahmo jolle nämä atribuutit kuuluvat
     */
    public void init(CharacterObject character) {
        this.character = character;
    }
}
