package toilari.otlite.view.renderer;

import lombok.NonNull;
import toilari.otlite.view.Camera;
import toilari.otlite.view.lwjgl.LWJGLGameRunner;

/**
 * Piirtäjä pelin komponenttien piirtämiseksi ruudulle.
 *
 * @param <TRenderable> piirrettävän objektin tyyppi
 */
public interface IRenderer<TRenderable, TCamera extends Camera> {
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
     * erikoistuneet implementaation (esim. {@link LWJGLGameRunner}) voivat pakottaa kameran ei-nulliksi.
     *
     * @param camera     kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param renderable objekti joka piirrettään
     * @throws NullPointerException jos kamera tai objekti on <code>null</code>
     */
    void draw(@NonNull TCamera camera, @NonNull TRenderable renderable);

    /**
     * Voidaan käyttää objektin piirtämiseen, mikäli objekti tulee piirtää kaiken muun päälle. (käyttöliittymä yms.)
     *
     * @param camera     kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param renderable objekti joka piirretään
     * @throws NullPointerException jos objetki tai kamera on <code>null</code>
     */
    default void postDraw(@NonNull TCamera camera, @NonNull TRenderable renderable) {
    }

    /**
     * Vapauttaa allokoidut resurssit.
     */
    default void destroy() {
    }
}
