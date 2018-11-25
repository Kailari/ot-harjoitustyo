package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;
import toilari.otlite.game.world.entities.characters.CharacterController;

public class EndTurnAbility extends AbstractAbility<EndTurnAbility, EndTurnControllerComponent> {
    public EndTurnAbility(AbstractCharacter character, int priority) {
        super(character, priority);
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public boolean perform(@NonNull CharacterController controller, @NonNull EndTurnControllerComponent component) {
        getCharacter().getWorld().getObjectManager().nextTurn();
        component.setWantsToEndTurn(false);
        return true;
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }
}
