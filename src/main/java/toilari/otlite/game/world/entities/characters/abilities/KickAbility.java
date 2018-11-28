package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.KickControllerComponent;

public class KickAbility extends AbstractAbility<KickAbility, KickControllerComponent> {
    public boolean canKick(@NonNull Direction direction) {
        if (direction == Direction.NONE) {
            return false;
        }

        return hasTarget(direction) && tileBehindTargetIsFree(direction);
    }

    private boolean tileBehindTargetIsFree(Direction direction) {
        val targetX = getCharacter().getTileX() + direction.getDx() * 2;
        val targetY = getCharacter().getTileY() + direction.getDy() * 2;

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
        return 1;
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }

    @Override
    public boolean perform(@NonNull KickControllerComponent component) {
        if (!canKick(component.getTargetDirection()) || component.getTarget() == null) {
            return false;
        }

        val oldX = component.getTarget().getTileX();
        val oldY = component.getTarget().getTileY();
        val newX = oldX + component.getTargetDirection().getDx();
        val newY = oldY + component.getTargetDirection().getDy();
        component.getTarget().setTilePos(newX, newY);

        if (component.getTarget() instanceof CharacterObject) {
            component.getTarget().getWorld().getTileAt(oldX, oldY).onCharacterExit(oldX, oldY, (CharacterObject) component.getTarget());
            component.getTarget().getWorld().getTileAt(newX, newY).onCharacterEnter(newX, newY, (CharacterObject) component.getTarget());
        }

        return true;
    }
}
