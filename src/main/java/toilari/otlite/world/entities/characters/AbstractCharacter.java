package toilari.otlite.world.entities.characters;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.world.Tile;
import toilari.otlite.world.entities.GameObject;
import toilari.otlite.world.entities.TurnObjectManager;
import toilari.otlite.world.entities.characters.controller.CharacterController;

/**
 * Hahmo pelimaailmassa.
 */
public abstract class AbstractCharacter extends GameObject {
    @NonNull private final CharacterController controller;

    public AbstractCharacter(@NonNull CharacterController controller) {
        this.controller = controller;
        this.controller.takeControl(this);
    }

    @Override
    public void update() {
        super.update();
    }

    public void updateOnOwnTurn(TurnObjectManager turnManager) {
        val inputX = Math.max(-1, Math.min(1, this.controller.getInputX()));
        var inputY = Math.max(-1, Math.min(1, this.controller.getInputY()));
        if (inputX != 0) {
            inputY = 0;
        }

        if (move(inputX * Tile.SIZE_IN_WORLD, inputY * Tile.SIZE_IN_WORLD)) {
            turnManager.endTurn();
        }
    }

    protected boolean move(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = Math.max(0, Math.min(getX() + dx, (getWorld().getCurrentLevel().getWidth() - 1) * Tile.SIZE_IN_WORLD));
        int newY = Math.max(0, Math.min(getY() + dy, (getWorld().getCurrentLevel().getHeight() - 1) * Tile.SIZE_IN_WORLD));
        if (!getWorld().getCurrentLevel().getTileAt(newX / Tile.SIZE_IN_WORLD, newY / Tile.SIZE_IN_WORLD).isWall()) {
            setX(newX);
            setY(newY);
            return true;
        }

        return false;
    }
}