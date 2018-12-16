package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

/**
 * Pelaajan ohjainkomponentti liikkumiskyvylle.
 */
@NoArgsConstructor
public class PlayerMoveControllerComponent extends MoveControllerComponent {
    /**
     * Kopio komponentin toisesta kopmponentista.
     *
     * @param template komponentti josta kopioidaan
     */
    public PlayerMoveControllerComponent(MoveControllerComponent template) {
        super(template);
    }

    /**
     * Hakee raa'an liikesyötteen x-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa onko kyseessä
     * painallus vai jatkuva tila
     *
     * @return -1 vasemmalle, 1 oikealle, 0 paikallaan
     */
    public int getMoveInputX() {
        val right = Input.getHandler().isKeyPressed(Key.RIGHT) ? 1 : 0;
        val left = Input.getHandler().isKeyPressed(Key.LEFT) ? -1 : 0;
        return right + left;
    }

    /**
     * Hakee raa'an liikesyötteen y-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa onko kyseessä
     * painallus vai jatkuva tila
     *
     * @return -1 ylös, 1 alas, 0 paikallaan
     */
    public int getMoveInputY() {
        val down = Input.getHandler().isKeyPressed(Key.DOWN) ? 1 : 0;
        val up = Input.getHandler().isKeyPressed(Key.UP) ? -1 : 0;
        return down + up;
    }

    @Override
    public void doUpdateInput(MoveAbility ability) {
        setInputX(getMoveInputX());
        setInputY(getMoveInputY());
    }

    @Override
    public void abilityPerformed(MoveAbility ability) {
        super.abilityPerformed(ability);

        val state = getCharacter().getWorld().getObjectManager().getGameState();
        if (state != null) {
            state.getGame().getStatistics().increment(Statistics.TILES_MOVED, state.getGame().getActiveProfile().getId());
        }
    }
}
