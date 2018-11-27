package toilari.otlite.game.world.entities.characters.abilities;

import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;

/**
 * Hahmon kyky liikkua.
 */
public class MoveAbility extends AbstractAbility<MoveAbility, MoveControllerComponent> {
    /**
     * Tarkistaa voiko hahmo liikkua annettuun suuntaan.
     *
     * @param direction suunta johon liikutaan
     * @param tiles     montako ruutua siirrytään
     * @return <code>true</code> jos liikkuminen on mahdollista
     */
    public boolean canMoveTo(Direction direction, int tiles) {
        if (tiles == 0) {
            return false;
        }

        int newX = getCharacter().getTileX() + direction.getDx();
        int newY = getCharacter().getTileY() + direction.getDy();

        val world = getCharacter().getWorld();
        if (!world.getCurrentLevel().isWithinBounds(newX, newY)) {
            return false;
        }

        val tileAtTarget = world.getCurrentLevel().getTileAt(newX, newY);
        val objectAtTarget = world.getObjectAt(newX, newY);

        val tileIsWalkable = !tileAtTarget.isWall();

        if (tileIsWalkable) {
            return objectAtTarget == null || objectAtTarget.isRemoved();
        }

        return false;
    }

    @Override
    public int getCooldownLength() {
        return getCharacter().getAttributes().getMoveCooldown();
    }

    @Override
    public int getCost() {
        return getCharacter().getAttributes().getMoveCost();
    }

    @Override
    public boolean perform(MoveControllerComponent component) {
        val direction = component.getInputDirection();

        if (canMoveTo(direction, 1)) {
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
