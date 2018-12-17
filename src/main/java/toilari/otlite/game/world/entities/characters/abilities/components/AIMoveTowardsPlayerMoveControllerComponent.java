package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.RequiredArgsConstructor;
import lombok.val;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

import java.util.Comparator;

/**
 * Tekoälyn ohjainkomponentti, joka pyrkii liikkumaan kohti pelaajaa pelaajan ollessa tarpeeksi lähellä.
 */
@RequiredArgsConstructor
public class AIMoveTowardsPlayerMoveControllerComponent extends AIRandomRoamMoveControllerComponent {
    private final int searchRange;

    /**
     * Kopio komponentin tiedot toisesta komponentista.
     *
     * @param template komponentti josta kopioidaan
     */
    public AIMoveTowardsPlayerMoveControllerComponent(MoveControllerComponent template) {
        this.searchRange = ((AIMoveTowardsPlayerMoveControllerComponent) template).searchRange;
    }

    /**
     * Luo uuden komponentin ja asettaa satunnaislukugeneraattorin siemenluvun.
     *
     * @param searchRange matka jolta pelaajaa etsitään
     * @param seed        siemenluku
     */
    public AIMoveTowardsPlayerMoveControllerComponent(int searchRange, long seed) {
        super(seed);
        this.searchRange = searchRange;
    }

    @Override
    public void doUpdateInput(MoveAbility ability) {
        if (targetIsNear()) {
            moveTowardsTarget();
        } else {
            super.doUpdateInput(ability);
        }
    }

    private void moveTowardsTarget() {
        getAvailableDirections().stream()
            .min(Comparator.comparingInt((d) -> {
                val x = getCharacter().getTileX();
                val y = getCharacter().getTileY();
                return squaredDistanceToPlayerFrom(x + d.getDx(), y + d.getDy());
            })).ifPresent(this::moveToDirection);
    }

    private int squaredDistanceToPlayerFrom(int x, int y) {
        val player = getCharacter().getWorld().getObjectManager().getPlayer();
        int dx = x - player.getTileX();
        int dy = y - player.getTileY();
        return dx * dx + dy * dy;
    }

    private boolean targetIsNear() {
        val player = getCharacter().getWorld().getObjectManager().getPlayer();
        val dx = Math.abs(player.getTileX() - getCharacter().getTileX());
        val dy = Math.abs(player.getTileY() - getCharacter().getTileY());
        return dx <= this.searchRange && dy <= this.searchRange;
    }
}
