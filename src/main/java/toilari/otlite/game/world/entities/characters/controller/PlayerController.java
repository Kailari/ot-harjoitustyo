package toilari.otlite.game.world.entities.characters.controller;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.Tile;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;

public class PlayerController extends CharacterController {
    private boolean isHolding = false;

    private int inputX;
    private int inputY;

    private int getMoveInputXRaw() {
        val right = Input.getHandler().isKeyDown(Key.RIGHT) ? 1 : 0;
        val left = Input.getHandler().isKeyDown(Key.LEFT) ? -1 : 0;
        return right + left;
    }

    private int getMoveInputYRaw() {
        val down = Input.getHandler().isKeyDown(Key.DOWN) ? 1 : 0;
        val up = Input.getHandler().isKeyDown(Key.UP) ? -1 : 0;
        return down + up;
    }

    private boolean getEndTurnInputRaw() {
        return Input.getHandler().isKeyDown(Key.SPACE);
    }

    @Override
    public int getMoveInputX() {
        return this.inputX;
    }

    @Override
    public int getMoveInputY() {
        return this.inputY;
    }

    @Override
    public boolean wantsMove() {
        return !wantsAttack() && (getMoveInputX() != 0 || getMoveInputY() != 0);
    }

    @Override
    public boolean wantsAttack() {
        if (getMoveInputX() == 0 && getMoveInputY() == 0) {
            return false;
        }

        val targetX = getControlledCharacter().getX() / Tile.SIZE_IN_WORLD + getMoveInputX();
        val targetY = getControlledCharacter().getY() / Tile.SIZE_IN_WORLD + getMoveInputY();
        val objectAtTarget = getControlledCharacter().getWorld().getObjectAt(targetX, targetY);
        return objectAtTarget instanceof AbstractCharacter && !objectAtTarget.equals(getControlledCharacter());
    }

    @Override
    public void update(@NonNull TurnObjectManager turnManager) {
        int rawInputX = getMoveInputXRaw();
        int rawInputY = getMoveInputYRaw();
        boolean rawInputEndTurn = getEndTurnInputRaw();

        if (this.isHolding) {
            this.inputX = 0;
            this.inputY = 0;
        } else {
            this.inputX = rawInputX;
            this.inputY = rawInputY;

            if (rawInputEndTurn || turnManager.getRemainingActionPoints() == 0) {
                turnManager.nextTurn();
            }
        }

        this.isHolding = rawInputX != 0 || rawInputY != 0 || rawInputEndTurn;
    }
}
