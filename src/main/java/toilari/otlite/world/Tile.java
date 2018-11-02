package toilari.otlite.world;

import lombok.Getter;
import toilari.otlite.world.characters.ICharacter;

/**
 * Määrittelee pelimaailman ruudun tyypin. Kustakin tyypistä on vain yksi
 * immutaabeli instanssi.
 * 
 * @see Tileset
 */
public class Tile {
    /**
     * Määrittää kohdellaanko ruutua seinänä. (hahmot eivät voi liikkua ruutuun joka
     * on seinä)
     * 
     * @return <code>true</code> jos ruutu on seinä, muutoin <code>false</code>
     */
    @Getter private final boolean wall;
    private final char symbol;

    /**
     * Rakentaa uuden ruututyypin.
     * 
     * @param wall   onko tämä ruutu seinä?
     * @param symbol ruudun symboli
     */
    public Tile(boolean wall, char symbol) {
        this.wall = wall;
        this.symbol = symbol;
    }

    /**
     * Kutsutaan kun hahmo astuu ruutuun.
     * 
     * @param x         ruudun x-koordinaatti
     * @param y         ruudun y-koordinaatti
     * @param character hahmo joka juuri astui ruutuun
     */
    public void onCharacterEnter(int x, int y, ICharacter character) {
        assert x == character.getX() && y == character.getY();
    }

    /**
     * Kutsutaan kun hahmo astuu pois ruudusta.
     * 
     * @param x         ruudun x-koordinaatti
     * @param y         ruudun y-koordinaatti
     * @param character hahmo joka juuri astui pois ruudusta
     */
    public void onCharacterExit(int x, int y, ICharacter character) {
    }

    @Override
    public String toString() {
        return String.valueOf(this.symbol);
    }
}
