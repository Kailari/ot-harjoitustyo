package toilari.otlite.view.lwjgl.renderer;

import lombok.*;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.Tile;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.renderer.IRenderer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Piirtäjä pelihahmojen piirtämiseen.
 */
public class CharacterRenderer implements IRenderer<AbstractCharacter, LWJGLCamera> {
    private static final int DAMAGE_LABEL_DURATION = 1000;
    private static final int DAMAGE_LABEL_OFFSET = Tile.SIZE_IN_WORLD;

    @NonNull private final TextureDAO textureDAO;

    @NonNull private final String filename;
    private final int frames;

    @Getter @Setter(AccessLevel.PROTECTED) private int currentFrame;

    @Getter(AccessLevel.PROTECTED) private Texture texture;
    @Getter(AccessLevel.PROTECTED) private Texture fontTexture;
    @Getter(AccessLevel.PROTECTED) private AnimatedSprite sprite;
    @Getter(AccessLevel.PROTECTED) private TextRenderer textRenderer;

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

        this.sprite = new AnimatedSprite(this.texture, this.frames, 8, 8);

        this.textRenderer = new TextRenderer(this.textureDAO, 1, 8);

        return false;
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.sprite.draw(camera, character.getX(), character.getY(), getCurrentFrame(), 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        if (character.getLastAttackTarget() != null && System.currentTimeMillis() < character.getLastAttackTime() + DAMAGE_LABEL_DURATION) {
            float dt = (System.currentTimeMillis() - character.getLastAttackTime()) / (float) DAMAGE_LABEL_DURATION;
            int offsetY = 2 - Math.round(dt * DAMAGE_LABEL_OFFSET);
            int offsetX = 2;

            String msg;
            val target = character.getLastAttackTarget();
            if (target.isDead()) {
                msg = "rekt";
                offsetX -= 6;
            } else {
                int damage = Math.round(character.getLastAttackAmount());
                msg = String.valueOf(damage);
            }

            this.textRenderer.draw(camera, target.getX() + offsetX, target.getY() + offsetY, 0.8f, 0.1f, 0.1f, 4, msg);
        }
    }

    @Override
    public void destroy(@NonNull AbstractCharacter character) {
        this.texture.destroy();
        this.fontTexture.destroy();
    }
}
