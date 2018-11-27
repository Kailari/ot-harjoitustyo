package toilari.otlite.dao;

import lombok.NonNull;
import toilari.otlite.dao.util.FileHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * DAO joka tunnistaa ja lataa kohdetiedostoja automaattisesti, ilman että spesifejä kohdenimiä tarvitaan.
 *
 * @param <T> ladattavien oliojen tyyppi
 */
public abstract class AutoDiscoverFileDAO<T> extends CachingDAO<T, Path> implements IGetAllDAO<T> {
    private final Path contentRoot;
    private boolean loaded = false;

    protected AutoDiscoverFileDAO(@NonNull String contentRoot) {
        this.contentRoot = Paths.get(contentRoot);
    }

    /**
     * Tiedostotyypit joiden <i>oletetaan</i> sisältävän ladattavan tyyppisiä olioja.
     *
     * @return taulukko jossa tiedostotyypit
     */
    @NonNull
    protected abstract String[] getFileExtensions();


    /**
     * Etsii juurihakemistosta kaikki {@link #getFileExtensions()} tyyppiset tiedostot ja yrittää ladata ne. Ladatut
     * oliot varastoidaan ja niihin pääsee käsiksi kutsumalla {@link #getAll()}. Tyhjentää vanhat ladatut oliot
     * ennen uusien lataamista.
     */
    public void discoverAndLoadAll() {
        FileHelper.discoverFiles(this.contentRoot, getFileExtensions()).forEach(this::get);
    }

    @Override
    public Collection<T> getAll() {
        if (!this.loaded) {
            discoverAndLoadAll();
            this.loaded = true;
        }

        return super.getAll();
    }

    /**
     * Lataa annetussa (merkkijono) polussa olevan tiedoston.
     *
     * @param path polku josta tiedosto ladataan
     * @return ladattu tiedosto tai <code>null</code> jos lataaminen epäonnistuu
     * @throws NullPointerException jos polku on <code>null</code>
     */
    public T get(@NonNull String path) {
        return get(this.contentRoot.resolve(path));
    }
}
