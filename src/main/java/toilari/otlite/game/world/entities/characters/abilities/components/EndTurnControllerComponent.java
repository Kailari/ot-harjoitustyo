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
public abstract class EndTurnControllerComponent extends AbstractControllerComponent<EndTurnAbility> {
    @Setter @Getter private boolean wantsToEndTurn;

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

    /**
     * Pelaajan vuoronlopetuskyvyn ohjainkomponentti.
     */
    @NoArgsConstructor
    public static class Player extends EndTurnControllerComponent {
        @Setter private boolean autoEndTurn;
        private boolean isHolding;

        public Player(EndTurnControllerComponent template) {
            this.autoEndTurn = ((EndTurnControllerComponent.Player) template).autoEndTurn;
        }

        private boolean getEndTurnInput() {
            return Input.getHandler().isKeyDown(Key.SPACE);
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
            if ((outOfActions && this.autoEndTurn) || (!this.isHolding && input)) {
                setWantsToEndTurn(true);
            }

            this.isHolding = input;
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

    /**
     * Tekoälyn vuoronlopetuskyvyn ohjainkomponentti.
     */
    public static class AI extends EndTurnControllerComponent {
        private int updateTicksWaited;
        private int prevRemaining = -1;

        /**
         * Kopioi komponentin toisesta komponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AI(EndTurnControllerComponent template) {
        }

        @Override
        public void setWantsToEndTurn(boolean wantsToEndTurn) {
            super.setWantsToEndTurn(wantsToEndTurn);
            this.updateTicksWaited = 0;
        }

        @Override
        public void updateInput(EndTurnAbility ability) {
            val remaining = getCharacter().getWorld().getObjectManager().getRemainingActionPoints();
            if (remaining == this.prevRemaining) {
                this.updateTicksWaited++;
            } else {
                this.updateTicksWaited = 0;
            }

            this.prevRemaining = remaining;

            if (this.updateTicksWaited > 1) {
                setWantsToEndTurn(true);
            }
        }
    }
}
