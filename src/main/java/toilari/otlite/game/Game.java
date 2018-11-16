package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pelin runko.
 */
@Slf4j
public class Game {
    private AtomicBoolean running = new AtomicBoolean();

    /**
     * Määrittää jatketaanko pääloopin suorittamista.
     *
     * @param running jos <code>false</code> pääloopin suoritus lopetetaan
     */
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public boolean isRunning() {
        return this.running.get();
    }

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
        }

        this.currentGameState = newState;
        this.currentGameState.setGame(this);
        this.currentGameState.init();

        this.stateChangeCallback.onStateChange(this.currentGameState);
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa.
     */
    public void init() {
        changeState(this.defaultGameState);
        setRunning(true);
    }

    /**
     * Kutsutaan toistuvasti niin kauan kuin peli on käynnissä.
     */
    public void update() {
        this.currentGameState.update();
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    public void destroy() {
        this.currentGameState.destroy();
    }

}
