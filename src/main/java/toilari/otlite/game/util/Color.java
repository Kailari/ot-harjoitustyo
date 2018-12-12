package toilari.otlite.game.util;

import lombok.Getter;
import lombok.NonNull;

/**
 * Apuluokka RGB-värien käsittelyyn.
 */
public class Color {
    public static final Color RED = new Color(1.0f, 0.0f, 0.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f);
    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
    @Getter private final float r, g, b;

    /**
     * Luo uuden värin.
     *
     * @param r punainen komponentti
     * @param g vihreä komponentti
     * @param b sininen komponentti
     */
    public Color(float r, float g, float b) {
        this.r = MathUtil.clamp01(r);
        this.g = MathUtil.clamp01(g);
        this.b = MathUtil.clamp01(b);
    }

    /**
     * Luo uuden värin sävyttämällä väriä annetulla arvolla. Sama kuin {@link #tint(float, float, float)} jossa kaikki
     * parametrit asetettu arvoon <code>tint</code>.
     *
     * @param tint värisävyn komponenttien arvo
     * @return uusi värisävy
     */
    public Color tint(float tint) {
        return tint(tint, tint, tint);
    }

    /**
     * Luo uuden värin sävyttämällä väriä annetulla värillä.
     *
     * @param tint värisävy
     * @return uusi värisävy
     */
    public Color tint(@NonNull Color tint) {
        return tint(tint.r, tint.g, tint.b);
    }

    /**
     * Luo uuden värin sävyttämällä väriä annetulla värillä.
     *
     * @param r värisävyn punainen komponentti
     * @param g värisävyn vihreä komponentti
     * @param b värisävyn sininen komponentti
     * @return uusi värisävy
     */
    public Color tint(float r, float g, float b) {
        return new Color(
            this.r + (1.0f - this.r) * MathUtil.clamp01(r),
            this.g + (1.0f - this.g) * MathUtil.clamp01(g),
            this.b + (1.0f - this.b) * MathUtil.clamp01(b));
    }

    /**
     * Luo uuden värin varjostamalla väriä annetulla arvolla.
     *
     * @param shade varjostuksen määrä
     * @return uusi väri
     */
    public Color shade(float shade) {
        return new Color(
            this.r * (1.0f - MathUtil.clamp01(shade)),
            this.g * (1.0f - MathUtil.clamp01(shade)),
            this.b * (1.0f - MathUtil.clamp01(shade)));
    }
}
