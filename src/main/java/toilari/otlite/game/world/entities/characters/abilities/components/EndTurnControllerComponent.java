package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;

/**
 * Vuoronlopetuskyvyn ohjainkomponentti.
 */
@NoArgsConstructor
public abstract class EndTurnControllerComponent extends AbstractControllerComponent<EndTurnAbility> {
    @Setter @Getter private boolean wantsToEndTurn;

    protected EndTurnControllerComponent(AbstractControllerComponent<EndTurnAbility> template) {
        super(template);
    }

    @Override
    public boolean wants(EndTurnAbility ability) {
        return this.wantsToEndTurn;
    }

    @Override
    public void updateInput(EndTurnAbility ability) {
        if (getCharacter().isRemoved() || getCharacter().isDead()) {
            this.wantsToEndTurn = true;
        }
    }

    @Override
    public void abilityPerformed(EndTurnAbility ability) {
        setWantsToEndTurn(false);
    }

    @Override
    public void reset() {
        setWantsToEndTurn(false);
    }
}
