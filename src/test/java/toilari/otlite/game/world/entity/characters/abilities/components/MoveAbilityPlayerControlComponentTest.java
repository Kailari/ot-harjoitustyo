package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeInputHandler;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.PlayerCharacter;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MoveAbilityPlayerControlComponentTest {
    @Test
    void getMoveInputXReturnsOneWhenInputHandlerOutputsRight() {
        Input.init(new FakeInputHandler(Key.RIGHT));
        val component = new MoveControllerComponent.Player(new PlayerCharacter());

        assertEquals(1, component.getMoveInputX());
    }

    @Test
    void getMoveInputXReturnsMinusOneWhenInputHandlerOutputsLeft() {
        Input.init(new FakeInputHandler(Key.LEFT));
        val component = new MoveControllerComponent.Player(new PlayerCharacter());

        assertEquals(-1, component.getMoveInputX());
    }

    @Test
    void getMoveInputYReturnsMinusOneWhenInputHandlerOutputsUp() {
        Input.init(new FakeInputHandler(Key.UP));
        val component = new MoveControllerComponent.Player(new PlayerCharacter());

        assertEquals(-1, component.getMoveInputY());
    }

    @Test
    void getMoveInputYReturnsOneWhenInputHandlerOutputsDown() {
        Input.init(new FakeInputHandler(Key.DOWN));
        val component = new MoveControllerComponent.Player(new PlayerCharacter());

        assertEquals(1, component.getMoveInputY());
    }

    @Test
    void wantsMoveReturnsFalseIfInputNoInput() {
        Input.init(new FakeInputHandler());
        val component = new MoveControllerComponent.Player(new PlayerCharacter());


        component.updateInput();
        assertFalse(component.wants());
    }

    @Test
    void getInputDirectionReturnsNoneAfterSecondUpdateWhenInputIsHeldPressed() {
        Input.init(new FakeInputHandler(Key.RIGHT, Key.DOWN));
        val component = new MoveControllerComponent.Player(new PlayerCharacter());

        component.updateInput();
        component.updateInput();
        assertEquals(Direction.NONE, component.getInputDirection());
    }
}
