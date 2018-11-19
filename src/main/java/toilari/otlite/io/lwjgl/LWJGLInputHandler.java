package toilari.otlite.io.lwjgl;

import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;
import toilari.otlite.io.IInputHandler;
import toilari.otlite.io.Key;

/**
 * LWJGL-pohjainen implementaatio syötekäsittelijästä.
 */
@RequiredArgsConstructor
public class LWJGLInputHandler implements IInputHandler {
    private final long windowHandle;

    @Override
    public boolean isKeyDown(Key key) {
        return GLFW.glfwGetKey(this.windowHandle, key.getKeycode()) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean isKeyUp(Key key) {
        return GLFW.glfwGetKey(this.windowHandle, key.getKeycode()) == GLFW.GLFW_RELEASE;
    }
}
