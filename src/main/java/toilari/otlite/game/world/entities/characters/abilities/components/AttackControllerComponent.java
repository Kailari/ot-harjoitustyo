package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.NonNull;
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
            super(template);
        }
    }

    @NoArgsConstructor
    public static class AI extends AbstractAttackControllerComponent<AttackAbility> {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AI(AbstractControllerComponent<AttackAbility> template) {
            super(template);
        }

        @Override
        protected void doUpdateInput(@NonNull AttackAbility ability) {
            setWantsPerform(getTargetSelector().getTarget() != null);
        }
    }
}
