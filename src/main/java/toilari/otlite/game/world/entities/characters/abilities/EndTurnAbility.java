package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;

/**
 * Hahmon kyky lopettaa vuoro.
 */
@NoArgsConstructor
public class EndTurnAbility extends AbstractAbility<EndTurnAbility, EndTurnControllerComponent> {
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
