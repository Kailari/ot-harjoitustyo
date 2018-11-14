package toilari.otlite.world.entities.characters;

import lombok.val;
import toilari.otlite.io.Input;
import toilari.otlite.io.Key;
import toilari.otlite.world.Tile;

public class PlayerCharacter extends AbstractCharacter {
    private boolean canMove;

    @Override
    public void update() {
        super.update();

        if (this.canMove) {
            val right = Input.getHandler().isKeyDown(Key.RIGHT) ? 1 : 0;
            val left = Input.getHandler().isKeyDown(Key.LEFT) ? -1 : 0;
            val up = Input.getHandler().isKeyDown(Key.UP) ? -1 : 0;
            val down = Input.getHandler().isKeyDown(Key.DOWN) ? 1 : 0;
            if (move((right + left) * Tile.SIZE_IN_WORLD, (down + up) * Tile.SIZE_IN_WORLD)) {
                this.canMove = false;
            }
        } else if (Input.getHandler().isKeyUp(Key.RIGHT)
            && Input.getHandler().isKeyUp(Key.LEFT)
            && Input.getHandler().isKeyUp(Key.UP)
            && Input.getHandler().isKeyUp(Key.DOWN)) {
            this.canMove = true;
        }
    }

    public boolean move(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = Math.max(0, Math.min(getX() + dx, (getWorld().getCurrentLevel().getWidth() - 1) * Tile.SIZE_IN_WORLD));
        int newY = Math.max(0, Math.min(getY() + dy, (getWorld().getCurrentLevel().getHeight() - 1) * Tile.SIZE_IN_WORLD));
        if (!getWorld().getCurrentLevel().getTileAt(newX / Tile.SIZE_IN_WORLD, newY / Tile.SIZE_IN_WORLD).isWall()) {
            setX(newX);
            setY(newY);
        }

        return true;
    }
}
