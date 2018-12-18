package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.WarcryAbility;

/**
 * Ohjainkomponentti sotahuudolle.
 */
@NoArgsConstructor
public abstract class WarcryControllerComponent
    extends AbstractTargetedControllerComponent<WarcryAbility>
    implements IAreaOfEffectControllerComponent<WarcryAbility> {

    protected WarcryControllerComponent(WarcryControllerComponent template) {
        super(template);
    }

    @Override
    public boolean wantsPerformOn(GameObject target, Direction direction) {
        return target.equals(getCharacter());
    }

    /**
     * Pelaajan ohjainkomponentti sotahuudolle.
     */
    @NoArgsConstructor
    public static class Player extends WarcryControllerComponent {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(WarcryControllerComponent template) {
            super(template);
        }

        @Override
        public void doUpdateInput(@NonNull WarcryAbility ability) {
            setWantsPerform(Input.getHandler().isKeyPressed(Key.SPACE));
        }
    }
}
