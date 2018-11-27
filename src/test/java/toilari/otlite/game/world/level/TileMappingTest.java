package toilari.otlite.game.world.level;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.IGetAllDAO;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class TileMappingTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsIfDAOIsNull() {
        assertThrows(NullPointerException.class, () -> new TileMapping(null));
    }

    @Test
    void tileMappingHasCorrectNumberOfTiles() {
        val mapping = new TileMapping(new TestTileDAO());
        assertEquals(4, mapping.getCount());
    }

    @Test
    void tileMappingReturnsCorrectTileForID() {
        val dao = new TestTileDAO();
        val mapping = new TileMapping(dao);

        for (val tile : dao.getAll()) {
            assertEquals(tile, mapping.getTile(tile.getId()));
        }
    }

    @Test
    void tileMappingRetunsNullForUnknownID() {
        val dao = new TestTileDAO();
        val mapping = new TileMapping(dao);

        assertNull(mapping.getTile("THIS_ID_DOES_NOT_EXIST"));
    }

    @Test
    void tileMappingRetunsMinusOneForUnknownID() {
        val dao = new TestTileDAO();
        val mapping = new TileMapping(dao);

        assertEquals(-1, mapping.getIndex("THIS_ID_DOES_NOT_EXIST"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileMappingThrowsIfIndexIDIsNull() {
        val dao = new TestTileDAO();
        val mapping = new TileMapping(dao);

        assertThrows(NullPointerException.class, () -> mapping.getIndex(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileMappingThrowsIfTileIDIsNull() {
        val dao = new TestTileDAO();
        val mapping = new TileMapping(dao);

        assertThrows(NullPointerException.class, () -> mapping.getTile(null));
    }

    private static class TestTileDAO implements IGetAllDAO<Tile> {
        private static final Tile A = new NormalTile(true, false, 0, "A");
        private static final Tile B = new NormalTile(false, false, 1, "B");
        private static final Tile C = new NormalTile(false, false, 2, "C");
        private static final Tile D = new NormalTile(true, false, 3, "D");

        @Override
        public Collection<Tile> getAll() {
            return Arrays.asList(A, B, C, D);
        }
    }
}
