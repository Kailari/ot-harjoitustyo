package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.util.Random;
import java.util.stream.StreamSupport;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public class CharacterObject extends GameObject implements IHealthHandler {
    @Getter private transient int turnsTaken;
    @Getter private transient long deathTime;
    private transient final Random random;

    // Overrides IHealthHandler get/setHealth
    @Getter(onMethod = @__({@Override})) @Setter(onMethod = @__({@Override}))
    private float health;

    @Getter private final CharacterAbilities abilities;
    @Getter private final CharacterAttributes attributes;
    @Getter private final CharacterLevels levels;
    @Getter private final CharacterInfo info;

    @Getter @Setter private String stateOverride;
    @Getter private boolean panicking;
    @Getter private int panicSourceX;
    @Getter private int panicSourceY;

    /**
     * Hakee pelihahmon tilan.
     *
     * @return merkkijono joka kuvaa hahmon tilaa
     */
    public String getState() {
        if (this.stateOverride != null) {
            return this.stateOverride;
        }

        return getWorld().getObjectManager().isCharactersTurn(this) && getWorld().getObjectManager().getRemainingActionPoints() > 0 ? "active" : "idle";
    }

    /**
     * Asettaa hahmon tilan paniikkiin.
     *
     * @param x paniikin aiheuttajan x-koordinaatti
     * @param y paniikin aiheuttajan y-koordinaatti
     */
    public void panic(int x, int y) {
        LOG.debug("a character is panicking!");
        this.panicking = true;
        this.panicSourceX = x;
        this.panicSourceY = y;
    }

    /**
     * Nostaa hahmon terveyspisteiden määrää.
     *
     * @param amount kuinka paljon terveyspisteitä hahmolle annetaan
     */
    public void heal(float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative!");
        }
        this.health = Math.min(this.attributes.getMaxHealth(), this.health + amount);
    }

    /**
     * Luo uuden hahmon asettaen sille attribuuttien ja tasojen oletusarvot.
     *
     * @param attributes attribuuttien arvot
     * @param levels     attribuuttien tasot
     * @param info       hahmon tiedot
     * @param random     hahmon satunnaislukugeneraattori
     */
    public CharacterObject(
        @NonNull CharacterAttributes attributes,
        @NonNull CharacterLevels levels,
        @NonNull CharacterInfo info,
        @NonNull Random random
    ) {
        this.attributes = attributes;
        this.levels = levels;
        this.info = info;
        this.random = random;

        this.abilities = new CharacterAbilities();

    }

    /**
     * Luo uuden hahmon ja antaa sille annetunmukaiset aloitusattribuutit.
     *
     * @param attributes hahmon attribuutit
     * @param random     hahmon satunnaislukugeneraattori
     */
    public CharacterObject(@NonNull CharacterAttributes attributes, @NonNull Random random) {
        this(attributes, new CharacterLevels(), new CharacterInfo(), random);
    }

    @Override
    public void init() {
        super.init();
        this.levels.init(this);
        this.attributes.init(this);
        this.health = getAttributes().getMaxHealth();

        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            ability.init(this);
            this.abilities.getComponentResponsibleFor(ability).init(this);
        }
    }

    /**
     * Tarkistaa onvatko hahmon terveyspisteet nollassa.
     *
     * @return <code>true</code> jos hahmon terveyspisteet ovat likimain nolla
     */
    @Override
    public boolean isDead() {
        return this.health < 0.000001f;
    }

    /**
     * Kutsutaan kun vuoro alkaa.
     */
    public void beginTurn() {
        this.turnsTaken++;
        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            ability.onBeginTurn();
        }
    }

    /**
     * Päivitysrutiini jota kutsutaan vain hahmon omalla vuorolla.
     *
     * @param turnManager objekti-/vuoromanageri jolla hahmojen vuoroja hallinnoidaan
     *
     * @throws NullPointerException jos vuoromanageri on <code>null</code>
     */
    public void updateOnTurn(@NonNull TurnObjectManager turnManager) {
        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            // Only update allowed abilities (move/end turn) while panicking
            if (this.panicking && !(ability instanceof MoveAbility || ability instanceof EndTurnAbility)) {
                LOG.debug("Skipping ability \"{}\" due to panic", ability.getClass().getSimpleName());
                continue;
            }

            // addAbility signature makes sure that abilities are always compatible with their associated components.
            // Thus, we can sefely ignore this warning.
            // noinspection unchecked
            if (handleAbility(turnManager, ability)) {
                StreamSupport.stream(this.abilities.getAbilitiesSortedByPriority().spliterator(), false)
                    .map((a) -> getAbilities().getComponentResponsibleFor(a))
                    .forEach((c) -> ((IControllerComponent) c).reset());
                break;
            }
        }
    }

    /**
     * Kutsutaan kun vuoro päättyy.
     */
    public void endTurn() {
        handleEndPanic();

        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            handleAbilityEndTurn(ability);
        }
    }

    private void handleEndPanic() {
        if (this.isPanicking()) {
            this.panicking = this.random.nextFloat() > 0.35f;
        }
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> void handleAbilityEndTurn(
        A ability
    ) {
        this.abilities.getComponentResponsibleFor(ability).reset();

        if (ability.isOnCooldown()) {
            ability.reduceCooldownTimer();
        }
    }


    @Override
    public void remove() {
        if (!this.isSpawned()) {
            throw new IllegalStateException("Cannot remove non-spawned character!");
        }

        this.deathTime = System.currentTimeMillis();
        super.remove();
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> boolean handleAbility(
        TurnObjectManager turnManager,
        A ability
    ) {
        if (ability.isOnCooldown()) {
            return false;
        }

        val component = this.abilities.getComponentResponsibleFor(ability);
        component.updateInput(ability);

        val cost = ability.getCost();
        if (cost != -1 && component.wants(ability) && canAfford(turnManager, cost)) {
            if (ability.perform(component)) {
                turnManager.spendActionPoints(cost);
                ability.putOnCooldown();
                component.abilityPerformed(ability);
                return true;
            }
        }

        return false;
    }

    private boolean canAfford(TurnObjectManager turnManager, int cost) {
        return turnManager.getRemainingActionPoints() >= cost;
    }
}