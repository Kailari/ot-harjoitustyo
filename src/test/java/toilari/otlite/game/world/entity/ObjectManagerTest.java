package toilari.otlite.game.world.entity;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.GameState;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.ObjectManager;

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
        val world = FakeWorld.create();

        assertThrows(NullPointerException.class, () -> world.getObjectManager().spawn(null));
    }

    @Test
    void spawningSetsSpawnedObjectsWorldInstance() {
        val world = FakeWorld.create();

        val object = new TestGameObject();
        world.getObjectManager().spawn(object);

        assertEquals(world, object.getWorld());
    }

    @Test
    void updatingWorldCallsObjectUpdate() {
        val world = FakeWorld.create();

        val obj = new TestGameObject();

        world.getObjectManager().spawn(obj);
        world.update(1.0f);

        assertTrue(obj.called);
    }

    @Test
    void getObjectsContainsAllSpawnedObjects() {
        val world = FakeWorld.create();

        val objects = new TestGameObject[1000];
        for (int i = 0; i < 1000; i++) {
            objects[i] = new TestGameObject();
            world.getObjectManager().spawn(objects[i]);
        }
        world.update(1.0f);

        val spawned = world.getObjectManager().getObjects();
        assertTrue(Arrays.stream(objects).allMatch(spawned::contains));
    }

    @Test
    void getObjectsContainsAllButRemovedObjectsAfterNextUpdate() {
        val world = FakeWorld.create();

        val objects = new TestGameObject[1000];
        for (int i = 0; i < 1000; i++) {
            objects[i] = new TestGameObject();
            world.getObjectManager().spawn(objects[i]);
        }

        for (int i = 0; i < 1000; i += 2) {
            objects[i].remove();
        }
        world.update(1.0f);

        val spawned = world.getObjectManager().getObjects();
        for (int i = 0; i < 1000; i++) {
            assertEquals(i % 2 != 0, spawned.contains(objects[i]));
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void spawnThrowsIfObjectIsNull() {
        assertThrows(NullPointerException.class, () -> new ObjectManager().spawn(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void initThrowsIfWorldIsNull() {
        assertThrows(NullPointerException.class, () -> new ObjectManager().init(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setGameStateThrowsIfStateIsNull() {
        assertThrows(NullPointerException.class, () -> new ObjectManager().setGameState(null));
    }

    @Test
    void callingSetGameStateMultipleTimesThrows() {
        val manager = new ObjectManager();
        manager.setGameState(new TestGameState());
        assertThrows(IllegalStateException.class, () -> manager.setGameState(new TestGameState()));
    }

    @Test
    void getGameStateReturnsCorrectStateAfterCallingSetGameState() {
        val manager = new ObjectManager();
        val state = new TestGameState();
        manager.setGameState(state);
        assertEquals(state, manager.getGameState());
    }

    private static class TestGameObject extends GameObject {
        private boolean called;

        @Override
        public void update(float delta) {
            super.update(delta);
            this.called = true;
        }
    }

    private static class TestGameState extends GameState {
        @Override
        public boolean init() {
            return false;
        }

        @Override
        public void update(float delta) {
        }

        @Override
        public void destroy() {
        }
    }
}
