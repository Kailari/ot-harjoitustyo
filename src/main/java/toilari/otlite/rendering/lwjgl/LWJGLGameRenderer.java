package toilari.otlite.rendering.lwjgl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import toilari.otlite.game.Game;
import toilari.otlite.io.Input;
import toilari.otlite.io.LWJGLInputHandler;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.GameRenderer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Piirtää pelin käyttäen LWJGL ja GLFW -kirjastoja.
 */
@Slf4j
public class LWJGLGameRenderer extends GameRenderer {
    @Getter @Setter private int windowWidth;
    @Getter @Setter private int windowHeight;
    private long windowHandle;

    private Camera camera;

    /**
     * Luo uuden LWJGL-pohjaisen piirtäjän.
     */
    public LWJGLGameRenderer() {
        this.windowWidth = 800;
        this.windowHeight = 600;

        this.windowHandle = NULL;
    }

    @Override
    public boolean init(Game game) {
        if (!initWindow()) {
            LOG.error("Fatal error initializing game renderer: could not initialize window!");
            return false;
        }

        glfwShowWindow(this.windowHandle);

        this.camera = new Camera(getWindowWidth(), getWindowHeight());

        GL.createCapabilities();
        glClearColor(0.35f, 0.35f, 0.95f, 1.0f);

        return true;
    }

    private boolean initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            LOG.error("Could not initialize window!");
            return false;
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        this.windowHandle = glfwCreateWindow(getWindowWidth(), getWindowHeight(), "OTLite", NULL, NULL);
        Input.init(new LWJGLInputHandler(this.windowHandle));

        if (!initVideoMode()) {
            return false;
        }

        glfwMakeContextCurrent(this.windowHandle);
        glfwSwapInterval(1);

        return true;
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
    public void draw(Camera camera, Game game) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glfwPollEvents();
        super.draw(this.camera, game);

        glfwSwapBuffers(this.windowHandle);

        // TODO: Input manager
        if (glfwWindowShouldClose(this.windowHandle)) {
            game.setRunning(false);
        }
    }

    @Override
    public void destroy(Game game) {
        super.destroy(game);
        if (this.windowHandle != NULL) {
            glfwFreeCallbacks(this.windowHandle);
            glfwDestroyWindow(this.windowHandle);
        }
    }
}
