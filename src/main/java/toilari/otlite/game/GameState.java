package toilari.otlite.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import toilari.otlite.rendering.Camera;

/**
 * "Pelitila", erillinen kokonaisuus pelin sisällä joka vaatii muista
 * pelitiloista poikkeavaa suorituslogiikkaa. Esim. valikot ja itse peli ovat
 * erillisiä pelitilojaan.
 */
@RequiredArgsConstructor
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
     * Piirtää pelimaailman nykytilanteen ruudulle.
     *
     * @param camera kamera jonka näkökulmasta piirretään
     */
    public abstract void draw(Camera camera);

    /**
     * Tuhoaa pelitilan. Kutsutaan kerran kun pelitila poistuu käytöstä.
     */
    public abstract void destroy();
}