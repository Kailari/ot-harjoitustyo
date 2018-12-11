package toilari.otlite.game.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Pelaajaprofiili.
 */
@AllArgsConstructor
public class Profile {
    @Getter private final int id;
    @Getter private final String name;
    @Getter private final Settings settings;
    private boolean unfinishedSave;

    /**
     * Kertoo onko pelaajalla tallennettua keskeneräistä peliä jota jatkaa.
     *
     * @return <code>true</code> jos pelaajalla on olemassa tallennettu peli jota jatkaa,
     * <code>false</code> muulloin
     */
    public boolean hasUnfinishedSave() {
        return this.unfinishedSave;
    }
}
