package toilari.otlite.world.entity;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.world.World;
import toilari.otlite.world.entities.GameObject;
import toilari.otlite.world.entities.ObjectManager;
import toilari.otlite.world.entities.TurnObjectManager;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ObjectManagerTest {
    /**
     * Testaa että .spawn() ennen .init() aiheuttaa virheen.
     */
    @Test
    void spawnThrowsBeforeInitIsCalled() {
        val manager = new ObjectManager();
        assertThrows(IllegalStateException.class, () -> manager.spawn(new GameObject()));
    }

    /**
     * Testaa että <code>null</code> objektin spawnaaminen heittää virheen.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void tryingToSpawnNullObjectThrows() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        manager.init(world);

        assertThrows(NullPointerException.class, () -> world.getObjectManager().spawn(null));
    }

    /**
     * Testaa että maailman päivittäminen kutsuu oikein managerin päivityskutsua, joka vuorostaan päivittää objektit.
     */
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

    /**
     * Testaa että {@link ObjectManager#getObjects()} palauttaa kaikki pelimaailmaan lisätyt objektit.
     */
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

    /**
     * Testaa että {@link ObjectManager#getObjects()} palauttaa {@link ObjectManager#update()}-kutsun jälkeen vain ne
     * pelimaailman objektit joita ei edellisen päivitysrutiinin aikana ole merkitty poistetuiksi.
     */
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
