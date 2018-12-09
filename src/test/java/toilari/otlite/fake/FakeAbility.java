package toilari.otlite.fake;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;

public class FakeAbility extends AbstractAbility<FakeAbility, FakeControllerComponent> {
    private final int cost;
    private final int cooldown;

    public static FakeAbility createFree() {
        return new FakeAbility(0, 0);
    }

    public static FakeAbility createWithCost(int cost, int cooldown) {
        return new FakeAbility(cost, cooldown);
    }

    private FakeAbility(int cost, int cooldown) {
        super("fake");
        this.cost = cost;
        this.cooldown = cooldown;
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public int getCooldownLength() {
        return this.cooldown;
    }

    @Override
    public boolean perform(@NonNull FakeControllerComponent component) {
        return false;
    }
}
