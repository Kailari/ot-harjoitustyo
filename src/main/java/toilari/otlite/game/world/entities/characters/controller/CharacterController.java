package toilari.otlite.game.world.entities.characters.controller;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;

/**
 * Ohjaa hahmoja pelimaailmassa. Voi ottaa syötteen esim. pelaajalta tai "tekoälyltä".
 * Yksi ohjain voi ohjata kerrallaan vain yhtä hahmoa.
 */
public abstract class CharacterController {
    @Getter private AbstractCharacter controlledCharacter;

    /**
     * Ottaa parametrina annetun hahmon hallintaan. Asettaa hahmon ohjainviitteen tarvittaessa
     *
     * @param character hahmo joka otetaan hallintaan. voi olla <code>null</code> jos ohjain halutaan poistaa
     */
    public void takeControl(AbstractCharacter character) {
        if (this.controlledCharacter != null) {
            this.controlledCharacter.giveControlTo(null);
        }

        this.controlledCharacter = character;
        if (character != null && character.getController() != this) {
            character.giveControlTo(this);
        }
    }

    /**
     * Hakee vaakasuutaisen liikkeen syötteen. Negatiivinen arvo tarkoittaa että hahmo pyrkii
     * liikkumaan seuraavalla vuorollaan vasemmalle, positiivinen arvo oikealle. Arvo nolla
     * tarkoittaa että hahmo pysyy vuorolla vaakasuunnassa paikallaan.
     *
     * @return <code>-1</code> jos ollaan liikkumassa vasemmalle, <code>1</code> oikealle ja
     * <code>0</code> jos pysytään paikallaan
     */
    public abstract int getMoveInputX();

    /**
     * Hakee pystysuuntaisen liikkeen syötteen. Negatiivinen arvo tarkoittaa että hahmo pyrkii
     * liikkumaan seuraavalla vuorollaan ylös, positiivinen arvo alas. Arvo nolla tarkoittaa että
     * hahmo pysyy vuorolla pystysuunnassa paikallaan.
     *
     * @return <code>-1</code> jos ollaan liikkumassa ylös, <code>1</code> alas ja <code>0</code>
     * jos pysytään paikallaan
     */
    public abstract int getMoveInputY();

    /**
     * Päivittää ohjaimen. Mahdollistaa tekoälyohjaimien monimutkaisemman logiikan simuloinnin.
     *
     * @param turnManager aktiivinen vuoromanageri
     * @throws NullPointerException jos vuoromanageri on <code>null</code>
     */
    public void update(@NonNull TurnObjectManager turnManager) {
    }
}
