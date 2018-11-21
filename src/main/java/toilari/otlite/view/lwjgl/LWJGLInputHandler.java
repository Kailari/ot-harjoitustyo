package toilari.otlite.view.lwjgl;

import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Key;

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
