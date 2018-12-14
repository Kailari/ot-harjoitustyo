package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NonNull;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.BlockAbility;

/**
 * Torjumiskyvyn ohjainkomponentti.
 */
public abstract class BlockControllerComponent
    extends AbstractTargetedControllerComponent<BlockAbility>
    implements ITargetedControllerComponent<BlockAbility> {

    protected BlockControllerComponent(BlockControllerComponent template) {
        super(template);
    }

    @Override
    public boolean wantsPerformOn(GameObject target, Direction direction) {
        return false;
    }

    /**
     * Pelaajan ohjainkomponentti.
     */
    public static class Player extends BlockControllerComponent {
        /**
         * Kopioi komponentin tiedot toisesta komponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(BlockControllerComponent template) {
            super(template);
        }

        @Override
        protected void doUpdateInput(@NonNull BlockAbility ability) {
            setWants(Input.getHandler().isKeyPressed(Key.SPACE));
        }
    }
}
