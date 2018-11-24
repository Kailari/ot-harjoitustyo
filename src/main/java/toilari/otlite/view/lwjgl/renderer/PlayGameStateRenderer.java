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
public class PlayGameStateRenderer implements ILWJGLGameStateRenderer<PlayGameState> {
    // TODO: mapping class for these to get rid of unchecked code
    @NonNull private final Map<Class, IRenderer> rendererMappings = new HashMap<>();
    private final TextureDAO textureDao;

    private LevelRenderer levelRenderer;
    private TextRenderer textRenderer;

    /**
     * Luo uuden pelitilapiirtäjän.
     *
     * @param textureDao dao jolla tekstuurit ladataan
     */
    public PlayGameStateRenderer(@NonNull TextureDAO textureDao) {
        this.textureDao = textureDao;
        this.levelRenderer = new LevelRenderer(this.textureDao, "tileset.png", 8, 8);
        this.rendererMappings.put(PlayerCharacter.class, new PlayerRenderer(this.textureDao));
        this.rendererMappings.put(AnimalCharacter.class, new CharacterRenderer(this.textureDao, "sheep.png", 1));
    }

    @Override
    public boolean init(@NonNull PlayGameState state) {
        for (val renderer : this.rendererMappings.values()) {
            if (renderer.init()) {
                return true;
            }
        }

        this.textRenderer = new TextRenderer(this.textureDao, 1, 8);

        return this.levelRenderer.init();
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull PlayGameState state) {
        val player = state.getPlayer();
        val cameraX = player.getX() - camera.getViewportWidth() / 16;
        val cameraY = player.getY() - camera.getViewportHeight() / 16;
        camera.setPosition(cameraX, cameraY);

        val world = state.getWorld();
        this.levelRenderer.draw(camera, world.getCurrentLevel());

        for (val object : world.getObjectManager().getObjects()) {
            val renderer = this.rendererMappings.get(object.getClass());
            if (renderer != null) {
                renderer.draw(camera, object);
            }
        }

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
            val total = state.getPlayer().getAttributes().getActionPoints(state.getPlayer().getLevels());
            if (remaining == 0) {
                apStr = "Press <SPACE> to end turn";
            } else {
                apStr = "AP: " + String.valueOf(remaining) + "/" + String.valueOf(total);
            }
        }

        this.textRenderer.draw(camera, x + 2, y + 2 + 8, 0.65f, 0.25f, 0.25f, 2, apStr);

        if (state.getPlayer().isDead()) {
            val ded = "You are dead";
            val len = ded.length();
            val size = 8;

            val w = (int) Math.ceil(camera.getViewportWidth() / camera.getPixelsPerUnit());
            val h = (int) Math.ceil(camera.getViewportHeight() / camera.getPixelsPerUnit());

            val dt = Math.min(1, (System.currentTimeMillis() - state.getPlayer().getDeathTime()) / 5000.0f);
            this.textRenderer.draw(
                camera,
                x + (w / 2) - (len * size) / 2,
                y + (h / 2) - (size / 2) + (int) Math.floor((h / 2f + size) - dt * (h / 2f + size)),
                0.65f, 0.25f, 0.25f,
                size,
                ded);
        }
    }

    @Override
    public void destroy(@NonNull PlayGameState state) {
        this.levelRenderer.destroy();
    }
}
