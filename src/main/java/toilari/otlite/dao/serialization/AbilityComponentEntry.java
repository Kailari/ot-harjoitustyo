package toilari.otlite.dao.serialization;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Apuluokka kyky- ja ohjainkomponenttiparien varastointiin.
 *
 * @param <A> kyvyn tyyppi
 * @param <C> komponentin tyyppi
 */
public class AbilityComponentEntry<A extends IAbility<A, C>, C extends IControllerComponent<A>> {
    @Getter @NonNull private final Class<? extends A> abilityClass;
    @Getter @NonNull private final Supplier<A> factory;
    @Getter @NonNull private final Map<String, Class<? extends C>> componentClasses = new HashMap<>();
    @Getter @NonNull private final Map<Class, Function<C, C>> componentFactories = new HashMap<>();

    /**
     * Luo uuden parin.
     *
     * @param abilityClass kyvyn tyyppi
     * @param factory      tehdas jolla kyvyn instansseja voidaan tuottaa
     */
    public AbilityComponentEntry(@NonNull Class<? extends A> abilityClass, Supplier<A> factory) {
        this.abilityClass = abilityClass;
        this.factory = factory;
    }

    /**
     * Lisää komponenttityypin kyvylle.
     *
     * @param key            avain jolla määritystiedostossa voidaan viitata komponenttiin
     * @param componentClass komponentin luokka
     * @param factory        tehdas jolla komponentteja voidaan tuottaa
     * @return tämä pari ketjutusta varten
     */
    public AbilityComponentEntry<A, C> registerComponent(String key, Class<? extends C> componentClass, Function<C, C> factory) {
        this.componentClasses.put(key, componentClass);
        this.componentFactories.put(componentClass, factory);
        return this;
    }
}
