package toilari.otlite.game.world.entity.characters;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;

import static org.junit.jupiter.api.Assertions.*;

class CharacterObjectTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsIfAttributesIsNull() {
        assertThrows(NullPointerException.class, () -> new CharacterObject(null));
    }

    @Test
    void characterIsAliveWhenSpawned() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        manager.spawn(character);
        assertFalse(character.isDead());
    }

    @Test
    void healthIsMaxedAtSpawn() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        manager.spawn(character);
        assertEquals(10.0f, character.getHealth());
    }

    @Test
    void characterIsDeadWhenHealthIsZero() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        manager.spawn(character);
        character.setHealth(0.0f);

        assertTrue(character.isDead());
    }
}
