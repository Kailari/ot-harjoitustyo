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
    @NonNull private final Map<Class, IRenderer> rendererMappings = new HashMap<>();
    private LevelRenderer levelRenderer;

    /**
     * Luo uuden pelitilapiirtäjän.
     */
    public PlayGameStateRenderer() {
        val textures = new TextureDAO("content/textures/");
        this.levelRenderer = new LevelRenderer(textures, "tileset.png", 8, 8);
        this.rendererMappings.put(PlayerCharacter.class, new CharacterRenderer(textures, "white_knight.png", 6));
        this.rendererMappings.put(AnimalCharacter.class, new CharacterRenderer(textures, "sheep.png", 1));
    }

    @Override
    public boolean init() {
        for (val renderer : this.rendererMappings.values()) {
            if (!renderer.init()) {
                return false;
            }
        }

        return this.levelRenderer.init();
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
    public void postDraw(@NonNull Camera camera, @NonNull PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.postDraw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.rendererMappings.get(object.getClass());
            if (renderer != null) {
                renderer.postDraw(camera, object);
            }
        }
    }

    @Override
    public void destroy(@NonNull PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.destroy(world.getCurrentLevel());
    }
}
