package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;
import toilari.otlite.game.world.level.KillTile;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.TileMapping;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MoveControllerComponentTest {
    @Test
    void availableDirectionsHasAllDirectionsAfterMoveInputWhenThereAreNoObstacles() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(2, 2,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        component.updateInput(ability);
        assertFalse(component.getAvailableDirections().contains(Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertTrue(component.getAvailableDirections().contains(direction));
        }
    }

    @Test
    void availableDirectionsDoesNotContainWallTiles() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        component.updateInput(ability);
        assertFalse(component.getAvailableDirections().contains(Direction.NONE));
        assertFalse(component.getAvailableDirections().contains(Direction.LEFT));
        assertFalse(component.getAvailableDirections().contains(Direction.UP));
        assertTrue(component.getAvailableDirections().contains(Direction.RIGHT));
        assertTrue(component.getAvailableDirections().contains(Direction.DOWN));
    }

    @Test
    void availableDirectionsIsEmptyWhenSurroundedByWalls() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(1, 6,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        component.updateInput(ability);
        assertTrue(component.getAvailableDirections().isEmpty());
    }

    @Test
    void availableDirectionsDoesNotContainDangerousTilesWhenNotPanicking() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(6, 6,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        component.updateInput(ability);
        assertTrue(component.getAvailableDirections().isEmpty());

        character.setTilePos(4, 4);
        component.updateInput(ability);
        assertFalse(component.getAvailableDirections().contains(Direction.NONE));
        assertFalse(component.getAvailableDirections().contains(Direction.UP));
        assertFalse(component.getAvailableDirections().contains(Direction.LEFT));
        assertTrue(component.getAvailableDirections().contains(Direction.RIGHT));
        assertTrue(component.getAvailableDirections().contains(Direction.DOWN));
    }

    @Test
    void availableDirectionsContainsDangerousTilesWhilePanicking() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(6, 6,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);
        character.panic(0, 0);

        component.updateInput(ability);
        assertFalse(component.getAvailableDirections().contains(Direction.NONE));
        assertTrue(component.getAvailableDirections().contains(Direction.UP));
        assertTrue(component.getAvailableDirections().contains(Direction.LEFT));
        assertTrue(component.getAvailableDirections().contains(Direction.RIGHT));
        assertTrue(component.getAvailableDirections().contains(Direction.DOWN));

        character.setTilePos(4, 4);
        component.updateInput(ability);
        assertFalse(component.getAvailableDirections().contains(Direction.NONE));
        assertTrue(component.getAvailableDirections().contains(Direction.UP));
        assertTrue(component.getAvailableDirections().contains(Direction.LEFT));
        assertTrue(component.getAvailableDirections().contains(Direction.RIGHT));
        assertTrue(component.getAvailableDirections().contains(Direction.DOWN));
    }

    @Test
    void availableDirectionsHasTileWithRemovedObject() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(2, 2,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        val around = FakeCharacterObject.createAround(manager, character);
        around.get(Direction.UP).remove();

        component.updateInput(ability);
        assertTrue(component.getAvailableDirections().contains(Direction.UP));
    }

    @Test
    void availableDirectionsHasTileWithDeadObject() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(2, 2,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        val around = FakeCharacterObject.createAround(manager, character);
        around.get(Direction.UP).setHealth(0.0f);

        component.updateInput(ability);
        assertTrue(component.getAvailableDirections().contains(Direction.UP));
    }

    @Test
    void availableDirectionsDoesNotHaveTIlesWithAliveObjectsInThem() {
        val world = createWorld();
        val manager = world.getObjectManager();

        val ability = new MoveAbility();
        val component = new TestMoveControllerComponent();
        val character = FakeCharacterObject.createAtWithAbilities(2, 2,
            new AbilityEntry<>(0, ability, component)
        );
        manager.spawn(character);

        val around = FakeCharacterObject.createAround(manager, character);
        around.get(Direction.UP).setHealth(0.0f);
        around.get(Direction.UP).remove();

        component.updateInput(ability);
        assertFalse(component.getAvailableDirections().contains(Direction.NONE));
        assertFalse(component.getAvailableDirections().contains(Direction.RIGHT));
        assertFalse(component.getAvailableDirections().contains(Direction.LEFT));
        assertFalse(component.getAvailableDirections().contains(Direction.DOWN));
        assertTrue(component.getAvailableDirections().contains(Direction.UP));
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void updateInputThrowsIfAbilityIsNull() {
        assertThrows(NullPointerException.class, () -> new MoveControllerComponent() {
            @Override
            protected void doUpdateInput(@NonNull MoveAbility ability) {
            }
        }.updateInput(null));
    }

    @Test
    void getInputDirectionReturnsNoneWhenInputIsZeros() {
        assertEquals(Direction.NONE, new TestMoveControllerComponent(0, 0).getInputDirection());
    }

    @Test
    void getInputDirectionReturnsRightWhenXInputIsOne() {
        assertEquals(Direction.RIGHT, new TestMoveControllerComponent(1, 0).getInputDirection());
    }

    @Test
    void getInputDirectionReturnsLeftWhenXInputIsMinusOne() {
        assertEquals(Direction.LEFT, new TestMoveControllerComponent(-1, 0).getInputDirection());
    }

    @Test
    void getInputDirectionReturnsDownWhenYInputIsOne() {
        assertEquals(Direction.DOWN, new TestMoveControllerComponent(0, 1).getInputDirection());
    }

    @Test
    void getInputDirectionReturnsUpWhenYInputIsMinusOne() {
        assertEquals(Direction.UP, new TestMoveControllerComponent(0, -1).getInputDirection());
    }

    @Test
    void getInputDirectionXAxisOverridesYAxis() {
        assertEquals(Direction.RIGHT, new TestMoveControllerComponent(1, 1).getInputDirection());
        assertEquals(Direction.RIGHT, new TestMoveControllerComponent(1, -1).getInputDirection());
        assertEquals(Direction.LEFT, new TestMoveControllerComponent(-1, 1).getInputDirection());
        assertEquals(Direction.LEFT, new TestMoveControllerComponent(-1, -1).getInputDirection());
    }

    private static World createWorld() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val mapping = new HashMap<String, Byte>();
        mapping.put("wall", (byte) 0);
        mapping.put("floor", (byte) 1);
        mapping.put("hole", (byte) 2);
        val tiles = Arrays.asList(
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 0, "floor"),
            new KillTile(false, true, 0, "hole")
        );
        val level = new byte[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 1, 1, 1, 2, 1, 1, 0,
            0, 1, 1, 1, 2, 1, 1, 0,
            0, 1, 0, 2, 1, 1, 1, 2,
            0, 0, 1, 1, 1, 2, 2, 2,
            0, 1, 0, 1, 2, 2, 1, 2,
            0, 0, 0, 0, 0, 2, 2, 2,
        };
        world.changeLevel(new Level(8, 8, new TileMapping(() -> tiles, mapping), level));

        return world;
    }

    @NoArgsConstructor
    private class TestMoveControllerComponent extends MoveControllerComponent {
        TestMoveControllerComponent(int inputX, int inputY) {
            setInputX(inputX);
            setInputY(inputY);
        }

        @Override
        protected void doUpdateInput(@NonNull MoveAbility ability) {
        }
    }
}
