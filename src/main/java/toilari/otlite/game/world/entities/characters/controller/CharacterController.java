package toilari.otlite.game.world.entities.characters.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Ohjaa hahmoja pelimaailmassa. Voi ottaa syötteen esim. pelaajalta tai "tekoälyltä".
 * Yksi ohjain voi ohjata kerrallaan vain yhtä hahmoa.
 */
public class CharacterController {
    @NonNull @Getter private final AbstractCharacter controlledCharacter;
    @Getter private int turnsTaken;

    private final Map<Class<? extends IAbility>, IControllerComponent> components = new HashMap<>();

    public CharacterController(@NonNull AbstractCharacter controlledCharacter) {
        this.controlledCharacter = controlledCharacter;
    }

    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> void registerComponent(A ability, IControllerComponent<A> component) {
        this.components.put(ability.getClass(), component);
    }

    /**
     * Kutsutaan kun ohjatun hahmon vuoro alkaa.
     */
    public void beginTurn() {
        this.turnsTaken++;
    }

    /**
     * Hakee ohjainkomponentin toiminnon suorittamista varten.
     *
     * @param ability toiminto jonka ohjainkomponentti haetaan
     * @param <A>     toiminnon tyyppi
     * @param <C>     jotain hirveää
     * @return ohjainkomponentti jonka syötteellä toiminto voidaan suorittaa
     * @throws IllegalStateException jos toiminto on rekisteröity virheellisesti, eikä komponenttia siksi löydy
     */
    @NonNull
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> C getComponentFor(@NonNull A ability) {
        val component = this.components.get(ability.getClass());
        if (component == null) {
            throw new IllegalStateException("No registered component for ability \"" + ability.getClass().getSimpleName() + "\"");
        }

        // Due to horrendous type signature of the component registration method, this cast is completely
        // safe, as long as no weird type-casting takes place when calling the register-method due to some
        // brainfart of an unbelieveable magnitude. Thus:
        // noinspection unchecked
        return (C) component;
    }
}
