package toilari.otlite.game.world.entities.characters;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
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

    @Getter private final CharacterAttributes attributes;

    protected AbstractCharacter(CharacterAttributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Tarkistaa onvatko hahmon terveyspisteet nollassa.
     *
     * @return <code>true</code> jos hahmon terveyspisteet ovat likimain nolla
     */
    public boolean isDead() {
        return this.health < 0.000001f;
    }

    /**
     * Tarkistaa voiko hahmo liikkua annettuun suuntaan.
     *
     * @param dx siirtymä x-akselilla
     * @param dy siirtymä y-akselilla
     * @return <code>true</code> jos liikkuminen on mahdollista
     */
    public boolean canMoveTo(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = Math.max(0, Math.min(getX() / Tile.SIZE_IN_WORLD + dx, getWorld().getCurrentLevel().getWidth() - 1));
        int newY = Math.max(0, Math.min(getY() / Tile.SIZE_IN_WORLD + dy, getWorld().getCurrentLevel().getHeight() - 1));

        val tileAtTarget = getWorld().getCurrentLevel().getTileAt(newX, newY);
        val objectAtTarget = getWorld().getObjectAt(newX, newY);

        val tileIsWalkable = !tileAtTarget.isWall() && !tileAtTarget.getId().equals("hole");

        if (tileIsWalkable) {
            return !(objectAtTarget instanceof AbstractCharacter) || objectAtTarget.isRemoved();
        }

        return false;
    }

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

        handleActions(turnManager);
    }

    protected void handleActions(@NonNull TurnObjectManager turnManager) {
        // Clamp input to range -1..1
        val inputX = Math.max(-1, Math.min(1, this.controller.getMoveInputX()));
        var inputY = Math.max(-1, Math.min(1, this.controller.getMoveInputY()));

        // Allow moving only on one axis per move
        if (inputX != 0) {
            inputY = 0;
        }

        if (this.controller.wantsMove() && turnManager.getRemainingActionPoints() >= getAttributes().getMoveCost()) {
            if (move(inputX, inputY)) {
                turnManager.spendActionPoints(getAttributes().getMoveCost());
            }
        } else if (this.controller.wantsAttack() && turnManager.getRemainingActionPoints() >= getAttributes().getAttackCost()) {
            val targetX = getX() / Tile.SIZE_IN_WORLD + inputX;
            val targetY = getY() / Tile.SIZE_IN_WORLD + inputY;
            if (attack(targetX, targetY)) {
                turnManager.spendActionPoints(getAttributes().getAttackCost());
            }
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
        if (canMoveTo(dx, dy)) {
            int newX = Math.max(0, Math.min(getX() / Tile.SIZE_IN_WORLD + dx, getWorld().getCurrentLevel().getWidth() - 1));
            int newY = Math.max(0, Math.min(getY() / Tile.SIZE_IN_WORLD + dy, getWorld().getCurrentLevel().getHeight() - 1));

            setPos(newX * Tile.SIZE_IN_WORLD, newY * Tile.SIZE_IN_WORLD);
            return true;
        }

        return false;
    }

    /**
     * Tekee kohteeseen annetun määrän vahinkopisteitä.
     *
     * @param target kohde jota vahingoitetaan
     * @param amount vahingon määrä
     */
    public void attack(@NonNull AbstractCharacter target, float amount) {
        if (target.isRemoved()) {
            return;
        }

        float current = target.getHealth();
        target.setHealth(Math.max(0, current - amount));

        this.lastAttackTime = System.currentTimeMillis();
        this.lastAttackAmount = current - target.getHealth();
        this.lastAttackTarget = target;

        if (target.isDead()) {
            target.setHealth(0.0f);
            target.remove();
        }
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

    private boolean attack(int targetX, int targetY) {
        int newX = Math.max(0, Math.min(targetX, getWorld().getCurrentLevel().getWidth() - 1));
        int newY = Math.max(0, Math.min(targetY, getWorld().getCurrentLevel().getHeight() - 1));
        if (newX == this.getX() && newY == this.getY()) {
            return false;
        }

        val objectAtTarget = getWorld().getObjectAt(newX, newY);
        if (!(objectAtTarget instanceof AbstractCharacter) || objectAtTarget.isRemoved()) {
            return true;
        }

        attack((AbstractCharacter) objectAtTarget, this.getAttackDamage());
        return true;
    }
}