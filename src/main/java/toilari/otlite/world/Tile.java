package toilari.otlite.world;

import lombok.NonNull;
import lombok.Value;
import toilari.otlite.world.entities.characters.Character;

import java.util.Objects;

/**
 * Määrittelee pelimaailman ruudun tyypin. Kustakin tyypistä on vain yksi
 * immutaabeli instanssi.
 */
@Value
public class Tile {
    /**
     * Määrittää kohdellaanko ruutua seinänä. (hahmot eivät voi liikkua ruutuun joka
     * on seinä)
     *
     * @return <code>true</code> jos ruutu on seinä, muutoin <code>false</code>
     */
    boolean wall;
    char symbol;
    @NonNull String id;

    /**
     * Kutsutaan kun hahmo astuu ruutuun.
     *
     * @param x         ruudun x-koordinaatti
     * @param y         ruudun y-koordinaatti
     * @param character hahmo joka juuri astui ruutuun
     * @throws NullPointerException jos <code>character</code> on null
     */
    public void onCharacterEnter(int x, int y, @NonNull Character character) {
    }

    /**
     * Kutsutaan kun hahmo astuu pois ruudusta.
     *
     * @param x         ruudun x-koordinaatti
     * @param y         ruudun y-koordinaatti
     * @param character hahmo joka juuri astui pois ruudusta
     * @throws NullPointerException jos <code>character</code> on null
     */
    public void onCharacterExit(int x, int y, @NonNull Character character) {
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
