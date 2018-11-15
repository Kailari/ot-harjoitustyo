package toilari.otlite.world.entities.characters.controller;

import java.util.Random;

public class AnimalController extends CharacterController {
    private final Random random = new Random();

    @Override
    public int getInputX() {
        return this.random.nextInt(3) - 1;
    }

    @Override
    public int getInputY() {
        return this.random.nextInt(3) - 1;
    }
}
