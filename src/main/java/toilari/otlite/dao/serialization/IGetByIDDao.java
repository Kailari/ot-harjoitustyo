package toilari.otlite.dao.serialization;


/**
 * DAO joka tarjoaa {@link #getByID(String)} metodin.
 *
 * @param <T> ladattavan olion tyyppi
 */
public interface IGetByIDDao<T> {
    /**
     * Lataa annettua tunnusta vastaavan olion.
     *
     * @param id olion tunniste
     * @return <code>null</code> jos olioa ei löydy, muulloin ladattu olio
     */
    T getByID(String id);
}
