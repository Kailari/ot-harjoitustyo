package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.PlayGameState;

public class GameStateRenderer implements IRenderer<PlayGameState> {
    private final LevelRenderer levelRenderer = new LevelRenderer();

    @Override
    public void draw(PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.draw(world.getCurrentLevel());
    }
}
