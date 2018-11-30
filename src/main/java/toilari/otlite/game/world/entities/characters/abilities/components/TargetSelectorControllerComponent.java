package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.ITargetedAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

@NoArgsConstructor
public abstract class TargetSelectorControllerComponent extends AbstractControllerComponent<TargetSelectorAbility> {
    @Getter(AccessLevel.PROTECTED) private transient ITargetedAbility[] abilities;
    @Getter(AccessLevel.PROTECTED) private transient ITargetedAbility active;
    @Getter @Setter(AccessLevel.PROTECTED) private transient GameObject target;
    @Getter private transient Direction targetDirection;

    private transient Iterator<Direction> directionIterator;

    protected TargetSelectorControllerComponent(TargetSelectorControllerComponent template) {
        super(template);
    }

    public boolean isActive(IAbility ability) {
        return Objects.equals(this.active, ability);
    }

    protected void setActive(int abilityIndex) {
        if (abilityIndex < 0 || abilityIndex >= this.abilities.length) {
            this.active = null;
        } else {
            this.active = this.abilities[abilityIndex];
        }
    }

    public void setActive(ITargetedAbility ability) {
        if (!Arrays.asList(this.abilities).contains(ability)) {
            throw new IllegalStateException("Character tried to update targeting for ability it does not own.");
        }

        this.active = ability;
    }

    @Override
    public void init(@NonNull CharacterObject character) {
        super.init(character);

        val list = new ArrayList<ITargetedAbility>();
        for (val ability : character.getAbilities().getAbilitiesSortedByPriority()) {
            if (ability instanceof ITargetedAbility) {
                list.add((ITargetedAbility) ability);
            }
        }

        this.abilities = list.toArray(new ITargetedAbility[0]);
    }

    @Override
    public boolean wants(@NonNull TargetSelectorAbility ability) {
        return false;
    }

    @Override
    public void abilityPerformed(TargetSelectorAbility ability) {
        this.active = null;
        this.target = null;
        this.targetDirection = Direction.NONE;
    }

    protected boolean hasAbility(int i) {
        return this.abilities.length > i;
    }

    protected boolean canAfford(int i) {
        return this.abilities[i].getCost() <= getCharacter().getWorld().getObjectManager().getRemainingActionPoints();
    }

    protected boolean notOnCooldown(int i) {
        return !this.abilities[i].isOnCooldown();
    }

    protected void findNewTarget() {
        if (this.target == null) {
            this.directionIterator = Direction.asLoopingIterator();
        }

        this.targetDirection = this.directionIterator.next();
        for (int i = 0; i < 4; i++, this.targetDirection = this.directionIterator.next()) {
            this.target = findTargetInDirection(this.targetDirection);

            if (this.target != null) {
                return;
            }
        }

        this.targetDirection = Direction.NONE;
        this.target = null;
    }

    protected GameObject findTargetInDirection(Direction direction) {
        if (getActive() == null) {
            return null;
        }

        val x = getCharacter().getTileX();
        val y = getCharacter().getTileY();
        val targetX = x + direction.getDx();
        val targetY = y + direction.getDy();
        val targetCandidate = getCharacter().getWorld().getObjectAt(targetX, targetY);

        val ability = getActive();
        val component = (ITargetedControllerComponent) getCharacter().getAbilities().getComponent(ability.getClass());
        if (targetCandidate != null
            && ability.canPerformOn(targetCandidate, direction)
            && component.wantsPerformOn(targetCandidate, direction)) {
            return targetCandidate;
        }

        return null;
    }

    @Override
    public void reset() {
        this.target = null;
        this.targetDirection = Direction.NONE;
        this.active = null;
    }

    @NoArgsConstructor
    public static class Player extends TargetSelectorControllerComponent {
        private final Key[] abilityKeys = {Key.ONE, Key.TWO, Key.THREE, Key.FOUR, Key.FIVE, Key.SIX, Key.SEVEN, Key.EIGHT, Key.NINE, Key.ZERO};

        public Player(TargetSelectorControllerComponent template) {
            super(template);
        }

        @Override
        public void updateInput(@NonNull TargetSelectorAbility ability) {
            for (int i = 0; i < this.abilityKeys.length; i++) {
                if (Input.getHandler().isKeyPressed(this.abilityKeys[i]) && hasAbility(i) && notOnCooldown(i) && canAfford(i)) {
                    setActive(i);
                    findNewTarget();
                    if (getTarget() == null) {
                        setActive(-1);
                    }
                }
            }

            // TODO Arrow keys to switch target
        }
    }

    @NoArgsConstructor
    public static class AlwaysAttackAdjacentIfPossible extends TargetSelectorControllerComponent {
        public AlwaysAttackAdjacentIfPossible(TargetSelectorControllerComponent template) {
            super(template);
        }

        @Override
        public void updateInput(@NonNull TargetSelectorAbility ability) {
            val world = getCharacter().getWorld();
            val manager = world.getObjectManager();
            val player = manager.getGameState().getPlayer();

            setActive(0);

            for (val direction : Direction.asIterable()) {
                val candidate = findTargetInDirection(direction);
                if (Objects.equals(player, candidate)) {
                    setTarget(player);
                    return;
                }
            }

            setTarget(null);
        }
    }
}
