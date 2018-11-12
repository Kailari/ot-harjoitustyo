package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.PlayGameState;

public class GameStateRenderer implements IRenderer<PlayGameState> {
    private final LevelRenderer levelRenderer = new LevelRenderer(tileset);

    @Override
    public void init(PlayGameState playGameState) {

    }

    @Override
    public void draw(PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.draw(world.getCurrentLevel());
    }
}
