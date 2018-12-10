package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import java.util.Iterator;

/**
 * Pelaajan kohteenvalintakyvyn ohjainkomponentti.
 */
@NoArgsConstructor
public class PlayerTargetSelectorControllerComponent extends TargetSelectorControllerComponent {
    private final Key[] abilityKeys = {Key.ONE, Key.TWO, Key.THREE, Key.FOUR, Key.FIVE, Key.SIX, Key.SEVEN, Key.EIGHT, Key.NINE, Key.ZERO};

    private transient Iterator<Direction> directionIterator;


    /**
     * Kopioi ohjainkomponentin templaatista.
     *
     * @param template templaatti josta kopioidaan
     */
    public PlayerTargetSelectorControllerComponent(TargetSelectorControllerComponent template) {
        super(template);
    }

    @Override
    public void setTarget(GameObject target, Direction direction) {
        super.setTarget(target, direction);
        this.directionIterator = Direction.asLoopingIterator(direction);
    }

    @Override
    public void updateInput(@NonNull TargetSelectorAbility ability) {
        cycleTargetWithAbilityKeys();
        selectTargetWithArrowKeys();
    }

    private void cycleTargetWithAbilityKeys() {
        for (int i = 0; i < this.abilityKeys.length; i++) {
            if (Input.getHandler().isKeyPressed(this.abilityKeys[i]) && hasAbility(i) && notOnCooldown(i) && canAfford(i)) {
                if (getTarget() == null || isActive(i) || (!getAbilities()[i].canPerformOn(getTarget(), getTargetDirection()))) {
                    setActiveTargetedAbility(i);
                    cycleTargets();
                    if (getTarget() == null) {
                        setActiveTargetedAbility(-1);
                    }
                } else {
                    setActiveTargetedAbility(i);
                }
            }
        }
    }

    public void cycleTargets() {
        if (getActive() == null) {
            return;
        }

        if (getTarget() == null) {
            this.directionIterator = Direction.asLoopingIterator();
        }

        var direction = this.directionIterator.next();
        for (int i = 0; i < 4; i++, direction = this.directionIterator.next()) {
            val targetCandidate = findTargetInDirection(direction);
            if (targetCandidate != null) {
                setTarget(targetCandidate, direction);
                return;
            }

            if (direction == Direction.LEFT && getActive().canTargetSelf()) {
                setTarget(getCharacter(), Direction.NONE);
                return;
            }
        }

        setTarget(null, Direction.NONE);
    }

    private void selectTargetWithArrowKeys() {
        Direction direction = Direction.NONE;
        if (Input.getHandler().isKeyPressed(Key.LEFT)) {
            direction = Direction.LEFT;
        } else if (Input.getHandler().isKeyPressed(Key.RIGHT)) {
            direction = Direction.RIGHT;
        } else if (Input.getHandler().isKeyPressed(Key.UP)) {
            direction = Direction.UP;
        } else if (Input.getHandler().isKeyPressed(Key.DOWN)) {
            direction = Direction.DOWN;
        }

        if (direction != Direction.NONE) {
            val candidate = findTargetInDirection(direction);
            if (candidate != null) {
                setTarget(candidate, direction);
            }
        }
    }

    private boolean isActive(int abilityIndex) {
        return hasAbility(abilityIndex) && isActive(getAbilities()[abilityIndex]);
    }

    private boolean canTargetSelf(int abilityIndex) {
        return hasAbility(abilityIndex) && getAbilities()[abilityIndex].canTargetSelf();
    }

    private boolean notOnCooldown(int abilityIndex) {
        return hasAbility(abilityIndex) && !getAbilities()[abilityIndex].isOnCooldown();
    }

    private boolean hasAbility(int abilityIndex) {
        return abilityIndex >= 0 && abilityIndex < getAbilities().length && isAvailableAbility(getAbilities()[abilityIndex]);
    }

    private void setActiveTargetedAbility(int abilityIndex) {
        if (!hasAbility(abilityIndex)) {
            setActiveTargetedAbility(null);
        } else {
            setActiveTargetedAbility(getAbilities()[abilityIndex]);
        }
    }

    private boolean canAfford(int i) {
        return getAbilities()[i].getCost() <= getCharacter().getWorld().getObjectManager().getRemainingActionPoints();
    }
}
