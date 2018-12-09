package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;

import java.util.Random;

/**
 * Hahmon hyökkäyskyky jolla hahmo voi potkia muita hahmoja tehden vahinkoa ja tönäisten niitä taaksepäin.
 */
public class KickAbility extends AbstractAttackAbility<KickAbility, AbstractAttackControllerComponent<KickAbility>> {
    private final Random random;

    /**
     * Luo uuden kyvyn.
     */
    public KickAbility() {
        this(new Random());
    }

    /**
     * Luo uuden kyvyn.
     *
     * @param seed pseudosatunnaislukugeneraattorin siemenluku
     */
    public KickAbility(long seed) {
        this(new Random(seed));
    }

    private KickAbility(@NonNull Random random) {
        super("Kick");
        this.random = random;
    }

    @Override
    public boolean canPerformOn(GameObject target, @NonNull Direction direction) {
        return getCharacter().getLevels().getAttributeLevel(Attribute.STRENGTH) >= 2 && super.canPerformOn(target, direction);
    }

    @Override
    public int getCost() {
        return Attribute.Strength.getKickCost(getCharacter().getLevels());
    }

    @Override
    public int getCooldownLength() {
        return Attribute.Strength.getKickCooldown(getCharacter().getLevels());
    }

    @Override
    public boolean perform(@NonNull AbstractAttackControllerComponent<KickAbility> component) {
        val target = component.getTargetSelector().getTarget();
        val direction = component.getTargetSelector().getTargetDirection();
        if (!canPerformOn(target, direction)) {
            return false;
        }

        if (tileBehindTargetIsFree(direction, 1)) {
            if (target instanceof IHealthHandler) {
                dealDamage(target, (IHealthHandler) target, calculateDamage() * 0.5f);
                if (((IHealthHandler) target).isDead()) {
                    return true;
                }
            }

            int knockbackAmount = calculateKnockbackAmount(component.getTargetSelector().getTargetDirection());
            knockBackTarget(target, direction, knockbackAmount);
        } else {
            dealDamage(target, (IHealthHandler) target, calculateDamage() * 1.5f);
        }

        return true;
    }

    private boolean tileBehindTargetIsFree(Direction direction, int delta) {
        val targetX = getCharacter().getTileX() + direction.getDx() * (1 + delta);
        val targetY = getCharacter().getTileY() + direction.getDy() * (1 + delta);

        val objectInTarget = getCharacter().getWorld().getObjectAt(targetX, targetY);
        val tileInTarget = getCharacter().getWorld().getTileAt(targetX, targetY);

        return objectInTarget == null && !tileInTarget.isWall();
    }

    private void knockBackTarget(GameObject target, Direction direction, int knockbackAmount) {
        if (knockbackAmount <= 0) {
            return;
        }

        val oldX = target.getTileX();
        val oldY = target.getTileY();
        val newX = oldX + direction.getDx() * knockbackAmount;
        val newY = oldY + direction.getDy() * knockbackAmount;
        target.setTilePos(newX, newY);

        if (target instanceof CharacterObject) {
            target.getWorld().getTileAt(oldX, oldY).onCharacterExit(oldX, oldY, (CharacterObject) target);
            target.getWorld().getTileAt(newX, newY).onCharacterEnter(newX, newY, (CharacterObject) target);
        }
    }

    private int calculateKnockbackAmount(Direction direction) {
        val min = Attribute.Strength.getKickKnockbackMin(getCharacter().getLevels());
        val max = numberOfFreeTilesInDirection(direction, Attribute.Strength.getKickKnockbackMax(getCharacter().getLevels()));
        return Math.round(min + (this.random.nextFloat() * (max - min)));
    }

    private int numberOfFreeTilesInDirection(Direction direction, int max) {
        for (int distance = 1; distance <= max; distance++) {
            if (!tileBehindTargetIsFree(direction, distance)) {
                return distance - 1;
            }
        }

        return max;
    }
}
