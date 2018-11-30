package toilari.otlite.game.world.entities.characters.abilities.components;

import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.ITargetedAbility;

public interface ITargetedControllerComponent<A extends ITargetedAbility> extends IControllerComponent<A> {
    /**
     * Halutaanko kyky suorittaa annetulle kohteelle.
     *
     * @param target    kohde johon kyvyn suoritus kohdistetaan
     * @param direction suunta suorittavasta objektista kohteeseen
     * @return <code>true</code> jos kyky voidaan suorittaa, muulloin <code>false</code>
     */
    boolean wantsPerformOn(GameObject target, Direction direction);
}
