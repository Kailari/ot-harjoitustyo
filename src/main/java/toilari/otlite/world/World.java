package toilari.otlite.world;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.world.entities.ObjectManager;

/**
 * Pelimaailma.
 */
public class World {
    private Level level;
    private final ObjectManager objectManager;

    public World(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    public Level getCurrentLevel() {
        return this.level;
    }

    /**
     * Vaihtaa pelin karttaa.
     * 
     * @param level Uusi kartta johon vaihdetaan
     */
    public void changeLevel(@NonNull Level level) {
        this.level = level;
    }

    /**
     * Päivittää pelimaailman.
     */
    public void update() {
        for (val gameObject : this.objectManager.getObjects()) {
            gameObject.update();
        }
    }
}
