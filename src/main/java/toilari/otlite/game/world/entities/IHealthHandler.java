package toilari.otlite.game.world.entities;

/**
 * Jotain jonka terveyspisteitä lasketaan.
 */
public interface IHealthHandler {
    /**
     * Onko tämä objekti kuollut.
     *
     * @return <code>true</code> jos objekti on kuollut/tuhottu, muulloin <code>false</code>
     */
    boolean isDead();

    /**
     * Objektin tämänhetkiset terveyspisteet.
     *
     * @return nykyiset terveyspisteet
     */
    float getHealth();

    /**
     * Asettaa objektille uudet terveyspisteet.
     *
     * @param health uudet terveyspisteet
     */
    void setHealth(float health);
}
