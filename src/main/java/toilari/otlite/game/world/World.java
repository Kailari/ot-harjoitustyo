package toilari.otlite.game.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.Tile;

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
     * Hakee nykyisen kartan leveyden.
     *
     * @return nykyisen kartan leveys tai 0 jos karttaa ei ole asetettu
     */
    public int getLevelWidth() {
        return getCurrentLevel() == null ? 0 : getCurrentLevel().getWidth();
    }

    /**
     * Hakee nykyisen kartan korkeuden.
     *
     * @return nykyisen kartan korkeus tai 0 jos karttaa ei ole asetettu
     */
    public int getLevelHeight() {
        return getCurrentLevel() == null ? 0 : getCurrentLevel().getHeight();
    }

    /**
     * Hakee ruudun annetuista koordinaateista. Koordinaatit otetaan ruutukoordinaatteina.
     *
     * @param x ruudun x-ruutukoordinaatti
     * @param y ruudun y-ruutukoordinaatti
     * @return ruutu annetuissa koordinaateissa, <code>null</code> jos karttaa ei ole asetettu
     * @throws IllegalArgumentException jos koordinaatit eivät ole kartan {@link #isWithinBounds(int, int)
     *                                  rajojen sisäpuolella}
     */
    @NonNull
    public Tile getTileAt(int x, int y) {
        return getCurrentLevel().getTileAt(x, y);
    }

    /**
     * Tarkistaa ovatko koordinaatit kartan rajojen sisäpuolella.
     *
     * @param x tarkistettava x-koordinaatti.
     * @param y tarkistettava y-koordinaatti.
     * @return <code>true</code> jos koordinaatit ovat kartan sisällä, muulloin <code>false</code>
     */
    public boolean isWithinBounds(int x, int y) {
        return getCurrentLevel().isWithinBounds(x, y);
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
     *
     * @param delta viimeisimmästä päivityksestä kulunut aika
     */
    public void update(float delta) {
        this.objectManager.update(delta);
    }
}
