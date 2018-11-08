package toilari.otlite.io.dao;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.io.util.FileHelper;
import toilari.otlite.io.util.TextFileHelper;
import toilari.otlite.world.Tile;

/**
 * DAO ruututyyppien lataamiseen määrittelytiedostoista.
 */
@Slf4j
public class TileDAO implements ITileDAO {
    private final Gson gson = new GsonBuilder().create();

    @NonNull private final Path contentRoot;

    @NonNull private Tile[] tiles = new Tile[0];

    /**
     * Hakee kaikki ladatut ruututyyppien määrittelyt. Mikäli ruututyyppejä ei ole,
     * yritetään ne etsiä tiedostoista.
     * 
     * @return kaikki ladatut ruututyypit. Tyhjä taulukko jos yhtään tyyppiä ei ole
     *         löydetty tai jos ruututyyppejä ei vielä ole ladattu
     */
    @NonNull
    @Override
    public Tile[] getTiles() {
        if (this.tiles.length == 0) {
            discoverAndLoadAll();
        }

        return this.tiles;
    }

    /**
     * Luo uuden TileDAOn joka etsii määrittelytiedostoja polusta
     * <code>&lt;contentRoot&gt;/tiles/*.json</code>.
     * 
     * @param contentRoot Juurihakemisto josta pelin resursseja etsitään
     */
    public TileDAO(String contentRoot) {
        this.contentRoot = Paths.get(contentRoot);
    }

    /**
     * Etsii polusta <code>[content_root]/tiles/</code> kaikki .json -tiedostot ja
     * yrittää ladata ne {@link Tile ruutuina}. Löydetyt ja ladatut ruudut
     * varastoidaan ja niihin pääsee käsiksi kutsumalla {@link #getTiles()}.
     */
    public void discoverAndLoadAll() {
        this.tiles = FileHelper.discoverFiles(this.contentRoot, "json")
                .map(this::tryLoad)
                .filter(Objects::nonNull)
                .toArray(Tile[]::new);
    }

    /**
     * Yrittää ladata ruututyypin määrittelyn tiedostosta.
     * 
     * @param path Tiedoston polku josta määrittely ladataan
     * @return <code>null</code> jos tiedosto ei ollut validi ruututyypin määrittely
     *         tai jos tiedostoa ei löytynyt tai sitä ei voitu lukea. Muutoin
     *         määrittelyn mukainen {@link Tile ruutu}-instanssi.
     */
    Tile tryLoad(Path path) {
        try (Reader reader = TextFileHelper.getReader(path)) {
            return this.gson.fromJson(reader, Tile.class);
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file {}", path);
        }

        return null;
    }
}