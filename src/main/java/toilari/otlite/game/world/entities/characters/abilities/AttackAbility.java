package toilari.otlite.game.world.entities.characters.abilities;

import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;

/**
 * Hahmon kyky hyökätä.
 */
public class AttackAbility extends AbstractAttackAbility<AttackAbility, AbstractAttackControllerComponent<AttackAbility>> {
    /**
     * Luo uuden kykyinstanssin.
     */
    public AttackAbility() {
        super("Attack");
    }

    @Override
    public int getCost() {
        return getCharacter().getAttributes().getAttackCost();
    }

    @Override
    public int getCooldownLength() {
        return getCharacter().getAttributes().getAttackCooldown();
    }

    @Override
    public boolean canPerformOn(GameObject target, Direction direction) {
        return true;
    }
}
