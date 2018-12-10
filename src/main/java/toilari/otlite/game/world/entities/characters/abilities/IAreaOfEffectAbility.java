package toilari.otlite.game.world.entities.characters.abilities;

import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.components.IAreaOfEffectControllerComponent;

/**
 * Kyky jonka vaikutuksella on tietty alue.
 *
 * @param <A> kyvyn tyyppi
 * @param <C> ohjainkomponentin tyyppi
 */
public interface IAreaOfEffectAbility<A extends IAreaOfEffectAbility<A, C>, C extends IAreaOfEffectControllerComponent<A>> extends IAbility<A, C> {
    /**
     * Vaikuttaako kyky annettuun objektiin jos se on vaikutusalueella.
     *
     * @param object peliobjekti jota testataan
     * @return <code>true</code> jos kyky vaikuttaa peliobjektiin, muulloin <code>false</code>
     */
    boolean canAffect(GameObject object);

    /**
     * Kyvyn vaikutusalueen koko yhteen suuntaan. Suure on ilmoitettu ruutuina yhteen suuntaan kohdekoordinaateista
     * poispäin. Esim. koko 2 tarkoittaa että alue on muotoa:<br/>
     * <code>
     * XXXXX<br/>
     * XXXXX<br/>
     * XXOXX<br/>
     * XXXXX<br/>
     * XXXXX<br/>
     * </code><br/>
     * jossa <code>X</code> on vaikutusaluetta ja <code>O</code> kohdekoordinaatti.
     *
     * @return vaikutusalueen koko yhteen suuntaan
     */
    int getAreaExtent();

    /**
     * Kyvyn vaikutusalueen todellinen koko.
     *
     * @return kyvyn vaikutusalueen koko
     */
    default int getAreaSize() {
        return 1 + getAreaExtent() * 2;
    }
}
