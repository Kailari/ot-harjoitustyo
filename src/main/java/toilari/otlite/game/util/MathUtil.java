package toilari.otlite.game.util;

/**
 * Hyödyllisiä metodeja lukujen pyörittelyyn, jotka sattuvat puuttumaan standardikirjastosta.
 */
public class MathUtil {
    /**
     * Rajoittaa arvon minimin ja maksimin välille.
     *
     * @param value rajoitettava arvo
     * @param min   minimiraja
     * @param max   maksimiraja
     * @return <code>max</code> jos <code>value > max</code>, <code>min</code> jos <code>value < min</code> ja muulloin <code>value</code>
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Rajoittaa arvon välille nollasta yhteen.
     *
     * @param value rajoitettava arvo
     * @return {@link #clamp(float, float, float) <code>MathUtil.clamp(value, 0.0f, 1.0f)</code>}
     */
    public static float clamp01(float value) {
        return clamp(value, 0.0f, 1.0f);
    }

    /**
     * Lineaarinen interpolointi välillä <code>a..b</code>. Ei rajoita parametrin <code>t</code> arvoa, eli
     * ekstrapolointi on mahdollista.
     *
     * @param a lähtöarvo
     * @param b kohdearvo
     * @param t aika
     * @return <code>a</code> silloin kun <code>t = 0.0</code>, <code>b</code> kun <code>t = 1.0</code> ja muulloin luku väliltä <code>a..b</code>
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
