package toilari.otlite.game.world.entity.characters;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.game.world.level.TileMapping;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCharacterTest {
    @Test
    void constructorThrowsIfAttributesIsNull() {
        assertThrows(NullPointerException.class, () -> new TestCharacter(null));
    }

    @Test
    void characterIsAliveWhenSpawned() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter();
        manager.spawn(character);
        assertFalse(character.isDead());
    }

    @Test
    void healthIsMaxedAtSpawn() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter();
        manager.spawn(character);
        assertEquals(10.0f, character.getHealth());
    }

    @Test
    void characterIsDeadWhenHealthIsZero() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter();
        manager.spawn(character);
        character.setHealth(0.0f);

        assertTrue(character.isDead());
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveOutOfBounds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val characterAtTopLeft = new TestCharacter();
        characterAtTopLeft.setPos(0, 0);

        val characterAtBottomRight = new TestCharacter();
        characterAtBottomRight.setPos(0, 0);
        manager.spawn(characterAtBottomRight);
        manager.spawn(characterAtTopLeft);

        assertFalse(characterAtTopLeft.canMoveTo(-1, 0));
        assertFalse(characterAtTopLeft.canMoveTo(0, -1));
        assertFalse(characterAtBottomRight.canMoveTo(1, 0));
        assertFalse(characterAtBottomRight.canMoveTo(0, 1));
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveInsideAWall() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val characterAtTopLeft = new TestCharacter();
        characterAtTopLeft.setPos(0, 0);

        val characterAtBottomRight = new TestCharacter();
        characterAtBottomRight.setPos(0, 0);
        manager.spawn(characterAtBottomRight);
        manager.spawn(characterAtTopLeft);

        assertFalse(characterAtTopLeft.canMoveTo(1, 0));
        assertFalse(characterAtTopLeft.canMoveTo(0, 1));
        assertFalse(characterAtBottomRight.canMoveTo(-1, 0));
        assertFalse(characterAtBottomRight.canMoveTo(0, -1));
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveInsideNonRemovedCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val obstacle = new TestCharacter();
        obstacle.setPos(2 * Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        val character = new TestCharacter();
        character.setPos(3 * Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);
        manager.spawn(obstacle);

        assertFalse(character.canMoveTo(-1, 0));
        assertFalse(obstacle.canMoveTo(1, 0));
    }

    @Test
    void canMoveToReturnsFalseWhenDeltaPosIsZero() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(3 * Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        assertFalse(character.canMoveTo(0, 0));
    }

    @Test
    void canMoveToReturnsTrueWhenCharacterCanMove() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        assertTrue(character.canMoveTo(1, 0));
    }

    @Test
    void canMoveToReturnsTrueWhenObjectInTargetTileIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        val obstacle = new TestCharacter();
        character.setPos(2 * Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(obstacle);
        obstacle.remove();

        assertTrue(character.canMoveTo(1, 0));
    }


    @Test
    void moveChangesPositionWhenMoveSucceeds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        character.move(1, 0);

        assertEquals(2 * Tile.SIZE_IN_WORLD, character.getX());
        assertEquals(2 * Tile.SIZE_IN_WORLD, character.getY());
    }

    @Test
    void moveReturnsTrueWhenMoveSucceeds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        assertTrue(character.move(1, 0));
    }

    @Test
    void moveDoesNotMoveCharacterInsideWall() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        character.move(-1, 0);

        assertEquals(Tile.SIZE_IN_WORLD, character.getX());
        assertEquals(Tile.SIZE_IN_WORLD, character.getY());
    }

    @Test
    void moveReturnsFalseWhenMovingFails() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(1, 1);
        manager.spawn(character);

        assertFalse(character.move(-1, 0));
    }

    @Test
    void moveReturnsTrueWhenObjectInTargetTileIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        val obstacle = new TestCharacter();
        character.setPos(2 * Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(obstacle);
        obstacle.remove();

        assertTrue(character.move(1, 0));
    }

    @Test
    void moveChangesPositionWhenObjectInTargetTileIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());
        world.init();

        val character = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, 2 * Tile.SIZE_IN_WORLD);
        manager.spawn(character);

        val obstacle = new TestCharacter();
        character.setPos(Tile.SIZE_IN_WORLD, Tile.SIZE_IN_WORLD);
        manager.spawn(obstacle);
        obstacle.remove();

        character.move(0, -1);

        assertEquals(Tile.SIZE_IN_WORLD, character.getX());
        assertEquals(Tile.SIZE_IN_WORLD, character.getY());
    }


    private static Level createLevel() {
        val tileMappings = new TileMapping(() -> new Tile[]{
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 1, "floor"),
            new NormalTile(false, true, 2, "hole"),
        });

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

    private static class TestCharacter extends AbstractCharacter {
        TestCharacter(CharacterAttributes attributes) {
            super(attributes);
        }

        TestCharacter() {
            super(new CharacterAttributes(10.0f, 1, 1, 1));
        }
    }
}
