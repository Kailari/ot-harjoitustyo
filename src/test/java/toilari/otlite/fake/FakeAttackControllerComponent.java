package toilari.otlite.fake;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractPlayerAttackControllerComponent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FakeAttackControllerComponent extends AbstractPlayerAttackControllerComponent<FakeAttackAbility> {
    public static FakeAttackControllerComponent create() {
        return new FakeAttackControllerComponent();
    }
}
