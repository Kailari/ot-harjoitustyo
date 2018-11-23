package toilari.otlite.game.world.level;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;

/**
 * Yksi kartta pelimaailmassa.
 */
public class Level {
    @Getter private final int width;
    @Getter private final int height;

    @Getter(AccessLevel.NONE) private final TileMapping tileMappings;
    @Getter(AccessLevel.NONE) private final byte[] tiles;

    /**
     * Luo uuden "kartan" pelimaailmaan.
     *
     * @param width        kartan leveys
     * @param height       kartan korkeus
     * @param tileMappings hakutaulu indeksi-ruututyyppi -muunnoksille
     * @param tiles        kartan ruutujen ruututyyppien indeksit
     * @throws IllegalArgumentException mikäli annetut kartan mitat eivät täsmää
     *                                  kartan datataulukon kanssa
     */
    public Level(int width, int height, TileMapping tileMappings, byte[] tiles) {
        this.tileMappings = tileMappings;
        this.tiles = tiles;

        this.width = width;
        this.height = height;
        if (tiles.length != width * height) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Palauttaa annetuissa koordinaateissa olevan ruututyypin.
     *
     * @param x ruudun x-koordinaatti
     * @param y ruudun y-koordinaatti
     * @return annetuissa koordinaateissa olevan ruudun tyyppi
     * @throws IllegalArgumentException jos koordinaatit ovat kartan ulkopuolella
     */
    public Tile getTileAt(int x, int y) {
        val index = (y * this.width) + x;
        val tileIndex = this.tiles[index];
        val tile = this.tileMappings.getTile(tileIndex);
        if (tile == null) {
            throw new IllegalStateException("Unknown tile type detected at (" + x + ", " + y + ")");
        }

        return tile;
    }

    public boolean isWithinBounds(int newX, int newY) {
        return newX >= 0 && newX < this.width && newY >= 0 && newY < this.height;
    }
}