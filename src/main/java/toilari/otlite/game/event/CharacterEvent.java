package toilari.otlite.game.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;

public class CharacterEvent implements IEvent {
    @RequiredArgsConstructor
    public static class Damage implements IEvent {
        @Getter @NonNull private final AbstractCharacter attacker;
        @Getter @NonNull private final AbstractCharacter target;
        @Getter private final float amount;
    }
}
