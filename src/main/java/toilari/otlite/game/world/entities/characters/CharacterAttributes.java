package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.Setter;

public class CharacterAttributes {
    @Getter @Setter private int moveCost;
    @Getter @Setter private int attackCost;
    @Getter @Setter private int actionPoints;

    /**
     * Luo uudet hahmon attribuutit.
     *
     * @param moveCost     paljonko hahmon liikkuminen kuluttaa toimintopisteitä
     * @param attackCost   paljonko hahmon hyökkääminen kuluttaa toimintopisteitä
     * @param actionPoints paljonko hahmo saa toimintopisteitä per vuoro
     */
    public CharacterAttributes(int moveCost, int attackCost, int actionPoints) {
        this.moveCost = moveCost;
        this.attackCost = attackCost;
        this.actionPoints = actionPoints;
    }
}
