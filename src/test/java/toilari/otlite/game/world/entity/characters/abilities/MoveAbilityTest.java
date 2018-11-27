package toilari.otlite.game.world.entity.characters.abilities;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.game.world.level.TileMapping;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MoveAbilityTest {
    @Test
    void getCooldownLengthMatchesCharacterAttributes() {
        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        assertEquals(character.getAttributes().getMoveCooldown(), ability.getCooldownLength());
    }

    @Test
    void getCostMatchesCharacterAttributes() {
        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        assertEquals(character.getAttributes().getMoveCost(), ability.getCost());
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveOutOfBounds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val characterAtTopLeft = new FakeCharacterObject();
        val abilityAtTopLeft = new MoveAbility();
        abilityAtTopLeft.init(characterAtTopLeft, 0);
        characterAtTopLeft.setTilePos(0, 0);
        manager.spawn(characterAtTopLeft);

        val characterAtBottomRight = new FakeCharacterObject();
        val abilityAtBottomRight = new MoveAbility();
        abilityAtBottomRight.init(characterAtBottomRight, 0);
        characterAtBottomRight.setTilePos(7, 7);
        manager.spawn(characterAtBottomRight);

        assertFalse(abilityAtTopLeft.canMoveTo(Direction.LEFT, 1));
        assertFalse(abilityAtTopLeft.canMoveTo(Direction.UP, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.RIGHT, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.DOWN, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveInsideAWall() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val characterAtTopLeft = new FakeCharacterObject();
        val abilityAtTopLeft = new MoveAbility();
        abilityAtTopLeft.init(characterAtTopLeft, 0);
        characterAtTopLeft.setTilePos(0, 0);
        manager.spawn(characterAtTopLeft);

        val characterAtBottomRight = new FakeCharacterObject();
        val abilityAtBottomRight = new MoveAbility();
        abilityAtBottomRight.init(characterAtBottomRight, 0);
        characterAtBottomRight.setTilePos(7, 7);
        manager.spawn(characterAtBottomRight);

        assertFalse(abilityAtTopLeft.canMoveTo(Direction.RIGHT, 1));
        assertFalse(abilityAtTopLeft.canMoveTo(Direction.DOWN, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.LEFT, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.UP, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveInsideNonRemovedCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val characterA = new FakeCharacterObject();
        val abilityA = new MoveAbility();
        abilityA.init(characterA, 0);
        characterA.setTilePos(2, 2);
        manager.spawn(characterA);

        val characterB = new FakeCharacterObject();
        val abilityB = new MoveAbility();
        abilityB.init(characterB, 0);
        characterB.setTilePos(3, 2);
        manager.spawn(characterB);

        assertFalse(abilityA.canMoveTo(Direction.RIGHT, 1));
        assertFalse(abilityB.canMoveTo(Direction.LEFT, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenDeltaIsZero() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        character.setTilePos(2, 2);
        manager.spawn(character);

        assertFalse(ability.canMoveTo(Direction.LEFT, 0));
    }

    @Test
    void canMoveToReturnsFalseWhenDirectionIsNone() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        character.setTilePos(2, 2);
        manager.spawn(character);

        assertFalse(ability.canMoveTo(Direction.NONE, 0));
    }

    @Test
    void canMoveToReturnsTrueWhenCharacterCanMove() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        character.setTilePos(2, 2);
        manager.spawn(character);

        assertTrue(ability.canMoveTo(Direction.RIGHT, 1));
    }

    @Test
    void canMoveToReturnsTrueWhenObjectInTargetTileIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val characterA = new FakeCharacterObject();
        val abilityA = new MoveAbility();
        abilityA.init(characterA, 0);
        characterA.setTilePos(2, 2);
        manager.spawn(characterA);

        val characterB = new FakeCharacterObject();
        characterB.setTilePos(3, 2);
        manager.spawn(characterB);
        characterB.remove();

        assertTrue(abilityA.canMoveTo(Direction.RIGHT, 1));
    }


    @Test
    void moveChangesPositionWhenMoveSucceeds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        character.setTilePos(1, 2);
        manager.spawn(character);

        ability.perform(component);

        assertEquals(2, character.getTileX());
        assertEquals(2, character.getTileY());
    }

    @Test
    void moveReturnsTrueWhenMoveSucceeds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        character.setTilePos(1, 2);
        manager.spawn(character);

        assertTrue(ability.perform(component));
    }

    @Test
    void moveDoesNotMoveCharacterInsideWall() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        val component = new TestMoveControllerComponent(Direction.LEFT);
        character.setTilePos(1, 1);
        manager.spawn(character);

        ability.perform(component);

        assertEquals(1, character.getTileX());
        assertEquals(1, character.getTileY());
    }

    @Test
    void moveReturnsFalseWhenMovingFails() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        val component = new TestMoveControllerComponent(Direction.LEFT);
        character.setTilePos(1, 1);
        manager.spawn(character);

        assertFalse(ability.perform(component));
    }

    @Test
    void moveReturnsTrueWhenObjectInTargetTileIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        character.setTilePos(1, 2);
        manager.spawn(character);

        val obstacle = new FakeCharacterObject();
        obstacle.setTilePos(2, 2);
        manager.spawn(obstacle);
        obstacle.remove();

        assertTrue(ability.perform(component));
    }

    @Test
    void moveChangesPositionWhenObjectInTargetTileIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new FakeCharacterObject();
        val ability = new MoveAbility();
        ability.init(character, 0);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        character.setTilePos(1, 2);
        manager.spawn(character);

        val obstacle = new FakeCharacterObject();
        obstacle.setTilePos(2, 2);
        manager.spawn(obstacle);
        obstacle.remove();

        ability.perform(component);

        assertEquals(2, character.getTileX());
        assertEquals(2, character.getTileY());
    }


    private static Level createLevel() {
        val tileMappings = new TileMapping(() -> Arrays.asList(new Tile[]{
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 1, "floor"),
            new NormalTile(false, true, 2, "hole"),
        }));

        val w = tileMappings.getIndex("wall");
        val f = tileMappings.getIndex("floor");
        val h = tileMappings.getIndex("hole");

        val indices = new byte[]{
            f, w, w, w, w, w, w, w,
            w, f, f, f, f, f, f, w,
            w, f, f, f, w, w, f, w,
            w, h, h, f, f, f, w, w,
            w, h, f, f, f, f, f, w,
            w, f, f, h, h, f, f, w,
            w, f, f, f, f, f, f, w,
            w, w, w, w, w, w, w, f};

        return new Level(8, 8, tileMappings, indices);
    }

    private class TestMoveControllerComponent extends MoveControllerComponent {
        private Direction direction;

        private TestMoveControllerComponent(Direction direction) {
            this.direction = direction;
        }

        @Override
        public Direction getInputDirection() {
            return this.direction;
        }

        @Override
        public void updateInput(@NonNull MoveAbility ability) {
        }
    }
}
