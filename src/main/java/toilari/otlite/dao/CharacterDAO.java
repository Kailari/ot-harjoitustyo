package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.dao.serialization.CharacterAdapter;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbilityRegistry;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

@Slf4j
public class CharacterDAO extends AutoDiscoverFileDAO<CharacterObject> implements IGetByIDDao<CharacterObject> {
    private static final String[] EXTENSIONS = {"json"};
    private final Gson gson;

    /**
     * Luo uuden DAO:n hahmojen lataamiseksi määritystiedostoista.
     *
     * @param root juurihakemisto josta hahmojen määrityksiä etsitään
     */
    public CharacterDAO(@NonNull String root) {
        super(root);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(CharacterObject.class, new CharacterAdapter())
            .create();
    }

    @NonNull
    @Override
    protected String[] getFileExtensions() {
        return CharacterDAO.EXTENSIONS;
    }


    @Override
    public CharacterObject getByID(@NonNull String path) {
        return super.get(path + ".json");
    }

    @Override
    protected CharacterObject load(Path path) {
        try (Reader reader = TextFileHelper.getReader(path)) {
            return this.gson.fromJson(reader, CharacterObject.class);
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file {}", path);
        }

        return null;
    }
}
