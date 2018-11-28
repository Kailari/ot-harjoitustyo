package toilari.otlite.view.lwjgl.renderer;

import lombok.*;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.renderer.IRenderer;

/**
 * Piirtäjä pelihahmojen piirtämiseen.
 */
public class CharacterRenderer implements IRenderer<CharacterObject, LWJGLCamera> {
    @Getter(AccessLevel.PROTECTED) @NonNull private final TextureDAO textureDAO;

    private final Context context;

    @Getter @Setter(AccessLevel.PROTECTED) private int currentFrameTime = 0;
    @Getter @Setter(AccessLevel.PROTECTED) private long lastFrameTime;
    @Getter(AccessLevel.PROTECTED) private Texture texture;
    @Getter(AccessLevel.PROTECTED) private Texture fontTexture;
    @Getter(AccessLevel.PROTECTED) private AnimatedSprite sprite;

    /**
     * Luo uuden hahmopiirtäjän.
     *
     * @param textureDAO tekstuuridao jolla piirtäjä luodaan.
     * @param context    piirtokonteksti
     */
    public CharacterRenderer(TextureDAO textureDAO, Context context) {
        this.textureDAO = textureDAO;
        this.context = context;
    }

    @Override
    public boolean init() {
        this.texture = this.textureDAO.load(this.context.texture);
        this.fontTexture = this.textureDAO.load("font.png");

        this.sprite = new AnimatedSprite(this.texture, this.context.nFrames, this.context.width, this.context.height);
        this.lastFrameTime = System.currentTimeMillis();
        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        val isOwnTurn = character.getWorld().getObjectManager().isCharactersTurn(character);
        val hasActionPoints = character.getWorld().getObjectManager().getRemainingActionPoints() > 0;

        int frame;
        frame = getFrame(isOwnTurn, hasActionPoints);

        float r, g, b;
        if (this.context.color != null && this.context.color.length == 3) {
            r = this.context.color[0];
            g = this.context.color[1];
            b = this.context.color[2];
        } else {
            r = g = b = 1.0f;
        }
        this.sprite.draw(camera, character.getX(), character.getY(), frame, r, g, b);
    }

    private int getFrame(boolean isOwnTurn, boolean hasActionPoints) {
        int frame;
        int time = (int) System.currentTimeMillis();
        float frameDuration = this.context.framesPerSecond == 0 ? 0 : 1000f / this.context.framesPerSecond;
        if (isOwnTurn && hasActionPoints) {
            float totalDuration = frameDuration * this.context.walkFrames.length;
            int subFrame = (int) ((time % totalDuration) / frameDuration);

            frame = this.context.walkFrames[subFrame];
        } else {
            float totalDuration = frameDuration * this.context.idleFrames.length;
            int subFrame = (int) ((time % totalDuration) / frameDuration);

            frame = this.context.idleFrames[subFrame];
        }
        return frame;
    }

    @Override
    public void destroy() {
        this.texture.destroy();
        this.fontTexture.destroy();
    }
}
