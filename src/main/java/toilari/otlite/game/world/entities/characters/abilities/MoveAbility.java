package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;
import toilari.otlite.game.world.entities.characters.CharacterController;

public class MoveAbility extends AbstractAbility<MoveAbility, MoveControllerComponent> {
    public MoveAbility(@NonNull AbstractCharacter character, int priority) {
        super(character, priority);
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }

    @Override
    public int getCost() {
        return getCharacter().getAttributes().getMoveCost();
    }

    @Override
    public boolean perform(CharacterController controller, MoveControllerComponent component) {
        val direction = component.getInputDirection();

        if (component.canMoveTo(direction, 1)) {
            // No need to bound-check, it is already performed in canMoveTo()
            int oldX = getCharacter().getTileX();
            int oldY = getCharacter().getTileY();
            int newX = oldX + direction.getDx();
            int newY = oldY + direction.getDy();

            getCharacter().setTilePos(newX, newY);

            getCharacter().getWorld().getTileAt(oldX, oldY).onCharacterExit(oldX, oldY, getCharacter());
            getCharacter().getWorld().getTileAt(newX, newY).onCharacterEnter(newX, newY, getCharacter());

            return true;
        }

        return false;
    }
}
