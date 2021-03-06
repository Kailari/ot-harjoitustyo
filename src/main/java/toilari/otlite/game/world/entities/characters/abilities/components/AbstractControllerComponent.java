package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;

@NoArgsConstructor
public abstract class AbstractControllerComponent<A extends AbstractAbility> implements IControllerComponent<A> {
    @Getter(AccessLevel.PROTECTED) private transient CharacterObject character;

    @Getter private boolean visibleOnHud;

    protected AbstractControllerComponent(AbstractControllerComponent<A> template) {
        this.visibleOnHud = template.isVisibleOnHud();
    }

    @Override
    public boolean isHidden() {
        return !this.visibleOnHud;
    }

    @Override
    public void init(@NonNull CharacterObject character) {
        if (this.character != null) {
            throw new IllegalStateException("Initializing already initialized controller component");
        }
        this.character = character;
    }
}
