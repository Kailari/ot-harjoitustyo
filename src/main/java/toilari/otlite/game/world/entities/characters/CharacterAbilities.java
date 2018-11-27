package toilari.otlite.game.world.entities.characters;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.serialization.AbilityComponentEntry;
import toilari.otlite.dao.serialization.CharacterAdapter;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AttackControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;

import java.util.*;
import java.util.function.Supplier;

/**
 * Säilöö hahmon {@link IAbility kykyjä} ja niitä vastaavia {@link IControllerComponent ohjainkomponentteja}.
 * <p>
 * Toiminnallisuus on toteutettu komponenttimallilla, jossa jokaista hahmon kykyä ohjaamaan on
 * asetettu yksi erityinen ohjainkomponentti. Esim. liikkumisesta vastaavat hahmon puolella {@link MoveAbility} ja
 * ohjaimen puolella {@link MoveControllerComponent}. Tämä sallii esimerkiksi erilaisten kyky- ja ohjainyhdistelmien
 * kasaamisen erilaisten hahmojen aikaansaamiseksi.
 * <p>
 * Toteutustavan vuoksi järjestelmä on hyvin tarkka siitä, mitä ohjainkomponentteja millekkin kyvylle asetetaan.
 * Käytettävän ohjainkomponentin tyyppi on määritelty {@link IAbility} tyyppiparametreissa, jolloin esim {@link MoveAbility}
 * voi käyttää vain luokasta {@link MoveControllerComponent} periviä ohjainkomponentteja (koska <code>MoveAbility</code>
 * asettaa <code>MoveControllerComponent</code> ohjaimen tyyppiä vastaavaan tyyppiparametriin eivätkä aliluokat voi enää
 * vaihtaa sitä.).
 * <p>
 * Etuna tästä saadaan esimerkiksi se että aina jos kyvyn luokka/tyyppi on tiedossa, on pääsy käytettäväksi määritellyn
 * ohjainkomponentin julkisiin metodeihin helppoa, eikä tyyppimuunnoksia useimmissa tapauksissa tarvita.
 */
public class CharacterAbilities {
    private static final CharacterAdapter ADAPTER = new CharacterAdapter();
    private static final Map<Class<? extends IAbility>, Supplier<? extends IAbility>> ABILITY_FACTORIES = new HashMap<>();
    private static final Map<String, AbilityComponentEntry<?, ?>> ABILITY_ENTRIES = new HashMap<>();
    private static final Map<Class, AbilityComponentEntry<?, ?>> ABILITY_ENTRIES_BY_CLASS = new HashMap<>();

    public static Map<String, AbilityComponentEntry<?, ?>> getAbilityEntries() {
        return CharacterAbilities.ABILITY_ENTRIES;
    }

    static {
        registerAbility("move", MoveAbility.class, MoveAbility::new)
            .addComponent("player", MoveControllerComponent.Player.class, MoveControllerComponent.Player::new)
            .addComponent("animal", MoveControllerComponent.AI.class, MoveControllerComponent.AI::new);

        registerAbility("end_turn", EndTurnAbility.class, EndTurnAbility::new)
            .addComponent("player", EndTurnControllerComponent.Player.class, EndTurnControllerComponent.Player::new)
            .addComponent("ai", EndTurnControllerComponent.AI.class, EndTurnControllerComponent.AI::new);

        registerAbility("attack", AttackAbility.class, AttackAbility::new)
            .addComponent("player", AttackControllerComponent.Player.class, AttackControllerComponent.Player::new);
    }

    private static <A extends IAbility<A, C>, C extends IControllerComponent<A>> AbilityComponentEntry<A, C> registerAbility(String key, Class<? extends A> abilityClass, Supplier<A> abilityFactory) {
        ABILITY_FACTORIES.put(abilityClass, abilityFactory);
        val entry = new AbilityComponentEntry<A, C>(abilityClass);
        ABILITY_ENTRIES.put(key, entry);
        ABILITY_ENTRIES_BY_CLASS.put(abilityClass, entry);
        return entry;
    }

    public static CharacterAdapter getAdapter() {
        return CharacterAbilities.ADAPTER;
    }

