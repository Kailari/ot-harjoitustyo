package toilari.otlite.fake;

import toilari.otlite.game.world.entities.characters.abilities.AbstractAttackAbility;

public class FakeAttackAbility extends AbstractAttackAbility<FakeAttackAbility, FakeAttackControllerComponent> {
    private final int cost;
    private final int cooldown;

    public static FakeAttackAbility create(int cost, int cooldown) {
        return new FakeAttackAbility(cost, cooldown);
    }

    private FakeAttackAbility(int cost, int cooldown) {
        super("fakeAttack");
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
}
