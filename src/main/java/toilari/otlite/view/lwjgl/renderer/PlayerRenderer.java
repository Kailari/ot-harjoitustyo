package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.world.Tile;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;

public class PlayerRenderer extends CharacterRenderer {
    private AnimatedSprite arrows;

    /**
     * Luo uuden piirtäjän pelaajan piirtämiseen.
     *
     * @param textureDAO DAO jolla tekstuuri saadaan ladattua
     * @throws NullPointerException jos dao on <code>null</code>
     */
    public PlayerRenderer(@NonNull TextureDAO textureDAO) {
        super(textureDAO, "white_knight.png", 12);
    }

    @Override
    public boolean init() {
        super.init();
        this.arrows = new AnimatedSprite(getTexture(), 12, 4, 4);

        return false;
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
        val world = character.getWorld();

        drawArrow(camera, world, x, y, -1, 0, 7);
        drawArrow(camera, world, x, y, 1, 0, 6);
        drawArrow(camera, world, x, y, 0, -1, 8);
        drawArrow(camera, world, x, y, 0, 1, 9);
    }

    private void drawArrow(@NonNull LWJGLCamera camera, @NonNull World world, int x, int y, int dx, int dy, int frame) {
        val tile = world.getCurrentLevel().getTileAt(x + dx, y + dy);
        val canMove = !tile.isWall() && !tile.getId().equals("hole");

        val isEnemy = world.getObjectAt(x + dx, y + dy) instanceof AbstractCharacter;
        if (!canMove) {
            frame = 10;
        } else if (isEnemy) {
            frame = 11;
        }

        float r = isEnemy ? 0.85f : 0.85f;
        float g = isEnemy ? 0.2f : 0.95f;
        float b = isEnemy ? 0.2f : 0.85f;
        this.arrows.draw(camera, (x + dx) * Tile.SIZE_IN_WORLD + 2, (y + dy) * Tile.SIZE_IN_WORLD + 2, frame, r, g, b);
    }
}