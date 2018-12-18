package toilari.otlite.view.lwjgl.renderer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetDAO;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.util.Color;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

/**
 * Piirtäjä pelihahmojen piirtämiseen.
 */
public class CharacterRenderer implements ILWJGLRenderer<CharacterObject> {
    private static final Color HEALTH_COLOR = new Color(0.85f, 0.15f, 0.15f);
    @Getter(AccessLevel.PROTECTED) @NonNull private final IGetDAO<Texture, String> textureDAO;
    @Getter(AccessLevel.PROTECTED) @NonNull private final TextRenderer textRenderer;

    @Getter private final Context context;

    @Getter(AccessLevel.PROTECTED) private Texture texture;
    @Getter(AccessLevel.PROTECTED) private AnimatedSprite sprite;

    /**
     * Luo uuden hahmopiirtäjän.
     *
     * @param textureDAO tekstuuridao jolla piirtäjä luodaan.
     * @param context    piirtokonteksti
     */
    public CharacterRenderer(IGetDAO<Texture, String> textureDAO, Context context) {
        this.textureDAO = textureDAO;
        this.textRenderer = new TextRenderer(this.textureDAO);
        this.context = context;
    }

    @Override
    public boolean init() {
        this.textRenderer.init();
        this.texture = this.textureDAO.get(this.context.texture);

        this.sprite = new AnimatedSprite(this.texture, this.context.nFrames);
        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, @NonNull SpriteBatch batch) {
        int frame = getFrame(character);

        this.sprite.draw(camera, batch, character.getX(), character.getY(), this.context.width, this.context.height, frame, this.context.color);
    }

    @Override
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, @NonNull SpriteBatch batch) {
        val mx = Input.getHandler().mouseX() / camera.getPixelsPerUnit();
        val my = Input.getHandler().mouseY() / camera.getPixelsPerUnit();

        val sx = character.getX() - camera.getX();
        val sy = character.getY() - camera.getY();
        val isHovering = mx >= sx && mx <= sx + this.context.width
            && my >= sy && my <= sy + this.context.height;

        if (isHovering) {
            val size = 3;
            val current = character.getHealth();
            val max = character.getAttributes().getMaxHealth();
            val str = String.format("%.1f/%.1f", current, max);

            val x = character.getX() + this.context.width / 2 - (size * str.length()) / 2;
            val y = character.getY() + this.context.height + 1;
            this.textRenderer.draw(camera, batch, x, y, HEALTH_COLOR, size, str);
        }
    }

    private int getFrame(CharacterObject character) {
        int frame;
        float time = character.getTimeAlive();
        float frameDuration = this.context.framesPerSecond == 0 ? 0 : 1.0f / this.context.framesPerSecond;

        val frames = this.context.states.get(character.getState());
        if (frames == null) {
            return 0;
        }

        float totalDuration = frameDuration * frames.length;
        int subFrame = (int) ((time % totalDuration) / frameDuration);

        frame = frames[Math.max(0, Math.min(frames.length - 1, subFrame))];

        return frame;
    }

    @Override
    public void destroy() {
        this.texture.destroy();
        this.textRenderer.destroy();
    }
}
