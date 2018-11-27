package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Texture;

public class PlayerRenderer extends CharacterRenderer {
    private transient Texture icons;
    private transient AnimatedSprite arrows;

    /**
     * Luo uuden piirtäjän pelaajan piirtämiseen.
     *
     * @param textureDAO DAO jolla tekstuuri saadaan ladattua
     * @param context    piirrettävän olion tiedot
     * @throws NullPointerException jos dao on <code>null</code>
     */
    public PlayerRenderer(TextureDAO textureDAO, Context context) {
        super(textureDAO, context);
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
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        if (character.getWorld().getObjectManager().isCharactersTurn(character)
            && character.getWorld().getObjectManager().getRemainingActionPoints() > 0) {
            drawActionVisualizers(camera, character);
        }

        super.postDraw(camera, character);
    }

    private void drawActionVisualizers(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        val x = character.getX() / Tile.SIZE_IN_WORLD;
        val y = character.getY() / Tile.SIZE_IN_WORLD;

        for (val direction : Direction.asIterable()) {
            drawArrow(camera, character, x, y, direction, direction.ordinal());
        }
    }

    private void drawArrow(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, int x, int y, Direction direction, int frame) {
        val canMove = character.getAbilities().getAbility(MoveAbility.class).canMoveTo(direction, 1);

        val dx = direction.getDx();
        val dy = direction.getDy();
        val canAttack = character.getAbilities().getAbility(AttackAbility.class).canAttack(x + dx, y + dy);
        if (canAttack) {
            frame = 5;
        } else if (!canMove) {
            frame = 4;
        }
        val isDangerous = (canMove && character.getWorld().getCurrentLevel().getTileAt(x + dx, y + dy).isDangerous());

        float r = canAttack || isDangerous ? 0.85f : 0.85f;
        float g = canAttack || isDangerous ? 0.2f : 0.95f;
        float b = canAttack || isDangerous ? 0.2f : 0.85f;
        this.arrows.draw(camera, (x + dx) * Tile.SIZE_IN_WORLD + 2, (y + dy) * Tile.SIZE_IN_WORLD + 2, frame, r, g, b);
    }
}
