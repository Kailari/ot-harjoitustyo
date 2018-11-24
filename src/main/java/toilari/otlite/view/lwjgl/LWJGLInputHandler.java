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

    private static final double[] MOUSE_X = new double[1];
    private static final double[] MOUSE_Y = new double[1];


    @Override
    public boolean isKeyDown(Key key) {
        return GLFW.glfwGetKey(this.windowHandle, key.getKeycode()) == GLFW.GLFW_PRESS;
    }

    @Override
    public int mouseX() {
        GLFW.glfwGetCursorPos(this.windowHandle, MOUSE_X, MOUSE_Y);
        return (int) Math.floor(MOUSE_X[0]);
    }

    @Override
    public int mouseY() {
        GLFW.glfwGetCursorPos(this.windowHandle, MOUSE_X, MOUSE_Y);
        return (int) Math.floor(MOUSE_Y[0]);
    }

    @Override
    public boolean isMouseDown(int button) {
        return GLFW.glfwGetMouseButton(this.windowHandle, button) == 1;
    }
}
