package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

/**
 * Hyökkäämiskyvyn ohjainkomponentti.
 */
public abstract class AttackControllerComponent extends AbstractControllerComponent<AttackAbility> {
    @Getter @Setter(AccessLevel.PROTECTED) private GameObject target;

    protected AttackControllerComponent(@NonNull CharacterObject character) {
        super(character);
    }

    @Override
    public void abilityPerformed(AttackAbility ability) {
        setTarget(null);
    }

    /**
     * Pelaajan hyökkäämiskyvyn ohjainkomponentti.
     */
    public static class Player extends AttackControllerComponent {
        private final MoveControllerComponent moveComponent;

        /**
         * Luo uuden ohjainkomponentin.
         *
         * @param character hahmo jolle komponentti lisätään
         */
        public Player(@NonNull CharacterObject character) {
            super(character);
            this.moveComponent = character.getAbilities().getComponent(MoveAbility.class);

            if (this.moveComponent == null) {
                throw new IllegalStateException("Attack component requires Move component on player! Make sure move component is added first.");
            }
        }

        @Override
        public void updateInput(AttackAbility ability) {
        }

        @Override
        public boolean wants(AttackAbility ability) {
            val direction = this.moveComponent.getInputDirection();
            if (direction == Direction.NONE) {
                setTarget(null);
                return false;
            }

            val targetX = getCharacter().getTileX() + direction.getDx();
            val targetY = getCharacter().getTileY() + direction.getDy();
            if (ability.canAttack(targetX, targetY)) {
                setTarget(getCharacter().getWorld().getObjectAt(targetX, targetY));
                return true;
            }

            setTarget(null);
            return false;
        }
    }

    /**
     * Tekoälyn hyökkäämiskyvyn ohjainkomponentti.
     */
    public static class AI extends AttackControllerComponent {
        /**
         * Luo uuden ohjainkomponentin.
         *
         * @param character hahmo jolle komponentti lisätään
         */
        public AI(@NonNull CharacterObject character) {
            super(character);
        }

        @Override
        public void updateInput(AttackAbility ability) {

        }

        @Override
        public boolean wants(AttackAbility ability) {
            return false;
        }
    }
}
