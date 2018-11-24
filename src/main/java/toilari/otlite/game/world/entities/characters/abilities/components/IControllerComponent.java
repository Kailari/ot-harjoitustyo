package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;

public interface IControllerComponent<T extends IAbility> {
    boolean wants(@NonNull T ability);

    void updateInput();
}
