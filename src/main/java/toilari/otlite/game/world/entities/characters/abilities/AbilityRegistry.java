package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.serialization.AbilityComponentEntry;
import toilari.otlite.game.world.entities.characters.abilities.components.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class AbilityRegistry {
    private static final Map<String, AbilityComponentEntry<?, ?>> ABILITY_ENTRIES_BY_KEY = new HashMap<>();
    private static final Map<Class, AbilityComponentEntry<?, ?>> ABILITY_ENTRIES_BY_CLASS = new HashMap<>();

    static {
        register("target_selector", TargetSelectorAbility.class, TargetSelectorAbility::new)
            .registerComponent("player", PlayerTargetSelectorControllerComponent.class, PlayerTargetSelectorControllerComponent::new)
            .registerComponent("attack_adjacent_if_possible", AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent.class, AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent::new);

        register("move", MoveAbility.class, MoveAbility::new)
            .registerComponent("player", MoveControllerComponent.Player.class, MoveControllerComponent.Player::new)
            .registerComponent("animal", MoveControllerComponent.AI.class, MoveControllerComponent.AI::new);

        register("end_turn", EndTurnAbility.class, EndTurnAbility::new)
            .registerComponent("player", EndTurnControllerComponent.Player.class, EndTurnControllerComponent.Player::new)
            .registerComponent("ai", EndTurnControllerComponent.AI.class, EndTurnControllerComponent.AI::new);

        register("attack", AttackAbility.class, AttackAbility::new)
            .registerComponent("player", AttackControllerComponent.Player.class, AttackControllerComponent.Player::new)
            .registerComponent("ai", AttackControllerComponent.AI.class, AttackControllerComponent.AI::new);

        register("kick", KickAbility.class, KickAbility::new)
            .registerComponent("player", KickControllerComponent.Player.class, KickControllerComponent.Player::new)
            .registerComponent("ai", KickControllerComponent.AI.class, KickControllerComponent.AI::new);

        register("warcry", WarcryAbility.class, WarcryAbility::new)
            .registerComponent("player", WarcryControllerComponent.Player.class, WarcryControllerComponent.Player::new);
    }

    /**
     * Lisää uuden kyvyn rekisteriin. Vain rekisteröityjä kykyjä voidaan ladata tiedostosta. Hyväksyy vain kykyjä ja
     * komponentteja joiden tyypit ovat keskenään yhteensopivia.
     *
     * @param key            kyvyn tunniste jota käytetään avaimena määritystiedostossa
     * @param abilityClass   kyvyn toteuttava luokka
     * @param abilityFactory tehdas jolla kyvyn instansseja voidaan tuottaa
     * @param <A>            kyvyn tyyppi
     * @param <C>            kyvyn ohjainkomponentin tyyppi
     * @return kyvyn rekisteri-instanssi jota voidaan käyttää komponenttien rekisteröintiin
     */
    private static <A extends IAbility<A, C>, C extends IControllerComponent<A>> AbilityComponentEntry<A, C> register(String key, Class<? extends A> abilityClass, Supplier<A> abilityFactory) {
        val entry = new AbilityComponentEntry<A, C>(abilityClass, abilityFactory);
        ABILITY_ENTRIES_BY_KEY.put(key, entry);
        ABILITY_ENTRIES_BY_CLASS.put(abilityClass, entry);
        return entry;
    }

    /**
     * Hakee tunnistetta vastaavan kyvyn luokan.
     *
     * @param key kyvyn tunniste
     * @return <code>null</code> jos tunnistetta vastaavaa kykyä ei löydy, muulloin tunnistetta vastaava kyky
     */
    public static Class<? extends IAbility> getAbilityClass(@NonNull String key) {
        val entry = ABILITY_ENTRIES_BY_KEY.get(key);
        return entry == null ? null : entry.getAbilityClass();
    }

    /**
     * Hakee tunnistetta ja kykyä vastaavan komponentin luokan.
     *
     * @param abilityKey   kyvyn tunniste
     * @param componentKey ohjainkomponentin tunniste
     * @return <code>null</code> jos tunnistetta vastaavaa kykyä ei löydy, muulloin tunnistetta vastaava kyky
     */
    public static Class<? extends IControllerComponent> getComponentClass(@NonNull String abilityKey, @NonNull String componentKey) {
        val entry = ABILITY_ENTRIES_BY_KEY.get(abilityKey);
        return entry == null ? null : entry.getComponentClasses().get(componentKey);
    }

    /**
     * Hakee komponentille tehtaan jolla annetuntyyppinen komponentti voidaan luoda.
     *
     * @param abilityTemplate   kykytemplaatti jota vastaavista komponenteista etsitään
     * @param componentTemplate komponenttitemplaatti jota etsitään
     * @param <A>               kyvyn tyyppi
     * @param <C>               komponentin tyyppi
     * @return <code>null</code> jos komponenttia vastaavaa tehdasta ei löydy, muulloin löydetty tehdas
     * @throws NullPointerException jos kumpikaan templaateista on <code>null</code>
     */
    public static <A extends IAbility<A, C>, C extends IControllerComponent<A>> Function<C, C> getComponentInstanceFactory(@NonNull A abilityTemplate, @NonNull C componentTemplate) {
        val abilityClass = abilityTemplate.getClass();
        AbilityComponentEntry<A, C> entry = getAbilityComponentEntry(abilityClass);
        if (entry == null) {
            LOG.error("Ability \"{}\" is unregistered or no components are registered for it!", abilityClass.getSimpleName());
            return null;
        }

        val factories = entry.getComponentFactories();
        return factories.get(componentTemplate.getClass());
    }

    /**
     * Hakee kyvylle tehtaan jolla annetuntyyppinen kyky voidaan luoda.
     *
     * @param abilityTemplate kykytemplaatti jota vastaavaa tehdasta etsitään
     * @param <A>             kyvyn tyyppi
     * @param <C>             komponentin tyyppi
     * @return <code>null</code> jos kykyä vastaavaa tehdasta ei löydy, muulloin löydetty tehdas
     * @throws NullPointerException jos templaatti on <code>null</code>
     */
    public static <A extends IAbility<A, C>, C extends IControllerComponent<A>> Supplier<A> getAbilityInstanceFactory(@NonNull A abilityTemplate) {
        val abilityClass = abilityTemplate.getClass();
        AbilityComponentEntry<A, C> entry = getAbilityComponentEntry(abilityClass);
        if (entry == null) {
            LOG.error("Tried to get factory for an unregistered ability \"{}\"!", abilityClass.getSimpleName());
            return null;
        }

        return entry.getFactory();
    }

    private static <A extends IAbility<A, C>, C extends IControllerComponent<A>> AbilityComponentEntry<A, C> getAbilityComponentEntry(Class<? extends IAbility> abilityClass) {
        // register-method enforces that types are compatible, meaning that as long as register implementation follows
        // its specification, this cast is guaranteed to succeed
        // noinspection unchecked
        AbilityComponentEntry<A, C> entry = (AbilityComponentEntry<A, C>) ABILITY_ENTRIES_BY_CLASS.get(abilityClass);
        if (entry == null || entry.getComponentFactories().isEmpty()) {
            return null;
        }
        return entry;
    }
}
