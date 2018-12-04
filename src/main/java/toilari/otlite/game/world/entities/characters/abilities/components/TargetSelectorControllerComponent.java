package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.ITargetedAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import java.util.*;

/**
 * Pohjaluokka kohteenvalintakyvyn ohjainkomponenteille.
 */
@NoArgsConstructor
public abstract class TargetSelectorControllerComponent extends AbstractControllerComponent<TargetSelectorAbility> {
    @Getter(AccessLevel.PROTECTED) private transient ITargetedAbility[] abilities;
    @Getter(AccessLevel.PROTECTED) private transient ITargetedAbility active;
    @Getter private transient GameObject target;
    @Getter private transient Direction targetDirection;

    private transient Iterator<Direction> directionIterator;

    /**
     * Onko annettu kyky aktiivinen eli ollaanko sille valitsemassa kohdetta.
     *
     * @param ability kyky jonka tila halutaan tarkistaa
     * @return <code>true</code> jos annetulle kyvylle ollaan valitsemassa kohdetta, muulloin <code>false</code>
     */
    public boolean isActive(IAbility ability) {
        return Objects.equals(this.active, ability);
    }

    /**
     * Asettaa annetun kyvyn aktiiviseksi.
     *
     * @param ability kyky jolle halutaan valita kohde
     */
    public void setActive(ITargetedAbility ability) {
        if (!Arrays.asList(this.abilities).contains(ability)) {
            throw new IllegalStateException("Character tried to update targeting for ability it does not own.");
        }

        this.active = ability;
    }

    TargetSelectorControllerComponent(TargetSelectorControllerComponent template) {
        super(template);
    }

    @Override
    public void init(@NonNull CharacterObject character) {
        super.init(character);

        val list = new ArrayList<ITargetedAbility>();
        for (val ability : character.getAbilities().getAbilitiesSortedByPriority()) {
            if (ability instanceof ITargetedAbility) {
                list.add((ITargetedAbility) ability);
            }
        }

        this.abilities = list.toArray(new ITargetedAbility[0]);
    }

    @Override
    public boolean wants(@NonNull TargetSelectorAbility ability) {
        return false;
    }

    @Override
    public void abilityPerformed(TargetSelectorAbility ability) {
        this.active = null;
        this.target = null;
        this.targetDirection = Direction.NONE;
    }

    protected boolean isActive(int i) {
        return hasAbility(i) && isActive(this.abilities[i]);
    }

    protected void setActive(int abilityIndex) {
        if (abilityIndex < 0 || abilityIndex >= this.abilities.length) {
            this.active = null;
        } else {
            this.active = this.abilities[abilityIndex];
        }
    }

    protected boolean hasAbility(int i) {
        return this.abilities.length > i;
    }

    protected boolean canAfford(int i) {
        return this.abilities[i].getCost() <= getCharacter().getWorld().getObjectManager().getRemainingActionPoints();
    }

    protected boolean notOnCooldown(int i) {
        return !this.abilities[i].isOnCooldown();
    }

    protected void findNewTarget() {
        if (this.target == null) {
            this.directionIterator = Direction.asLoopingIterator();
        }

        this.targetDirection = this.directionIterator.next();
        for (int i = 0; i < 4; i++, this.targetDirection = this.directionIterator.next()) {
            this.target = findTargetInDirection(this.targetDirection);

            if (this.target != null) {
                return;
            }
        }

        this.targetDirection = Direction.NONE;
        this.target = null;
    }

    protected GameObject findTargetInDirection(Direction direction) {
        if (getActive() == null) {
            return null;
        }

        val x = getCharacter().getTileX();
        val y = getCharacter().getTileY();
        val targetX = x + direction.getDx();
        val targetY = y + direction.getDy();
        val targetCandidate = getCharacter().getWorld().getObjectAt(targetX, targetY);

        val ability = getActive();
        val component = (ITargetedControllerComponent) getCharacter().getAbilities().getComponent(ability.getClass());
        if (targetCandidate != null
            && ability.canPerformOn(targetCandidate, direction)
            && component.wantsPerformOn(targetCandidate, direction)) {
            return targetCandidate;
        }

        return null;
    }

    protected void setTarget(GameObject target, Direction direction) {
        this.target = target;
        this.targetDirection = direction;
        this.directionIterator = Direction.asLoopingIterator(direction);
    }

    @Override
    public void reset() {
        this.target = null;
        this.targetDirection = Direction.NONE;
        this.active = null;
    }

    /**
     * Ohjainkomponentti joka valitsee ensimmäiselle saatavilla olevalle, ei jäähtymässä olevalle hyökkäyskyvylle
     * ensimmäisen löydetyn kohteen.
     */
    @NoArgsConstructor
    public static class AlwaysAttackAdjacentIfPossible extends TargetSelectorControllerComponent {

        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AlwaysAttackAdjacentIfPossible(TargetSelectorControllerComponent template) {
            super(template);
        }

        @Override
        public void updateInput(@NonNull TargetSelectorAbility ability) {
            val world = getCharacter().getWorld();
            val manager = world.getObjectManager();
            val player = manager.getGameState().getPlayer(); // TODO: Instead of targetting only player, determine by target candidate instead

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
                this.setActive(-1);
                return true;
            }

            this.setActive(activeCandidate.get());
            return false;
        }
    }
}
