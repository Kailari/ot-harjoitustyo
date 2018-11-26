package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;

public abstract class AbstractControllerComponent<A extends AbstractAbility> implements IControllerComponent<A> {
    @Getter(AccessLevel.PROTECTED) @NonNull private final CharacterObject character;

    protected AbstractControllerComponent(@NonNull CharacterObject character) {
        this.character = character;
    }
}
