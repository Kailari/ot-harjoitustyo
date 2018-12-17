package toilari.otlite.fake;

import lombok.NonNull;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;

import java.util.Random;

public class FakeAttackAbility extends AbstractAttackAbility<FakeAttackAbility, FakeAttackControllerComponent> {
    private final int cost;
    private final int cooldown;

    public static FakeAttackAbility create(int cost, int cooldown) {
        return new FakeAttackAbility(cost, cooldown);
    }

    public static FakeAttackAbility createFree() {
        return create(0, 0);
    }

    private FakeAttackAbility(int cost, int cooldown) {
        super("fakeAttack", new Random(1337));
        this.cost = cost;
        this.cooldown = cooldown;
    }

    public static FakeAttackAbility createWithoutTargetValidation(int cost, int cooldown) {
        return new FakeAttackAbility(cost, cooldown) {
            @Override
            public boolean canPerformOn(GameObject target, @NonNull Direction direction) {
                return true;
            }
        };
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public int getCooldownLength() {
        return this.cooldown;
    }
}
