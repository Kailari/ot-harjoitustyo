package toilari.otlite.fake;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.AbstractGameRunner;
import toilari.otlite.game.Game;
import toilari.otlite.game.input.IInputHandler;
import toilari.otlite.view.Camera;

public class FakeGameRunner extends AbstractGameRunner<Camera> {
    @Getter private FakeInputHandler inputHandler;

    private FakeGameRunner(@NonNull Game game, FakeInputHandler input) {
        super(game, FakeRendererMappings.create());
        this.inputHandler = input;
    }

    public static FakeGameRunner create(@NonNull Game game, FakeInputHandler input) {
        return new FakeGameRunner(game, input);
    }

    @Override
    public boolean init() {
        return super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected IInputHandler createInputHandler() {
        return this.inputHandler;
    }

    @Override
    protected Camera createCamera() {
        return new Camera();
    }
}
