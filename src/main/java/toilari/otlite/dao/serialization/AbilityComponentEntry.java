package toilari.otlite.dao.serialization;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AbilityComponentEntry<A extends IAbility<A, C>, C extends IControllerComponent<A>> {
    @Getter private final Class<? extends A> abilityClass;
    @Getter private final Map<String, Class<? extends C>> componentClasses = new HashMap<>();
    @Getter private final Map<Class, Function<C, C>> factories = new HashMap<>();

    public AbilityComponentEntry(@NonNull Class<? extends A> abilityClass) {
        this.abilityClass = abilityClass;
    }

    public AbilityComponentEntry<A, C> addComponent(String key, Class<? extends C> componentClass, Function<C, C> factory) {
        this.componentClasses.put(key, componentClass);
        this.factories.put(componentClass, factory);
        return this;
    }
}
