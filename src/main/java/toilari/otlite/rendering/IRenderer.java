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
     * @param renderable objekti jolle resurssit varataan
     * @return <code>true</code> jos alustus onnistuu, muulloin <code>false</code>
     * @throws NullPointerException jos objekti on <code>null</code>
     */
    default boolean init(@NonNull TRenderable renderable) {
        return true;
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
     * Vapauttaa allokoidut resurssit.
     *
     * @param renderable object objekti jolle varatut resurssit vapautetaan
     * @throws NullPointerException jos objetki on <code>null</code>
     */
    default void destroy(@NonNull TRenderable renderable) {
    }
}
