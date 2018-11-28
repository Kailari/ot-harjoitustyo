package toilari.otlite.fake;

import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Key;

import java.util.Arrays;
import java.util.List;

public class FakeInputHandler implements IInputHandler {
    private final List<Key> pressedKeys;

    public FakeInputHandler(Key... pressedKeys) {
        this.pressedKeys = Arrays.asList(pressedKeys);
    }

    @Override
    public boolean isKeyDown(Key key) {
        return this.pressedKeys.contains(key);
    }

    @Override
    public boolean isKeyPressed(Key key) {
        return false;
    }

    @Override
    public int mouseX() {
        return 0;
    }

    @Override
    public int mouseY() {
        return 0;
    }

    @Override
    public boolean isMouseDown(int button) {
        return false;
    }

    @Override
    public void update() {
    }
}
