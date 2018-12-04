package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.util.FileHelper;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.profile.Settings;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DAO profiilikohtaisten asetusten noutamiseen JSON-tiedostoista.
 */
@Slf4j
public class SettingsDAO {
    private final Gson gson = new GsonBuilder().create();
    private final Path root;

    /**
     * Luo uuden DAO:n joka etsii asetustiedostoja annetusta polusta.
     *
     * @param root polku josta tallennustiedostoja etsitään
     * @throws NullPointerException jos polku on <code>null</code>
     */
    public SettingsDAO(@NonNull String root) {
        this.root = Paths.get(root);
    }

    /**
     * Lataa asetustiedoston annetun pelaajanimen mukaan.
     *
     * @param name profiilin nimi jonka asetukset haetaan
     * @return <code>null</code> jos asetustiedostoa ei löytynyt, muulloin ladattu asetustiedosto
     * @throws NullPointerException jos nimi on <code>null</code>
     */
    public Settings loadByProfileName(@NonNull String name) {
        val filename = nameToKey(name);
        val path = this.root.resolve(filename);
        if (!FileHelper.fileExists(path.toString())) {
            try (val writer = TextFileHelper.getWriter(path)) {
                this.gson.toJson(new Settings(), Settings.class, writer);
            } catch (IOException e) {
                LOG.error("Error creating settings file.");
            }
        }

        try (val reader = TextFileHelper.getReader(path)) {
            return this.gson.fromJson(reader, Settings.class);
        } catch (IOException e) {
            LOG.error("Error reading settings from file: {}", e.getMessage());
            LOG.error("Falling back to default settings.");
            return new Settings();
        }
    }

    /**
     * Poistaa asetukset profiilin nimen perusteella.
     *
     * @param name profiilinimi
     * @throws NullPointerException jos nimi on <code>null</code>
     */
    public void removeByName(@NonNull String name) {
        val filename = nameToKey(name);
        FileHelper.deleteFile(this.root.resolve(filename));
    }

    private String nameToKey(@NonNull String name) {
        return name.replaceAll("[^A-Za-z0-9 ]", "") + ".settings";
    }
}
