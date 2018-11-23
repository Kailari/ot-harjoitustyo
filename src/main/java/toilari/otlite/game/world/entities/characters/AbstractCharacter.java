package toilari.otlite.game.world.entities.characters;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.controller.CharacterController;
import toilari.otlite.game.world.level.Tile;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public abstract class AbstractCharacter extends GameObject {
    @Getter private CharacterController controller;
    @Getter @Setter private float health;
    @Getter @Setter private float attackDamage = 1.0f;

    @Getter private long lastAttackTime;
    @Getter private float lastAttackAmount;
    @Getter private AbstractCharacter lastAttackTarget;

    @Getter private final CharacterAttributes attributes;

    protected AbstractCharacter(@NonNull CharacterAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public void init() {
        super.init();
        this.health = getAttributes().getMaxHealth();
    }

    /**
     * Tarkistaa onvatko hahmon terveyspisteet nollassa.
     *
     * @return <code>true</code> jos hahmon terveyspisteet ovat likimain nolla
     */
    public boolean isDead() {
        return this.health < 0.000001f;
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
            if (attack(inputX, inputY)) {
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
            // No need to bound-check, it is already performed in canMoveTo()
            int newX = getX() / Tile.SIZE_IN_WORLD + dx;
            int newY = getY() / Tile.SIZE_IN_WORLD + dy;

            int oldX = getX() / Tile.SIZE_IN_WORLD;
            int oldY = getY() / Tile.SIZE_IN_WORLD;

            setPos(newX * Tile.SIZE_IN_WORLD, newY * Tile.SIZE_IN_WORLD);

            getWorld().getCurrentLevel().getTileAt(oldX, oldY).onCharacterExit(oldX, oldY, this);
            getWorld().getCurrentLevel().getTileAt(newX, newY).onCharacterEnter(newX, newY, this);

            return true;
        }

        return false;
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

        int newX = getX() / Tile.SIZE_IN_WORLD + dx;
        int newY = getY() / Tile.SIZE_IN_WORLD + dy;

        if (!getWorld().getCurrentLevel().isWithinBounds(newX, newY)) {
            return false;
        }

        val tileAtTarget = getWorld().getCurrentLevel().getTileAt(newX, newY);
        val objectAtTarget = getWorld().getObjectAt(newX, newY);

        val tileIsWalkable = !tileAtTarget.isWall();

        if (tileIsWalkable) {
            return !(objectAtTarget instanceof AbstractCharacter) || objectAtTarget.isRemoved();
        }

        return false;
    }

    /**
     * Yrittää hyökätä annettuun suuntaan.
     *
     * @param dx siirtymä x-akselilla
     * @param dy siirtymä y-akselilla
     * @return <code>true</code> jos hyökkäys tapahtui, muulloin <code>false</code>
     */
    protected boolean attack(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = getX() / Tile.SIZE_IN_WORLD + dx;
        int newY = getY() / Tile.SIZE_IN_WORLD + dy;

        if (!canAttack(newX, newY)) {
            return false;
        }

        attack((AbstractCharacter) getWorld().getObjectAt(newX, newY), this.getAttackDamage());
        return true;
    }

    /**
     * Testaa voiko hahmo hyökätä annettuun suuntaan. Hyökkääminen onnistuu jos koordinaateista löytyy toinen hahmo,
     * jota ei vielä ole poistettu.
     *
     * @param x tarkistettava x-koordinaatti
     * @param y tarkistettava y-koordinaatti
     * @return <code>true</code> jos voidaan hyökätä, muulloin <code>false</code>
     */
    public boolean canAttack(int x, int y) {
        val objectAtTarget = getWorld().getObjectAt(x, y);
        return objectAtTarget instanceof AbstractCharacter && !objectAtTarget.isRemoved();

    }

    /**
     * Tekee kohteeseen annetun määrän vahinkopisteitä.
     *
     * @param target kohde jota vahingoitetaan
     * @param amount vahingon määrä
     */
    protected void attack(@NonNull AbstractCharacter target, float amount) {
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
}