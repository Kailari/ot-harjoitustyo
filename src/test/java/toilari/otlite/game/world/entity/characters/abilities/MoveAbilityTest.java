package toilari.otlite.game.world.entity.characters.abilities;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.util.Direction;
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
        val character = FakeCharacterObject.create();
        val ability = new MoveAbility();
        ability.init(character);
        assertEquals(character.getAttributes().getMoveCooldown(), ability.getCooldownLength());
    }

    @Test
    void getCostMatchesCharacterAttributes() {
        val character = FakeCharacterObject.create();
        val ability = new MoveAbility();
        ability.init(character);
        assertEquals(character.getAttributes().getMoveCost(), ability.getCost());
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveOutOfBounds() {
        val world = FakeWorld.createWithLevel(createLevel());

        val characterAtTopLeft = FakeCharacterObject.createAt(0, 0);
        val abilityAtTopLeft = new MoveAbility();
        abilityAtTopLeft.init(characterAtTopLeft);
        world.getObjectManager().spawn(characterAtTopLeft);

        val characterAtBottomRight = FakeCharacterObject.createAt(7, 7);
        val abilityAtBottomRight = new MoveAbility();
        abilityAtBottomRight.init(characterAtBottomRight);
        world.getObjectManager().spawn(characterAtBottomRight);

        assertFalse(abilityAtTopLeft.canMoveTo(Direction.LEFT, 1));
        assertFalse(abilityAtTopLeft.canMoveTo(Direction.UP, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.RIGHT, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.DOWN, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveInsideAWall() {
        val world = FakeWorld.createWithLevel(createLevel());

        val characterAtTopLeft = FakeCharacterObject.createAt(0, 0);
        val abilityAtTopLeft = new MoveAbility();
        abilityAtTopLeft.init(characterAtTopLeft);
        world.getObjectManager().spawn(characterAtTopLeft);

        val characterAtBottomRight = FakeCharacterObject.createAt(7, 7);
        val abilityAtBottomRight = new MoveAbility();
        abilityAtBottomRight.init(characterAtBottomRight);
        world.getObjectManager().spawn(characterAtBottomRight);

        assertFalse(abilityAtTopLeft.canMoveTo(Direction.RIGHT, 1));
        assertFalse(abilityAtTopLeft.canMoveTo(Direction.DOWN, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.LEFT, 1));
        assertFalse(abilityAtBottomRight.canMoveTo(Direction.UP, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveInsideNonRemovedCharacter() {
        val world = FakeWorld.createWithLevel(createLevel());

        val characterA = FakeCharacterObject.createAt(2, 2);
        val abilityA = new MoveAbility();
        abilityA.init(characterA);
        world.getObjectManager().spawn(characterA);

        val characterB = FakeCharacterObject.createAt(3, 2);
        val abilityB = new MoveAbility();
        abilityB.init(characterB);
        world.getObjectManager().spawn(characterB);

        assertFalse(abilityA.canMoveTo(Direction.RIGHT, 1));
        assertFalse(abilityB.canMoveTo(Direction.LEFT, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenDeltaIsZero() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(2, 2);
        val ability = new MoveAbility();
        ability.init(character);
        world.getObjectManager().spawn(character);

        assertFalse(ability.canMoveTo(Direction.LEFT, 0));
    }

    @Test
    void canMoveToReturnsFalseWhenDirectionIsNone() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(2, 2);
        val ability = new MoveAbility();
        ability.init(character);
        world.getObjectManager().spawn(character);

        assertFalse(ability.canMoveTo(Direction.NONE, 0));
    }

    @Test
    void canMoveToReturnsTrueWhenCharacterCanMove() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(2, 2);
        val ability = new MoveAbility();
        ability.init(character);
        world.getObjectManager().spawn(character);

        assertTrue(ability.canMoveTo(Direction.RIGHT, 1));
    }

    @Test
    void canMoveToReturnsTrueWhenObjectInTargetTileIsRemoved() {
        val world = FakeWorld.createWithLevel(createLevel());

        val characterA = FakeCharacterObject.createAt(2, 2);
        val abilityA = new MoveAbility();
        abilityA.init(characterA);
        world.getObjectManager().spawn(characterA);

        val characterB = FakeCharacterObject.createAt(3, 2);
        world.getObjectManager().spawn(characterB);
        characterB.remove();

        assertTrue(abilityA.canMoveTo(Direction.RIGHT, 1));
    }


    @Test
    void moveChangesPositionWhenMoveSucceeds() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(1, 2);
        val ability = new MoveAbility();
        ability.init(character);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        world.getObjectManager().spawn(character);

        ability.perform(component);

        assertEquals(2, character.getTileX());
        assertEquals(2, character.getTileY());
    }

    @Test
    void moveReturnsTrueWhenMoveSucceeds() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(1, 2);
        val ability = new MoveAbility();
        ability.init(character);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        world.getObjectManager().spawn(character);

        assertTrue(ability.perform(component));
    }

    @Test
    void moveDoesNotMoveCharacterInsideWall() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(1, 1);
        val ability = new MoveAbility();
        ability.init(character);
        val component = new TestMoveControllerComponent(Direction.LEFT);
        world.getObjectManager().spawn(character);

        ability.perform(component);

        assertEquals(1, character.getTileX());
        assertEquals(1, character.getTileY());
    }

    @Test
    void moveReturnsFalseWhenMovingFails() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(1, 1);
        val ability = new MoveAbility();
        ability.init(character);
        val component = new TestMoveControllerComponent(Direction.LEFT);
        world.getObjectManager().spawn(character);

        assertFalse(ability.perform(component));
    }

    @Test
    void moveReturnsTrueWhenObjectInTargetTileIsRemoved() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(1, 2);
        val ability = new MoveAbility();
        ability.init(character);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        world.getObjectManager().spawn(character);

        val obstacle = FakeCharacterObject.createAt(2, 2);
        world.getObjectManager().spawn(obstacle);
        obstacle.remove();

        assertTrue(ability.perform(component));
    }

    @Test
    void moveChangesPositionWhenObjectInTargetTileIsRemoved() {
        val world = FakeWorld.createWithLevel(createLevel());

        val character = FakeCharacterObject.createAt(1, 2);
        val ability = new MoveAbility();
        ability.init(character);
        val component = new TestMoveControllerComponent(Direction.RIGHT);
        world.getObjectManager().spawn(character);

        val obstacle = FakeCharacterObject.createAt(2, 2);
        world.getObjectManager().spawn(obstacle);
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
        public void doUpdateInput(@NonNull MoveAbility ability) {
        }
    }
}
