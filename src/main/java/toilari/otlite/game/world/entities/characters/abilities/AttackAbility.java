package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NoArgsConstructor;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;

/**
 * Hahmon kyky hyökätä.
 */
@NoArgsConstructor
public class AttackAbility extends AbstractAttackAbility<AttackAbility, AbstractAttackControllerComponent<AttackAbility>> {
    @Override
    public int getCost() {
        return getCharacter().getAttributes().getAttackCost();
    }

    @Override
    public int getCooldownLength() {
        return getCharacter().getAttributes().getAttackCooldown();
    }
}
