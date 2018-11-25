package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;

/**
 * Hahmon kyky. Kaikki hahmon toiminnallisuus jolla se vuorovaikuttaa pelimaailman kanssa toteutetaan kykyinä. Kyvyt
 * itsessään kertovat vain <i>miten</i> jokin toiminto suoritetaan. Lisäksi tarvitaan myös ohjainkomponentti kertomaan
 * <i>milloin</i> kykyjä käytetään.
 *
 * @param <A> tyyppi itse. (Esim {@link MoveAbility}:lla tämä on <code>MoveAbility</code>) Tarvitaan ohjainkomponentin
 *            tyypin varmistamiseen
 * @param <C> ohjainkomponentin tyyppi
 * @see IControllerComponent
 */
public interface IAbility<A extends IAbility<A, C>, C extends IControllerComponent<A>> {
    /**
     * Toiminnon prioriteetti. Hahmon omalla vuorolla kykyjä yritetään suorittaa prioriteetin mukaan järjestyksessä
     * pienimmästä suurimpaan ja kullakin päivityskerralla suoritetaan korkeintaan yksi kyky.
     *
     * @return kyvyn suoritusprioriteetti
     */
    int getPriority();

    /**
     * Kertoo kyvyn hinnan toimintopisteinä.
     *
     * @return kyvyn hinta
     */
    int getCost();

    /**
     * Onko kyky jäähtymässä? Jäähtymässä olevia kykyjä ei yritetä suorittaa, vaan sen sijaan niiden jäähtymisajastinta
     * lasketaan kunkin vuoron lopuksi, kunnes ne ovat taas käytettävissä ajastimen päästessä nollaan
     *
     * @return onko
     * @see #putOnCooldown()
     * @see #getCooldownLength()
     * @see #reduceCooldownTimer()
     */
    boolean isOnCooldown();

    /**
     * Asettaa kyvyn jäähtymään. Asettaa jäähtymisajastimen arvoksi {@link #getCooldownLength()} arvon
     *
     * @see #isOnCooldown()
     * @see #reduceCooldownTimer()
     * @see #getCooldownLength()
     */
    void putOnCooldown();

    /**
     * Vähentää jäähtymisajastimen arvoa yhdellä.
     *
     * @see #isOnCooldown()
     * @see #putOnCooldown()
     * @see #getCooldownLength()
     */
    void reduceCooldownTimer();

    /**
     * Kertoo kuinka pitkä tämän kyvyn jäähtymisajastin on.
     *
     * @return kyvyn jäähtymisajastimen kesto
     * @see #isOnCooldown()
     * @see #putOnCooldown()
     * @see #reduceCooldownTimer()
     */
    int getCooldownLength();

    /**
     * Suorittaa kyvyn. Suorittaa kyvyn pelilogiikkaosuuden esim. {@link MoveAbility#perform(MoveControllerComponent)}
     * siirtää pelihahmoan siihen suuntaan johon ohjainkomponentti pyytää.
     *
     * @param component ohjainkomponentti joka pyysi siirtämään hahmoa
     * @return <code>true</code> jos toiminto suoritettiin, <code>false</code> muulloin
     * @throws NullPointerException jos komponentti on <code>null</code>
     */
    boolean perform(@NonNull C component);
}