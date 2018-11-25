//package toilari.otlite.game.world.entity.characters.controller;
//
//import lombok.val;
//import org.junit.jupiter.api.Test;
//import toilari.otlite.game.world.World;
//import toilari.otlite.game.world.entities.TurnObjectManager;
//import toilari.otlite.game.world.entities.characters.AbstractCharacter;
//import toilari.otlite.game.world.entities.characters.CharacterAttributes;
//import toilari.otlite.game.world.entities.characters.CharacterController;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class CharacterControllerTest {
//    @Test
//    void giveControlWithNullControllerOnCharacterWithoutControllerDoesNothing() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val character = new TestCharacter();
//        character.giveControlTo(null);
//
//        assertNull(character.getController());
//    }
//
//    @Test
//    void takeControlWithNullCharacterOnControllerWithoutCharacterDoesNothing() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        controller.takeControl(null);
//
//        assertNull(controller.getControlledCharacter());
//    }
//
//    @Test
//    void giveControlWithValidControllerSetsReferencesForBothObjects() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        val character = new TestCharacter();
//        character.giveControlTo(controller);
//
//        assertEquals(character, controller.getControlledCharacter());
//        assertEquals(controller, character.getController());
//    }
//
//    @Test
//    void takeControlWithValidCharacterSetsReferencesForBothObjects() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        val character = new TestCharacter();
//        controller.takeControl(character);
//
//        assertEquals(character, controller.getControlledCharacter());
//        assertEquals(controller, character.getController());
//    }
//
//    @Test
//    void giveControlNullifiesPreviousControllersReferenceToCharacter() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controllerA = new TestController();
//        val controllerB = new TestController();
//        val character = new TestCharacter();
//
//        controllerA.takeControl(character);
//        controllerB.takeControl(character);
//
//        assertNull(controllerA.getControlledCharacter());
//    }
//
//    @Test
//    void takeControlNullifiesPreviousCharactersReferenceToController() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        val characterA = new TestCharacter();
//        val characterB = new TestCharacter();
//
//        characterA.giveControlTo(controller);
//        characterB.giveControlTo(controller);
//
//        assertNull(characterA.getController());
//    }
//
//    @Test
//    void beginTurnIncreasesTurnCount() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        assertEquals(0, controller.getTurnsTaken());
//        controller.beginTurn();
//        assertEquals(1, controller.getTurnsTaken());
//        controller.beginTurn();
//        assertEquals(2, controller.getTurnsTaken());
//        controller.beginTurn();
//        assertEquals(3, controller.getTurnsTaken());
//    }
//
//    @Test
//    @SuppressWarnings("ConstantConditions")
//    void controllerUpdateThrowsIfTurnManagerIsNull() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        assertThrows(NullPointerException.class, () -> controller.update(null));
//    }
//
//    @Test
//    void controllerUpdateThrowsIfControlledCharacterIsNull() {
//        val manager = new TurnObjectManager();
//        val world = new World(manager);
//        world.init();
//
//        val controller = new TestController();
//        assertThrows(IllegalStateException.class, () -> controller.update(manager));
//    }
//
//    private static class TestCharacter extends AbstractCharacter {
//        TestCharacter() {
//            super(new CharacterAttributes(10.0f, 1, 1, 1));
//        }
//    }
//
//    private static class TestController extends CharacterController<TestCharacter> {
//        private TestController() {
//            super(controlledCharacter);
//        }
//
//        @Override
//        public int getMoveInputX() {
//            return 0;
//        }
//
//        @Override
//        public int getMoveInputY() {
//            return 0;
//        }
//
//        @Override
//        public boolean wantsMove() {
//            return false;
//        }
//
//        @Override
//        public boolean wantsAttack() {
//            return false;
//        }
//    }
//}
