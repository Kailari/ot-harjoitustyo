package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.profile.statistics.Statistics;
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

    @Override
    public void abilityPerformed(AttackAbility ability) {
        setTarget(null);
    }

    /**
     * Pelaajan hyökkäämiskyvyn ohjainkomponentti.
     */
    public static class Player extends AttackControllerComponent {
        private MoveControllerComponent moveComponent;

        /**
         * Kopioi komponentin toisesta komponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(AttackControllerComponent template) {
        }

        /**
         * Luo uuden ohjainkomponentin.
         *
         * @param character hahmo jolle komponentti lisätään
         */
        @Override
        public void init(@NonNull CharacterObject character) {
            super.init(character);
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

        @Override
        public void abilityPerformed(AttackAbility ability) {
            super.abilityPerformed(ability);

            val state = getCharacter().getWorld().getObjectManager().getGameState();
            if (state != null) {
                state.getGame().getStatistics().increment(Statistics.ATTACKS_PERFORMED, state.getGame().getActiveProfile().getId());
                state.getGame().getStatistics().incrementBy(Statistics.DAMAGE_DEALT, ability.getLastAttackDamage(), state.getGame().getActiveProfile().getId());
                if (ability.isLastAttackKill()) {
                    state.getGame().getStatistics().increment(Statistics.KILLS, state.getGame().getActiveProfile().getId());
                }
            }
        }
    }

    /**
     * Tekoälyn hyökkäämiskyvyn ohjainkomponentti.
     */
    public static class AIAlwaysAttackAdjacentIfPossible extends AttackControllerComponent {
        /**
         * Kopio komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AIAlwaysAttackAdjacentIfPossible(AttackControllerComponent template) {
        }

        @Override
        public void updateInput(AttackAbility ability) {
            val world = getCharacter().getWorld();
            val manager = world.getObjectManager();
            val player = manager.getGameState().getPlayer();

            setTarget(null);
            for (val direction : Direction.asIterable()) {
                val x = getCharacter().getTileX() + direction.getDx();
                val y = getCharacter().getTileY() + direction.getDy();
                val possibleTarget = world.getObjectAt(x, y);
                if (possibleTarget != null && possibleTarget.equals(player)) {
                    setTarget(player);
                    break;
                }
            }
        }

        @Override
        public boolean wants(AttackAbility ability) {
            return getTarget() != null;
        }

        @Override
        public void abilityPerformed(AttackAbility ability) {
            super.abilityPerformed(ability);
        }
    }
}
