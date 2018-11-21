package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Input;
import toilari.otlite.view.Camera;
import toilari.otlite.view.renderer.IRenderer;

import java.util.Map;

/**
 * Apuluokka pelin pääloopin määrittelyyn ja suorittamiseen. Käärii pelin logiikan ja käyttää sitä ajamaan
 * käyttöliittymää.
 *
 * @param <T> käyettävän kameran tyyppi
 */
@Slf4j
public abstract class AbstractGameRunner<T extends Camera> {
    @NonNull private final Map<Class, IRenderer> stateRendererMappings;

    @NonNull @Getter private final Game game;

    @Getter private T camera;


    protected AbstractGameRunner(@NonNull Game game, @NonNull Map<Class, IRenderer> stateRendererMappings) {
        this.game = game;
        this.stateRendererMappings = stateRendererMappings;
        this.game.setStateChangeCallback(this::onStateChange);
    }

    /**
     * Takaisinkutsu joka ajetaan kun käärityn pelin pelitila vaihtuu.
     *
     * @param state uusi pelitila
     */
    protected void onStateChange(@NonNull GameState state) {
        val renderer = this.stateRendererMappings.get(state.getClass());
        if (renderer == null) {
            throw new IllegalStateException("No renderer registered for state \"" + state.getClass().getSimpleName() + "\"");
        }
        if (renderer.init()) {
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
        stateRenderer.postDraw(camera, state);
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

        while (this.game.isRunning()) {
            this.game.update();
            display(this.camera);
        }
        this.game.destroy();
        destroy();
    }
}
