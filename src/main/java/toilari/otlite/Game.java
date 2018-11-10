package toilari.otlite;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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

    /**
     * Nykyinen pelitila.
     *
     * @return pelin nykyinen tila
     */
    @Getter private GameState currentGameState;
    @Getter @NonNull private GameState defaultGameState;


    public Game(GameState defaultState) {
        this.defaultGameState = defaultState;
    }

    /**
     * Aloittaa sovelluksen suorituksen. Aloittaa {@link #init() alustamalla}
     * sovelluksen tarvitsemat resurssit ja siirtyy {@link #loop() päälooppiin} sen
     * jälkeen. Kun päälooppi viimein valmistuu, viimeistellään suoritus metodissa
     * {@link #destroy()}
     */
    public void run() {
        init();
        loop();
        destroy();
    }

    /**
     * Vaihtaa pelitilaa.
     *
     * @param newState uusi pelitila
     */
    public void changeState(@NonNull GameState newState) {
        LOG.info("Changing the game state to: {}", newState);

        if (this.currentGameState != null) {
            this.currentGameState.destroy();
        }

        this.currentGameState = newState;
        this.currentGameState.setGame(this);
        this.currentGameState.init();
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa.
     */
    private void init() {
        changeState(this.defaultGameState);
        setRunning(true);
    }

    /**
     * Kutsutaan kerran kun sovellus siirtyy päälooppiin. Vastaa pääloopin
     * suorittamisesta. Metodi palaa vasta kun pääloopin suoritus on valmis.
     */
    private void loop() {
        while (this.running.get()) {
            this.currentGameState.update();
            this.currentGameState.draw();
        }
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    private void destroy() {
        this.currentGameState.destroy();
    }
}
