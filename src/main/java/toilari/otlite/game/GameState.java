package toilari.otlite.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import toilari.otlite.game.event.EventSystem;

/**
 * "Pelitila", erillinen kokonaisuus pelin sisällä joka vaatii muista
 * pelitiloista poikkeavaa suorituslogiikkaa. Esim. valikot ja itse peli ovat
 * erillisiä pelitilojaan.
 */
public abstract class GameState {
    @Getter @NonNull private final EventSystem eventSystem = new EventSystem();
    @Getter @Setter(AccessLevel.PROTECTED) private Game game;

    /**
     * Alustaa/valmistelee pelitilan. Kutsutaan kerran kun pelitila aktivoituu.
     *
     * @return <code>true</code> jos alustus epäonnistuu kohtalokkaasti
     */
    public abstract boolean init();

    /**
     * Päivittää pelin tilan.
     */
    public abstract void update();

    /**
     * Tuhoaa pelitilan. Kutsutaan kerran kun pelitila poistuu käytöstä.
     */
    public abstract void destroy();
}