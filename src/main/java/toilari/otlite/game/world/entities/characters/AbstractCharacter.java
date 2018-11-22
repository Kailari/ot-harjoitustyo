package toilari.otlite.game.world.entities.characters;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.game.profile.tracking.Statistics;
import toilari.otlite.game.world.Tile;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.controller.CharacterController;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public abstract class AbstractCharacter extends GameObject {
    @Getter private CharacterController controller;
    @Getter @Setter private float health = 10.0f;
    @Getter @Setter private float attackDamage = 1.0f;

    @Getter private long lastAttackTime;
    @Getter private float lastAttackAmount;
    @Getter private AbstractCharacter lastAttackTarget;

    @Override
    public void update() {
        super.update();
    }

    /**
     * Päivitysrutiini jota kutsutaan vain hahmon omalla vuorolla.
     *
     * @param turnManager objekti-/vuoromanageri jolla hahmojen vuoroja hallinnoidaan
     * @throws NullPointerException jos vuoromanageri on <code>null</code>
     */
    public void updateOnOwnTurn(@NonNull TurnObjectManager turnManager) {
        if (this.controller == null) {
            return;
        }

        this.controller.update(turnManager);

        // If controller ended turn, do not continue
        if (!turnManager.isCharactersTurn(this.controller.getControlledCharacter())) {
            return;
        }

        // Clamp input to range -1..1
        val inputX = Math.max(-1, Math.min(1, this.controller.getMoveInputX()));
        var inputY = Math.max(-1, Math.min(1, this.controller.getMoveInputY()));

        // Allow moving only on one axis per move
        if (inputX != 0) {
            inputY = 0;
        }

        // If character moved succesfully, end turn
        if (move(inputX * Tile.SIZE_IN_WORLD, inputY * Tile.SIZE_IN_WORLD)) {
            turnManager.endTurn();
        }
    }

    /**
     * Siirtää hahmoa. Ottaa törmäykset huomioon.
     *
     * @param dx siirtymä x-akselilla
     * @param dy siirtymä y-akselilla
     * @return <code>true</code> jos hahmo liikkui, muutoin <code>false</code>
     */
    protected boolean move(int dx, int dy) {
        int newX = Math.max(0, Math.min(getX() + dx, (getWorld().getCurrentLevel().getWidth() - 1) * Tile.SIZE_IN_WORLD));
        int newY = Math.max(0, Math.min(getY() + dy, (getWorld().getCurrentLevel().getHeight() - 1) * Tile.SIZE_IN_WORLD));
        val tileAtTarget = getWorld().getCurrentLevel().getTileAt(newX / Tile.SIZE_IN_WORLD, newY / Tile.SIZE_IN_WORLD);
        val objectAtTarget = getWorld().getObjectAt(newX, newY);

        val tileIsWalkable = !tileAtTarget.isWall() && !tileAtTarget.getId().equals("hole");

        if (tileIsWalkable) {
            if (!this.equals(objectAtTarget) && objectAtTarget instanceof AbstractCharacter) {
                val character = (AbstractCharacter) objectAtTarget;
                if (attack(character, this.attackDamage)) {
                    return true;
                }
            }

            // Target object will be flagged as removed if we happened to kill it during this turn
            if (objectAtTarget == null || objectAtTarget.isRemoved()) {
                setPos(newX, newY);

                val game = getWorld().getObjectManager().getGameState().getGame();
                game.getStatistics().increment(Statistics.TILES_MOVED, game.getActiveProfile().getId());
                return true;
            }
        }

        return false;
    }

    /**
     * Tekee kohteeseen annetun määrän vahinkopisteitä.
     *
     * @param target kohde jota vahingoitetaan
     * @param amount vahingon määrä
     * @return <code>true</code> jos hyökkäys päätti hahmon vuoron, <code>false</code> muutoin
     */
    public boolean attack(@NonNull AbstractCharacter target, float amount) {
        if (target.isRemoved()) {
            return false;
        }

        LOG.info("Attacking! ({}->{})", getClass().getSimpleName(), target.getClass().getSimpleName());

        float current = target.getHealth();
        target.setHealth(Math.max(0, current - amount));

        this.lastAttackTime = System.currentTimeMillis();
        this.lastAttackAmount = current - target.getHealth();
        this.lastAttackTarget = target;

        if (target.getHealth() < 0.0001f) {
            target.setHealth(0.0f);
            target.remove();

            val game = getWorld().getObjectManager().getGameState().getGame();
            game.getStatistics().increment(Statistics.KILLS, game.getActiveProfile().getId());
            return false;
        }

        return true;
    }

    /**
     * Luovuttaa hallinnan parametrina annetulle ohjaimelle.
     *
     * @param controller ohjain jolle hallinta luovutetaan. <code>null</code> jos ohjain halutaan poistaa
     */
    public void giveControlTo(CharacterController controller) {
        if (this.controller != null) {
            this.controller.takeControl(null);
        }

        this.controller = controller;
        if (controller != null && controller.getControlledCharacter() != this) {
            controller.takeControl(this);
        }
    }
}