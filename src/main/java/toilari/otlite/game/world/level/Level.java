package toilari.otlite.game.world.level;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Yksi kartta pelimaailmassa.
 */
@Slf4j
public class Level {
    private static final Tile OUT_OF_BOUNDS_TILE = new NormalTile(true, true, 0, "__out_of_bounds");
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

        this.width = width;
        this.height = height;
        this.tiles = new byte[width * height];
        if (tiles.length != this.tiles.length) {
            LOG.error("Loaded level bounds ({}:{} => {} tiles) do not match with actual size! ({} tiles)", width, height, this.tiles.length, tiles.length);
        }

        System.arraycopy(tiles, 0, this.tiles, 0, Math.min(this.tiles.length, tiles.length));
    }

    /**
     * Palauttaa annetuissa koordinaateissa olevan ruututyypin.
     *
     * @param x ruudun x-koordinaatti
     * @param y ruudun y-koordinaatti
     * @return annetuissa koordinaateissa olevan ruudun tyyppi
     */
    @NonNull
    public Tile getTileAt(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return Level.OUT_OF_BOUNDS_TILE;
        }

        val index = (y * this.width) + x;
        val tileIndex = this.tiles[index];
        val tile = this.tileMappings.getTile(tileIndex);
        if (tile == null) {
            LOG.error("Unknown tile index {} detected at ({}, {})", tileIndex, x, y);
            return new NormalTile(false, false, 0, "__null");
        }

        return tile;
    }

    /**
     * Tarkistaa ovatko koordinaatit kartan rajojen sisäpuolella.
     *
     * @param x tarkistettava x-koordinaatti.
     * @param y tarkistettava y-koordinaatti.
     * @return <code>true</code> jos koordinaatit ovat kartan sisällä, muulloin <code>false</code>
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < this.width && y >= 0 && y < this.height;
    }
}