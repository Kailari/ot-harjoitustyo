package toilari.otlite.game.world.level;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;

import java.util.Objects;

/**
 * Määrittelee pelimaailman ruudun tyypin. Kustakin tyypistä on vain yksi
 * immutaabeli instanssi.
 */
@RequiredArgsConstructor
public abstract class Tile {
    public static final int SIZE_IN_WORLD = 8;
    /**
     * Määrittää kohdellaanko ruutua seinänä. (hahmot eivät voi liikkua ruutuun joka
     * on seinä)
     *
     * @return <code>true</code> jos ruutu on seinä, muulloin <code>false</code>
     */
    @Getter private final boolean wall;

    /**
     * Määrittää kohdellaanko ruutua vaarallisena. (tekoäly välttelee oletuksena vaarallisia ruutuja)
     *
     * @return <code>true</code> jos ruutu on vaarallinen, muulloin <code>false</code>
     */
    @Getter private final boolean dangerous;

    /**
     * Määrittää ruututyypin ulkonäön. Luku on indeksi tileset-tekstuuriin.
     *
     * @return ruudun indeksi tilesetissä
     */
    @Getter private final int tileIndex;

    /**
     * Ruututyypin yksilöllinen tunniste. Mikäli kahdella ruututyypillä on sama tunniste, on kyseessä
     * suoritukselle fataali virhe.
     *
     * @return ruututyypin tunniste
     */
    @Getter private final @NonNull String id;

    /**
     * Kutsutaan kun hahmo astuu ruutuun.
     *
     * @param x         ruudun x-koordinaatti
     * @param y         ruudun y-koordinaatti
     * @param character hahmo joka juuri astui ruutuun
     * @throws NullPointerException jos <code>character</code> on null
     */
    public void onCharacterEnter(int x, int y, @NonNull AbstractCharacter character) {
    }

    /**
     * Kutsutaan kun hahmo astuu pois ruudusta.
     *
     * @param x         ruudun x-koordinaatti
     * @param y         ruudun y-koordinaatti
     * @param character hahmo joka juuri astui pois ruudusta
     * @throws NullPointerException jos <code>character</code> on null
     */
    public void onCharacterExit(int x, int y, @NonNull AbstractCharacter character) {
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tile) {
            return ((Tile) o).getId().equals(this.getId());
        }

        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
