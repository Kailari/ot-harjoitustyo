package toilari.otlite.dao.serialization;

import com.google.gson.GsonBuilder;
import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.renderer.ILWJGLRenderer;
import toilari.otlite.view.lwjgl.renderer.PlayerRenderer;

import static org.junit.jupiter.api.Assertions.*;

class RendererAdapterTest {
    private static final String VALID_INPUT = "{\n" +
        "  \"texture\": \"white_knight.png\",\n" +
        "  \"nFrames\": 8,\n" +
        "  \"states\": {\n" +
        "    \"idle\": [2, 3, 2, 4],\n" +
        "    \"active\": [0, 1],\n" +
        "    \"block\": [5, 6, 7]\n" +
        "  },\n" +
        "  \"framesPerSecond\": 2,\n" +
        "  \"color\": [1.0, 1.0, 1.0],\n" +
        "  \"width\": 8,\n" +
        "  \"height\": 8,\n" +
        "  \"class\": \"player\"\n" +
        "}\n";

    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsIfDaoIsNull() {
        assertThrows(NullPointerException.class, () -> new RendererAdapter(null));
    }

    @Test
    void deserializationSucceedsWithValidInput() {
        val gson = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .registerTypeAdapter(ILWJGLRenderer.class, new RendererAdapter(new TextureDAO("") {
                @Override
                protected Texture load(@NonNull String path) {
                    return new Texture(0, 0, 0);
                }
            }))
            .create();

        val renderer = gson.fromJson(VALID_INPUT, ILWJGLRenderer.class);
        assertTrue(renderer instanceof PlayerRenderer);
        val context = ((PlayerRenderer) renderer).getContext();
        assertEquals(context.texture, "white_knight.png");
        assertEquals(context.nFrames, 8);
        assertEquals(context.width, 8);
        assertEquals(context.height, 8);
    }

    @Test
    void deserializationSucceedsEvenIfTagsAreMissing() {
        val gson = new GsonBuilder()
            .registerTypeAdapter(ILWJGLRenderer.class, new RendererAdapter(new TextureDAO("") {
                @Override
                protected Texture load(@NonNull String path) {
                    return new Texture(0, 0, 0);
                }
            }))
            .create();

        val renderer = gson.fromJson("{}", ILWJGLRenderer.class);
        assertTrue(renderer instanceof RendererAdapter.NOPRenderer);
    }

    @Test
    void deserializationSucceedsEvenIfClassIsUnknown() {
        val gson = new GsonBuilder()
            .registerTypeAdapter(ILWJGLRenderer.class, new RendererAdapter(new TextureDAO("") {
                @Override
                protected Texture load(@NonNull String path) {
                    return new Texture(0, 0, 0);
                }
            }))
            .create();

        val renderer = gson.fromJson("{\"class\": \"this_class_does_not_exist\"}", ILWJGLRenderer.class);
        assertTrue(renderer instanceof RendererAdapter.NOPRenderer);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void registerRendererThrowsIfParametersAreNull() {
        assertThrows(NullPointerException.class, () -> RendererAdapter.registerRenderer(null, null, null));
        assertThrows(NullPointerException.class, () -> RendererAdapter.registerRenderer("null", null, null));
        assertThrows(NullPointerException.class, () -> RendererAdapter.registerRenderer("null", TestRenderer::new, null));
    }

    private class TestRenderer implements ILWJGLRenderer<Object> {
        TestRenderer(TextureDAO textureDAO, Object o) {
        }

        @Override
        public void draw(@NonNull LWJGLCamera camera, @NonNull Object renderable, @NonNull SpriteBatch batch) {
        }
    }
}
