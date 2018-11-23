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

@Slf4j
public class SettingsDAO {
    private final Gson gson = new GsonBuilder().create();
    private final Path root;

    public SettingsDAO(@NonNull String root) {
        this.root = Paths.get(root);
    }

    public Settings loadByName(@NonNull String name) {
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

    public String nameToKey(@NonNull String name) {
        return name.replaceAll("[^A-Za-z0-9 ]", "") + ".sav";
    }
}
