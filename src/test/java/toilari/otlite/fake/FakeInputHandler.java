package toilari.otlite.fake;

import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.game.input.Key;

import java.util.Arrays;
import java.util.List;

public class FakeInputHandler implements IInputHandler {
    private List<Key> pressedKeys;
    private int updatesCalled;

    public FakeInputHandler(Key... pressedKeys) {
        this.pressedKeys = Arrays.asList(pressedKeys);
    }

    @Override
    public boolean isKeyDown(Key key) {
        return this.pressedKeys.contains(key);
    }

    @Override
    public boolean isKeyPressed(Key key) {
        return this.updatesCalled == 0 && this.pressedKeys.contains(key);
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
    public boolean isMousePressed(int button) {
        return false;
    }

    @Override
    public void update() {
        this.updatesCalled++;
    }

    public void setPressedKeys(Key... pressedKeys) {
        this.pressedKeys = Arrays.asList(pressedKeys);
        this.updatesCalled = 0;
    }
}
