//package toilari.otlite.game.world.entity.characters.controller;
//
//import lombok.val;
//import org.junit.jupiter.api.Test;
//import toilari.otlite.game.input.IInputHandler;
//import toilari.otlite.game.input.Input;
//import toilari.otlite.game.input.Key;
//import toilari.otlite.game.world.World;
//import toilari.otlite.game.world.entities.TurnObjectManager;
//import toilari.otlite.game.world.entities.characters.CharacterAttributes;
//import toilari.otlite.game.world.entities.characters.PlayerCharacter;
//import toilari.otlite.game.world.entities.characters.controller.PlayerController;
//import toilari.otlite.game.world.level.Tile;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PlayerControllerTest {
//    @Test
//    void getInputRawReturnsValuesProvidedByInputHandler() {
//        Input.init(new TestInputHandler());
//        val controller = new PlayerController(true);
//
//        assertEquals(1, controller.getMoveInputXRaw());
//        assertEquals(-1, controller.getMoveInputYRaw());
//        assertTrue(controller.getEndTurnInputRaw());
//    }
//
//    @Test
//    void getMoveInputXReturnsOneWhenInputHandlerOutputsRight() {
//        Input.init(new TestInputHandler());
//        val controller = new PlayerController<TestCharacter>(true);
//        controller.takeControl(new TestCharacter());
//
//        controller.update(new TurnObjectManager());
//
//        assertEquals(1, controller.getMoveInputX());
//    }
//
//    @Test
//    void getMoveInputXReturnsMinusOneWhenInputHandlerOutputsLeft() {
//        val handler = new TestInputHandler();
//        handler.flag = true;
//        Input.init(handler);
//        val controller = new PlayerController<TestCharacter>(true);
//        controller.takeControl(new TestCharacter());
//
//        controller.update(new TurnObjectManager());
//
//        assertEquals(-1, controller.getMoveInputX());
//    }
//
//    @Test
//    void getMoveInputYReturnsMinusOneWhenInputHandlerOutputsUp() {
//        Input.init(new TestInputHandler());
//        val controller = new PlayerController<TestCharacter>(true);
//        controller.takeControl(new TestCharacter());
//
//        controller.update(new TurnObjectManager());
//
//        assertEquals(-1, controller.getMoveInputY());
//    }
//
//    @Test
//    void getMoveInputYReturnsOneWhenInputHandlerOutputsDown() {
//        val handler = new TestInputHandler();
//        Input.init(handler);
//        handler.flag = true;
//        val controller = new PlayerController<TestCharacter>(true);
//        controller.takeControl(new TestCharacter());
//
//        controller.update(new TurnObjectManager());
//
//        assertEquals(1, controller.getMoveInputY());
//    }
//
//    @Test
//    @SuppressWarnings("ConstantConditions")
//    void controllerUpdateThrowsIfTurnManagerIsNull() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new PlayerController(true);
//        assertThrows(NullPointerException.class, () -> controller.update(null));
//    }
//
//    @Test
//    void controllerUpdateEndsTurnWhenActionPointsRunOutAndAutoEndTurnIsTrue() {
//        val handler = new TestInputHandler();
//        handler.flag = true;
//        Input.init(handler);
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 0));
//        val controller = new PlayerController(true);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//
//        controller.update(manager);
//
//        assertEquals(1, manager.getTotalTurn());
//    }
//
//    @Test
//    void controllerUpdateDoesNotEndTurnWhenActionPointsRunOutAndAutoEndTurnIsFalse() {
//        val handler = new TestInputHandler();
//        handler.flag = true;
//        Input.init(handler);
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 0));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//
//        controller.update(manager);
//
//        assertEquals(0, manager.getTotalTurn());
//    }
//
//    @Test
//    void controllerUpdateEndsTurnWhenEndTurnInputIsOn() {
//        val handler = new TestInputHandler();
//        Input.init(handler);
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 0));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//
//        controller.update(manager);
//
//        assertEquals(1, manager.getTotalTurn());
//    }
//
//    @Test
//    void controllerUpdateDoesNotEndTurnWhenEndTurnInputIsTrueOnSecondUpdateInRow() {
//        val handler = new TestInputHandler();
//        Input.init(handler);
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 0));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//        controller.update(manager);
//        controller.update(manager);
//
//        assertEquals(1, manager.getTotalTurn());
//    }
//
//    @Test
//    void controllerUpdateSkipsToNextTurnIfCharacterIsRemoved() {
//        val handler = new TestInputHandler();
//        Input.init(handler);
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 0));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//        character.remove();
//
//        controller.update(manager);
//
//        assertEquals(1, manager.getTotalTurn());
//    }
//
//    @Test
//    void getMoveInputReturnsZeroAfterSecondUpdateWhenInputIsHeldPressed() {
//        val handler = new TestInputHandler();
//        Input.init(handler);
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//        controller.update(manager);
//        controller.update(manager);
//
//        assertEquals(0, controller.getMoveInputX());
//        assertEquals(0, controller.getMoveInputY());
//    }
//
//    @Test
//    void wantsAttackReturnsFalseIfInputIsZeros() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//        assertFalse(controller.wantsAttack());
//    }
//
//    @Test
//    void wantsMoveReturnsFalseIfInputIsZeros() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        manager.spawn(character);
//
//        assertFalse(controller.wantsMove());
//    }
//
//    @Test
//    void wantsMoveReturnsTrueIfInputIsRightAndThereIsNoAttackTarget() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
//        val controller = new PlayerController(false);
//        character.giveControlTo(controller);
//        character.setPos(Tile.SIZE_IN_WORLD, 6 * Tile.SIZE_IN_WORLD);
//        manager.spawn(character);
//
//        controller.update(manager);
//
//        assertTrue(controller.wantsMove());
//    }
//
//    private class TestInputHandler implements IInputHandler {
//        boolean flag;
//
//        @Override
//        public boolean isKeyDown(Key key) {
//            if (!this.flag) {
//                return key == Key.RIGHT || key == Key.UP || key == Key.SPACE;
//            } else {
//                return key == Key.LEFT || key == Key.DOWN;
//            }
//        }
//
//        @Override
//        public int mouseX() {
//            return 0;
//        }
//
//        @Override
//        public int mouseY() {
//            return 0;
//        }
//
//        @Override
//        public boolean isMouseDown(int button) {
//            return false;
//        }
//    }
//
//
//    private static class TestCharacter extends PlayerCharacter {
//        TestCharacter(CharacterAttributes attributes) {
//            super(attributes);
//        }
//
//        TestCharacter() {
//            super(new CharacterAttributes(10.0f, 1, 1, 1));
//        }
//    }
//}
