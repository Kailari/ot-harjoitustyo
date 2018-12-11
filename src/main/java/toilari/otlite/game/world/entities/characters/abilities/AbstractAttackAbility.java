package toilari.otlite.game.world.entities.characters.abilities;

import lombok.*;
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

    private boolean canTargetSelf = false;

    @Getter private boolean lastAttackKill;
    @Getter private float lastAttackDamage;

    @Override
    public boolean canTargetSelf() {
        return this.canTargetSelf;
    }

    protected AbstractAttackAbility(@NonNull String name) {
        super(name);
    }

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
