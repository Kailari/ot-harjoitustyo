package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;

/**
 * Tekoälyn hyökkäämiskyvyn ohjainkomponentti joka hyökkää aina jos mahdollista.
 */
public class AlwaysAttackAdjacentIfPossibleControllerComponent<A extends AbstractAttackAbility> extends AbstractAttackControllerComponent<A> {
    protected AlwaysAttackAdjacentIfPossibleControllerComponent(AbstractControllerComponent<A> template) {
        super(template);
    }

    @Override
    public void doUpdateInput(A ability) {
        val world = getCharacter().getWorld();
        val manager = world.getObjectManager();
        val player = manager.getGameState().getPlayer();

        for (val direction : Direction.asIterable()) {
            val x = getCharacter().getTileX() + direction.getDx();
            val y = getCharacter().getTileY() + direction.getDy();
            val possibleTarget = world.getObjectAt(x, y);
            if (possibleTarget != null && possibleTarget.equals(player)) {
                //setTarget(player);
                break;
            }
        }
    }

    @Override
    public boolean wants(A ability) {
        return getTargetSelector().getTarget() != null;
    }
}
