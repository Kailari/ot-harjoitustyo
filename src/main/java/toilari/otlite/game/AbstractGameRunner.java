package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Input;
import toilari.otlite.view.Camera;

/**
 * Apuluokka pelin pääloopin määrittelyyn ja suorittamiseen. Käärii pelin logiikan ja käyttää sitä ajamaan
 * käyttöliittymää.
 *
 * @param <T> käyettävän kameran tyyppi
 */
public abstract class AbstractGameRunner<T extends Camera> {
    @NonNull @Getter private final Game game;
    @Getter private T camera;

    protected AbstractGameRunner(@NonNull Game game) {
        this.game = game;
        this.game.setStateChangeCallback(this::onStateChange);
    }

    /**
     * Takaisinkutsu joka ajetaan kun käärityn pelin pelitila vaihtuu.
     *
     * @param gameState uusi pelitila
     */
    protected abstract void onStateChange(@NonNull GameState gameState);

    /**
     * Alustaa pelin piirtämiseen tarvittavat resurssit.
     *
     * @return <code>true</code> jos alustus onnistuu, <code>false</code> jos ilmentyy virhe
     */
    protected abstract boolean init();

    /**
     * Piirtää pelin.
     */
    protected abstract void display(@NonNull Camera camera);

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
