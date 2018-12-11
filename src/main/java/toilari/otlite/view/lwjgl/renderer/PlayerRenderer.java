package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.IAreaOfEffectAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.view.lwjgl.AnimatedSprite;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.Texture;

public class PlayerRenderer extends CharacterRenderer {
    private transient Texture icons;
    private transient AnimatedSprite smallIcons;
    private transient AnimatedSprite largeIcons;

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
        this.smallIcons = new AnimatedSprite(this.icons, 7, 4, 4);
        this.largeIcons = new AnimatedSprite(this.icons, 7, 7, 7);

        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        this.smallIcons.destroy();
        this.largeIcons.destroy();
        this.icons.destroy();
    }


    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        drawAbilityAreaVisualizer(camera, character);
        super.draw(camera, character);
    }

    private void drawAbilityAreaVisualizer(@NonNull LWJGLCamera camera, @NonNull CharacterObject character) {
        val component = character.getAbilities().getComponent(TargetSelectorAbility.class);
        if (component != null) {
            val active = component.getActive();
            if (active instanceof IAreaOfEffectAbility) {
                val range = ((IAreaOfEffectAbility) active).getAreaExtent();

                val x = character.getTileX();
                val y = character.getTileY();
                for (var dx = -range; dx <= range; dx++) {
                    for (var dy = -range; dy <= range; dy++) {
                        drawAreaAbilityVisualizerTile(camera, character, (IAreaOfEffectAbility) active, x, y, dx, dy);
                    }
                }
            }
        }
    }

    private void drawAreaAbilityVisualizerTile(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, IAreaOfEffectAbility active, int x, int y, int dx, int dy) {
        val tileX = x + dx;
        val tileY = y + dy;
        val r = 0.85f;
        val g = 0.15f;
        var b = 0.85f;
        val objectAtCoordinates = character.getWorld().getObjectAt(tileX, tileY);
        if (objectAtCoordinates != null && active.canAffect(objectAtCoordinates)) {
            b = 0.15f;
        }

        if (!character.getWorld().getTileAt(tileX, tileY).isWall()) {
            this.largeIcons.draw(camera, tileX * Tile.SIZE_IN_WORLD + 0.5f, tileY * Tile.SIZE_IN_WORLD + 0.5f, 6, r, g, b);
        }
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
        for (val direction : Direction.asIterable()) {
            drawArrow(camera, character, direction, direction.ordinal());
        }

        val targetSelectorComponent = character.getAbilities().getComponent(TargetSelectorAbility.class);
        if (targetSelectorComponent != null) {
            val target = targetSelectorComponent.getTarget();
            drawTargetSelector(camera, target);
        }
    }

    private void drawTargetSelector(LWJGLCamera camera, GameObject target) {
        if (target == null) {
            return;
        }

        val x = target.getX() + 2;
        val y = target.getY() - 6 - (1.5 * Math.sin(System.currentTimeMillis() / 75.0));
        this.smallIcons.draw(camera, x, (float) y, 2, 0.25f, 0.65f, 0.25f);
    }

    private void drawArrow(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, Direction direction, int frame) {
        val canMove = checkCanMove(character, direction);

        val targetX = character.getTileX() + direction.getDx();
        val targetY = character.getTileY() + direction.getDy();
        val canAttack = checkCanAttack(character, targetX, targetY, direction);
        if (canAttack) {
            frame = 5;
        } else if (!canMove) {
            frame = 4;
        }
        val isDangerous = (canMove && character.getWorld().getCurrentLevel().getTileAt(targetX, targetY).isDangerous());

        float r = canAttack || isDangerous ? 0.85f : 0.85f;
        float g = canAttack || isDangerous ? 0.2f : 0.95f;
        float b = canAttack || isDangerous ? 0.2f : 0.85f;
        this.smallIcons.draw(camera, targetX * Tile.SIZE_IN_WORLD + 2, targetY * Tile.SIZE_IN_WORLD + 2, frame, r, g, b);
    }

    private boolean checkCanMove(@NonNull CharacterObject character, Direction direction) {
        val ability = character.getAbilities().getAbility(MoveAbility.class);
        return ability != null && ability.canMoveTo(direction, 1);
    }

    private boolean checkCanAttack(@NonNull CharacterObject character, int x, int y, Direction direction) {
        val ability = character.getAbilities().getAbility(AttackAbility.class);
        return ability != null && ability.canPerformOn(character.getWorld().getObjectAt(x, y), direction);
    }
}
