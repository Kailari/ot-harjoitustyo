package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

@NoArgsConstructor
public abstract class AbstractAttackControllerComponent<A extends AbstractAttackAbility>
    extends AbstractControllerComponent<A>
    implements ITargetedControllerComponent<A> {

    @Getter @Setter(AccessLevel.PROTECTED) private transient boolean wantsPerform;
    @Getter private transient TargetSelectorControllerComponent targetSelector;

    protected AbstractAttackControllerComponent(AbstractControllerComponent<A> template) {
        super(template);
    }

    protected abstract void doUpdateInput(@NonNull A ability);

    @Override
    public final void updateInput(@NonNull A ability) {
        if (!this.targetSelector.isActive(ability)) {
            return;
        }

        doUpdateInput(ability);
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
        return getTargetSelector().getTarget() != null && isWantsPerform();
    }

    @Override
    public void abilityPerformed(A ability) {
        if (ability.isLastAttackKill()) {
            val target = this.targetSelector.getTarget();
            if (target instanceof CharacterObject) {
                val targetCharacter = (CharacterObject) target;
                getCharacter().getLevels().rewardExperience(targetCharacter.getAttributes().getXpReward());
            }
        }

        this.targetSelector.abilityPerformed(getCharacter().getAbilities().getAbility(TargetSelectorAbility.class));
        setWantsPerform(false);
    }


    @Override
    public boolean wantsPerformOn(GameObject target, Direction direction) {
        return target != null && direction != Direction.NONE;
    }

    @Override
    public void reset() {
        this.wantsPerform = false;
    }
}
