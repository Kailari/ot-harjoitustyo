package toilari.otlite.world;

import lombok.NonNull;

/**
 * Pelimaailma.
 */
public class World {
    private Level level;

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

    }
}
