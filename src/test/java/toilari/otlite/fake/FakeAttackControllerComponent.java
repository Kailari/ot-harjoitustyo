package toilari.otlite.fake;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractPlayerAttackControllerComponent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FakeAttackControllerComponent extends AbstractPlayerAttackControllerComponent<FakeAttackAbility> {
    public static FakeAttackControllerComponent create() {
        return new FakeAttackControllerComponent();
    }

    public static FakeAttackControllerComponent createWithoutTargetValidation() {
        return new FakeAttackControllerComponent() {
            @Override
            public boolean wantsPerformOn(GameObject target, Direction direction) {
                return true;
            }
        };
    }
}
