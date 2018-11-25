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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public abstract class AbstractCharacter extends GameObject {
    @Getter @Setter private transient float health;
    @Getter private transient CharacterController controller;
    @Getter private transient long lastAttackTime;
    @Getter private transient float lastAttackAmount;
    @Getter private transient AbstractCharacter lastAttackTarget;

    @Getter private final CharacterAttributes attributes;
    @Getter private final CharacterLevels levels;
    @Getter private final List<IAbility> abilities;

    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> void addAbility(A ability, IControllerComponent<A> component) {
        this.abilities.add(ability);
        this.controller.registerComponent(ability, component);
    }

    /**
     * Hakee toimintoluokkaa vastaavan ohjainkomponentin jos tällä hahmolla on yhteensopiva toiminto.
     *
     * @param abilityClass toiminnon luokka
     * @param <A>          toiminnon tyyppi
     * @param <C>          ohjainkomponentin tyyppi
     * @return <code>null</code> jos komponenttia ei löydy, muulloin löydetty komponentti
     */
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> C getComponent(Class<? extends A> abilityClass) {
        for (val ability : this.abilities) {
            if (ability.getClass().equals(abilityClass)) {
                // The horrific addAbility signature makes sure that this operation is actually checked as long as
                // system isn't purposedly tricked using type-casting magic to believing that incompatible types are
                // compatible. Thus if this line throws, it's an error somewhere else.
                // noinspection unchecked
                return (C) this.controller.getComponentFor(ability);
            }
        }

        return null;
    }

    protected AbstractCharacter(@NonNull CharacterAttributes attributes) {
        this.attributes = attributes;
        this.abilities = new ArrayList<>();
        this.levels = new CharacterLevels();
        this.controller = new CharacterController(this);
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
        if (this.controller == null) {
            return;
        }

        this.abilities.sort(Comparator.comparingInt(IAbility::getPriority));
        for (val ability : this.abilities) {
            // addAbility signature makes sure that abilities are always compatible with their associated components.
            // Thus, we can sefely ignore this warning.
            // noinspection unchecked
            if (handleAbility(turnManager, ability)) {
                break;
            }
        }
    }

    public void updateAfterTurn() {
        for (val ability : this.abilities) {
            if (ability.isOnCooldown()) {
                ability.reduceCooldownTimer();
            }
        }
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> boolean handleAbility(@NonNull TurnObjectManager turnManager, @NonNull A ability) {
        if (ability.isOnCooldown()) {
            return false;
        }

        val component = this.controller.getComponentFor(ability);
        component.updateInput();

        val cost = ability.getCost();
        if (component.wants() && canAfford(turnManager, cost)) {
            if (ability.perform(this.controller, component)) {
                turnManager.spendActionPoints(cost);
                ability.setOnCooldown();
                return true;
            }
        }

        return false;
    }

    private boolean canAfford(TurnObjectManager turnManager, int cost) {
        return turnManager.getRemainingActionPoints() >= cost;
    }
}