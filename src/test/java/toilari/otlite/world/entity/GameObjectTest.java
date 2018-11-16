package toilari.otlite.world.entity;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.world.World;
import toilari.otlite.world.entities.GameObject;
import toilari.otlite.world.entities.TurnObjectManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testaa peliobjektien toimintaa.
 */
class GameObjectTest {
    /**
     * Testaa että objekteja joilla on sama ID kohdellaan samana objektina.
     */
    @Test
    void objectsWithEqualIDAreTreatedAsEqual() {
        val a = new TestGameObject(715517);
        val b = new TestGameObject(715517);

        assertEquals(a, b);
    }

    /**
     * Testaa että objekteja joilla on eri ID kohdellaan eri objekteina.
     */
    @Test
    void objectsWithNotEqualIDAreNotEqual() {
        val a = new TestGameObject(1337);
        val b = new TestGameObject(715517);

        assertNotEquals(a, b);
    }

    /**
     * Testaa että objekteille annettu ID on yksilöllinen.
     */
    @Test
    void objectIDsAreUnique() {
        val a = new GameObject();
        val b = new GameObject();
        val c = new GameObject();

        assertNotEquals(a.getId(), b.getId());
        assertNotEquals(a.getId(), c.getId());
        assertNotEquals(b.getId(), c.getId());
    }

    /**
     * Testaa että objekteille annettu ID on yksilöllinen.
     */
    @Test
    void objectIDsAreUniqueLarge() {
        val objs = new GameObject[1000];
        for (int i = 0; i < 1000; i++) {
            objs[i] = new GameObject();
        }

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                if (i == j) {
                    continue;
                }
                assertNotEquals(objs[i], objs[j]);
            }

        }
    }

    @Test
    void objectsAreNotSpawnedWhenCreated() {
        assertFalse(new GameObject().isSpawned());
    }

    @Test
    void objectsAreNotRemovedWhenCreated() {
        assertFalse(new GameObject().isRemoved());
    }

    @Test
    void objectsAreFlaggedAsSpawnedWhenSpawned() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        world.getObjectManager().spawn(obj);

        assertTrue(obj.isSpawned());
    }

    @Test
    void objectsAreFlaggedAsRemovedWhenSpawned() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        world.getObjectManager().spawn(obj);
        obj.remove();

        assertTrue(obj.isRemoved());
    }

    @Test
    void spawningObjectMultipleTimesThrows() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        world.getObjectManager().spawn(obj);
        assertThrows(IllegalArgumentException.class, () -> world.getObjectManager().spawn(obj));
    }

    @Test
    void initObjectMultipleTimesThrows() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        obj.init();
        assertThrows(IllegalStateException.class, obj::init);
    }

    @Test
    void spawningRemovedObjectThrows() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        world.getObjectManager().spawn(obj);
        obj.remove();
        world.getObjectManager().update();
        assertThrows(IllegalStateException.class, () -> world.getObjectManager().spawn(obj));
    }

    @Test
    void updatingRemovedObjectThrows() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        world.getObjectManager().spawn(obj);
        obj.remove();

        assertThrows(IllegalStateException.class, obj::update);
    }

    @Test
    void updatingNotSpawnedObjectThrows() {
        val obj = new GameObject();
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        assertThrows(IllegalStateException.class, obj::update);
    }

    private static class TestGameObject extends GameObject {
        private final int id;

        private TestGameObject(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return this.id;
        }
    }
}
