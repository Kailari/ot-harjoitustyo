package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.ProfileDAO;
import toilari.otlite.dao.SettingsDAO;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.statistics.StatisticsManager;

import java.sql.SQLException;

/**
 * Pelin runko.
 */
@Slf4j
public class Game {
    @Getter @Setter private Profile activeProfile;
    @Getter private StatisticsManager statistics;
    @Getter private ProfileDAO profileDao;

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
     * @return nykyinen pelitila. Saattaa olla <code>null</code> jos mitään pelitilaa ei ole vielä asetettu aktiiviseksi
     */
    @Getter private GameState currentGameState;
    @NonNull private final GameState defaultGameState;
    @NonNull private final String saveDataPath;

    @Setter private StateChangeCallback stateChangeCallback;

    /**
     * Luo uuden peli-instanssin.
     *
     * @param defaultState oletuspelitila joka asetetaan aktiiviseksi pelin suorituksen {@link #init() alkaessa}
     * @param savePath     hakemisto johon tallennustiedostot tulee säilöä
     * @throws NullPointerException jos oletustila on <code>null</code>
     */
    public Game(@NonNull GameState defaultState, @NonNull String savePath) {
        this.defaultGameState = defaultState;
        this.saveDataPath = savePath;
    }

    /**
     * Vaihtaa pelitilaa. Kutsuu uudelle ja vanhalle pelitilalle tarvittavat alustus- ja tuhoamismetodit.
     *
     * @param newState uusi pelitila
     * @throws NullPointerException jos pelitila on <code>null</code>
     */
    public void changeState(@NonNull GameState newState) {
        LOG.info("Changing the game state to: {}", newState);

        if (this.currentGameState != null) {
            this.currentGameState.destroy();
            this.currentGameState.setGame(null);
        }

        this.currentGameState = newState;
        this.currentGameState.setGame(this);
        if (this.currentGameState.init()) {
            LOG.error("Gamestate state initializatin failed, trying to shutdown gracefully...");
            setRunning(false);
        } else {
            if (this.stateChangeCallback != null) {
                this.stateChangeCallback.onStateChange(this.currentGameState);
            }
        }
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa. Asettaa oletuspelitilan aktiiviseksi jos aktiivista pelitilaa ei
     * ole vielä manuaalisesti asetettu
     */
    public void init() {
        try {
            val database = new Database(this.saveDataPath + "profiles.db");
            this.profileDao = new ProfileDAO(database, new SettingsDAO(this.saveDataPath));
            this.statistics = new StatisticsManager(database);
        } catch (SQLException e) {
            LOG.error("Could not initialize statistics statistics. Shutting down.");
            LOG.error("Cause: {}", e.getMessage());
            return;
        }

        setRunning(true);

        if (this.currentGameState == null) {
            changeState(this.defaultGameState);
        }
    }

    /**
     * Kutsutaan toistuvasti niin kauan kuin peli on käynnissä.
     *
     * @throws IllegalStateException jos {@link #isRunning()} on <code>false</code>
     */
    public void update() {
        if (!isRunning()) {
            throw new IllegalStateException("Game updated while not running!");
        }

        this.currentGameState.update();
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    public void destroy() {
        this.currentGameState.destroy();
    }
}
