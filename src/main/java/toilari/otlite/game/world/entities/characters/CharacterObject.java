package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.util.stream.StreamSupport;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public class CharacterObject extends GameObject implements IHealthHandler {
    @Getter private transient int turnsTaken;
    @Getter private transient long deathTime;

    // Overrides IHealthHandler get/setHealth
    @Getter(onMethod = @__({@Override})) @Setter(onMethod = @__({@Override}))
    private float health;

    @Getter private final CharacterAbilities abilities;
    @Getter private final CharacterAttributes attributes;
    @Getter private final CharacterLevels levels;
    @Getter private final CharacterInfo info;

    /**
     * Luo uuden hahmon asettaen sille attribuuttien ja tasojen oletusarvot.
     *
     * @param attributes attribuuttien arvot
     * @param levels     attribuuttien tasot
     * @param info       hahmon tiedot
     */
    public CharacterObject(@NonNull CharacterAttributes attributes, @NonNull CharacterLevels levels, CharacterInfo info) {
        this.attributes = attributes;
        this.levels = levels;

        this.abilities = new CharacterAbilities();
        this.info = info;
    }

    /**
     * Luo uuden hahmon ja antaa sille annetunmukaiset aloitusattribuutit.
     *
     * @param attributes hahmon attribuutit
     */
    public CharacterObject(@NonNull CharacterAttributes attributes) {
        this.attributes = attributes;
        this.levels = new CharacterLevels();
        this.info = new CharacterInfo();
        this.abilities = new CharacterAbilities();
    }

    @Override
    public void init() {
        super.init();
        this.health = getAttributes().getMaxHealth(this.levels);

        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            ability.init(this, 0);
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
                StreamSupport.stream(this.abilities.getAbilitiesSortedByPriority().spliterator(), false)
                    .map(a -> getAbilities().getComponentResponsibleFor(a))
                    .forEach(c -> ((IControllerComponent) c).reset());

                break;
            }
        }
    }

    /**
     * Kutsutaan kun vuoro päättyy.
     */
    public void endTurn() {
        for (val ability : this.abilities.getAbilitiesSortedByPriority()) {
            this.abilities.getComponentResponsibleFor(ability).reset();

            if (ability.isOnCooldown()) {
                ability.reduceCooldownTimer();
            }
        }
    }


    @Override
    public void remove() {
        this.deathTime = System.currentTimeMillis();
        super.remove();
        getWorld().getObjectManager().getEventSystem().fire(new CharacterEvent.Died(this));
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> boolean handleAbility(@NonNull TurnObjectManager turnManager, @NonNull A ability) {
        if (ability.isOnCooldown()) {
            return false;
        }

        val component = this.abilities.getComponentResponsibleFor(ability);
        component.updateInput(ability);

        val cost = ability.getCost();
        if (component.wants(ability) && canAfford(turnManager, cost)) {
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