package toilari.otlite.game.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;

/**
 * Pelimaailma.
 */
public class World {
    @Getter private Level currentLevel;
    @NonNull @Getter private final TurnObjectManager objectManager;

    /**
     * Tarkistaa onko annetuissa koordinaateissa objektia ja palauttaa sen jos sellainen löytyy. Ei palauta objekteja
     * jotka on jo merkitty poisteuksi.
     *
     * @param x x-koordinaatti josta etsitään
     * @param y y-koordinaatti josta etsitään
     * @return <code>null</code> jos koordinaateissa ei ole objektia, muulloin löydetty objekti
     */
    public GameObject getObjectAt(int x, int y) {
        val obj = this.objectManager.getObjectAt(x, y);
        return (obj != null && obj.isRemoved()) ? null : obj;
    }

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
