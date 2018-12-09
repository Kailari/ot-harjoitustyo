package toilari.otlite.fake;

import lombok.Getter;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

public class AbilityEntry<A extends IAbility<A, C>, C extends IControllerComponent<A>> {
    @Getter private final A ability;
    @Getter private final C component;

    public AbilityEntry(int priority, A ability, C component) {
        this.ability = ability;
        this.component = component;

        this.ability.setPriority(priority);
    }
}
