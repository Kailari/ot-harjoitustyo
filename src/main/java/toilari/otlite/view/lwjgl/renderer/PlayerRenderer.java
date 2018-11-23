package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.Tile;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Texture;

public class PlayerRenderer extends CharacterRenderer {
    private Texture icons;
    private AnimatedSprite arrows;

    /**
     * Luo uuden piirtäjän pelaajan piirtämiseen.
     *
     * @param textureDAO DAO jolla tekstuuri saadaan ladattua
     * @throws NullPointerException jos dao on <code>null</code>
     */
    public PlayerRenderer(@NonNull TextureDAO textureDAO) {
        super(textureDAO, "white_knight.png", 6);
    }

    @Override
    public boolean init() {
        super.init();
        this.icons = getTextureDAO().load("icons.png");
        this.arrows = new AnimatedSprite(this.icons, 6, 4, 4);

        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        this.icons.destroy();
    }

    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        if (character.getWorld().getObjectManager().getRemainingActionPoints() > 0) {
            this.setCurrentFrame((int) (System.currentTimeMillis() % 1000 / 500));
        } else {
            this.setCurrentFrame(2 + (int) (System.currentTimeMillis() % (500 * 4) / 500));
        }
        super.draw(camera, character);
    }

    @Override
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        if (character.getWorld().getObjectManager().isCharactersTurn(character)
            && character.getWorld().getObjectManager().getRemainingActionPoints() > 0) {
            drawActionVisualizers(camera, character);
        }

        super.postDraw(camera, character);
    }

    private void drawActionVisualizers(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character) {
        val x = character.getX() / Tile.SIZE_IN_WORLD;
        val y = character.getY() / Tile.SIZE_IN_WORLD;

        drawArrow(camera, character, x, y, -1, 0, 1);
        drawArrow(camera, character, x, y, 1, 0, 0);
        drawArrow(camera, character, x, y, 0, -1, 2);
        drawArrow(camera, character, x, y, 0, 1, 3);
    }

    private void drawArrow(@NonNull LWJGLCamera camera, @NonNull AbstractCharacter character, int x, int y, int dx, int dy, int frame) {
        val canMove = character.canMoveTo(dx, dy);

        val isEnemy = character.getWorld().getObjectAt(x + dx, y + dy) instanceof AbstractCharacter;
        if (isEnemy) {
            frame = 5;
        } else if (!canMove) {
            frame = 4;
        }

        float r = isEnemy ? 0.85f : 0.85f;
        float g = isEnemy ? 0.2f : 0.95f;
        float b = isEnemy ? 0.2f : 0.85f;
        this.arrows.draw(camera, (x + dx) * Tile.SIZE_IN_WORLD + 2, (y + dy) * Tile.SIZE_IN_WORLD + 2, frame, r, g, b);
    }
}
