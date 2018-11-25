package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;
import toilari.otlite.game.world.entities.characters.CharacterController;

public interface IAbility<T extends IAbility<T, C>, C extends IControllerComponent<T>> {
    int getPriority();

    boolean isOnCooldown();

    void reduceCooldownTimer();

    int getCost();

    boolean perform(@NonNull CharacterController controller, @NonNull C component);

    void setOnCooldown();

    int getCooldownLength();
}
