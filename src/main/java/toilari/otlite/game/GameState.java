package toilari.otlite.game;

import lombok.*;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.IRenderer;

/**
 * "Pelitila", erillinen kokonaisuus pelin sisällä joka vaatii muista
 * pelitiloista poikkeavaa suorituslogiikkaa. Esim. valikot ja itse peli ovat
 * erillisiä pelitilojaan.
 */
public abstract class GameState {
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) private Game game;

    /**
     * Alustaa/valmistelee pelitilan. Kutsutaan kerran kun pelitila aktivoituu.
     */
    public abstract void init();

    /**
     * Päivittää pelin tilan.
     */
    public abstract void update();

    /**
     * Tuhoaa pelitilan. Kutsutaan kerran kun pelitila poistuu käytöstä.
     */
    public abstract void destroy();
}