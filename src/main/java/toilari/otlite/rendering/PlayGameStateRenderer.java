package toilari.otlite.rendering;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.io.dao.TextureDAO;
import toilari.otlite.world.entities.characters.AnimalCharacter;
import toilari.otlite.world.entities.characters.PlayerCharacter;

import java.util.HashMap;
import java.util.Map;

/**
 * Piirtäjä pelitilan piirtämiseen. Vastaa maailman
 */
public class PlayGameStateRenderer implements IRenderer<PlayGameState> {
    @NonNull private final TextureDAO textures = new TextureDAO("content/textures/");
    @NonNull private final Map<Class, IRenderer> rendererMappings = new HashMap<>();
    private LevelRenderer levelRenderer;

    @Override
    public boolean init(@NonNull PlayGameState playGameState) {
        val tileset = this.textures.load("tileset.png");
        this.levelRenderer = new LevelRenderer(tileset, 8, 8);

        val textureDao = new TextureDAO("content/textures/");
        this.rendererMappings.put(PlayerCharacter.class, new CharacterRenderer(textureDao.load("white_knight.png"), 6));
        this.rendererMappings.put(AnimalCharacter.class, new CharacterRenderer(textureDao.load("sheep.png"), 1));


        return true;
    }

    @Override
    public void draw(@NonNull Camera camera, @NonNull PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.draw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.rendererMappings.get(object.getClass());
            if (renderer != null) {
                renderer.draw(camera, object);
            }
        }
    }

    @Override
    public void destroy(@NonNull PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.destroy(world.getCurrentLevel());
    }
}
