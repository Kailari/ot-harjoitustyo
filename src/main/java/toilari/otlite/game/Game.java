package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Pelin runko.
 */
@Slf4j
public class Game {
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

    @Setter private StateChangeCallback stateChangeCallback;

    /**
     * Luo uuden peli-instanssin.
     *
     * @param defaultState oletuspelitila joka asetetaan aktiiviseksi pelin suorituksen {@link #init() alkaessa}
     * @throws NullPointerException jos oletustila on <code>null</code>
     */
    public Game(@NonNull GameState defaultState) {
        this.defaultGameState = defaultState;
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
        this.currentGameState.init();

        if (this.stateChangeCallback != null) {
            this.stateChangeCallback.onStateChange(this.currentGameState);
        }
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa. Asettaa oletuspelitilan aktiiviseksi jos aktiivista pelitilaa ei
     * ole vielä manuaalisesti asetettu
     */
    public void init() {
        if (this.currentGameState == null) {
            changeState(this.defaultGameState);
        }

        setRunning(true);
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
