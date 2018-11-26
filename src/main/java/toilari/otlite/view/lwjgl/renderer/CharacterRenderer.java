package toilari.otlite.view.lwjgl.renderer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.TextRenderer;
import toilari.otlite.view.lwjgl.Texture;
import toilari.otlite.view.renderer.IRenderer;

/**
 * Piirtäjä pelihahmojen piirtämiseen.
 */
public class CharacterRenderer implements IRenderer<CharacterObject, LWJGLCamera> {
    private static final int DAMAGE_LABEL_DURATION = 1000;
    private static final int DAMAGE_LABEL_OFFSET = Tile.SIZE_IN_WORLD;

    @Getter(AccessLevel.PROTECTED) @NonNull private final TextureDAO textureDAO;

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
    public void draw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        this.sprite.draw(camera, character.getX(), character.getY(), getCurrentFrame(), 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void destroy() {
        this.texture.destroy();
        this.fontTexture.destroy();
    }
}
