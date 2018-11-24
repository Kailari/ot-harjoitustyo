package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;

public abstract class EndTurnControllerComponent extends AbstractControllerComponent<EndTurnAbility> {
    @Setter @Getter private boolean wantsToEndTurn;

    private EndTurnControllerComponent(@NonNull AbstractCharacter character) {
        super(character);
    }

    @Override
    public boolean wants(@NonNull EndTurnAbility ability) {
        return this.wantsToEndTurn;
    }

    @Override
    public void updateInput() {
        if (getCharacter().isRemoved() || getCharacter().isDead()) {
            this.wantsToEndTurn = true;
        }
    }

    public static class Player extends EndTurnControllerComponent {
        private final boolean autoEndTurn;
        private boolean isHolding;

        private boolean getEndTurnInput() {
            return Input.getHandler().isKeyDown(Key.SPACE);
        }

        public Player(AbstractCharacter character, boolean autoEndTurn) {
            super(character);
            this.autoEndTurn = autoEndTurn;
        }

        @Override
        public void updateInput() {
            super.updateInput();

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

    public static class AI extends EndTurnControllerComponent {
        private int updateTicksWaited;
        private int prevRemaining = -1;

        @Override
        public void setWantsToEndTurn(boolean wantsToEndTurn) {
            super.setWantsToEndTurn(wantsToEndTurn);
            this.updateTicksWaited = 0;
        }

        public AI(@NonNull AbstractCharacter character) {
            super(character);
        }

        @Override
        public void updateInput() {
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
