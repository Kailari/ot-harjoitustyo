package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;

/**
 * Pelaajan vuoronlopetuskyvyn ohjainkomponentti.
 */
@NoArgsConstructor
public class PlayerEndTurnControllerComponent extends EndTurnControllerComponent {
    @Setter private boolean autoEndTurn;

    /**
     * Luo uuden komponentin kopioimalla sen annetusta komponentista.
     *
     * @param template kopioitava komponentti
     */
    public PlayerEndTurnControllerComponent(EndTurnControllerComponent template) {
        super(template);
        this.autoEndTurn = ((PlayerEndTurnControllerComponent) template).autoEndTurn;
    }

    private boolean getEndTurnInput() {
        return Input.getHandler().isKeyPressed(Key.SPACE);
    }

    @Override
    public void updateInput(EndTurnAbility ability) {
        super.updateInput(ability);

        if (isWantsToEndTurn()) {
            return;
        }

        val input = getEndTurnInput();

        val manager = getCharacter().getWorld().getObjectManager();
        val outOfActions = manager.getRemainingActionPoints() <= 0;
        if ((outOfActions && this.autoEndTurn) || input) {
            setWantsToEndTurn(true);
        }
    }

    @Override
    public void abilityPerformed(EndTurnAbility ability) {
        super.abilityPerformed(ability);

        val state = getCharacter().getWorld().getObjectManager().getGameState();
        if (state != null) {
            state.getGame().getStatistics().increment(Statistics.TURNS_PLAYED, state.getGame().getActiveProfile().getId());
        }
    }
}
