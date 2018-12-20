package toilari.otlite.game.world.entity;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;

import static org.junit.jupiter.api.Assertions.*;

class GameObjectTest {
    @Test
    void instanceOfAnotherClassIsNotEqual() {
        val a = new TestGameObject(1337);
        val b = new Object();

        assertNotEquals(a, b);
    }

    @Test
    void objectsWithEqualIDAreTreatedAsEqual() {
        val a = new TestGameObject(715517);
        val b = new TestGameObject(715517);

        assertEquals(a, b);
    }

    @Test
    void objectsWithNotEqualIDAreNotEqual() {
        val a = new TestGameObject(1337);
        val b = new TestGameObject(715517);

        assertNotEquals(a, b);
    }

    @Test
    void equalObjectsHaveEqualHashCode() {
        val a = new TestGameObject(715517);
        val b = new TestGameObject(715517);

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEqualObjectsDoNotHaveEqualHashCode() {
        val a = new TestGameObject(715517);
        val b = new TestGameObject(1337);

        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void objectIDsAreUnique() {
        val a = new GameObject();
        val b = new GameObject();
        val c = new GameObject();

        assertNotEquals(a.getId(), b.getId());
        assertNotEquals(a.getId(), c.getId());
        assertNotEquals(b.getId(), c.getId());
    }

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
        val world = FakeWorld.create();

        world.getObjectManager().spawn(obj);

        assertTrue(obj.isSpawned());
    }

    @Test
    void objectsAreFlaggedAsRemovedWhenSpawned() {
        val obj = new GameObject();
        val world = FakeWorld.create();

        world.getObjectManager().spawn(obj);
        obj.remove();

        assertTrue(obj.isRemoved());
    }

    @Test
    void spawningObjectMultipleTimesThrows() {
        val obj = new GameObject();
        val world = FakeWorld.create();

        world.getObjectManager().spawn(obj);
        assertThrows(IllegalArgumentException.class, () -> world.getObjectManager().spawn(obj));
    }

    @Test
    void initObjectMultipleTimesThrows() {
        val obj = new GameObject();

        obj.init();
        assertThrows(IllegalStateException.class, obj::init);
    }

    @Test
    void spawningRemovedObjectThrows() {
        val obj = new GameObject();
        val world = FakeWorld.create();

        world.getObjectManager().spawn(obj);
        obj.remove();
        world.getObjectManager().update(1.0f);
        assertThrows(IllegalStateException.class, () -> world.getObjectManager().spawn(obj));
    }

    @Test
    void updatingRemovedObjectThrows() {
        val obj = new GameObject();
        val world = FakeWorld.create();

        world.getObjectManager().spawn(obj);
        obj.remove();

        assertThrows(IllegalStateException.class, () -> obj.update(1.0f));
    }

    @Test
    void updatingNotSpawnedObjectThrows() {
        val obj = new GameObject();
        val world = FakeWorld.create();

        assertThrows(IllegalStateException.class, () -> obj.update(1.0f));
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
