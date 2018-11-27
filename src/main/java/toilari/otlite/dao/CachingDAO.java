package toilari.otlite.dao;

import lombok.val;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO joka säilöö ladatut oliot muistiin jotta niiden lataaminen myöhemmin olisi nopeampaa.
 *
 * @param <T> ladattavan olion tyyppi
 * @param <K> oliojen tunnisteen tyyppi
 */
public abstract class CachingDAO<T, K> implements IGetDAO<T, K>, IGetAllDAO<T> {
    private final Map<K, T> loaded = new HashMap<>();

    @Override
    public Collection<T> getAll() {
        return this.loaded.values();
    }

    @Override
    public T get(K key) {
        if (!this.loaded.containsKey(key)) {
            val loaded = load(key);
            if (loaded == null) {
                return null;
            }

            this.loaded.put(key, loaded);
        }

        return this.loaded.get(key);
    }

    protected abstract T load(K key);
}
