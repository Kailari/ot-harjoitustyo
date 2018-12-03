package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;

/**
 * Hahmon kyky lopettaa vuoro.
 */
public class EndTurnAbility extends AbstractAbility<EndTurnAbility, EndTurnControllerComponent> {
    public EndTurnAbility() {
        super("End turn");
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public boolean perform(@NonNull EndTurnControllerComponent component) {
        getCharacter().getWorld().getObjectManager().nextTurn();
        return true;
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }
}
