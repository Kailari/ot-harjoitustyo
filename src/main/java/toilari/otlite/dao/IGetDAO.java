package toilari.otlite.dao;

/**
 * DAO joka tarjoaa {@link #get(K)} metodin.
 *
 * @param <T> ladattavan olion tyyppi
 * @param <K> ladattavan olion tunnisteen tyyppi
 */
public interface IGetDAO<T, K> {
    /**
     * Lataa annettua avainta vastaavan olion.
     *
     * @param key olion tunniste
     * @return <code>null</code> jos olioa ei löydy, muulloin ladattu olio
     */
    T get(K key);
}
