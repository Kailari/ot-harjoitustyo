package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.serialization.TileAdapter;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.world.level.KillTile;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * DAO ruututyyppien lataamiseen määrittelytiedostoista.
 */
@Slf4j
public class TileDAO extends AutoDiscoverFileDAO<Tile> {
    private static final String[] EXTENSIONS = {"json", "tile"};

    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Tile.class, constructTileAdapter())
        .create();

    private static TileAdapter constructTileAdapter() {
        val adapter = new TileAdapter();
        adapter.registerTileType("normal", NormalTile.class);
        adapter.registerTileType("kill", KillTile.class);
        return adapter;
    }

    /**
     * Luo uuden TileDAOn joka etsii määrittelytiedostoja polusta
     * <code>&lt;contentRoot&gt;/tiles/*.json</code>.
     *
     * @param contentRoot Juurihakemisto josta pelin resursseja etsitään
     * @throws NullPointerException jos juurihakemisto on <code>null</code>
     */
    public TileDAO(@NonNull String contentRoot) {
        super(contentRoot);
    }

    @Override
    protected @NonNull String[] getFileExtensions() {
        return TileDAO.EXTENSIONS;
    }

    /**
     * Yrittää ladata ruututyypin määrittelyn tiedostosta.
     *
     * @param path Tiedoston polku josta määrittely ladataan
     * @return <code>null</code> jos tiedosto ei ollut validi ruututyypin määrittely
     * tai jos tiedostoa ei löytynyt tai sitä ei voitu lukea. Muutoin
     * määrittelyn mukainen {@link Tile ruutu}-instanssi.
     * @throws NullPointerException jos polku on <code>null</code>
     */
    @Override
    protected Tile load(@NonNull Path path) {
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