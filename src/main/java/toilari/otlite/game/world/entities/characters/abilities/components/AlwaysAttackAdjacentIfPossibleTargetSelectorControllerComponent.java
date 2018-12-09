package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * Ohjainkomponentti joka valitsee ensimmäiselle saatavilla olevalle, ei jäähtymässä olevalle hyökkäyskyvylle
 * ensimmäisen löydetyn kohteen.
 */
@NoArgsConstructor
public class AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent extends TargetSelectorControllerComponent {

    /**
     * Kopioi komponentin templaatista.
     *
     * @param template komponentti josta kopioidaan
     */
    public AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent(TargetSelectorControllerComponent template) {
        super(template);
    }

    @Override
    public void updateInput(@NonNull TargetSelectorAbility ability) {
        val world = getCharacter().getWorld();
        val manager = world.getObjectManager();
        val player = manager.getPlayer(); // TODO: Instead of targetting only player, determine by target candidate instead

        if (findNewActiveAbility(manager)) {
            return;
        }

        for (val direction : Direction.asIterable()) {
            val candidate = findTargetInDirection(direction);
            if (Objects.equals(player, candidate)) {
                setTarget(player, direction);
                return;
            }
        }

        setTarget(null, Direction.NONE);
    }

    private boolean findNewActiveAbility(@NonNull TurnObjectManager manager) {
        val activeCandidate = Arrays.stream(getAbilities())
            .sorted(Comparator.comparingInt(IAbility::getPriority))
            .filter(a -> !a.isOnCooldown() && manager.getRemainingActionPoints() >= a.getCost())
            .findFirst();

        if (!activeCandidate.isPresent()) {
            this.setActiveTargetedAbility(null);
            return true;
        }

        this.setActiveTargetedAbility(activeCandidate.get());
        return false;
    }
}
