package toilari.otlite.fake;

import lombok.val;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.level.Level;

public class FakeWorld extends World {
    private FakeWorld() {
        super(new TurnObjectManager(), null, null, null);
    }

    public static FakeWorld create() {
        val world = new FakeWorld();
        world.init();
        return world;
    }

    public static FakeWorld createWithLevel(Level level) {
        val world = create();
        world.changeLevel(level);
        return world;
    }
}
