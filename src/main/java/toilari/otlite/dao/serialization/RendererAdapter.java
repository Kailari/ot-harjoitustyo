package toilari.otlite.dao.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.renderer.CharacterRenderer;
import toilari.otlite.view.lwjgl.renderer.Context;
import toilari.otlite.view.lwjgl.renderer.ILWJGLRenderer;
import toilari.otlite.view.lwjgl.renderer.PlayerRenderer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Sarjoitusadapteri piirtäjien määritystiedostojen parsimiseen.
 */
@Slf4j
public class RendererAdapter implements JsonDeserializer<ILWJGLRenderer> {
    private static final Map<String, RendererEntry<?, ?>> RENDERERS = new HashMap<>();

    static {
        registerRenderer("player", PlayerRenderer::new, Context.class);
        registerRenderer("character", CharacterRenderer::new, Context.class);
    }

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
    public static <R extends ILWJGLRenderer, C> void registerRenderer(
        @NonNull String key,
        @NonNull RendererFactory<R, C> rendererFactory,
        @NonNull Class<? extends C> contextClass
    ) {
        RENDERERS.put(key, new RendererEntry<>(rendererFactory, contextClass));
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
            return new NOPRenderer();
        }

        val classPrimitive = jsonObj.getAsJsonPrimitive("class");
        if (classPrimitive == null) {
            LOG.warn("Renderer definition did not contain a \"class\" tag!");
            return new NOPRenderer();
        }

        val key = classPrimitive.getAsString();
        val entry = RENDERERS.get(key);
        if (entry == null) {
            LOG.warn("Unknown renderer class: \"{}\"", key);
            return new NOPRenderer();
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

    public class NOPRenderer implements ILWJGLRenderer {
        @Override
        public void draw(@NonNull LWJGLCamera camera, @NonNull Object renderable, @NonNull SpriteBatch batch) {
            // NOP
        }
    }
}
