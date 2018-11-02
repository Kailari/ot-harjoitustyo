package toilari.otlite.io.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;
import toilari.otlite.world.Tile;

/**
 * DAO ruututyyppien lataamiseen määrittelytiedostoista.
 */
@Slf4j
public class TileDAO {
    private static final String PATH = "tiles";

    private final File contentRoot;
    private final Gson gson;

    /**
     * Luo uuden TileDAOn, joka olettaa määrittelytiedostojen löytyvän
     * juurihakemiston <code>contentRoot</code> alta.
     * 
     * @param contentRoot Juurihakemisto mistä ruutujen määrittelytiedostoja
     *                        etsitään
     * @throws NullPointerException     if ContentRoot is null
     * @throws IllegalArgumentException if ContentRoot is not a directory
     */
    public TileDAO(File contentRoot) {
        if (contentRoot == null) {
            throw new NullPointerException("ContentRoot cannot be null!");
        }
        if (!contentRoot.isDirectory()) {
            throw new IllegalArgumentException("ContentRoot must be a directory!");
        }

        this.contentRoot = findContentRootOrCrash(contentRoot);
        this.gson = new GsonBuilder().create();
    }

    /**
     * Etsii polusta <code>[content_root]/tiles/</code> kaikki .json -tiedostot ja
     * yrittää ladata ne {@link Tile ruutuina}.
     * 
     * @return Taulukko jossa kaikki löydetyt ruututyypit.
     */
    public Tile[] discoverAndLoadAll() {
        File[] fileCandidates = contentRoot.listFiles((dir, name) -> {
            return name.toLowerCase().endsWith(".json");
        });

        List<Tile> successfullyLoadedTiles = new ArrayList<>();
        for (File candidate : fileCandidates) {
            // accept only files, not directories etc.
            if (!candidate.isFile()) {
                continue;
            }

            Tile tile = tryLoad(candidate);
            if (tile != null) {
                successfullyLoadedTiles.add(tile);
            }
        }

        return successfullyLoadedTiles.toArray(new Tile[successfullyLoadedTiles.size()]);
    }

    /**
     * Yrittää ladata ruututyypin määrittelyn tiedostosta.
     * 
     * @param file Tiedosto josta määrittely ladataan.
     * @return <code>null</code> jos tiedosto ei ollut validi ruututyypin määrittely
     *         tai jos tiedostoa ei löytynyt tai sitä ei voitu lukea. Muutoin
     *         määrittelyn mukainen {@link Tile ruutu}-instanssi.
     */
    public Tile tryLoad(File file) {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Tile.class);
        } catch (FileNotFoundException e) {
            LOG.warn("File %s not found or could not be read.", file.getPath());
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file %s: %s", file.getPath(), e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file %s", file.getPath());
        }

        return null;
    }


    private static File findContentRootOrCrash(File contentRoot) {
        File[] potentialTileRoots = contentRoot.listFiles((dir, name) -> {
            return name.equals(PATH);
        });
        if (potentialTileRoots.length != 1 || !potentialTileRoots[0].isDirectory()) {
            throw new IllegalStateException("Could not find valid content root!");
        }

        return potentialTileRoots[0];
    }
}