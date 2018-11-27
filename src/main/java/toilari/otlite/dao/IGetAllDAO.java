package toilari.otlite.dao;

import java.util.Collection;

/**
 * DAO joka tarjoaa metodin {@link #getAll()}.
 *
 * @param <T> ladattavien oliojen tyyppi
 */
public interface IGetAllDAO<T> {
    /**
     * Hakee kaikki saatavilla olevat oliot.
     *
     * @return kokoelma saatavilla olevia olioja
     */
    Collection<T> getAll();
}
