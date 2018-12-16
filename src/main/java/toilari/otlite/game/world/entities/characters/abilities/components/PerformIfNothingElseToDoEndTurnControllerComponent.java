package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.val;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;

/**
 * Tekoälyn vuoronlopetuskyvyn ohjainkomponentti joka lopettaa vuoron välittömästi kun mitään kykyä ei voida enää
 * käyttää.
 */
public class PerformIfNothingElseToDoEndTurnControllerComponent extends EndTurnControllerComponent {
    private transient int updateTicksWaited;
    private transient int prevRemaining = -1;

    /**
     * Kopioi komponentin toisesta komponentista.
     *
     * @param template komponentti josta kopioidaan
     */
    public PerformIfNothingElseToDoEndTurnControllerComponent(EndTurnControllerComponent template) {
        super(template);
    }

    @Override
    public void setWantsToEndTurn(boolean wantsToEndTurn) {
        super.setWantsToEndTurn(wantsToEndTurn);
        this.updateTicksWaited = 0;
    }

    @Override
    public void updateInput(EndTurnAbility ability) {
        val remaining = getCharacter().getWorld().getObjectManager().getRemainingActionPoints();
        if (remaining == this.prevRemaining) {
            this.updateTicksWaited++;
        } else {
            this.updateTicksWaited = 0;
        }

        this.prevRemaining = remaining;

        if (this.updateTicksWaited > 1) {
            setWantsToEndTurn(true);
        }
    }
}
