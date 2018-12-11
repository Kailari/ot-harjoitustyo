package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.ITargetedAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Pohjaluokka kohteenvalintakyvyn ohjainkomponenteille.
 */
@NoArgsConstructor
public abstract class TargetSelectorControllerComponent extends AbstractControllerComponent<TargetSelectorAbility> {
    @Getter(AccessLevel.PROTECTED) private transient ITargetedAbility[] abilities;
    @Getter private transient ITargetedAbility active;
    @Getter private transient GameObject target;
    @Getter private transient Direction targetDirection;

    /**
     * Onko annettu kyky aktiivinen eli ollaanko sille valitsemassa kohdetta.
     *
     * @param ability kyky jonka tila halutaan tarkistaa
     * @return <code>true</code> jos annetulle kyvylle ollaan valitsemassa kohdetta, muulloin <code>false</code>
     * @throws NullPointerException jos kyky on null
     */
    public boolean isActive(@NonNull IAbility ability) {
        return Objects.equals(this.active, ability);
    }

    /**
     * Asettaa nykyisen kohteen.
     *
     * @param target    peliobjekti jota tulee käyttää kohteena
     * @param direction suunta johon ollaan hyökkäämässä
     */
    public void setTarget(GameObject target, Direction direction) {
        this.target = target;
        this.targetDirection = direction;
    }

    /**
     * Asettaa annetun kyvyn aktiiviseksi.
     *
     * @param ability kyky jolle halutaan valita kohde, voi olla <code>null</code>
     */
    public void setActiveTargetedAbility(ITargetedAbility ability) {
        if (ability != null && !isAvailableAbility(ability)) {
            throw new IllegalStateException("Character tried to update targeting for ability it does not own.");
        }

        this.active = ability;
    }

    /**
     * Tarkistaa onko annettu kyky käytettävissä kohteenvalintaa varten. Kyvyille joille tämä metodi
     * palauttaa <code>true</code> voidaan kutsua {@link #setActiveTargetedAbility(ITargetedAbility)}
     *
     * @param ability kyky joka tarkistetaan
     * @return <code>true</code> jos kyky on käytettävissä
     */
    public boolean isAvailableAbility(IAbility ability) {
        return ability instanceof ITargetedAbility && Arrays.asList(this.abilities).contains(ability);
    }

    protected TargetSelectorControllerComponent(TargetSelectorControllerComponent template) {
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

    @Override
    public void reset() {
        this.target = null;
        this.active = null;
        this.targetDirection = Direction.NONE;
    }

    protected GameObject findTargetInDirection(Direction direction) {
        if (getActive() == null) {
            return null;
        }

        val targetX = getCharacter().getTileX() + direction.getDx();
        val targetY = getCharacter().getTileY() + direction.getDy();
        val targetCandidate = getCharacter().getWorld().getObjectAt(targetX, targetY);

        val ability = getActive();
        val component = (ITargetedControllerComponent) getCharacter().getAbilities().getComponent(ability.getClass());
        if (targetCandidate != null
            && ability.canPerformOn(targetCandidate, direction)
            && component.wantsPerformOn(targetCandidate, direction)) {

            if (targetCandidate instanceof CharacterObject) {
                return ((CharacterObject) targetCandidate).isDead() ? null : targetCandidate;
            }

            return targetCandidate;
        }

        return null;
    }
}
