package toilari.otlite.fake;

import lombok.NonNull;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.ITargetedAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.TargetSelectorControllerComponent;

public class FakeTargetSelectorControllerComponent extends TargetSelectorControllerComponent {
    private FakeTargetSelectorControllerComponent(GameObject target, Direction direction) {
        setTarget(target, direction);
    }

    public static FakeTargetSelectorControllerComponent create(GameObject target, Direction direction) {
        return new FakeTargetSelectorControllerComponent(target, direction);
    }

    @Override
    public ITargetedAbility getActive() {
        return super.getActive();
    }

    @Override
    public GameObject findTargetInDirection(Direction direction) {
        return super.findTargetInDirection(direction);
    }

    @Override
    public void updateInput(@NonNull TargetSelectorAbility ability) {
    }
}
