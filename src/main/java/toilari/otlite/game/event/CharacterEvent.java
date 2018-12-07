package toilari.otlite.game.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;

public class CharacterEvent implements IEvent {
    @RequiredArgsConstructor
    public static class Damage extends CharacterEvent {
        @Getter @NonNull private final CharacterObject attacker;
        @Getter @NonNull private final GameObject target;
        @Getter private final float amount;
    }

    /**
     * Viesti joka lähetetään kun pelaaja kuolee.
     */
    @RequiredArgsConstructor
    public static class Died extends CharacterEvent {
        @Getter private final CharacterObject character;
    }
}
