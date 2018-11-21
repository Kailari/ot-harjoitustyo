package toilari.otlite.rendering;

import lombok.NonNull;
import toilari.otlite.rendering.lwjgl.LWJGLGameRenderer;

/**
 * Piirtäjä pelin komponenttien piirtämiseksi ruudulle.
 *
 * @param <TRenderable> piirrettävän objektin tyyppi
 */
public interface IRenderer<TRenderable> {
    /**
     * Alustaa objektin piirtämiseksi tarvittavat resurssit.
     *
     * @return <code>true</code> jos alustus epäonnistuu, muulloin <code>false</code>
     */
    default boolean init() {
        return false;
    }

    /**
     * Piirtää annetun objektin. Huomaa että abstraktissa implementaatiossa null kamera on sallittu, mutta
     * erikoistuneet implementaation (esim. {@link LWJGLGameRenderer}) voivat pakottaa kameran ei-nulliksi.
     *
     * @param camera     kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param renderable objekti joka piirrettään
     * @throws NullPointerException jos objekti on <code>null</code>
     */
    void draw(Camera camera, @NonNull TRenderable renderable);

    /**
     * Voidaan käyttää objektin piirtämiseen, mikäli objekti tulee piirtää kaiken muun päälle. (käyttöliittymä yms.)
     *
     * @param camera     kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param renderable objekti joka piirretään
     * @throws NullPointerException jos objetki tai kamera on <code>null</code>
     */
    default void postDraw(@NonNull Camera camera, @NonNull TRenderable renderable) {
    }

    /**
     * Vapauttaa allokoidut resurssit.
     *
     * @param renderable object objekti jolle varatut resurssit vapautetaan
     * @throws NullPointerException jos objetki on <code>null</code>
     */
    default void destroy(@NonNull TRenderable renderable) {
    }
}
