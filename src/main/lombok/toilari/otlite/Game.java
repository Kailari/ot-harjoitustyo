package toilari.otlite;

import java.util.function.Supplier;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

    protected Game(Supplier<GameState> defaultStateFactory) {
        this.defaultStateFactory = defaultStateFactory;
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

        currentGameState.destroy();
        currentGameState = newState;
        currentGameState.init();
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa.
     */
    private void init() {
        currentGameState = defaultStateFactory.get();
        currentGameState.init();
        running = true;
    }

    /**
     * Kutsutaan kerran kun sovellus siirtyy päälooppiin. Vastaa pääloopin
     * suorittamisesta. Metodi palaa vasta kun pääloopin suoritus on valmis.
     */
    private void loop() {
        while (running) {
            currentGameState.update();
            currentGameState.draw();
        }
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    private void destroy() {
        currentGameState.destroy();
    }
}
