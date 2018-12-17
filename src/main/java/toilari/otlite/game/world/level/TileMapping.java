package toilari.otlite.game.world.level;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * Hakutaulu joka mahdollistaa ruututyyppien haun joko pysyvän ID:n tai
 * väliaikaisen indeksin avulla. Pysyvä ID on jokaiselle tilelle tilen
 * määrittelytiedostossa asetettu yksilöllinen merkkijonomuotoinen nimike, kun
 * taas indeksi on järjestysnumero, joka riippuu järjestyksestä jossa
 * ruututyypit ladataan.
 */
public class TileMapping {
    @NonNull private final Map<String, Byte> idToIndex = new HashMap<>();
    @NonNull private final Map<Byte, Tile> indexToTile = new HashMap<>();

    public Map<String, Byte> getMapping() {
        return this.idToIndex;
    }

    /**
     * Luo hakutaulun johon lisätään annetulla DAO:lla löydetyt ruututyyppien
     * määrittelyt.
     *
     * @param dao DAO jolla ruututyypit haetaan
     * @throws NullPointerException jos dao on null
     */
    public TileMapping(@NonNull IGetAllDAO<Tile> dao) {
        val loaded = dao.getAll();
        byte index = 0;
        for (val tile : loaded) {
            this.idToIndex.put(tile.getId(), index);
            this.indexToTile.put(index, tile);
            index++;
        }
    }

    /**
     * Luo uuden hakutaulun.
     *
     * @param dao     dao jolla ruututyypit ladataan
     * @param mapping olemassaoleva hakutaulu jolla indeksit määritetään
     */
    public TileMapping(@NonNull IGetAllDAO<Tile> dao, Map<String, Byte> mapping) {
        for (val entry : mapping.entrySet()) {
            this.idToIndex.put(entry.getKey(), entry.getValue());
        }

        val loaded = dao.getAll();
        for (val tile : loaded) {
            val index = this.idToIndex.get(tile.getId());
            if (index != null) {
                this.indexToTile.put(index, tile);
            }
        }
    }

    /**
     * Palauttaa indeksin tilelle ID:n perusteella.
     *
     * @param id etsittävän tilen ID
     * @return -1 jos tileä annetulla ID:llä ei löydy, muutoin etsityn tilen indeksi
     * @throws NullPointerException jos id on null
     */
    public byte getIndex(@NonNull String id) {
        val index = this.idToIndex.get(id);
        return index == null ? (byte) -1 : index;
    }

    /**
     * Etsii tilen ID:n perusteella.
     *
     * @param id etsittävän tilen ID
     * @return <code>null</code> jos tileä ei löydy, muutoin etsitty tile
     * @throws NullPointerException jos id on null
     */
    public Tile getTile(@NonNull String id) {
        val index = getIndex(id);
        return getTile(index);
    }

    /**
     * Etsii tilen indeksin perusteella.
     *
     * @param index etsittävän tilen indeksi
     * @return <code>null</code> jos tileä ei löydy, muutoin etsitty tile
     */
    public Tile getTile(byte index) {
        return this.indexToTile.get(index);
    }

    /**
     * Kertoo hakutaulussa olevien ruututyypin määrittelyiden lukumäärän.
     *
     * @return montako määrittelyä hakutaulussa on
     */
    public int getCount() {
        return this.indexToTile.size();
    }
}
