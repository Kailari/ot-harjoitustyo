package toilari.otlite.world;

import java.util.Map;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.io.dao.TileDAO;

import java.util.HashMap;

/**
 * Hakutaulu joka mahdollistaa ruututyyppien haun joko pysyvän ID:n tai
 * väliaikaisen indeksin avulla. Pysyvä ID on jokaiselle tilelle tilen
 * määrittelytiedostossa asetettu yksilöllinen merkkijonomuotoinen nimike, kun
 * taas indeksi on järjestysnumero, joka riippuu järjestyksestä jossa
 * ruututyypit ladataan.
 */
public class TileMapping {
    @NonNull private final Map<String, Byte> idToIndex = new HashMap<>();
    @NonNull private Tile[] tiles;

    /**
     * Luo tyhjän hakutaulun, jossa ei ole yhtäkään ruututyypin määrittelyä.
     */
    public TileMapping() {
        this.tiles = new Tile[0];
    }

    /**
     * Luo hakutaulun johon lisätään annetulla DAO:lla löydetyt ruututyyppien
     * määrittelyt. Määrittelyt ladataan kutsumalla {@link #init(TileDAO)}, joten
     * <code>init()</code> ei enää tarvitse kutsua mikäli tätä konstruktoria
     * käytetään.
     * 
     * @param dao DAO jolla ruututyypit haetaan
     */
    public TileMapping(TileDAO dao) {
        this();
        init(dao);
    }

    /**
     * Lataa ruututyypit DAO:sta ja täydentää hakutaulun.
     * 
     * @param dao DAO jolla ruututyypit haetaan.
     * @throws IllegalStateException jos
     */
    public void init(TileDAO dao) {
        if (this.tiles.length != 0) {
            throw new IllegalStateException("TileMapping must be initialized exactly once!");
        }

        this.tiles = dao.getTiles();
        for (byte i = 0; i < this.tiles.length; i++) {
            this.idToIndex.put(this.tiles[i].getId(), i);
        }
    }

    /**
     * Palauttaa indeksin tilelle ID:n perusteella.
     * 
     * @param id etsittävän tilen ID
     * @return -1 jos tileä annetulla ID:llä ei löydy, muutoin etsityn tilen indeksi
     */
    public byte getIndex(String id) {
        val index = this.idToIndex.get(id);
        return index == null ? (byte) -1 : index;
    }

    /**
     * Etsii tilen ID:n perusteella.
     * 
     * @param id etsittävän tilen ID
     * @return <code>null</code> jos tileä ei löydy, muutoin etsitty tile
     */
    public Tile getTile(String id) {
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
        return index >= 0 && index < this.tiles.length ? this.tiles[index] : null;
    }
}
