package toilari.otlite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * "Pelitila", erillinen kokonaisuus pelin sisällä joka vaatii muista
 * pelitiloista poikkeavaa suorituslogiikkaa. Esim. valikot ja itse peli ovat
 * erillisiä pelitilojaan.
 */
@ToString
@RequiredArgsConstructor
public abstract class GameState {
    @Getter private final String name;

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
     */
	public abstract void draw();

    /**
     * Tuhoaa pelitilan. Kutsutaan kerran kun pelitila poistuu käytöstä.
     */
	public abstract void destroy();
}