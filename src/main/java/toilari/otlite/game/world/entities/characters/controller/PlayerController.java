package toilari.otlite.game.world.entities.characters.controller;

import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;

public class PlayerController extends CharacterController {
    @Override
    public int getMoveInputX() {
        val right = Input.getHandler().isKeyDown(Key.RIGHT) ? 1 : 0;
        val left = Input.getHandler().isKeyDown(Key.LEFT) ? -1 : 0;
        return right + left;
    }

    @Override
    public int getMoveInputY() {
        val down = Input.getHandler().isKeyDown(Key.DOWN) ? 1 : 0;
        val up = Input.getHandler().isKeyDown(Key.UP) ? -1 : 0;
        return down + up;
    }
}
