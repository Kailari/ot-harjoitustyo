package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public abstract class AbstractCharacter extends GameObject {
    @Getter private transient int turnsTaken;

    @Getter @Setter private float health;
    @Getter private final CharacteAbilities abilities;
    @Getter private final CharacterAttributes attributes;
    @Getter private final CharacterLevels levels;

    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> void addAbility(A ability, IControllerComponent<A> component) {
        this.abilities.addAbility(ability, component);
    }

    protected AbstractCharacter(@NonNull CharacterAttributes attributes) {
        this.attributes = attributes;
        this.levels = new CharacterLevels();
        this.abilities = new CharacteAbilities();
    }

    @Override
    public void init() {
        super.init();
        this.health = getAttributes().getMaxHealth(this.levels);
    }

    /**
     * Tarkistaa onvatko hahmon terveyspisteet nollassa.
     *
     * @return <code>true</code> jos hahmon terveyspisteet ovat likimain nolla
     */
    public boolean isDead() {
        return this.health < 0.000001f;
    }

    /**
     * Päivitysrutiini jota kutsutaan vain hahmon omalla vuorolla.
     *
     * @param turnManager objekti-/vuoromanageri jolla hahmojen vuoroja hallinnoidaan
     * @throws NullPointerException jos vuoromanageri on <code>null</code>
     */
    public void updateOnTurn(@NonNull TurnObjectManager turnManager) {
        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            // addAbility signature makes sure that abilities are always compatible with their associated components.
            // Thus, we can sefely ignore this warning.
            // noinspection unchecked
            if (handleAbility(turnManager, ability)) {
                break;
            }
        }
    }

    public void updateAfterTurn() {
        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            if (ability.isOnCooldown()) {
                ability.reduceCooldownTimer();
            }
        }
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> boolean handleAbility(@NonNull TurnObjectManager turnManager, @NonNull A ability) {
        if (ability.isOnCooldown()) {
            return false;
        }

        val component = this.abilities.getComponentResponsibleFor(ability);
        component.updateInput();

        val cost = ability.getCost();
        if (component.wants() && canAfford(turnManager, cost)) {
            if (ability.perform(component)) {
                turnManager.spendActionPoints(cost);
                ability.putOnCooldown();
                return true;
            }
        }

        return false;
    }

    private boolean canAfford(TurnObjectManager turnManager, int cost) {
        return turnManager.getRemainingActionPoints() >= cost;
    }

    /**
     * Kutsutaan kun ohjatun hahmon vuoro alkaa.
     */
    public void beginTurn() {
        this.turnsTaken++;
    }
}