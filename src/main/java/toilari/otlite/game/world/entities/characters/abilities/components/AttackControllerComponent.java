package toilari.otlite.game.world.entities.characters.abilities.components;

import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;

/**
 * Hyökkäämiskyvyn ohjainkomponentti.
 */
public class AttackControllerComponent {
    public static class Player extends AbstractPlayerAttackControllerComponent<AttackAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(AbstractAttackControllerComponent<AttackAbility> template) {
        }

        @Override
        protected boolean getAbilityInput() {
            return Input.getHandler().isKeyPressed(Key.ONE);
        }
    }

    public static class AlwaysAttackAdjacentIfPossible extends AlwaysAttackAdjacentIfPossibleControllerComponent<AttackAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AlwaysAttackAdjacentIfPossible(AbstractAttackControllerComponent<AttackAbility> template) {
        }
    }
}
