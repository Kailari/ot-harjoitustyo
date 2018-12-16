package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeInputHandler;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerEndTurnControllerComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerEndTurnControllerComponentTest {
    @Test
    void doesNotEndTurnWhenActionPointsRunOutAndAutoEndTurnIsFalse() {
        Input.init(new FakeInputHandler());
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), new PlayerEndTurnControllerComponent()));
        manager.spawn(character);

        manager.spendActionPoints(manager.getRemainingActionPoints());
        manager.update(1.0f);
        assertEquals(0, manager.getTotalTurn());
    }

    @Test
    void endsTurnWhenActionPointsRunOutAndAutoEndTurnIsTrue() {
        Input.init(new FakeInputHandler());
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val component = new PlayerEndTurnControllerComponent();
        component.setAutoEndTurn(true);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), component));
        manager.spawn(character);

        manager.spendActionPoints(manager.getRemainingActionPoints());
        manager.update(1.0f);
        assertEquals(1, manager.getTotalTurn());
    }

    @Test
    void updateEndsTurnWhenEndTurnInputIsOn() {
        Input.init(new FakeInputHandler(Key.SPACE));
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), new PlayerEndTurnControllerComponent()));
        manager.spawn(character);

        manager.update(1.0f);
        assertEquals(1, manager.getTotalTurn());
    }

    @Test
    void updateDoesNotEndTurnWhenEndTurnInputIsTrueOnSecondUpdateInRow() {
        Input.init(new FakeInputHandler(Key.SPACE));
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new EndTurnAbility(), new PlayerEndTurnControllerComponent()));
        manager.spawn(character);

        manager.update(1.0f);
        Input.getHandler().update();
        manager.update(1.0f);

        assertEquals(1, manager.getTotalTurn());
    }

}
