package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeInputHandler;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerEndTurnControllerComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerEndTurnControllerComponentTest {
    @Test
    void doesNotEndTurnWhenActionPointsRunOutAndAutoEndTurnIsFalse() {
        Input.init(new FakeInputHandler());
        val world = FakeWorld.create();

        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), new PlayerEndTurnControllerComponent()));
        world.getObjectManager().spawn(character);

        world.getObjectManager().spendActionPoints(world.getObjectManager().getRemainingActionPoints());
        world.getObjectManager().update(1.0f);
        assertEquals(0, world.getObjectManager().getTotalTurn());
    }

    @Test
    void endsTurnWhenActionPointsRunOutAndAutoEndTurnIsTrue() {
        Input.init(new FakeInputHandler());
        val world = FakeWorld.create();

        val component = new PlayerEndTurnControllerComponent();
        component.setAutoEndTurn(true);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), component));
        world.getObjectManager().spawn(character);

        world.getObjectManager().spendActionPoints(world.getObjectManager().getRemainingActionPoints());
        world.getObjectManager().update(1.0f);
        assertEquals(1, world.getObjectManager().getTotalTurn());
    }

    @Test
    void updateEndsTurnWhenEndTurnInputIsOn() {
        Input.init(new FakeInputHandler(Key.SPACE));
        val world = FakeWorld.create();

        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), new PlayerEndTurnControllerComponent()));
        world.getObjectManager().spawn(character);

        world.getObjectManager().update(1.0f);
        assertEquals(1, world.getObjectManager().getTotalTurn());
    }

    @Test
    void updateDoesNotEndTurnWhenEndTurnInputIsTrueOnSecondUpdateInRow() {
        Input.init(new FakeInputHandler(Key.SPACE));
        val world = FakeWorld.create();

        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), new PlayerEndTurnControllerComponent()));
        world.getObjectManager().spawn(character);

        world.getObjectManager().update(1.0f);
        Input.getHandler().update();
        world.getObjectManager().update(1.0f);

        assertEquals(1, world.getObjectManager().getTotalTurn());
    }

}
