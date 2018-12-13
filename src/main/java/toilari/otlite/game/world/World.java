package toilari.otlite.game.world;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.event.PlayEvent;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.LevelData;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;

/**
 * Pelimaailma.
 */
@Slf4j
public class World {
    private static final Tile NULL_TILE = new NormalTile(true, true, 0, "__null");

    private IGetByIDDao<LevelData> levels = null;
    private IGetAllDAO<Tile> tiles = null;
    private IGetByIDDao<CharacterObject> characters = null;

    @Getter private int floor;
    @Getter private Level currentLevel;
    @NonNull @Getter private final TurnObjectManager objectManager;

    /**
     * Luo uuden pelimaailman.
     *
     * @param manager    objektimanageri jolla pelimaailman objekteja hallinnoidaan
     * @param tiles      ruutu DAO jolla ruututyypit ladataan
     * @param levels     kartta DAO jolla kartat ladataan
     * @param characters hahmo DAO jolla pelihahmot ladataan
     */
    public World(TurnObjectManager manager, IGetAllDAO<Tile> tiles, IGetByIDDao<LevelData> levels, IGetByIDDao<CharacterObject> characters) {
        this.objectManager = manager;
        this.levels = levels;
        this.tiles = tiles;
        this.characters = characters;
    }

    /**
     * Tarkistaa onko annetuissa koordinaateissa objektia ja palauttaa sen jos sellainen löytyy. Ei palauta objekteja
     * jotka on jo merkitty poisteuksi.
     *
     * @param x x-koordinaatti josta etsitään
     * @param y y-koordinaatti josta etsitään
     *
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
     *
     * @return ruutu annetuissa koordinaateissa, <code>null</code> jos karttaa ei ole asetettu
     *
     * @throws IllegalArgumentException jos koordinaatit eivät ole kartan {@link #isWithinBounds(int, int) rajojen
     *                                  sisäpuolella}
     */
    @NonNull
    public Tile getTileAt(int x, int y) {
        return getCurrentLevel() == null ? NULL_TILE : getCurrentLevel().getTileAt(x, y);
    }

    /**
     * Tarkistaa ovatko koordinaatit kartan rajojen sisäpuolella.
     *
     * @param x tarkistettava x-koordinaatti.
     * @param y tarkistettava y-koordinaatti.
     *
     * @return <code>true</code> jos koordinaatit ovat kartan sisällä, muulloin <code>false</code>
     */
    public boolean isWithinBounds(int x, int y) {
        return getCurrentLevel() != null && getCurrentLevel().isWithinBounds(x, y);
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
        this.objectManager.clearAllNonPlayerObjects();
        this.currentLevel = level;
    }

    /**
     * Vaihtaa pelin karttaa.
     *
     * @param levelId Uuden kartan ID
     */
    public void changeLevel(@NonNull String levelId) {
        if (this.levels == null || this.tiles == null || this.characters == null) {
            LOG.warn("Level/Tile/Character accessors are not initialized properly.");
            return;
        }

        val level = this.levels.getByID(levelId);
        if (level == null) {
            LOG.warn("Could not load level \"{}\"", levelId);
            return;
        }

        changeLevel(level.asLevel(this.tiles));
        level.setNextLevel();
        level.spawn(this.characters, this.objectManager);

        this.floor++;
        getObjectManager().getEventSystem().fire(new PlayEvent.NextFloor());
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
