package toilari.otlite.dao;

import lombok.NonNull;
import toilari.otlite.game.world.Tile;

/**
 * Rajapinta ruututyyppien lataamiseen.
 */
public interface ITileDAO {
    /**
     * Hakee kaikki ladatut ruututyyppien määrittelyt. Implementaation tulee varmistaa ettei palautettu taulukko ole
     * <code>null</code> ja ettei taulukko sisällä alkioita joiden arvo on <code>null</code>.
     *
     * @return kaikki ladatut ruututyypit. Palauttaa tyhjän taulukon jos ruututyyppejä ei ole.
     */
    @NonNull Tile[] getTiles();
}
