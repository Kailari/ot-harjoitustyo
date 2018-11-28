package toilari.otlite.view.lwjgl.renderer;

import lombok.*;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.renderer.IRenderer;

/**
 * Piirtäjä pelihahmojen piirtämiseen.
 */
public class CharacterRenderer implements IRenderer<CharacterObject, LWJGLCamera> {
    @Getter(AccessLevel.PROTECTED) @NonNull private final TextureDAO textureDAO;

    private final Context context;

    @Getter @Setter(AccessLevel.PROTECTED) private int currentFrameTime = 0;
    @Getter(AccessLevel.PROTECTED) private Texture texture;
    @Getter(AccessLevel.PROTECTED) private Texture fontTexture;
    @Getter(AccessLevel.PROTECTED) private TextRenderer textRenderer;
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
        this.texture = this.textureDAO.get(this.context.texture);
        this.fontTexture = this.textureDAO.get("font.png");
        this.textRenderer = new TextRenderer(this.textureDAO, 1, 16);

        this.sprite = new AnimatedSprite(this.texture, this.context.nFrames, this.context.width, this.context.height);
        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        val isOwnTurn = character.getWorld().getObjectManager().isCharactersTurn(character);
        val hasActionPoints = character.getWorld().getObjectManager().getRemainingActionPoints() > 0;

        int frame = getFrame(isOwnTurn, hasActionPoints);

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

    @Override
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        val mx = Input.getHandler().mouseX() / camera.getPixelsPerUnit();
        val my = Input.getHandler().mouseY() / camera.getPixelsPerUnit();

        val sx = character.getX() - camera.getPosition().x;
        val sy = character.getY() - camera.getPosition().y;
        val isHovering = mx >= sx && mx <= sx + this.context.width
            && my >= sy && my <= sy + this.context.height;

        if (isHovering) {
            val size = 2;
            val current = character.getHealth();
            val max = character.getAttributes().getMaxHealth(character.getLevels());
            val str = current + "/" + max;

            val x = character.getX() + this.context.width / 2 - (size * str.length()) / 2;
            val y = character.getY() + this.context.height + 1;
            this.textRenderer.draw(camera, x, y, 0.85f, 0.25f, 0.25f, size, str);
        }
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