    private final Map<Class<? extends IAbility>, IControllerComponent> components = new HashMap<>();
    private final SortedSet<IAbility> abilities = new TreeSet<>(Comparator.comparingInt(IAbility::getPriority));


    /**
     * Lisää hahmolle uuden kyvyn.
     *
     * @param ability   uusi kyky
     * @param component kyvystä vastaava ohjainkomponentti
     * @param <A>       kyvyn tyyppi
     * @param <C>       ohjainkomponentin tyyppi
     */
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> void addAbility(A ability, IControllerComponent<A> component) {
        this.abilities.add(ability);
        this.components.put(ability.getClass(), component);
    }

    /**
     * Hakee kykyä vastaavan ohjainkomponentin jos tällä hahmolla on yhteensopiva toiminto.
     *
     * @param abilityClass toiminnon luokka
     * @param <A>          toiminnon tyyppi
     * @param <C>          ohjainkomponentin tyyppi
     * @return <code>null</code> jos komponenttia ei löydy, muulloin löydetty komponentti
     */
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> C getComponent(Class<? extends A> abilityClass) {
        val ability = getAbility(abilityClass);
        return ability == null ? null : getComponentResponsibleFor(ability);
    }

    /**
     * Hakee tyyppiä vastaavan hahmon kyvyn jos tällä hahmolla on yhteensopiva kyky.
     *
     * @param abilityClass kyvyn luokka
     * @param <A>          kyvyn tyyppi
     * @param <C>          ohjainkomponentin tyyppi
     * @return <code>null</code> jos kykyä ei löydy, muulloin löydetty kyky
     */
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> A getAbility(Class<? extends A> abilityClass) {
        for (val ability : this.abilities) {
            if (ability.getClass().equals(abilityClass)) {
                // The horrific addAbility signature makes sure that this operation is actually checked as long as
                // system isn't purposedly tricked using type-casting magic to believing that incompatible types are
                // compatible. Thus if this line throws, it's an error somewhere else.
                // noinspection unchecked
                return (A) ability;
            }
        }

        return null;
    }

    /**
     * Hakee kyvyn ohjaamisesta vastaavan ohjainkomponentin. Mikäli kyseessä on kyky joka on rekisteröity metodilla
     * {@link #addAbility(IAbility, IControllerComponent)} on paluuarvo aina validi ei-null ohjainkomponentti.
     *
     * @param ability toiminto jonka ohjainkomponentti haetaan
     * @param <A>     toiminnon tyyppi
     * @param <C>     ohjainkomponentin tyyppi
     * @return ohjainkomponentti joka vastaa annetusta kyvystä
     * @throws IllegalStateException jos kyvystä vastaavaa ohjainkomponenttia ei löydy
     */
    @NonNull <A extends IAbility<A, C>, C extends IControllerComponent<A>> C getComponentResponsibleFor(@NonNull A ability) {
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

    /**
     * Hakee hahmon kyvyt iteroitavana listana, järjestettynä prioriteetin mukaan.
     *
     * @return iteroitava lista jossa hahmon kyvyt
     */
    Iterable<IAbility> getAbilitiesSortedByPriority() {
        return this.abilities;
    }

    public void cloneAbilitiesFrom(CharacterAbilities template) {
        for (val abilityTemplate : template.getAbilitiesSortedByPriority()) {
            val componentTemplate = template.getComponent(abilityTemplate.getClass());

            AbilityComponentEntry<?, IControllerComponent<?>> entry = (AbilityComponentEntry<?, IControllerComponent<?>>) ABILITY_ENTRIES_BY_CLASS.get(abilityTemplate.getClass());
            val instanceComponentFactory = entry.getFactories().get(componentTemplate.getClass());
            val instanceComponent = instanceComponentFactory.apply(componentTemplate);
            val instanceAbility = ABILITY_FACTORIES.get(abilityTemplate.getClass()).get();
            instanceAbility.setPriority(abilityTemplate.getPriority());

            addAbilityUnsafe(instanceAbility, instanceComponent);
        }
    }

    private void addAbilityUnsafe(IAbility ability, IControllerComponent component) {
        this.abilities.add(ability);
        this.components.put(ability.getClass(), component);
    }
}
