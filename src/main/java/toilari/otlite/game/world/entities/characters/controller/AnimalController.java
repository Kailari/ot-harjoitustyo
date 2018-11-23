package toilari.otlite.game.world.entities.characters.controller;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.world.entities.TurnObjectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimalController extends CharacterController {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    private final Random random = new Random();

    private int inputX = 0;
    private int inputY = 0;

    private List<Integer> availableDirections = new ArrayList<>(4);

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

        refreshMoveDirections();
        if (this.availableDirections.isEmpty()) {
            turnManager.nextTurn();
            return;
        }

        val direction = this.availableDirections.get(this.random.nextInt(this.availableDirections.size()));
        this.inputX = direction == LEFT ? -1 : (direction == RIGHT ? 1 : 0);
        this.inputY = direction == UP ? -1 : (direction == DOWN ? 1 : 0);
    }

    private void refreshMoveDirections() {
        val character = getControlledCharacter();
        this.availableDirections.clear();
        if (character.canMoveTo(1, 0)) {
            this.availableDirections.add(RIGHT);
        }

        if (character.canMoveTo(-1, 0)) {
            this.availableDirections.add(LEFT);
        }

        if (character.canMoveTo(0, 1)) {
            this.availableDirections.add(DOWN);
        }

        if (character.canMoveTo(0, -1)) {
            this.availableDirections.add(UP);
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
