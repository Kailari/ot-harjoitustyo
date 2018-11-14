package toilari.otlite.io;

import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;

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
