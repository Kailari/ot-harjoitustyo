package toilari.otlite.game.world.entities.characters.controller;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.level.Tile;

public class PlayerController extends CharacterController {
    private final boolean autoEndTurn;

    private boolean isHolding = false;
    private int inputX;
    private int inputY;

    public PlayerController(boolean autoEndTurn) {
        this.autoEndTurn = autoEndTurn;
    }


    /**
     * Hakee raan liikesyötteen x-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa
     * onko kyseessä painallus vai jatkuva tila
     *
     * @return -1 vasemmalle, 1 oikealle, 0 paikallaan
     */
    public int getMoveInputXRaw() {
        val right = Input.getHandler().isKeyDown(Key.RIGHT) ? 1 : 0;
        val left = Input.getHandler().isKeyDown(Key.LEFT) ? -1 : 0;
        return right + left;
    }

    /**
     * Hakee raan liikesyötteen x-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa
     * onko kyseessä painallus vai jatkuva tila
     *
     * @return -1 ylös, 1 alas, 0 paikallaan
     */
    public int getMoveInputYRaw() {
        val down = Input.getHandler().isKeyDown(Key.DOWN) ? 1 : 0;
        val up = Input.getHandler().isKeyDown(Key.UP) ? -1 : 0;
        return down + up;
    }

    /**
     * Hakee raan vuoronlopetussyötteen.
     *
     * @return <code>true</code> jos näppäin on pohjassa, <code>false</code> jos ylhäällä
     */
    public boolean getEndTurnInputRaw() {
        return Input.getHandler().isKeyDown(Key.SPACE);
    }

    @Override
    public int getMoveInputX() {
        return this.inputX;
    }

    @Override
    public int getMoveInputY() {
        return this.inputY;
    }

    @Override
    public boolean wantsMove() {
        return !wantsAttack() && (getMoveInputX() != 0 || getMoveInputY() != 0);
    }

    @Override
    public boolean wantsAttack() {
        if (getMoveInputX() == 0 && getMoveInputY() == 0) {
            return false;
        }

        val targetX = getControlledCharacter().getX() / Tile.SIZE_IN_WORLD + getMoveInputX();
        val targetY = getControlledCharacter().getY() / Tile.SIZE_IN_WORLD + getMoveInputY();
        return getControlledCharacter().canAttack(targetX, targetY);
    }

    @Override
    public void update(@NonNull TurnObjectManager turnManager) {
        super.update(turnManager);

        int rawInputX = getMoveInputXRaw();
        int rawInputY = getMoveInputYRaw();
        boolean rawInputEndTurn = getEndTurnInputRaw();

        if (getControlledCharacter().isRemoved()) {
            turnManager.nextTurn();
            return;
        } else if (this.isHolding) {
            this.inputX = this.inputY = 0;
        } else {
            this.inputX = rawInputX;
            this.inputY = rawInputY;

            if (rawInputEndTurn || (turnManager.getRemainingActionPoints() == 0 && autoEndTurn)) {
                turnManager.nextTurn();
            }
        }

        this.isHolding = rawInputX != 0 || rawInputY != 0 || rawInputEndTurn;
    }
}
