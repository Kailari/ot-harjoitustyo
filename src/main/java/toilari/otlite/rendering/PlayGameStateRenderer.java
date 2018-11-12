package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.io.dao.TextureDAO;

public class PlayGameStateRenderer implements IRenderer<PlayGameState> {
    private final TextureDAO textures = new TextureDAO("content/textures/");
    private LevelRenderer levelRenderer;

    @Override
    public boolean init(PlayGameState playGameState) {
        val tileset = this.textures.load("tileset.png");

        this.levelRenderer = new LevelRenderer(tileset, 8, 8);
        return true;
    }

    @Override
    public void draw(PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.draw(world.getCurrentLevel());
    }
}
