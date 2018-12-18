package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;

@NoArgsConstructor
public abstract class AbstractAttackControllerComponent<A extends AbstractAttackAbility>
    extends AbstractTargetedControllerComponent<A>
    implements ITargetedControllerComponent<A> {

    protected AbstractAttackControllerComponent(AbstractControllerComponent<A> template) {
        super(template);
    }

    @Override
    public void abilityPerformed(@NonNull A ability) {
        if (ability.isLastAttackKill()) {
            val target = getTargetSelector().getTarget();
            if (target instanceof CharacterObject) {
                val targetCharacter = (CharacterObject) target;
                getCharacter().getLevels().rewardExperience(targetCharacter.getAttributes().getXpReward());
            }
        }

        super.abilityPerformed(ability);
    }

    @Override
    public boolean wantsPerformOn(GameObject target, Direction direction) {
        return target != null && direction != null && direction != Direction.NONE;
    }
}
