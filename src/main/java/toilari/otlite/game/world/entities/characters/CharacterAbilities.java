package toilari.otlite.game.world.entities.characters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.characters.abilities.AbilityRegistry;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;

import java.util.*;

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
@Slf4j
public class CharacterAbilities {
    private final Map<Class<? extends IAbility>, IControllerComponent> components = new HashMap<>();
    private final SortedSet<IAbility> abilities = new TreeSet<>(Comparator.comparingInt(IAbility::getPriority));

    /**
     * Lisää hahmolle uuden kyvyn.
     *
     * @param ability   uusi kyky
     * @param component kyvystä vastaava ohjainkomponentti
     * @param <A>       kyvyn tyyppi
     * @param <C>       ohjainkomponentin tyyppi
     * @throws NullPointerException jos kyky tai ohjainkomponentti ovat <code>null</code>
     */
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> void addAbility(@NonNull A ability, @NonNull IControllerComponent<A> component) {
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
     * @throws NullPointerException jos luokka on <code>null</code>
     */
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> C getComponent(@NonNull Class<? extends A> abilityClass) {
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
    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> A getAbility(@NonNull Class<? extends A> abilityClass) {
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
     * Hakee kyvyn INSTANSSIN ohjaamisesta vastaavan ohjainkomponentin. Tätä metodia voidaan siis käyttää jos tiedossa
     * on jo kyvyn instanssi ja voimme olla varmoja että se on lähtöisin tältä hahmolta. Muulloin tulee käyttää metodia
     * {@link #getComponent(Class)}.
     *
     * @param ability toiminto jonka ohjainkomponentti haetaan
     * @param <A>     toiminnon tyyppi
     * @param <C>     ohjainkomponentin tyyppi
     * @return ohjainkomponentti joka vastaa annetusta kyvystä
     * @throws IllegalStateException jos kyvystä vastaavaa ohjainkomponenttia ei löydy
     */
    public @NonNull <A extends IAbility<A, C>, C extends IControllerComponent<A>> C getComponentResponsibleFor(@NonNull A ability) {
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
    public Iterable<IAbility> getAbilitiesSortedByPriority() {
        return this.abilities;
    }


    /**
     * Kopioi kyvyt toisesta instanssista.
     *
     * @param template instanssi joka kopioidaan
     */
    public void cloneAbilitiesFrom(@NonNull CharacterAbilities template) {
        for (IAbility abilityTemplate : template.abilities) {
            if (abilityTemplate == null) {
                LOG.error("Template had a NULL ability!");
                continue;
            }

            // Type signature of IAbility prevents its clashing with the matching type signature of addAbilityFromTemplate,
            // but as compiler cannot see that template has to satisfy those requirements due to being parameterless in
            // this context, it comes to conclusion that this call is unsafe and should be flagged as unchecked, while actually
            // under normal circumstances it cannot fail.
            // noinspection unchecked
            addAbilityFromTemplate(template, abilityTemplate);
        }
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> void addAbilityFromTemplate(CharacterAbilities template, A abilityTemplate) {
        val componentTemplate = template.getComponentResponsibleFor(abilityTemplate);

        val instanceAbility = cloneAbility(abilityTemplate);
        val instanceComponent = cloneControllerComponent(abilityTemplate, componentTemplate);
        if (instanceAbility == null || instanceComponent == null) {
            return;
        }

        // We know for sure that signatures of the ability and the component match, perform add without type validation
        this.abilities.add(instanceAbility);
        this.components.put(instanceAbility.getClass(), instanceComponent);
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> IAbility cloneAbility(A abilityTemplate) {
        val factory = AbilityRegistry.getAbilityInstanceFactory(abilityTemplate);
        if (factory == null) {
            return null;
        }

        val ability = factory.get();
        if (ability == null) {
            return null;
        }

        ability.setPriority(abilityTemplate.getPriority());
        return ability;
    }

    private static <A extends IAbility<A, C>, C extends IControllerComponent<A>> C cloneControllerComponent(A abilityTemplate, C componentTemplate) {
        val factory = AbilityRegistry.getComponentInstanceFactory(abilityTemplate, componentTemplate);
        return factory == null ? null : factory.apply(componentTemplate);
    }
}
