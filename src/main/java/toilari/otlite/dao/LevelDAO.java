package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.world.level.LevelData;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * DAO karttojen lataamiseen levyltä.
 */
@Slf4j
public class LevelDAO extends AutoDiscoverFileDAO<LevelData> implements IGetByIDDao<LevelData> {
    private static final String[] EXTENSIONS = {"json"};

    @NonNull private final Gson gson;

    /**
     * Luo uuden DAOn karttojen lataamiseksi.
     *
     * @param contentRoot juurihakemisto josta karttoja etsitään
     */
    public LevelDAO(@NonNull String contentRoot) {
        super(contentRoot);
        this.gson = new GsonBuilder()
            .create();
    }

    @NonNull
    @Override
    protected String[] getFileExtensions() {
        return LevelDAO.EXTENSIONS;
    }

    @Override
    protected LevelData load(Path path) {
        try (Reader reader = TextFileHelper.getReader(path)) {
            val data = this.gson.fromJson(reader, LevelData.class);
            return data;
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file {}", path);
        }

        return null;
    }

    @Override
    public LevelData getByID(String id) {
        return get(id + "." + LevelDAO.EXTENSIONS[0]);
    }
}
