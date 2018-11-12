package toilari.otlite;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.rendering.IRenderer;

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

    @Getter private GameState currentGameState;

    @NonNull private GameState defaultGameState;

    @NonNull private final IRenderer<Game> renderer;


    public Game(@NonNull GameState defaultState, @NonNull IRenderer<Game> renderer) {
        this.defaultGameState = defaultState;
        this.renderer = renderer;
    }

    /**
     * Aloittaa sovelluksen suorituksen. Aloittaa {@link #init() alustamalla}
     * sovelluksen tarvitsemat resurssit ja siirtyy {@link #loop() päälooppiin} sen
     * jälkeen. Kun päälooppi viimein valmistuu, viimeistellään suoritus metodissa
     * {@link #destroy()}
     */
    public void run() {
        init();
        while (this.running.get()) {
            loop();
        }
        destroy();
    }

    /**
     * Vaihtaa pelitilaa. Kutsuu uudelle ja vanhalle pelitilalle tarvittavat alustus- ja tuhoamismetodit.
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
    protected void init() {
        this.renderer.init(this);
        
        changeState(this.defaultGameState);
        setRunning(true);
    }

    /**
     * Kutsutaan toistuvasti niin kauan kuin peli on käynnissä.
     */
    protected void loop() {
        this.currentGameState.update();
        this.renderer.draw(this);
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    protected void destroy() {
        this.currentGameState.destroy();
        this.renderer.destroy(this);
    }
}
