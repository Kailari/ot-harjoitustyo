package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.serialization.ColorAdapter;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.dao.serialization.RendererAdapter;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.renderer.CharacterRenderer;
import toilari.otlite.view.lwjgl.renderer.Context;
import toilari.otlite.view.lwjgl.renderer.ILWJGLRenderer;
import toilari.otlite.view.lwjgl.renderer.PlayerRenderer;
import toilari.otlite.view.renderer.IRenderer;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * Lataa piirtäjiä määritystiedostoista.
 */
@Slf4j
public class RendererDAO extends AutoDiscoverFileDAO<ILWJGLRenderer> implements IGetByIDDao<ILWJGLRenderer> {
    private static final String[] EXTENSIONS = {"json"};
    private final Gson gson;

    /**
     * Luo uuden daon piirtäjien lataamista varten.
     *
     * @param root       juurihakemisto josta piirtäjiä etsitään
     * @param textureDAO tekstuuridao piirtäjien tekstuurien lataamiseen
     *
     * @throws NullPointerException jos polku tai dao on <code>null</code>
     */
    public RendererDAO(@NonNull String root, @NonNull TextureDAO textureDAO) {
        super(root);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .registerTypeAdapter(ILWJGLRenderer.class, new RendererAdapter(textureDAO))
            .create();
    }

    @NonNull
    @Override
    protected String[] getFileExtensions() {
        return RendererDAO.EXTENSIONS;
    }

    @Override
    protected ILWJGLRenderer load(Path path) {
        try (Reader reader = TextFileHelper.getReader(path)) {
            return this.gson.fromJson(reader, ILWJGLRenderer.class);
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file {}", path);
        }

        return null;
    }

    @Override
    public ILWJGLRenderer getByID(String id) {
        return get(id + ".json");
    }
}
