package toilari.otlite.game.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;

public class CharacterEvent implements IEvent {
    @RequiredArgsConstructor
    public static class Damage implements IEvent {
        @Getter @NonNull private final CharacterObject attacker;
        @Getter @NonNull private final GameObject target;
        @Getter private final float amount;
    }
}
