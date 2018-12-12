package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.ProfileDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.StatisticsManager;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.level.LevelData;
import toilari.otlite.game.world.level.Tile;

import java.util.function.Supplier;

/**
 * Pelin runko.
 */
@Slf4j
public class Game {
    @Getter @Setter private Profile activeProfile;

    @Getter @NonNull private final StatisticsManager statistics;
    @Getter @NonNull private final ProfileDAO profiles;
    @Getter @NonNull private final IGetAllDAO<Tile> tiles;
    @Getter @NonNull private final IGetByIDDao<CharacterObject> characters;
    @Getter @NonNull private final IGetByIDDao<LevelData> levels;
    @Getter @NonNull private final String initialLevelId;
    @NonNull private final Supplier<TurnObjectManager> managerSupplier;

    private boolean running = false;


    /**
     * Määrittää jatketaanko pääloopin suorittamista.
     *
     * @param running jos <code>false</code> pääloopin suoritus lopetetaan
     */
    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    /**
     * Nykyinen pelitila. Määrittelemätön ennen kuin {@link #init()} tai {@link #changeState(GameState)} on kutsuttu,
     * jonka jälkeen taattu validi ei-null pelitila-instanssi.
     *
     * @return nykyinen pelitila. Saattaa olla <code>null</code> jos mitään pelitilaa ei ole vielä asetettu
     * aktiiviseksi
     */
    @Getter private GameState currentGameState;
    @NonNull private final GameState defaultGameState;

    @Setter private StateChangeCallback stateChangeCallback;


    /**
     * Luo uuden peli-instanssin.
     *
     * @param defaultState    oletuspelitila joka asetetaan aktiiviseksi pelin suorituksen {@link #init() alkaessa}
     * @param initialLevelId  ensimmäisen kartan ID
     * @param tiles           dao jolla ruututyypit voidaan ladata
     * @param characters      dao jolla pelihahmot voidaan ladata
     * @param levels          dao jolla kartat voidaan ladata
     * @param profiles      dao jolla profiilit saadaan ladattua
     * @param statistics      dao jolla statistiikkoihin pääsee käsiksi
     * @param managerSupplier tehdas jolla pelille saadaan luotua objektimanageri
     *
     * @throws NullPointerException jos oletustila on <code>null</code>
     */
    public Game(
        @NonNull GameState defaultState,
        @NonNull String initialLevelId,
        @NonNull IGetAllDAO<Tile> tiles,
        @NonNull IGetByIDDao<CharacterObject> characters,
        @NonNull IGetByIDDao<LevelData> levels,
        @NonNull ProfileDAO profiles,
        @NonNull StatisticsManager statistics,
        @NonNull Supplier<TurnObjectManager> managerSupplier
    ) {
        this.defaultGameState = defaultState;
        this.initialLevelId = initialLevelId;
        this.tiles = tiles;
        this.characters = characters;
        this.levels = levels;
        this.managerSupplier = managerSupplier;
        this.profiles = profiles;
        this.statistics = statistics;
    }

    /**
     * Vaihtaa pelitilaa. Kutsuu uudelle ja vanhalle pelitilalle tarvittavat alustus- ja tuhoamismetodit.
     *
     * @param newState uusi pelitila
     *
     * @throws NullPointerException jos pelitila on <code>null</code>
     */
    public void changeState(@NonNull GameState newState) {
        LOG.info("Changing the game state to: {}", newState);

        if (this.currentGameState != null) {
            this.currentGameState.destroy();
            this.currentGameState.setGame(null);
        }

        val old = this.currentGameState;
        this.currentGameState = newState;
        this.currentGameState.setGame(this);
        if (this.currentGameState.init()) {
            LOG.error("Gamestate state initializatin failed, trying to shutdown gracefully...");
            setRunning(false);
        } else {
            if (this.stateChangeCallback != null) {
                this.stateChangeCallback.onStateChange(old, this.currentGameState);
            }
        }
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus alkaa. Asettaa oletuspelitilan
     * aktiiviseksi jos aktiivista pelitilaa ei ole vielä manuaalisesti asetettu
     */
    public void init() {
        setRunning(true);

        if (this.currentGameState == null) {
            changeState(this.defaultGameState);
        }
    }

    /**
     * Kutsutaan toistuvasti niin kauan kuin peli on käynnissä.
     *
     * @param delta viimeisimmästä päivityksestä kulunut aika
     *
     * @throws IllegalStateException jos {@link #isRunning()} on <code>false</code>
     */
    public void update(float delta) {
        if (!isRunning()) {
            throw new IllegalStateException("Game updated while not running!");
        }

        this.currentGameState.update(delta);
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    public void destroy() {
        this.currentGameState.destroy();
    }

    TurnObjectManager getNewObjectManager() {
        return this.managerSupplier.get();
    }
}
