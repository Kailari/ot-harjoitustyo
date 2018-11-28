package toilari.otlite.view.lwjgl;

import lombok.val;
import org.lwjgl.glfw.GLFW;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Key;

import java.util.HashMap;
import java.util.Map;

/**
 * LWJGL-pohjainen implementaatio syötekäsittelijästä.
 */
public class LWJGLInputHandler implements IInputHandler {
    private final long windowHandle;

    private final Map<Key, KeyState> keyStates = new HashMap<>();

    private final double[] mouseX = new double[1];
    private final double[] mouseY = new double[1];

    public LWJGLInputHandler(long windowHandle) {
        this.windowHandle = windowHandle;

        for (val key : Key.values()) {
            this.keyStates.put(key, KeyState.Up);
        }
    }

    @Override
    public boolean isKeyDown(Key key) {
        val state = this.keyStates.get(key);
        return state == KeyState.Pressed || state == KeyState.Down;
    }

    @Override
    public boolean isKeyPressed(Key key) {
        val state = this.keyStates.get(key);
        return state == KeyState.Pressed;
    }

    @Override
    public int mouseX() {
        GLFW.glfwGetCursorPos(this.windowHandle, this.mouseX, this.mouseY);
        return (int) Math.floor(this.mouseX[0]);
    }

    @Override
    public int mouseY() {
        GLFW.glfwGetCursorPos(this.windowHandle, this.mouseX, this.mouseY);
        return (int) Math.floor(this.mouseY[0]);
    }

    @Override
    public boolean isMouseDown(int button) {
        return GLFW.glfwGetMouseButton(this.windowHandle, button) == 1;
    }

    @Override
    public void update() {
        for (val key : Key.values()) {
            if (key == Key.UNKNOWN) {
                continue;
            }

            boolean input = GLFW.glfwGetKey(this.windowHandle, key.getKeycode()) == GLFW.GLFW_PRESS;
            KeyState old = this.keyStates.get(key);
            KeyState newState = resolveNewState(input, old);
            this.keyStates.put(key, newState);
        }
    }

    private KeyState resolveNewState(boolean input, KeyState old) {
        KeyState newState;
        if (input) {
            if (old == KeyState.Pressed || old == KeyState.Down) {
                newState = KeyState.Released;
            } else {
                newState = KeyState.Up;
            }
        } else {
            if (old == KeyState.Released || old == KeyState.Up) {
                newState = KeyState.Pressed;
            } else {
                newState = KeyState.Down;
            }
        }
        return newState;
    }

    private enum KeyState {
        Up,
        Pressed,
        Down,
        Released,
    }
}
