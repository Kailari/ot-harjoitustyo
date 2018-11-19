package toilari.otlite.rendering.lwjgl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import toilari.otlite.game.Game;
import toilari.otlite.game.GameState;
import toilari.otlite.io.Input;
import toilari.otlite.io.lwjgl.LWJGLInputHandler;
import toilari.otlite.rendering.AbstractGameRenderer;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.IRenderer;

import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Piirtää pelin käyttäen LWJGL ja GLFW -kirjastoja.
 */
@Slf4j
public class LWJGLGameRenderer extends AbstractGameRenderer {
    @Getter @Setter private int windowWidth;
    @Getter @Setter private int windowHeight;
    private long windowHandle;

    @NonNull private final Map<Class, IRenderer> stateRendererMappings;

    private Camera camera;

    /**
     * Luo uuden LWJGL-pohjaisen piirtäjän.
     *
     * @param game                  peli jonka tämä käyttöliittymämoottori käärii
     * @param stateRendererMappings hakutaulu pelitilojen piirtäjille
     */
    public LWJGLGameRenderer(@NonNull Game game, @NonNull Map<Class, IRenderer> stateRendererMappings) {
        super(game);
        this.stateRendererMappings = stateRendererMappings;
        this.windowWidth = 800;
        this.windowHeight = 600;

        this.windowHandle = NULL;
    }

    @Override
    public boolean init() {
        if (!initContext()) {
            LOG.error("Fatal error initializing game renderer: could not initialize window!");
            return false;
        }

        glfwShowWindow(this.windowHandle);

        this.camera = new Camera(getWindowWidth(), getWindowHeight());

        GL.createCapabilities();
        glClearColor(0.35f, 0.35f, 0.95f, 1.0f);

        return true;
    }

    private boolean initContext() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            LOG.error("Could not initialize window!");
            return false;
        }

        initWindow();
        Input.init(new LWJGLInputHandler(this.windowHandle));

        if (!initVideoMode()) {
            return false;
        }

        glfwMakeContextCurrent(this.windowHandle);
        glfwSwapInterval(1);

        return true;
    }

    private void initWindow() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        this.windowHandle = glfwCreateWindow(getWindowWidth(), getWindowHeight(), "OTLite", NULL, NULL);
    }

    @Override
    protected void onStateChange(@NonNull GameState state) {
        val renderer = this.stateRendererMappings.get(state.getClass());
        if (renderer == null) {
            throw new IllegalStateException("No renderer registered for state \"" + state.getClass().getSimpleName() + "\"");
        }
        renderer.init(state);
    }

    private boolean initVideoMode() {
        try (val stack = MemoryStack.stackPush()) {
            val pWidth = stack.mallocInt(1);
            val pheight = stack.mallocInt(1);

            glfwGetWindowSize(this.windowHandle, pWidth, pheight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode == null) {
                LOG.error("Initializing video mode failed.");
                return false;
            }

            glfwSetWindowPos(
                this.windowHandle,
                (vidMode.width() - pWidth.get(0)) / 2,
                (vidMode.height() - pheight.get(0)) / 2);
        }

        return true;
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glfwPollEvents();
        val state = getGame().getCurrentGameState();
        val stateRenderer = this.stateRendererMappings.get(state.getClass());
        if (stateRenderer == null) {
            throw new IllegalStateException("No renderer registered for state \"" + state.getClass().getSimpleName() + "\"");
        }
        stateRenderer.draw(this.camera, state);

        glfwSwapBuffers(this.windowHandle);

        if (glfwWindowShouldClose(this.windowHandle)) {
            getGame().setRunning(false);
        }
    }

    @Override
    public void destroy() {
        if (this.windowHandle != NULL) {
            glfwFreeCallbacks(this.windowHandle);
            glfwDestroyWindow(this.windowHandle);
        }
    }
}
