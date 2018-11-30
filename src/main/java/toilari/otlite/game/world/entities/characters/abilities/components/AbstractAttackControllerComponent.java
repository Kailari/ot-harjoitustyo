package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;

import java.util.Iterator;

public abstract class AbstractAttackControllerComponent<A extends AbstractAttackAbility> extends AbstractControllerComponent<A> {
    @Getter @Setter(AccessLevel.PROTECTED) private transient GameObject target;
    @Getter @Setter(AccessLevel.PROTECTED) private transient boolean wantsPerform;
    @Setter(AccessLevel.PROTECTED) private transient Direction targetDirection = Direction.NONE;
    private transient Iterator<Direction> directionIterator;

    /**
     * Hakee nykyisen suunnan johon kykyä ollaan käyttämässä.
     *
     * @return suunta johon kykyä ollaan käyttämässä tai NONE jos ei olla
     */
    @NonNull
    public Direction getTargetDirection() {
        if (this.targetDirection == null) {
            this.targetDirection = Direction.NONE;
        }
        return this.targetDirection;
    }

    @Override
    public boolean wants(@NonNull A ability) {
        return getTarget() != null && isWantsPerform();
    }

    @Override
    public void abilityPerformed(A ability) {
        setTarget(null);
        setTargetDirection(Direction.NONE);
        setWantsPerform(false);
    }

    protected void findNewTarget(@NonNull A ability) {
        val x = getCharacter().getTileX();
        val y = getCharacter().getTileY();
        setTargetDirection(this.directionIterator.next());

        for (int i = 0; i < 4; i++, setTargetDirection(this.directionIterator.next())) {
            val targetX = x + getTargetDirection().getDx();
            val targetY = y + getTargetDirection().getDy();
            val targetCandidate = getCharacter().getWorld().getObjectAt(targetX, targetY);

            if (targetCandidate != null && ability.canPerformOn(getTargetDirection()) && wantsPerformOn(getTargetDirection())) {
                setTarget(targetCandidate);
                return;
            }
        }

        setTargetDirection(Direction.NONE);
        setTarget(null);
    }

    protected boolean wantsPerformOn(Direction targetDirection) {
        return targetDirection != Direction.NONE;
    }

    protected void resetTargetSearchDirection() {
        this.directionIterator = Direction.asLoopingIterator();
    }

}
