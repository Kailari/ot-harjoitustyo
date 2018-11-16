package toilari.otlite.world;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.world.entities.TurnObjectManager;

/**
 * Pelimaailma.
 */
public class World {
    @Getter private Level currentLevel;
    @NonNull @Getter private final TurnObjectManager objectManager;

    /**
     * Luo uuden pelimaailman.
     *
     * @param objectManager objektimanageri jolla pelimaailman objekteja tulee hallinnoida
     */
    public World(@NonNull TurnObjectManager objectManager) {
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
     * Alustaa pelimaailman.
     */
    public void init() {
        this.objectManager.init(this);
    }

    /**
     * Päivittää pelimaailman.
     */
    public void update() {
        this.objectManager.update();
    }
}
