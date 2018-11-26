package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;

/**
 * Hahmon kyky lopettaa vuoro.
 */
public class EndTurnAbility extends AbstractAbility<EndTurnAbility, EndTurnControllerComponent> {
    /**
     * Luo uuden kyvyn.
     *
     * @param character hahmo jolle kyky lisätään
     * @param priority  kyvyn prioriteetti
     */
    public EndTurnAbility(CharacterObject character, int priority) {
        super(character, priority);
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public boolean perform(@NonNull EndTurnControllerComponent component) {
        getCharacter().getWorld().getObjectManager().nextTurn();
        component.setWantsToEndTurn(false);
        return true;
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }
}
