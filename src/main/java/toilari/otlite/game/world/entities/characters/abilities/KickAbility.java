package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.KickControllerComponent;

import java.util.Random;

public class KickAbility extends AbstractAbility<KickAbility, KickControllerComponent> {
    private final Random random;

    public KickAbility() {
        this.random = new Random();
    }

    public KickAbility(long seed) {
        this.random = new Random(seed);
    }


    /**
     * Kokeilee voiko hahmo potkaista annettuun suuntaan.
     *
     * @param direction suunta johon potkaistaan
     * @return <code>true</code> jos hahmo voi potkaista, muulloin <code>false</code>
     */
    public boolean canKick(@NonNull Direction direction) {
        if (getCharacter().getLevels().getAttributeLevel(Attribute.STRENGTH) < 2) {
            return false;
        }

        if (direction == Direction.NONE) {
            return false;
        }

        return hasTarget(direction) && tileBehindTargetIsFree(direction, 1);
    }

    private boolean tileBehindTargetIsFree(Direction direction, int delta) {
        val targetX = getCharacter().getTileX() + direction.getDx() * (1 + delta);
        val targetY = getCharacter().getTileY() + direction.getDy() * (1 + delta);

        val objectInTarget = getCharacter().getWorld().getObjectAt(targetX, targetY);
        val tileInTarget = getCharacter().getWorld().getTileAt(targetX, targetY);

        return objectInTarget == null && !tileInTarget.isWall();
    }

    private boolean hasTarget(@NonNull Direction targetDirection) {
        val targetX = getCharacter().getTileX() + targetDirection.getDx();
        val targetY = getCharacter().getTileY() + targetDirection.getDy();
        val objectInDirection = getCharacter().getWorld().getObjectAt(targetX, targetY);
        if (objectInDirection instanceof CharacterObject && !objectInDirection.isRemoved()) {
            val targetCharacter = (CharacterObject) objectInDirection;
            return !targetCharacter.isDead();
        }

        return false;
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
    public boolean perform(@NonNull KickControllerComponent component) {
        if (!canKick(component.getTargetDirection()) || component.getTarget() == null) {
            return false;
        }

        int knockbackAmount = calculateKnockbackAmount(component);
        val oldX = component.getTarget().getTileX();
        val oldY = component.getTarget().getTileY();
        val newX = oldX + component.getTargetDirection().getDx() * knockbackAmount;
        val newY = oldY + component.getTargetDirection().getDy() * knockbackAmount;
        component.getTarget().setTilePos(newX, newY);

        if (component.getTarget() instanceof CharacterObject) {
            component.getTarget().getWorld().getTileAt(oldX, oldY).onCharacterExit(oldX, oldY, (CharacterObject) component.getTarget());
            component.getTarget().getWorld().getTileAt(newX, newY).onCharacterEnter(newX, newY, (CharacterObject) component.getTarget());
        }

        return true;
    }

    private int calculateKnockbackAmount(@NonNull KickControllerComponent component) {
        val min = Attribute.Strength.getKickKnockbackMin(getCharacter().getLevels());
        val max = numberOfFreeTilesInDirection(component.getTargetDirection(), Attribute.Strength.getKickKnockbackMax(getCharacter().getLevels()));
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
