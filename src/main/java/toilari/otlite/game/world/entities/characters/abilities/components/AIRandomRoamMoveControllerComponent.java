package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

/**
 * Tekoälyn ohjainkomponentti joka siirtää hahmoa satunnaiseen suuntaan joka vuorolla.
 */
@NoArgsConstructor
public class AIRandomRoamMoveControllerComponent extends MoveControllerComponent {
    /**
     * Kopioi komponentin toisesta komponentista.
     *
     * @param template komponentti josta kopioidaan
     */
    public AIRandomRoamMoveControllerComponent(MoveControllerComponent template) {
        super(template);
    }

    /**
     * Luo uuden ohjainkomponentin ja asettaa satunnaislukugeneraattorin siemenluvun.
     *
     * @param seed siemenluku
     */
    public AIRandomRoamMoveControllerComponent(long seed) {
        super(seed);
    }

    @Override
    public void doUpdateInput(MoveAbility ability) {
        val direction = getAvailableDirections().get(getRandom().nextInt(getAvailableDirections().size()));
        moveToDirection(direction);
    }

    protected void moveToDirection(Direction direction) {
        setInputX(direction.getDx());
        setInputY(direction.getDy());
    }
}
