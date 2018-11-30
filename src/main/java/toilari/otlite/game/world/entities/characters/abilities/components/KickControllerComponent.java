package toilari.otlite.game.world.entities.characters.abilities.components;

import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;

public class KickControllerComponent {
    public static class Player extends AbstractPlayerAttackControllerComponent<KickAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(AbstractAttackControllerComponent<KickAbility> template) {
        }

        @Override
        protected boolean getAbilityInput() {
            return Input.getHandler().isKeyPressed(Key.TWO);
        }
    }

    public static class AlwaysAttackAdjacentIfPossible extends AlwaysAttackAdjacentIfPossibleControllerComponent<KickAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AlwaysAttackAdjacentIfPossible(AbstractAttackControllerComponent<KickAbility> template) {
        }
    }
}
