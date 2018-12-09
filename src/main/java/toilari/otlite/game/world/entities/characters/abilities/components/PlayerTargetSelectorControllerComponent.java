package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

/**
 * Pelaajan kohteenvalintakyvyn ohjainkomponentti.
 */
@NoArgsConstructor
public class PlayerTargetSelectorControllerComponent extends TargetSelectorControllerComponent {

    private final Key[] abilityKeys = {Key.ONE, Key.TWO, Key.THREE, Key.FOUR, Key.FIVE, Key.SIX, Key.SEVEN, Key.EIGHT, Key.NINE, Key.ZERO};

    /**
     * Kopioi ohjainkomponentin templaatista.
     *
     * @param template templaatti josta kopioidaan
     */
    public PlayerTargetSelectorControllerComponent(TargetSelectorControllerComponent template) {
        super(template);
    }

    @Override
    public void updateInput(@NonNull TargetSelectorAbility ability) {
        cycleTargetWithAbilityKeys();
        selectTargetWithArrowKeys();
    }

    private void cycleTargetWithAbilityKeys() {
        for (int i = 0; i < this.abilityKeys.length; i++) {
            if (Input.getHandler().isKeyPressed(this.abilityKeys[i]) && hasAbility(i) && notOnCooldown(i) && canAfford(i)) {
                if (getTarget() == null || isActive(i)) {
                    setActiveTargetedAbility(i);
                    findNewTarget();
                    if (getTarget() == null) {
                        setActiveTargetedAbility(-1);
                    }
                } else {
                    setActiveTargetedAbility(i);
                }
            }
        }
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
