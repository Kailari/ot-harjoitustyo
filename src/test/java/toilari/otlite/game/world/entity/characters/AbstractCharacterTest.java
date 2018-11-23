package toilari.otlite.game.world.entity.characters;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.game.world.level.TileMapping;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCharacterTest {
    @Test
    void constructorThrowsIfAttributesIsNull() {
        assertThrows(NullPointerException.class, () -> new TestCharacter(null));
    }

    @Test
    void characterIsAliveWhenSpawned() {
        val manager = new TurnObjectManager();
        new World(manager);
        val character = new TestCharacter();

        manager.spawn(character);
        assertFalse(character.isDead());
    }

    @Test
    void healthIsMaxedAtSpawn() {
        val manager = new TurnObjectManager();
        new World(manager);
        val character = new TestCharacter();

        manager.spawn(character);
        assertEquals(10.0f, character.getHealth());
    }

    @Test
    void canMoveToReturnsFalseWhenTryingToMoveOutOfBounds() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.changeLevel(createLevel());

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

        val obstacle = new TestCharacter();
        obstacle.setPos(2, 2);
        val character = new TestCharacter();
        character.setPos(3, 2);
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

        val character = new TestCharacter();
        character.setPos(3, 2);
        manager.spawn(character);

        assertFalse(character.canMoveTo(0, 0));
    }


    private static Level createLevel() {
        val tileMappings = new TileMapping(() -> new Tile[]{
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 1, "floor"),
            new NormalTile(false, true, 2, "hole"),
        });

        val indices = new byte[]{
            1, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 1, 1, 1, 0, 0, 1, 0,
            0, 2, 2, 1, 1, 1, 0, 0,
            0, 2, 1, 1, 1, 1, 1, 0,
            0, 1, 1, 2, 2, 1, 1, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 1,
        };

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
