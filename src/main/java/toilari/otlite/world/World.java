package toilari.otlite.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.world.entities.ObjectManager;

/**
 * Pelimaailma.
 */
public class World {
    @Getter private Level currentLevel;
    @NonNull @Getter private final ObjectManager objectManager;

    public World(@NonNull ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    /**
     * Vaihtaa pelin karttaa.
     *
     * @param level Uusi kartta johon vaihdetaan
     */
    public void changeLevel(@NonNull Level level) {
        this.currentLevel = level;
    }

    /**
     * Päivittää pelimaailman.
     */
    public void update() {
        for (val gameObject : this.objectManager.getObjects()) {
            gameObject.update();
        }

        this.objectManager.deleteRemoved();
    }
}
