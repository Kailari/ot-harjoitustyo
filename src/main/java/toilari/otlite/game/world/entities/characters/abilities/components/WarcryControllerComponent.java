package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.WarcryAbility;

/**
 * Ohjainkomponentti sotahuudolle.
 */
@NoArgsConstructor
public abstract class WarcryControllerComponent
    extends AbstractControllerComponent<WarcryAbility>
    implements ITargetedControllerComponent<WarcryAbility>, IAreaOfEffectControllerComponent<WarcryAbility> {

    @Getter private transient TargetSelectorControllerComponent targetSelector;
    @Setter(AccessLevel.PROTECTED) private boolean wants;

    protected WarcryControllerComponent(WarcryControllerComponent template) {
        super(template);
    }

    @Override
    public void init(@NonNull CharacterObject character) {
        super.init(character);
        this.targetSelector = character.getAbilities().getComponent(TargetSelectorAbility.class);

        if (this.targetSelector == null) {
            throw new IllegalStateException("The component \"" + getClass().getSimpleName() + "\" requires a TargetSelector on the object to function!");
        }
    }

    @Override
    public boolean wants(@NonNull WarcryAbility ability) {
        return this.wants;
    }

    @Override
    public void abilityPerformed(WarcryAbility ability) {
        this.targetSelector.abilityPerformed(getCharacter().getAbilities().getAbility(TargetSelectorAbility.class));
        this.wants = false;
    }

    @Override
    public void reset() {
        this.wants = false;
    }

    @Override
    public boolean wantsPerformOn(GameObject target, Direction direction) {
        return target.equals(getCharacter());
    }

    @Override
    public final void updateInput(@NonNull WarcryAbility ability) {
        if (!this.targetSelector.isActive(ability)) {
            return;
        }

        doUpdateInput(ability);
    }

    protected abstract void doUpdateInput(@NonNull WarcryAbility ability);

    /**
     * Pelaajan ohjainkomponentti sotahuudolle.
     */
    @NoArgsConstructor
    public static class Player extends WarcryControllerComponent {
        /**
         * Kopioi komponentin templaatista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(WarcryControllerComponent template) {
            super(template);
        }

        @Override
        public void doUpdateInput(@NonNull WarcryAbility ability) {
            setWants(Input.getHandler().isKeyPressed(Key.SPACE));
        }
    }
}
