package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.world.entities.characters.AnimalCharacter;
import toilari.otlite.game.world.entities.characters.PlayerCharacter;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.renderer.IRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Piirtäjä pelitilan piirtämiseen. Vastaa maailman
 */
public class PlayGameStateRenderer implements ILWJGLRenderer<PlayGameState> {
    // TODO: mapping class for these to get rid of unchecked code
    @NonNull private final Map<Class, IRenderer> rendererMappings = new HashMap<>();
    private final TextureDAO textures;

    private LevelRenderer levelRenderer;
    private TextRenderer textRenderer;

    /**
     * Luo uuden pelitilapiirtäjän.
     */
    public PlayGameStateRenderer() {
        this.textures = new TextureDAO("content/textures/");
        this.levelRenderer = new LevelRenderer(this.textures, "tileset.png", 8, 8);
        this.rendererMappings.put(PlayerCharacter.class, new PlayerRenderer(this.textures));
        this.rendererMappings.put(AnimalCharacter.class, new CharacterRenderer(this.textures, "sheep.png", 1));
    }

    @Override
    public boolean init() {
        for (val renderer : this.rendererMappings.values()) {
            if (renderer.init()) {
                return true;
            }
        }

        this.textRenderer = new TextRenderer(this.textures, 1, 8);

        return this.levelRenderer.init();
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull PlayGameState playGameState) {
        val player = playGameState.getPlayer();
        val cameraX = player.getX() - camera.getViewportWidth() / 16;
        val cameraY = player.getY() - camera.getViewportHeight() / 16;
        camera.setPosition(cameraX, cameraY);

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
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val world = state.getWorld();
        this.levelRenderer.postDraw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.rendererMappings.get(object.getClass());
            if (renderer != null) {
                renderer.postDraw(camera, object);
            }
        }

        int x = Math.round(camera.getPosition().x);
        int y = Math.round(camera.getPosition().y);

        val str = state.getGame().getActiveProfile().getName()
            + "\nTurn: " + state.getPlayer().getController().getTurnsTaken();
        this.textRenderer.draw(camera, x + 2, y + 2, 0.25f, 0.65f, 0.25f, 4, str);

        String apStr = "Waiting...";
        if (world.getObjectManager().isCharactersTurn(state.getPlayer())) {
            val remaining = world.getObjectManager().getRemainingActionPoints();
            val total = state.getPlayer().getAttributes().getActionPoints();
            if (remaining == 0) {
                apStr = "Press <SPACE> to end turn";
            } else {
                apStr = "AP: " + String.valueOf(remaining) + "/" + String.valueOf(total);
            }
        }

        this.textRenderer.draw(camera, x + 2, y + 2 + 8, 0.65f, 0.25f, 0.25f, 2, apStr);
    }

    @Override
    public void destroy() {
        this.levelRenderer.destroy();
    }
}
