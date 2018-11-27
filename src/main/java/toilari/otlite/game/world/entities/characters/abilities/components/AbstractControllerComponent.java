package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;

public abstract class AbstractControllerComponent<A extends AbstractAbility> implements IControllerComponent<A> {
    @Getter(AccessLevel.PROTECTED) private transient CharacterObject character;

    @Override
    public void init(@NonNull CharacterObject character) {
        if (this.character != null) {
            throw new IllegalStateException("Initializing already initialized controller component");
        }
        this.character = character;
    }
}
