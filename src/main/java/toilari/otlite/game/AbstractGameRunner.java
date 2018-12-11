package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Input;
import toilari.otlite.view.Camera;
import toilari.otlite.view.renderer.IGameStateRenderer;

import java.util.Map;

/**
 * Apuluokka pelin pääloopin määrittelyyn ja suorittamiseen. Käärii pelin logiikan ja käyttää sitä ajamaan
 * käyttöliittymää.
 *
 * @param <T> käyettävän kameran tyyppi
 */
@Slf4j
public abstract class AbstractGameRunner<T extends Camera> {
    @NonNull private final Map<Class, IGameStateRenderer> stateRendererMappings;

    @NonNull @Getter private final Game game;

    @Getter private T camera;


    protected AbstractGameRunner(@NonNull Game game, @NonNull Map<Class, IGameStateRenderer> stateRendererMappings) {
        this.game = game;
        this.stateRendererMappings = stateRendererMappings;
        this.game.setStateChangeCallback(this::onStateChange);
    }

    /**
     * Takaisinkutsu joka ajetaan kun käärityn pelin pelitila vaihtuu.
     *
     * @param newState uusi pelitila
     */
    protected void onStateChange(GameState old, @NonNull GameState newState) {
        if (old != null) {
            val oldRenderer = this.stateRendererMappings.get(old.getClass());
            if (oldRenderer != null) {
                oldRenderer.destroy(old);
            }
        }

        val renderer = this.stateRendererMappings.get(newState.getClass());
        if (renderer == null) {
            throw new IllegalStateException("No renderer registered for state \"" + newState.getClass().getSimpleName() + "\"");
        }
        if (renderer.init(newState)) {
            LOG.error("Initializing gamestate renderer failed, trying to shut down gracefully...");
            getGame().setRunning(false);
        }
    }

    /**
     * Alustaa pelin piirtämiseen tarvittavat resurssit.
     *
     * @return <code>true</code> jos alustus onnistuu, <code>false</code> jos ilmentyy virhe
     */
    protected abstract boolean init();

    /**
     * Piirtää pelin.
     */
    protected void display(@NonNull T camera) {
        val state = getGame().getCurrentGameState();
        val stateRenderer = this.stateRendererMappings.get(state.getClass());
        if (stateRenderer == null) {
            throw new IllegalStateException("No renderer registered for state \"" + state.getClass().getSimpleName() + "\"");
        }
        // TODO: Wrapper class to handle state-to-renderer -mappings to get rid of unchecked behavior
        stateRenderer.draw(camera, state);
    }

    /**
     * Vapauttaa pelin piirtämiseen varatut resurssit.
     */
    protected abstract void destroy();

    /**
     * Luo uuden syötteenkäsittelijän. Kutsutaan kerran {@link #init()} jälkeen, ennen päälooppiin siirtymistä.
     *
     * @return luotu syötteenkäsittelijä
     */
    protected abstract IInputHandler createInputHandler();

    /**
     * Luo uuden kameran. Kutsutaan kerran {@link #init()} jälkeen, ennen päälooppiin siirtymistä.
     *
     * @return luotu kamera
     */
    protected abstract T createCamera();

    /**
     * Aloittaa pelin suorittamisen.
     */
    public void run() {
        init();
        this.game.init();

        this.camera = createCamera();
        Input.init(createInputHandler());

        long time = System.currentTimeMillis();
        while (this.game.isRunning()) {
            long current = System.currentTimeMillis();
            long elapsed = current - time;
            float delta = elapsed / 1000.0f;
            time = current;

            Input.getHandler().update();
            this.game.update(delta);
            display(this.camera);
        }
        this.game.destroy();
        destroy();
    }
}
