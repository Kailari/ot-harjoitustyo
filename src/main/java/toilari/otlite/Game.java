package toilari.otlite;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Pelin runko.
 */
@Slf4j
public class Game {
    /**
     * Määrittää jatketaanko pääloopin suorittamista.
     *
     * @param running jos <code>true</code>, pääloopin suorittamista jatketaan
     */
    @Setter private boolean running;

    @NonNull private GameState currentGameState;

    private final Supplier<GameState> defaultStateFactory;

    Game(Supplier<GameState> defaultStateFactory) {
        this.defaultStateFactory = defaultStateFactory;
    }

    /**
     * Aloittaa sovelluksen suorituksen. Aloittaa {@link #init() alustamalla}
     * sovelluksen tarvitsemat resurssit ja siirtyy {@link #loop() päälooppiin} sen
     * jälkeen. Kun päälooppi viimein valmistuu, viimeistellään suoritus metodissa
     * {@link #destroy()}
     */
    void run() {
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

        this.currentGameState.destroy();
        this.currentGameState = newState;
        this.currentGameState.init();
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa.
     */
    private void init() {
        this.currentGameState = this.defaultStateFactory.get();
        this.currentGameState.init();
        this.running = true;
    }

    /**
     * Kutsutaan kerran kun sovellus siirtyy päälooppiin. Vastaa pääloopin
     * suorittamisesta. Metodi palaa vasta kun pääloopin suoritus on valmis.
     */
    private void loop() {
        while (this.running) {
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
