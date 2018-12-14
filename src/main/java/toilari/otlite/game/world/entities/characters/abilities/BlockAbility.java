package toilari.otlite.game.world.entities.characters.abilities;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.BlockControllerComponent;

/**
 * Hahmon kyky torjua hyökkäyksiä.
 */
public class BlockAbility
    extends AbstractAbility<BlockAbility, BlockControllerComponent>
    implements ITargetedAbility<BlockAbility, BlockControllerComponent> {

    @Getter private transient int blocksAvailable;
    private String stateOverride = "block";

    /**
     * Luo uuden kyvyn.
     */
    public BlockAbility() {
        super("block");
    }

    private int getMaxBlocks() {
        return Attribute.Endurance.getNumberOfAttacksBlocked(getCharacter().getLevels());
    }

    @Override
    public int getCost() {
        return Attribute.Endurance.getBlockingCost(getCharacter().getLevels());
    }

    @Override
    public int getCooldownLength() {
        return Attribute.Endurance.getBlockingCooldown(getCharacter().getLevels());
    }

    @Override
    public void onBeginTurn() {
        this.blocksAvailable = 0;
        getCharacter().setStateOverride(null);
    }

    @Override
    public boolean perform(@NonNull BlockControllerComponent component) {
        this.blocksAvailable = getMaxBlocks();
        getCharacter().setStateOverride(this.stateOverride);
        return true;
    }

    /**
     * Torjuu hyökkäyksen.
     *
     * @param character hahmo joka hyökkää.
     */
    public void blockAttack(CharacterObject character) {
        if (this.blocksAvailable == 0) {
            throw new IllegalStateException("Cannot block attacks if out of blocks!");
        }
        this.blocksAvailable--;

        if (this.blocksAvailable == 0) {
            getCharacter().setStateOverride(null);
        }
    }

    @Override
    public boolean canPerformOn(GameObject target, Direction direction) {
        return target.equals(getCharacter());
    }

    @Override
    public boolean canTargetSelf() {
        return true;
    }
}
