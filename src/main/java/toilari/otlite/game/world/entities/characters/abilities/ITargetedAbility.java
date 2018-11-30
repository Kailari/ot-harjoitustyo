package toilari.otlite.game.world.entities.characters.abilities;

import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.components.ITargetedControllerComponent;

public interface ITargetedAbility<A extends ITargetedAbility<A, C>, C extends ITargetedControllerComponent<A>> extends IAbility<A, C> {
    /**
     * Voiko kyvyn suorittaa annetulle kohteelle.
     *
     * @param target    kohde johon kyvyn suoritus kohdistetaan
     * @param direction suunta suorittavasta objektista kohteeseen
     * @return <code>true</code> jos kyky voidaan suorittaa, muulloin <code>false</code>
     */
    boolean canPerformOn(GameObject target, Direction direction);
}
