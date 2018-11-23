package toilari.otlite.game.world.entities.characters.controller;

import lombok.NonNull;
import toilari.otlite.game.world.entities.TurnObjectManager;

import java.util.Random;

public class AnimalController extends CharacterController {
    private final Random random = new Random();

    private int inputX = 0;
    private int inputY = 0;

    @Override
    public int getMoveInputX() {
        return this.inputX;
    }

    @Override
    public int getMoveInputY() {
        return this.inputY;
    }

    @Override
    public void update(@NonNull TurnObjectManager turnManager) {
        if (turnManager.getRemainingActionPoints() < getControlledCharacter().getAttributes().getMoveCost()) {
            turnManager.nextTurn();
            return;
        }

        this.inputX = this.random.nextInt(3) - 1;
        this.inputY = this.random.nextInt(3) - 1;

        if (this.inputX == 0 && this.inputY == 0) {
            turnManager.nextTurn();
        }
    }

    @Override
    public boolean wantsMove() {
        return !wantsAttack() && (getMoveInputX() != 0 || getMoveInputY() != 0);
    }

    @Override
    public boolean wantsAttack() {
        return false;
    }
}
