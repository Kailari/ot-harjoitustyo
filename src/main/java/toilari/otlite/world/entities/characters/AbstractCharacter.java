package toilari.otlite.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.var;
import toilari.otlite.world.Tile;
import toilari.otlite.world.entities.GameObject;
import toilari.otlite.world.entities.TurnObjectManager;
import toilari.otlite.world.entities.characters.controller.CharacterController;

/**
 * Hahmo pelimaailmassa.
 */
public abstract class AbstractCharacter extends GameObject {
    @Getter private CharacterController controller;

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

        val inputX = Math.max(-1, Math.min(1, this.controller.getMoveInputX()));
        var inputY = Math.max(-1, Math.min(1, this.controller.getMoveInputY()));
        if (inputX != 0) {
            inputY = 0;
        }

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
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = Math.max(0, Math.min(getX() + dx, (getWorld().getCurrentLevel().getWidth() - 1) * Tile.SIZE_IN_WORLD));
        int newY = Math.max(0, Math.min(getY() + dy, (getWorld().getCurrentLevel().getHeight() - 1) * Tile.SIZE_IN_WORLD));
        val tileAtTarget = getWorld().getCurrentLevel().getTileAt(newX / Tile.SIZE_IN_WORLD, newY / Tile.SIZE_IN_WORLD);
        val objectAtTarget = getWorld().getObjectAt(newX, newY);

        val tileIsWalkable = !tileAtTarget.isWall() && !tileAtTarget.getId().equals("hole");

        if (tileIsWalkable && objectAtTarget == null) {
            setX(newX);
            setY(newY);
            return true;
        }

        return false;
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