package toilari.otlite.game.world.entity;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.ObjectManager;
import toilari.otlite.game.world.entities.TurnObjectManager;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ObjectManagerTest {
    @Test
    void callingSpawnThrowsBeforeInitIsCalled() {
        val manager = new ObjectManager();
        assertThrows(IllegalStateException.class, () -> manager.spawn(new GameObject()));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tryingToSpawnNullObjectThrows() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        assertThrows(NullPointerException.class, () -> world.getObjectManager().spawn(null));
    }

    @Test
    void spawningSetsWorldInstance() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);
        val object = new TestGameObject();
        world.getObjectManager().spawn(object);

        assertEquals(world, object.getWorld());
    }

    @Test
    void updatingWorldCallsObjectUpdate() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);
        val obj = new TestGameObject();

        world.getObjectManager().spawn(obj);
        world.update();

        assertTrue(obj.called);
    }

    @Test
    void getObjectsContainsAllSpawnedObjects() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);
        val objects = new TestGameObject[1000];
        for (int i = 0; i < 1000; i++) {
            objects[i] = new TestGameObject();
            world.getObjectManager().spawn(objects[i]);
        }
        world.update();

        val spawned = world.getObjectManager().getObjects();
        assertTrue(Arrays.stream(objects).allMatch(spawned::contains));
    }

    @Test
    void getObjectsContainsAllButRemovedObjectsAfterNextUpdate() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);
        val objects = new TestGameObject[1000];
        for (int i = 0; i < 1000; i++) {
            objects[i] = new TestGameObject();
            world.getObjectManager().spawn(objects[i]);
        }

        for (int i = 0; i < 1000; i += 2) {
            objects[i].remove();
        }
        world.update();

        val spawned = world.getObjectManager().getObjects();
        for (int i = 0; i < 1000; i++) {
            assertEquals(i % 2 != 0, spawned.contains(objects[i]));
        }
    }

    private static class TestGameObject extends GameObject {
        private boolean called;

        @Override
        public void update() {
            super.update();
            this.called = true;
        }
    }
}
