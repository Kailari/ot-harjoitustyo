package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Liikkumiskyvyn ohjainkomponentti.
 */
public abstract class MoveControllerComponent extends AbstractControllerComponent<MoveAbility> {
    @Setter(AccessLevel.PROTECTED) private transient int inputX, inputY;
    @Getter(AccessLevel.PROTECTED) private transient final Random random;
    @Getter private transient List<Direction> availableDirections = new ArrayList<>();

    protected MoveControllerComponent() {
        this.random = new Random();
    }

    protected MoveControllerComponent(AbstractControllerComponent<MoveAbility> template) {
        super(template);
        this.random = new Random();
    }

    /**
     * Luo uuden ohjainkomponentin.
     *
     * @param seed satunnaislukugeneraattorin siemenluku
     */
    public MoveControllerComponent(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public boolean wants(MoveAbility ability) {
        return getCharacter().isPanicking() || getInputDirection() != Direction.NONE;
    }

    /**
     * Hakee suunnan johon hahmo haluaa liikkua.
     *
     * @return suunta johon hahmo haluaa liikkua
     */
    public Direction getInputDirection() {
        if (this.inputX != 0) {
            return this.inputX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (this.inputY != 0) {
            return this.inputY > 0 ? Direction.DOWN : Direction.UP;
        } else {
            return Direction.NONE;
        }
    }

    protected abstract void doUpdateInput(@NonNull MoveAbility ability);

    @Override
    public final void updateInput(@NonNull MoveAbility ability) {
        if (getCharacter().getWorld().getCurrentLevel() != null) {
            refreshMoveDirections(ability);

            if (getAvailableDirections().isEmpty()) {
                setInputX(0);
                setInputY(0);
                return;
            }
        }

        if (!getCharacter().isPanicking()) {
            doUpdateInput(ability);
        } else {
            handlePanickMove();
        }
    }

    private void handlePanickMove() {
        Direction direction;
        if (!this.availableDirections.isEmpty()) {
            this.availableDirections.sort(Comparator.comparingInt(this::distanceToPanicSource));
            direction = this.availableDirections.get(this.availableDirections.size() - 1);
        } else {
            direction = Direction.NONE;
        }
        setInputX(direction.getDx());
        setInputY(direction.getDy());
    }

    private int distanceToPanicSource(Direction direction) {
        val dx = (getCharacter().getTileX() + direction.getDx()) - getCharacter().getPanicSourceX();
        val dy = (getCharacter().getTileY() + direction.getDy()) - getCharacter().getPanicSourceY();
        return dx * dx + dy * dy;
    }

    private void refreshMoveDirections(MoveAbility ability) {
        this.availableDirections.clear();

        val world = getCharacter().getWorld();
        val x = getCharacter().getTileX();
        val y = getCharacter().getTileY();

        for (val direction : Direction.asIterable()) {
            val tileAtTarget = world.getTileAt(x + direction.getDx(), y + direction.getDy());
            val panickingOrNotDangerous = !tileAtTarget.isDangerous() || getCharacter().isPanicking();

            if (ability.canMoveTo(direction, 1) && panickingOrNotDangerous) {
                this.availableDirections.add(direction);
            }
        }
    }


    @Override
    public void abilityPerformed(MoveAbility ability) {
        this.inputX = 0;
        this.inputY = 0;
    }

    @Override
    public void reset() {
        this.inputX = 0;
        this.inputY = 0;
    }

}
