package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

/**
 * Piirtäjän erikoistapaus LWJGL-pohjaiseen piirtämiseen.
 *
 * @param <T> piirrettävän objektin tyyppi
 */
public interface ILWJGLRenderer<T> {
    /**
     * Alustaa objektin piirtämiseksi tarvittavat resurssit.
     *
     * @return <code>true</code> jos alustus epäonnistuu, muulloin <code>false</code>
     */
    default boolean init() {
        return false;
    }

    /**
     * Piirtää annetun objektin.
     *
     * @param camera     kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param renderable objekti joka piirrettään
     * @param batch      sarjapiirtäjä jonka jonoon piirtokomennot asetetaan
     *
     * @throws NullPointerException jos kamera tai objekti on <code>null</code>
     */
    void draw(@NonNull LWJGLCamera camera, @NonNull T renderable, @NonNull SpriteBatch batch);

    /**
     * Voidaan käyttää objektin piirtämiseen, mikäli objekti tulee piirtää kaiken muun päälle. (käyttöliittymä yms.)
     *
     * @param camera     kamera jonka näkökulmasta piirtäminen tapahtuu
     * @param renderable objekti joka piirretään
     * @param batch      sarjapiirtäjä jonka jonoon piirtokomennot asetetaan
     *
     * @throws NullPointerException jos objetki tai kamera on <code>null</code>
     */
    default void postDraw(@NonNull LWJGLCamera camera, @NonNull T renderable, @NonNull SpriteBatch batch) {
    }

    /**
     * Vapauttaa allokoidut resurssit.
     */
    default void destroy() {
    }
}
