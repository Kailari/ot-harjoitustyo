package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;

/**
 * Pelaajan ohjainkomponentti hyökkäyskyvyille.
 *
 * @param <A> kyvyn tyyppi
 */
public abstract class AbstractPlayerAttackControllerComponent<A extends AbstractAttackAbility> extends AbstractAttackControllerComponent<A> {
    protected AbstractPlayerAttackControllerComponent(AbstractControllerComponent<A> template) {
        super(template);
    }

    @Override
    public void doUpdateInput(A ability) {
        boolean inputPerform = getPerformInput();

        if (inputPerform) {
            setWantsPerform(getTargetSelector().getTarget() != null);
        }
    }

    protected boolean getPerformInput() {
        return Input.getHandler().isKeyPressed(Key.SPACE);
    }

    @Override
    public void abilityPerformed(A ability) {
        super.abilityPerformed(ability);

        val state = getCharacter().getWorld().getObjectManager().getGameState();
        if (state != null) {
            state.getGame().getStatistics().increment(Statistics.ATTACKS_PERFORMED, state.getGame().getActiveProfile().getId());
            state.getGame().getStatistics().incrementBy(Statistics.DAMAGE_DEALT, ability.getLastAttackDamage(), state.getGame().getActiveProfile().getId());
            if (ability.isLastAttackKill()) {
                state.getGame().getStatistics().increment(Statistics.KILLS, state.getGame().getActiveProfile().getId());
            }
        }
    }
}
