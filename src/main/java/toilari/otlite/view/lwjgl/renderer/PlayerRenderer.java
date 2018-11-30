package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;
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
        this.icons = getTextureDAO().get("icons.png");
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

        val kickComponent = character.getAbilities().getComponent(KickAbility.class);
        if (kickComponent != null) {
            drawKickVisualizer(camera, kickComponent);
        }

        val attackComponent = character.getAbilities().getComponent(AttackAbility.class);
        if (attackComponent != null) {
            drawKickVisualizer(camera, attackComponent);
        }
    }

    private void drawKickVisualizer(LWJGLCamera camera, AbstractAttackControllerComponent component) {
        val target = component.getTarget();
        if (target == null) {
            return;
        }

        val x = target.getX() + 2;
        val y = target.getY() - 9 - Math.round(3 * (float) Math.sin((int) System.currentTimeMillis() / 250.0f));
        this.arrows.draw(camera, x, y, 2, 0.25f, 0.65f, 0.25f);
    }

    private void drawArrow(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, int x, int y, Direction direction, int frame) {
        val canMove = checkCanMove(character, direction);

        val dx = direction.getDx();
        val dy = direction.getDy();
        val canAttack = checkCanAttack(character, x, y, dx, dy);
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

    private boolean checkCanMove(@NonNull CharacterObject character, Direction direction) {
        val ability = character.getAbilities().getAbility(MoveAbility.class);
        return ability != null && ability.canMoveTo(direction, 1);
    }

    private boolean checkCanAttack(@NonNull CharacterObject character, int x, int y, int dx, int dy) {
        val ability = character.getAbilities().getAbility(AttackAbility.class);
        return ability != null && ability.canAttack(x + dx, y + dy);
    }
}
