package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;
import toilari.otlite.game.world.entities.characters.abilities.ITargetedAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

/**
 * Abstrakti pohja kohdistetuille kyvyille.
 *
 * @param <A> kyvyn tyyppi
 */
@NoArgsConstructor
public abstract class AbstractTargetedControllerComponent<A extends AbstractAbility & ITargetedAbility>
    extends AbstractControllerComponent<A>
    implements ITargetedControllerComponent<A> {

    @Getter private transient TargetSelectorControllerComponent targetSelector;
    @Setter(AccessLevel.PROTECTED) private transient boolean wants;


    protected AbstractTargetedControllerComponent(AbstractControllerComponent template) {
        super(template);
    }

    @Override
    public void init(@NonNull CharacterObject character) {
        super.init(character);
        this.targetSelector = character.getAbilities().getComponent(TargetSelectorAbility.class);

        if (this.targetSelector == null) {
            throw new IllegalStateException("The component \"" + getClass().getSimpleName() + "\" requires a TargetSelector on the object to function!");
        }
    }

    @Override
    public boolean wants(@NonNull A ability) {
        return this.wants;
    }

    @Override
    public void abilityPerformed(A ability) {
        this.targetSelector.abilityPerformed(getCharacter().getAbilities().getAbility(TargetSelectorAbility.class));
        this.wants = false;
    }

    @Override
    public void reset() {
        this.wants = false;
    }

    @Override
    public final void updateInput(@NonNull A ability) {
        if (!this.targetSelector.isActive(ability)) {
            return;
        }

        doUpdateInput(ability);
    }

    protected abstract void doUpdateInput(@NonNull A ability);
}
