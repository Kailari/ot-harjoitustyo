package toilari.otlite.game.world.level;

import lombok.NonNull;

/**
 * Normaali ruututyyppi vailla mitään erikoista.
 */
public class NormalTile extends Tile {
    /**
     * Luo normaalin tappavan ruututyypin.
     *
     * @param wall      kohdellaanko ruutua seinänä
     * @param dangerous kohdellaanko ruutua vaarallisena
     * @param tileIndex ruudun ulkonäön indeksi
     * @param id        ruudun tunniste
     * @throws NullPointerException jos id on <code>null</code>
     */
    public NormalTile(boolean wall, boolean dangerous, int tileIndex, @NonNull String id) {
        super(wall, dangerous, tileIndex, id);
    }
}
