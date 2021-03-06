package toilari.otlite.view.lwjgl.renderer;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.game.util.Color;
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
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

public class PlayerRenderer extends CharacterRenderer {
    private static final Color AREA_ABILITY_VISUALIZER_COLOR_NORMAL = new Color(0.85f, 0.15f, 0.85f);
    private static final Color AREA_ABILITY_VISUALIZER_COLOR_WITH_TARGET = new Color(0.85f, 0.15f, 0.15f);
    private static final Color TARGET_SELECTOR_COLOR = new Color(0.25f, 0.65f, 0.25f);
    private static final Color MOVE_COLOR_DANGEROUS = new Color(0.85f, 0.15f, 0.15f);
    private static final Color MOVE_COLOR_NORMAL = new Color(0.85f, 0.95f, 0.85f);
    private static final float SMALL_ICON_WIDTH = 4;
    private static final float SMALL_ICON_HEIGHT = 4;
    private static final float LARGE_ICON_WIDTH = 7;
    private static final float LARGE_ICON_HEIGHT = 7;

    private transient Texture icons;
    private transient AnimatedSprite smallIcons;
    private transient AnimatedSprite largeIcons;

    /**
     * Luo uuden piirtäjän pelaajan piirtämiseen.
     *
     * @param textureDAO DAO jolla tekstuuri saadaan ladattua
     * @param context    piirrettävän olion tiedot
     *
     * @throws NullPointerException jos dao on <code>null</code>
     */
    public PlayerRenderer(TextureDAO textureDAO, Context context) {
        super(textureDAO, context);
    }

    @Override
    public boolean init() {
        super.init();
        this.icons = getTextureDAO().get("icons.png");
        this.smallIcons = new AnimatedSprite(this.icons, 7);
        this.largeIcons = new AnimatedSprite(this.icons, 7);

        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        this.icons.destroy();
    }


    @Override
    public void draw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, @NonNull SpriteBatch batch) {
        drawAbilityAreaVisualizer(camera, character, batch);
        super.draw(camera, character, batch);
    }

    private void drawAbilityAreaVisualizer(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, SpriteBatch batch) {
        val component = character.getAbilities().getComponent(TargetSelectorAbility.class);
        if (component != null) {
            val active = component.getActive();
            if (active instanceof IAreaOfEffectAbility) {
                val range = ((IAreaOfEffectAbility) active).getAreaExtent();

                val x = character.getTileX();
                val y = character.getTileY();
                for (var dx = -range; dx <= range; dx++) {
                    for (var dy = -range; dy <= range; dy++) {
                        drawAreaAbilityVisualizerTile(camera, batch, character, (IAreaOfEffectAbility) active, x, y, dx, dy);
                    }
                }
            }
        }
    }

    private void drawAreaAbilityVisualizerTile(@NonNull LWJGLCamera camera, @NonNull SpriteBatch batch, @NonNull CharacterObject character, IAreaOfEffectAbility active, int x, int y, int dx, int dy) {
        val tileX = x + dx;
        val tileY = y + dy;
        var color = AREA_ABILITY_VISUALIZER_COLOR_NORMAL;
        val objectAtCoordinates = character.getWorld().getObjectAt(tileX, tileY);
        if (objectAtCoordinates != null && active.canAffect(objectAtCoordinates)) {
            color = AREA_ABILITY_VISUALIZER_COLOR_WITH_TARGET;
        }

        if (!character.getWorld().getTileAt(tileX, tileY).isWall()) {
            this.largeIcons.draw(camera, batch, tileX * Tile.SIZE_IN_WORLD + 0.5f, tileY * Tile.SIZE_IN_WORLD + 0.5f, LARGE_ICON_WIDTH, LARGE_ICON_HEIGHT, 6, color);
        }
    }

    @Override
    public void postDraw(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, @NonNull SpriteBatch batch) {
        if (character.getWorld().getObjectManager().isCharactersTurn(character)
            && character.getWorld().getObjectManager().getRemainingActionPoints() > 0) {
            drawActionVisualizers(camera, character, batch);
        }

        super.postDraw(camera, character, batch);
    }

    private void drawActionVisualizers(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, SpriteBatch batch) {
        for (val direction : Direction.asIterable()) {
            drawArrow(camera, character, direction, direction.ordinal(), batch);
        }

        val targetSelectorComponent = character.getAbilities().getComponent(TargetSelectorAbility.class);
        if (targetSelectorComponent != null) {
            val target = targetSelectorComponent.getTarget();
            drawTargetSelector(camera, target, batch);
        }
    }

    private void drawTargetSelector(LWJGLCamera camera, GameObject target, SpriteBatch batch) {
        if (target == null) {
            return;
        }

        val x = target.getX() + 2;
        val y = target.getY() - 6 - (1.5 * Math.sin(System.currentTimeMillis() / 75.0));
        this.smallIcons.draw(camera, batch, x, (float) y, SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, 2, TARGET_SELECTOR_COLOR);
    }

    private void drawArrow(@NonNull LWJGLCamera camera, @NonNull CharacterObject character, Direction direction, int frame, SpriteBatch batch) {
        val canMove = checkCanMove(character, direction);

        val targetX = character.getTileX() + direction.getDx();
        val targetY = character.getTileY() + direction.getDy();
        val canAttack = checkCanAttack(character, targetX, targetY, direction);
        if (canAttack) {
            frame = 5;
        } else if (!canMove) {
            frame = 4;
        }
        val isDangerous = (canMove && character.getWorld().getTileAt(targetX, targetY).isDangerous());

        val color = canAttack || isDangerous ? MOVE_COLOR_DANGEROUS : MOVE_COLOR_NORMAL;
        this.smallIcons.draw(camera, batch, targetX * Tile.SIZE_IN_WORLD + 2, targetY * Tile.SIZE_IN_WORLD + 2, SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, frame, color);
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
