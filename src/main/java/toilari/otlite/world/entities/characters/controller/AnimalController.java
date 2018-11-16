package toilari.otlite.world.entities.characters.controller;

import java.util.Random;

public class AnimalController extends CharacterController {
    private final Random random = new Random();

    @Override
    public int getMoveInputX() {
        return this.random.nextInt(3) - 1;
    }

    @Override
    public int getMoveInputY() {
        return this.random.nextInt(3) - 1;
    }
}
