package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;

public class KickControllerComponent {
    public static class Player extends AbstractPlayerAttackControllerComponent<KickAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(AbstractAttackControllerComponent<KickAbility> template) {
            super(template);
        }
    }

    public static class AI extends AbstractAttackControllerComponent<KickAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AI(AbstractAttackControllerComponent<KickAbility> template) {
            super(template);
        }

        @Override
        protected void doUpdateInput(@NonNull KickAbility ability) {
            setWantsPerform(getTargetSelector().getTarget() != null);
        }
    }
}
