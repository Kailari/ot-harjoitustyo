package toilari.otlite.view.lwjgl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import toilari.otlite.game.AbstractGameRunner;
import toilari.otlite.game.Game;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.view.renderer.IGameStateRenderer;

import java.util.Arrays;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Piirtää pelin käyttäen LWJGL ja GLFW -kirjastoja.
 */
@Slf4j
public class LWJGLGameRunner extends AbstractGameRunner<LWJGLCamera> {
    @Getter @Setter private int windowWidth;
    @Getter @Setter private int windowHeight;
    private long windowHandle;

    private double[] fpsCache = new double[120];
    private double[] deltaCache = new double[120];
    private int frames = 0;

    /**
     * Luo uuden LWJGL-pohjaisen piirtäjän.
     *
     * @param game                  peli jonka tämä käyttöliittymämoottori käärii
     * @param stateRendererMappings hakutaulu pelitilojen piirtäjille
     */
    public LWJGLGameRunner(@NonNull Game game, @NonNull Map<Class, IGameStateRenderer> stateRendererMappings) {
        super(game, stateRendererMappings);
        this.windowWidth = 1024;
        this.windowHeight = 768;

        this.windowHandle = NULL;
    }

    @Override
    public boolean init() {
        if (!initContext()) {
            LOG.error("Fatal error initializing game renderer: could not initialize window!");
            return false;
        }

        glfwShowWindow(this.windowHandle);

        GL.createCapabilities();
        glClearColor(0.125f, 0.1f, 0.25f, 1.0f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        return true;
    }

    @Override
    protected IInputHandler createInputHandler() {
        return new LWJGLInputHandler(this.windowHandle);
    }

    @Override
    protected LWJGLCamera createCamera() {
        return new LWJGLCamera(getWindowWidth(), getWindowHeight(), 8.0f);
    }

    private boolean initContext() {
        GLFWErrorCallback
            .createPrint(System.err)
            .set();

        if (!glfwInit()) {
            LOG.error("Could not initialize window!");
            return false;
        }

        initWindow();

        if (!initVideoMode()) {
            return false;
        }

        glfwMakeContextCurrent(this.windowHandle);
        glfwSwapInterval(1);

        return true;
    }

    private void initWindow() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        this.windowHandle = glfwCreateWindow(getWindowWidth(), getWindowHeight(), "OTLite", NULL, NULL);

        GLFWWindowSizeCallback
            .create((handle, width, height) -> {
                if (getCamera() != null) {
                    getCamera().resizeViewport(width, height);
                }
            })
            .set(this.windowHandle);
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
    public void display(@NonNull LWJGLCamera camera) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glfwPollEvents();
        super.display(camera);

        glfwSwapBuffers(this.windowHandle);

        if (glfwWindowShouldClose(this.windowHandle)) {
            getGame().setRunning(false);
        }

        val delta = getDelta();
        val fps = Math.round(1.0 / delta);
        this.frames++;
        val frame = this.frames >= this.deltaCache.length ? this.deltaCache.length - 1 : this.frames;
        for (int i = frame; i > 0; i--) {
            this.deltaCache[i] = this.deltaCache[i - 1];
            this.fpsCache[i] = this.fpsCache[i - 1];
        }
        this.fpsCache[0] = fps;
        this.deltaCache[0] = delta;

        val averageFps = Arrays.stream(this.fpsCache).limit(this.frames).average().orElse(0.0);
        val averageDelta = Arrays.stream(this.deltaCache).limit(this.frames).average().orElse(0.0);
        glfwSetWindowTitle(this.windowHandle, String.format("OT-lite - FPS:%4d - Frame duration: %.8f", Math.round(averageFps), averageDelta));
    }


    @Override
    public void destroy() {
        if (this.windowHandle != NULL) {
            glfwFreeCallbacks(this.windowHandle);
            glfwDestroyWindow(this.windowHandle);
        }
    }
}
