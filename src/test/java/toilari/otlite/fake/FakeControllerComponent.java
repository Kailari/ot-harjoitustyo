package toilari.otlite.fake;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractControllerComponent;

@AllArgsConstructor
public class FakeControllerComponent extends AbstractControllerComponent<FakeAbility> {
    @Setter private boolean wants;

    public static FakeControllerComponent create(boolean wants) {
        return new FakeControllerComponent(wants);
    }

    @Override
    public boolean wants(@NonNull FakeAbility ability) {
        return this.wants;
    }

    @Override
    public void updateInput(@NonNull FakeAbility ability) {

    }

    @Override
    public void abilityPerformed(FakeAbility ability) {

    }

    @Override
    public void reset() {

    }
}
