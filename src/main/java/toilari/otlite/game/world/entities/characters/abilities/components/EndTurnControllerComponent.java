package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;

/**
 * Vuoronlopetuskyvyn ohjainkomponentti.
 */
public abstract class EndTurnControllerComponent extends AbstractControllerComponent<EndTurnAbility> {
    @Setter @Getter private boolean wantsToEndTurn;

    private EndTurnControllerComponent(@NonNull CharacterObject character) {
        super(character);
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

    /**
     * Pelaajan vuoronlopetuskyvyn ohjainkomponentti.
     */
    public static class Player extends EndTurnControllerComponent {
        private final boolean autoEndTurn;
        private boolean isHolding;

        private boolean getEndTurnInput() {
            return Input.getHandler().isKeyDown(Key.SPACE);
        }

        /**
         * Luo uuden ohjainkomponentin.
         *
         * @param character   hahmo jolle komponentti lisätään
         * @param autoEndTurn lopetetaanko voro automaattisesti kun toimintopisteet loppuvat
         */
        public Player(CharacterObject character, boolean autoEndTurn) {
            super(character);
            this.autoEndTurn = autoEndTurn;
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
    }

    /**
     * Tekoälyn vuoronlopetuskyvyn ohjainkomponentti.
     */
    public static class AI extends EndTurnControllerComponent {
        private int updateTicksWaited;
        private int prevRemaining = -1;

        @Override
        public void setWantsToEndTurn(boolean wantsToEndTurn) {
            super.setWantsToEndTurn(wantsToEndTurn);
            this.updateTicksWaited = 0;
        }

        /**
         * Luo uuden ohjainkomponentin.
         *
         * @param character hahmo jolle komponentti lisätään
         */
        public AI(@NonNull CharacterObject character) {
            super(character);
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
