package toilari.otlite.dao.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.view.renderer.IRenderer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RendererAdapter implements JsonDeserializer<IRenderer> {
    private final Map<String, RendererEntry<?, ?>> renderers = new HashMap<>();
    private final TextureDAO textureDAO;

    public <R extends IRenderer, C> void registerRenderer(
        @NonNull String key,
        @NonNull RendererFactory<R, C> rendererFactory,
        @NonNull Class<? extends C> contextClass
    ) {
        this.renderers.put(key, new RendererEntry<>(rendererFactory, contextClass));
    }

    public RendererAdapter(@NonNull TextureDAO textureDAO) {
        this.textureDAO = textureDAO;
    }

    @Override
    public IRenderer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        val jsonObj = json.getAsJsonObject();
        if (jsonObj == null) {
            return null;
        }

        val classPrimitive = jsonObj.getAsJsonPrimitive("class");
        if (classPrimitive == null) {
            return null;
        }

        val key = classPrimitive.getAsString();
        val entry = this.renderers.get(key);
        if (entry == null) {
            return null;
        }

        val renderContext = context.deserialize(jsonObj, entry.getContextClass());

        return entry.createRenderer(this.textureDAO, renderContext);
    }

    public static class RendererEntry<R extends IRenderer, C> {
        @Getter private final RendererFactory<R, C> factory;
        @Getter private final Class<? extends C> contextClass;

        public RendererEntry(@NonNull RendererFactory<R, C> factory, @NonNull Class<? extends C> contextClass) {
            this.factory = factory;
            this.contextClass = contextClass;
        }

        public R createRenderer(TextureDAO textureDAO, Object context) {
            return this.factory.provide(textureDAO, (C) context);
        }
    }

    public interface RendererFactory<R extends IRenderer, C> {
        R provide(TextureDAO textures, C context);
    }
}
