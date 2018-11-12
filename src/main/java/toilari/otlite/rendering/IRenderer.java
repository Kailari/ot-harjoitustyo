package toilari.otlite.rendering;

public interface IRenderer<TRenderable> {
    /**
     * Piirtää annetun objektin.
     *
     * @param renderable objekti joka piirrettään
     */
    void draw(TRenderable renderable);

    /**
     * Alustaa objektin piirtämiseksi tarvittavat resurssit.
     *
     * @param renderable object objekti jolle resurssit varataan
     * @return <code>true</code> jos alustus onnistuu, muulloin <code>false</code>
     */
    default boolean init(TRenderable renderable) {
        return true;
    }

    /**
     * Vapauttaa allokoidut resurssit.
     *
     * @param renderable object objekti jolle varatut resurssit vapautetaan
     */
    default void destroy(TRenderable renderable) {
    }
}
