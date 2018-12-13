package toilari.otlite.game.world.entities.characters.abilities;

import lombok.*;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;

import java.util.Random;

/**
 * Hahmon kyky hyökätä.
 */
public abstract class AbstractAttackAbility<A extends AbstractAttackAbility<A, C>, C extends AbstractAttackControllerComponent<A>>
    extends AbstractAbility<A, C>
    implements ITargetedAbility<A, C> {

    @Getter private transient Random random;
    @Getter @Setter(AccessLevel.PROTECTED) private transient boolean lastAttackCritical;
    @Getter @Setter(AccessLevel.PROTECTED) private transient boolean lastAttackKill;
    @Getter @Setter(AccessLevel.PROTECTED) private transient float lastAttackDamage;

    private boolean canTargetSelf = false;

    @Override
    public boolean canTargetSelf() {
        return this.canTargetSelf;
    }

    protected AbstractAttackAbility(@NonNull String name) {
        this(name, new Random());
    }

    protected AbstractAttackAbility(@NonNull String name, Random random) {
        super(name);
        this.random = random;
    }

    @Override
    public void init(@NonNull CharacterObject character) {
        super.init(character);
        if (this.random == null) {
            this.random = new Random();
        }
    }

    @Override
    public boolean canPerformOn(GameObject target, @NonNull Direction direction) {
        if (direction == Direction.NONE || !(target instanceof CharacterObject) || target.isRemoved()) {
            return false;
        }

        val character = (CharacterObject) target;
        return !character.isDead() && !getCharacter().equals(character);
    }

    protected void reset() {
        this.lastAttackKill = false;
        this.lastAttackCritical = false;
        this.lastAttackDamage = 0.0f;
    }

    @Override
    public boolean perform(@NonNull C component) {
        val target = component.getTargetSelector().getTarget();
        val direction = component.getTargetSelector().getTargetDirection();
        if (!canPerformOn(target, direction)) {
            return false;
        }

        reset();

        if (target instanceof CharacterObject) {
            if (targetEvadesAttack((CharacterObject) target, this.random.nextFloat())) {
                getEventSystem().fire(new CharacterEvent.MissedAttack(getCharacter(), (CharacterObject) target));
                return true;
            }
        }

        val amount = calculateDamage(target);
        if (target instanceof IHealthHandler) {
            this.lastAttackDamage = amount;
            dealDamage(target, (IHealthHandler) target, amount);
        }

        return true;
    }

    protected boolean targetEvadesAttack(CharacterObject target, float randomValue) {
        val evasion = target.getAttributes().getEvasion();
        return randomValue < evasion;
    }

    protected boolean hitsCritically(float randomValue) {
        val chance = getCharacter().getAttributes().getCriticalHitChance();
        return randomValue < chance;
    }

    protected float calculateDamage(GameObject target) {
        var rawAmount = getCharacter().getAttributes().getAttackDamage();
        if (hitsCritically(this.random.nextFloat())) {
            this.lastAttackCritical = true;
            rawAmount = calculateCriticalDamage(rawAmount);
        }

        if (target instanceof CharacterObject) {
            val reduction = ((CharacterObject) target).getAttributes().calculateDamageReduction(rawAmount);
            return rawAmount - reduction;
        } else {
            return rawAmount;
        }
    }

    protected float calculateCriticalDamage(float rawAmount) {
        val multiplier = getCriticalHitDamageMultiplier();
        return rawAmount * multiplier;
    }

    protected float getCriticalHitDamageMultiplier() {
        return getCharacter().getAttributes().getCriticalHitDamage();
    }

    protected void dealDamage(GameObject target, IHealthHandler targetWithHealth, float amount) {
        float current = targetWithHealth.getHealth();
        targetWithHealth.setHealth(Math.max(0, current - amount));

        this.lastAttackKill = false;
        if (targetWithHealth.isDead()) {
            targetWithHealth.setHealth(0.0f);
            target.remove();
            this.lastAttackKill = true;
        }

        if (target instanceof CharacterObject) {
            getEventSystem().fire(new CharacterEvent.Damage(getCharacter(), target, amount, this.lastAttackCritical));
            if (this.lastAttackKill) {
                getEventSystem().fire(new CharacterEvent.Death((CharacterObject) target, CharacterEvent.Death.Cause.CHARACTER));
            }
        }
    }
}
