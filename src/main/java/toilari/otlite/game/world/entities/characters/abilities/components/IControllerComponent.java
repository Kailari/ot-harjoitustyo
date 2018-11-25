package toilari.otlite.game.world.entities.characters.abilities.components;

import toilari.otlite.game.world.entities.characters.abilities.IAbility;

public interface IControllerComponent<T extends IAbility> {
    boolean wants();

    void updateInput();
}
