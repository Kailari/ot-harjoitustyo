package toilari.otlite.game.world.entities.characters.abilities;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;

/**
 * Hahmon kyky hyökätä.
 */
public abstract class AbstractAttackAbility<A extends AbstractAttackAbility<A, C>, C extends AbstractAttackControllerComponent<A>>
    extends AbstractAbility<A, C>
    implements ITargetedAbility<A, C> {

    @Getter private boolean lastAttackKill;
    @Getter private float lastAttackDamage;

    protected AbstractAttackAbility(@NonNull String name) {
        super(name);
    }

    /**
     * Voiko hyökkäyksen suorittaa annetulle hahmolle annettuun suuntaan. Oletuksena hyväksyy kaikki hahmot paitsi
     * hyökkääjän itsensä ja minkä tahansa suunnan.
     *
     * @param target    kohde johon kyvyn suoritus kohdistetaan
     * @param direction suunta suorittavasta objektista kohteeseen
     * @return
     */
    @Override
    public boolean canPerformOn(GameObject target, @NonNull Direction direction) {
        if (direction == Direction.NONE || !(target instanceof CharacterObject) || target.isRemoved()) {
            return false;
        }

        val character = (CharacterObject) target;
        return !character.isDead() && !getCharacter().equals(character);
    }

    @Override
    public boolean perform(@NonNull C component) {
        val target = component.getTargetSelector().getTarget();
        val direction = component.getTargetSelector().getTargetDirection();
        if (!canPerformOn(target, direction)) {
            return false;
        }

        this.lastAttackKill = false;
        this.lastAttackDamage = 0.0f;

        val amount = calculateDamage();
        if (target instanceof IHealthHandler) {
            this.lastAttackDamage = amount;
            dealDamage(target, (IHealthHandler) target, amount);
        }

        return true;
    }

    protected float calculateDamage() {
        return getCharacter().getAttributes().getAttackDamage(getCharacter().getLevels());
    }

    protected void dealDamage(GameObject target, IHealthHandler targetWithHealth, float amount) {
        float current = targetWithHealth.getHealth();
        targetWithHealth.setHealth(Math.max(0, current - amount));

        if (targetWithHealth.isDead()) {
            targetWithHealth.setHealth(0.0f);
            target.remove();
            this.lastAttackKill = true;
        }

        if (target instanceof CharacterObject) {
            getEventSystem().fire(new CharacterEvent.Damage(getCharacter(), target, amount));
        }
    }
}
