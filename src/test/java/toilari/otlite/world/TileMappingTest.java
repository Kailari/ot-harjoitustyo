package toilari.otlite.world;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toilari.otlite.io.dao.ITileDAO;

import static org.junit.jupiter.api.Assertions.*;

class TileMappingTest {
    private static ITileDAO dao;
    private TileMapping mapping;

    /**
     * Luo testien tarvitsemat ruututyypit.
     */
    @BeforeAll
    static void beforeAll() {
        dao = new TestTileDAO();
    }

    /**
     * Luo uuden ruututyyppien hakutaulun testattavaksi.
     */
    @BeforeEach
    void beforeEach() {
        this.mapping = new TileMapping(dao);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileMappingsCtorFailsForNullDAO() {
        assertThrows(NullPointerException.class, () -> new TileMapping(null));
    }

    @Test
    void tileMappingHasCorrectNumberOfTiles() {
        assertEquals(4, this.mapping.getCount());
    }

    @Test
    void tileMappingReturnsCorrectTileForID() {
        for (val tile : dao.getTiles()) {
            assertEquals(tile, this.mapping.getTile(tile.getId()));
        }
    }

    @Test
    void tileMappingRetunsNullForUnknownID() {
        assertNull(this.mapping.getTile("THIS_ID_DOES_NOT_EXIST"));
    }

    @Test
    void tileMappingRetunsMinusOneForUnknownID() {
        assertEquals(-1, this.mapping.getIndex("THIS_ID_DOES_NOT_EXIST"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileMappingThrowsIfIndexIDIsNull() {
        assertThrows(NullPointerException.class, () -> this.mapping.getIndex(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileMappingThrowsIfTileIDIsNull() {
        assertThrows(NullPointerException.class, () -> this.mapping.getTile(null));
    }

    private static class TestTileDAO implements ITileDAO {
        private static final Tile A = new Tile(true, 'a', "A");
        private static final Tile B = new Tile(false, 'b', "B");
        private static final Tile C = new Tile(false, 'c', "C");
        private static final Tile D = new Tile(true, 'd', "D");

        @NonNull
        @Override
        public Tile[] getTiles() {
            return new Tile[]{A, B, C, D};
        }
    }
}
