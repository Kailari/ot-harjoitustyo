package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.WarcryControllerComponent;

import java.util.ArrayList;
import java.util.Random;

/**
 * "Sotahuuto" joka kylvää pelkoa lähellä oleviin vihollisiin.
 */
public class WarcryAbility
    extends AbstractAbility<WarcryAbility, WarcryControllerComponent>
    implements ITargetedAbility<WarcryAbility, WarcryControllerComponent>, IAreaOfEffectAbility<WarcryAbility, WarcryControllerComponent> {
    @NonNull private final Random random;

    /**
     * Luo uuden kyvyn. Satunnaislukugeneraattorin siemenluku on "satunnainen"
     */
    public WarcryAbility() {
        this(new Random());
    }

    /**
     * Luo uuden kyvyn.
     *
     * @param seed satunnaislukugeneraattorin siemenluku
     */
    public WarcryAbility(long seed) {
        this(new Random(seed));
    }

    private WarcryAbility(@NonNull Random random) {
        super("Warcry");
        this.random = random;
    }

    @Override
    public boolean canAffect(GameObject object) {
        return object instanceof CharacterObject && !object.isRemoved();
    }

    @Override
    public boolean canPerformOn(GameObject target, Direction direction) {
        return getCharacter().equals(target);
    }

    @Override
    public boolean canTargetSelf() {
        return true;
    }

    @Override
    public int getCost() {
        return Attribute.Charisma.getWarcryCost(getCharacter().getLevels());
    }

    @Override
    public int getCooldownLength() {
        return Attribute.Charisma.getWarcryCooldown(getCharacter().getLevels());
    }

    @Override
    public boolean perform(@NonNull WarcryControllerComponent component) {
        val targets = findTargets();

        for (val target : targets) {
            tryApplyPanic(target);
        }

        return true;
    }

    private void tryApplyPanic(@NonNull CharacterObject target) {
        val chance = calculateFearChance(target);
        float value = this.random.nextFloat();
        if (value < chance) {
            target.panic(getCharacter().getTileX(), getCharacter().getTileY());
        }
    }

    private float calculateFearChance(@NonNull CharacterObject target) {
        val chance = Attribute.Charisma.getWarcryFearChance(getCharacter().getLevels());
        val resistance = target.getAttributes().getFearResistance();
        return chance * (1.0f - Math.min(0.99f, resistance));
    }

    @NonNull
    private ArrayList<CharacterObject> findTargets() {
        val world = getCharacter().getWorld();
        val targets = new ArrayList<CharacterObject>();
        val x = getCharacter().getTileX();
        val y = getCharacter().getTileY();
        val range = getAreaExtent();
        for (var dx = -range; dx <= range; dx++) {
            for (var dy = -range; dy <= range; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                val object = world.getObjectAt(x + dx, y + dy);
                if (object instanceof CharacterObject && !((CharacterObject) object).isDead() && canAffect(object)) {
                    targets.add((CharacterObject) object);
                }
            }
        }
        return targets;
    }

    @Override
    public int getAreaExtent() {
        return Attribute.Charisma.getWarcryRange(getCharacter().getLevels());
    }
}
