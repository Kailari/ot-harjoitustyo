package toilari.otlite.game.world.entities.characters.abilities.components;

import toilari.otlite.game.world.entities.characters.abilities.IAreaOfEffectAbility;

/**
 * Ohjainkomponentti kyvylle jonka vaikutuksella on tietty alue.
 *
 * @param <A> kyvyn tyyppi
 */
public interface IAreaOfEffectControllerComponent<A extends IAreaOfEffectAbility> extends IControllerComponent<A> {
}
