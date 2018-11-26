package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeInputHandler;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerEndTurnAbilityTest {
    @Test
    void doesNotEndTurnWhenActionPointsRunOutAndAutoEndTurnIsFalse() {
        Input.init(new FakeInputHandler());
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.addAbility(new EndTurnAbility(character, 0), new EndTurnControllerComponent.Player(character, false));
        manager.spawn(character);

        manager.spendActionPoints(manager.getRemainingActionPoints());
        manager.update();
        assertEquals(0, manager.getTotalTurn());
    }

    @Test
    void endsTurnWhenActionPointsRunOutAndAutoEndTurnIsTrue() {
        Input.init(new FakeInputHandler());
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.addAbility(new EndTurnAbility(character, 0), new EndTurnControllerComponent.Player(character, true));
        manager.spawn(character);

        manager.spendActionPoints(manager.getRemainingActionPoints());
        manager.update();
        assertEquals(1, manager.getTotalTurn());
    }

    @Test
    void updateEndsTurnWhenEndTurnInputIsOn() {
        Input.init(new FakeInputHandler(Key.SPACE));
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.addAbility(new EndTurnAbility(character, 0), new EndTurnControllerComponent.Player(character, false));
        manager.spawn(character);

        manager.update();
        assertEquals(1, manager.getTotalTurn());
    }

    @Test
    void updateDoesNotEndTurnWhenEndTurnInputIsTrueOnSecondUpdateInRow() {
        Input.init(new FakeInputHandler(Key.SPACE));
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.addAbility(new EndTurnAbility(character, 0), new EndTurnControllerComponent.Player(character, false));
        manager.spawn(character);

        manager.update();
        manager.update();

        assertEquals(1, manager.getTotalTurn());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void performThrowsWhenGivenNullArgument() {
        assertThrows(NullPointerException.class, () -> new EndTurnAbility(new FakeCharacterObject(), 0).perform(null));
    }
}
