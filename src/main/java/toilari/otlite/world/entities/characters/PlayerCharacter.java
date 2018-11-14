package toilari.otlite.world.entities.characters;

import toilari.otlite.io.Input;
import toilari.otlite.io.Key;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerCharacter extends AbstractCharacter {
    private boolean canMove;

    @Override
    public void update() {
        super.update();

        if (this.canMove) {
            if (Input.getHandler().isKeyDown(Key.RIGHT)) {
                setX(getX() + 1);
                this.canMove = false;
            } else if (Input.getHandler().isKeyDown(Key.LEFT)) {
                setX(getX() - 1);
                this.canMove = false;
            } else if (Input.getHandler().isKeyDown(Key.UP)) {
                setY(getY() - 1);
                this.canMove = false;
            } else if (Input.getHandler().isKeyDown(Key.DOWN)) {
                setY(getY() + 1);
                this.canMove = false;
            }
        } else if (Input.getHandler().isKeyUp(Key.RIGHT)
            && Input.getHandler().isKeyUp(Key.LEFT)
            && Input.getHandler().isKeyUp(Key.UP)
            && Input.getHandler().isKeyUp(Key.DOWN)) {
            this.canMove = true;
        }
    }
}
