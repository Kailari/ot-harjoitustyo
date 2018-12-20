package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeInputHandler;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerMoveControllerComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MovePlayerControlComponentTest {
    @Test
    void getMoveInputXReturnsOneWhenInputHandlerOutputsRight() {
        Input.init(new FakeInputHandler(Key.RIGHT));
        val component = new PlayerMoveControllerComponent();

        assertEquals(1, component.getMoveInputX());
    }

    @Test
    void getMoveInputXReturnsMinusOneWhenInputHandlerOutputsLeft() {
        Input.init(new FakeInputHandler(Key.LEFT));
        val component = new PlayerMoveControllerComponent();

        assertEquals(-1, component.getMoveInputX());
    }

    @Test
    void getMoveInputYReturnsMinusOneWhenInputHandlerOutputsUp() {
        Input.init(new FakeInputHandler(Key.UP));
        val component = new PlayerMoveControllerComponent();

        assertEquals(-1, component.getMoveInputY());
    }

    @Test
    void getMoveInputYReturnsOneWhenInputHandlerOutputsDown() {
        Input.init(new FakeInputHandler(Key.DOWN));
        val component = new PlayerMoveControllerComponent();

        assertEquals(1, component.getMoveInputY());
    }

    @Test
    void wantsMoveReturnsFalseIfNoInput() {
        Input.init(new FakeInputHandler());
        val world = FakeWorld.create();

        val ability = new MoveAbility();
        val component = new PlayerMoveControllerComponent();
        val player = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component));
        world.getObjectManager().spawn(player);

        component.updateInput(ability);
        assertFalse(component.wants(ability));
    }

    @Test
    void getInputDirectionReturnsNoneAfterSecondUpdateWhenInputIsHeldPressed() {
        Input.init(new FakeInputHandler(Key.RIGHT, Key.DOWN));
        val world = FakeWorld.create();

        val ability = new MoveAbility();
        val component = new PlayerMoveControllerComponent();
        val player = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component)
        );
        world.getObjectManager().spawn(player);

        component.updateInput(ability);
        Input.getHandler().update();
        component.updateInput(ability);
        assertEquals(Direction.NONE, component.getInputDirection());
    }
}
