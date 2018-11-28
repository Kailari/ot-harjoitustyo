package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;

import java.util.Iterator;

public abstract class KickControllerComponent extends AbstractControllerComponent<KickAbility> {
    @Getter @Setter private transient GameObject target;
    @Getter @Setter private transient boolean wantsToKick;
    @Getter @Setter private transient Direction targetDirection = Direction.NONE;

    @Override
    public boolean wants(@NonNull KickAbility ability) {
        return getTarget() != null && isWantsToKick();
    }

    @NoArgsConstructor
    public static class Player extends KickControllerComponent {
        private transient Iterator<Direction> directionIterator;

        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(KickControllerComponent template) {
        }

        @Override
        public void updateInput(@NonNull KickAbility ability) {
            boolean inputSwitchTarget = Input.getHandler().isKeyPressed(Key.ONE);
            boolean inputAttack = Input.getHandler().isKeyPressed(Key.SPACE);

            if (!ability.canKick(getTargetDirection())) {
                setTarget(null);
                setTargetDirection(Direction.NONE);
            }

            if (inputSwitchTarget) {
                // If searching for new target, reset the direction we start searching from
                if (getTarget() == null) {
                    this.directionIterator = Direction.asLoopingIterator();
                }

                findNewTarget(ability);
            } else if (inputAttack) {
                setWantsToKick(getTarget() != null);
            }
        }

        private void findNewTarget(@NonNull KickAbility ability) {
            val x = getCharacter().getTileX();
            val y = getCharacter().getTileY();
            setTargetDirection(this.directionIterator.next());
            for (int i = 0; i < 4; i++, setTargetDirection(this.directionIterator.next())) {
                if (ability.canKick(this.getTargetDirection())) {
                    setTarget(getCharacter().getWorld().getObjectAt(x + getTargetDirection().getDx(), y + getTargetDirection().getDy()));
                    return;
                }
            }

            setTargetDirection(Direction.NONE);
            setTarget(null);
        }

        @Override
        public void abilityPerformed(KickAbility ability) {
            setTarget(null);
            setWantsToKick(false);
        }
    }
}
