package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;

/**
 * Ohjainkomponentti hahmon kykyjen hallintaan.
 *
 * @param <A> ohjattavan kyvyn tyyppi
 */
public interface IControllerComponent<A extends IAbility> {
    /**
     * Onko ohjaimen kyky näkyvissä toimintopalkissa jos ohjattava hahmo on pelaaja.
     *
     * @return false jos kyky näkyy, muulloin true
     */
    boolean isHidden();

    /**
     * Alustaa komponentin ja asettaa ohjatun hahmon.
     *
     * @param character hahmo jonka kykyä ohjataan
     */
    void init(@NonNull CharacterObject character);

    /**
     * Haluaako hahmo käyttää ohjattavaa kykyä.
     *
     * @param ability kyky jota ohjataan
     * @return <code>true</code> jos hahmo haluaa käyttää kykyä tällä vuorolla
     * @throws NullPointerException jos kyky on <code>null</code>
     */
    boolean wants(@NonNull A ability);

    /**
     * Päivittää ohjainkomponentin syötteet.
     *
     * @param ability kyky jota ohjataan
     * @throws NullPointerException jos kyky on <code>null</code>
     */
    void updateInput(@NonNull A ability);

    /**
     * Kutsutaan sen jälkeen kun ohjattua kykyä on käytetty.
     *
     * @param ability kyky jota ohjataan
     */
    void abilityPerformed(A ability);

    /**
     * Siivoaa ohjaimen vuoron lopuksi.
     */
    void reset();
}
