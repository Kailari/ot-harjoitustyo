package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.serialization.RendererAdapter;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.view.lwjgl.renderer.CharacterRenderer;
import toilari.otlite.view.lwjgl.renderer.Context;
import toilari.otlite.view.lwjgl.renderer.PlayerRenderer;
import toilari.otlite.view.renderer.IRenderer;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

@Slf4j
public class RendererDAO extends AutoDiscoverFileDAO<IRenderer> {
    private static final String[] EXTENSIONS = {"json", "renderer"};
    private final Gson gson;

    public RendererDAO(@NonNull String root, @NonNull TextureDAO textureDAO) {
        super(root);
        val typeAdapter = new RendererAdapter(textureDAO);
        typeAdapter.registerRenderer("player", PlayerRenderer::new, Context.class);
        typeAdapter.registerRenderer("character", CharacterRenderer::new, Context.class);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(IRenderer.class, typeAdapter)
            .create();
    }

    @NonNull
    @Override
    protected String[] getFileExtensions() {
        return EXTENSIONS;
    }

    @Override
    protected IRenderer load(Path path) {
        try (Reader reader = TextFileHelper.getReader(path)) {
            return this.gson.fromJson(reader, IRenderer.class);
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file {}", path);
        }

        return null;
    }
}
