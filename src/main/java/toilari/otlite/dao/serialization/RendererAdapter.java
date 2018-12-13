package toilari.otlite.dao.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.view.lwjgl.renderer.ILWJGLRenderer;
import toilari.otlite.view.renderer.IRenderer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Sarjoitusadapteri piirtäjien määritystiedostojen parsimiseen.
 */
@Slf4j
public class RendererAdapter implements JsonDeserializer<ILWJGLRenderer> {
    private final Map<String, RendererEntry<?, ?>> renderers = new HashMap<>();
    private final TextureDAO textureDAO;

    /**
     * Rekisteröi uuden piirtäjän.
     *
     * @param key             määritystiedostoissa käytettävä piirtäjän luokan tunnus
     * @param rendererFactory tehdas jolla piirtäjiä saadaan tuotettua
     * @param contextClass    piirtokontekstiluokka jolla piirtäjän tarkemmat tiedot saadaan
     * @param <R>             piirtäjän tyyppi
     * @param <C>             piirtokontekstin tyyppi
     */
    public <R extends ILWJGLRenderer, C> void registerRenderer(
        @NonNull String key,
        @NonNull RendererFactory<R, C> rendererFactory,
        @NonNull Class<? extends C> contextClass
    ) {
        this.renderers.put(key, new RendererEntry<>(rendererFactory, contextClass));
    }

    /**
     * Luo uuden piirtäjien sarjoitusadapterin.
     *
     * @param textureDAO dao jolla piirtäjät voivat ladata tekstuureja
     */
    public RendererAdapter(@NonNull TextureDAO textureDAO) {
        this.textureDAO = textureDAO;
    }

    @Override
    public ILWJGLRenderer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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

    /**
     * Tehdas jolla piirtäjiä voidaan tuottaa.
     *
     * @param <R> piirtäjän tyyppi
     * @param <C> piirtokontekstin tyyppi
     */
    public interface RendererFactory<R extends ILWJGLRenderer, C> {
        /**
         * Tuottaa uuden piirtäjän.
         *
         * @param textures tekstuuridao piirtäjän tekstuurien lataamiseen
         * @param context  piirtokonteksti
         *
         * @return uusi piirtäjä jolle on asetettu annettu konteksti
         */
        R provide(TextureDAO textures, C context);
    }
}
