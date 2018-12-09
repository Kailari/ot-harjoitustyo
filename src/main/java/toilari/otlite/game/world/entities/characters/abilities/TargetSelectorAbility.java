package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.components.TargetSelectorControllerComponent;

/**
 * Kyky valita kohteita. Käytetään kyvyissä jotka haluavat valita kohteita. Itse kyky on tynkä, ja varsinainen logiikka
 * on ohjainkomponenteissa.
 */
public class TargetSelectorAbility extends AbstractAbility<TargetSelectorAbility, TargetSelectorControllerComponent> {
    /**
     * Luo uuden kyvyn.
     */
    public TargetSelectorAbility() {
        super("Select Target");
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }

    @Override
    public boolean perform(@NonNull TargetSelectorControllerComponent component) {
        return false;
    }
}
