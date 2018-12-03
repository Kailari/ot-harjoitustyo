package toilari.otlite.view.renderer;


import lombok.NonNull;
import toilari.otlite.game.GameState;
import toilari.otlite.view.Camera;

/**
 * Piirtäjä pelitilojen piirtämiseksi ruudulle.
 *
 * @param <T> piirrettävän pelitilan tyyppi
 */
public interface IGameStateRenderer<T extends GameState, TCamera extends Camera> {
    /**
     * Alustaa pelitilan piirtämiseksi tarvittavat resurssit.
     *
     * @param state piirettävä pelitila
     * @return <code>true</code> jos alustus epäonnistuu, muulloin <code>false</code>
     */
    boolean init(@NonNull T state);

    /**
     * Piirtää annetun pelitilan.
     *
     * @param camera kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param state  pelitila joka piirrettään
     * @throws NullPointerException jos kamera tai objekti on <code>null</code>
     */
    void draw(@NonNull TCamera camera, @NonNull T state);

    /**
     * Vapauttaa allokoidut resurssit.
     *
     * @param state pelitila joka piirretään
     */
    void destroy(@NonNull T state);
}
