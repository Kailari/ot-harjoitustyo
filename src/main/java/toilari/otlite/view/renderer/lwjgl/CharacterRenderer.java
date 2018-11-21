package toilari.otlite.view.renderer.lwjgl;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Sprite;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.renderer.IRenderer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Piirtäjä pelihahmojen piirtämiseen.
 */
public class CharacterRenderer implements IRenderer<AbstractCharacter, LWJGLCamera> {
    private static final int DAMAGE_LABEL_DURATION = 1000;

    @NonNull private final TextureDAO textureDAO;

    @NonNull private final String filename;
    private final int frames;

    private Texture texture;
    private Texture fontTexture;
    private Sprite sprite;
    private AnimatedSprite font;

    /**
     * Luo uuden piirtäjän. Olettaa että annetussa tekstuurissa on kaikki framet ladottuna vaakasuunnassa vierekkäin.
     *
     * @param textureDAO      DAO jolla tekstuuri saadaan ladattua
     * @param textureFilename polku tekstuuriin jolla hahmo piirretään
     * @param frames          montako framea tekstuurissa on
     * @throws NullPointerException jos dao tai tiedostopolku on <code>null</code>
     */
    public CharacterRenderer(@NonNull TextureDAO textureDAO,
                             @NonNull String textureFilename,
                             int frames) {
        this.textureDAO = textureDAO;
        this.filename = textureFilename;
        this.frames = frames;
    }

    @Override
    public boolean init() {
        this.texture = this.textureDAO.load(this.filename);
        this.fontTexture = this.textureDAO.load("font.png");

        this.sprite = new Sprite(
            this.texture,
            0,
            0,
            this.texture.getWidth() / this.frames,
            this.texture.getHeight(),
            8,
            8
        );

        this.font = new AnimatedSprite(this.fontTexture, 10, 4, 4);

        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.sprite.draw(camera, character.getX(), character.getY(), 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        if (character.getLastAttackTarget() != null && System.currentTimeMillis() < character.getLastAttackTime() + DAMAGE_LABEL_DURATION) {
            int frame = Math.round(character.getLastAttackAmount() % 10);
            val target = character.getLastAttackTarget();
            this.font.draw(camera, target.getX() + 2, target.getY() + 2, frame, 0.8f, 0.1f, 0.1f);
        }
    }

    @Override
    public void destroy(@NonNull AbstractCharacter character) {
        this.texture.destroy();
        this.fontTexture.destroy();
    }
}
