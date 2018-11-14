package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.io.dao.TextureDAO;
import toilari.otlite.world.entities.characters.PlayerCharacter;

public class PlayGameStateRenderer implements IRenderer<PlayGameState> {
    private final TextureDAO textures = new TextureDAO("content/textures/");
    private LevelRenderer levelRenderer;

    @Override
    public boolean init(PlayGameState playGameState) {
        val tileset = this.textures.load("tileset.png");
        this.levelRenderer = new LevelRenderer(tileset, 8, 8);

        val textureDao = new TextureDAO("content/textures/");

        val om = playGameState.getWorld().getObjectManager();
        om.assignRenderer(PlayerCharacter.class, new PlayerRenderer(textureDao.load("white_knight.png")));

        return true;
    }

    @Override
    public void draw(Camera camera, PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.draw(camera, world.getCurrentLevel());
        world.getObjectManager().draw(camera);
    }

    @Override
    public void destroy(PlayGameState playGameState) {

    }
}
