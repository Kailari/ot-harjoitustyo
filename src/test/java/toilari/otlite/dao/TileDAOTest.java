package toilari.otlite.dao;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.util.FileHelper;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testaa TileDAO-luokan toimintaa.
 */
class TileDAOTest {

    private static final Path ROOT = Paths.get("target/test-temp/content/tiles/").normalize();

    private static final String[][] TILE_DEFINITIONS = {
        {"wall.json", "{\"id\":\"wall\",\"wall\":true,\"symbol\":\"#\"}"},
        {"floor.json", "{\"id\":\"floor\",\"wall\":false,\"symbol\":\".\"}"},
        {"hole.json", "{\"id\":\"hole\",\"wall\":false,\"symbol\":\" \"}"},
        {"invalid.json", "{this_is_not_a_field:wtf,\"wall\":true,symbol:,,:asd}"}
    };
    private static final Tile[] CORRECT_TILES = {
        new NormalTile(true, false, 0, "wall"),
        new NormalTile(false, false, 1, "floor"),
        new NormalTile(false, true, 2, "hole")
    };

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);

        for (val definition : TILE_DEFINITIONS) {
            Files.write(ROOT.resolve(definition[0]), definition[1].getBytes());
        }
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsWithNullRoot() {
        assertThrows(NullPointerException.class, () -> new TileDAO(null));
    }

    @Test
    void tileDAOHasAllDefinedTiles() {
        val dao = new TileDAO("target/test-temp/content/tiles/");
        dao.discoverAndLoadAll();

        assertEquals(CORRECT_TILES.length, dao.getAll().size());
    }

    @Test
    void tileDAOHasCorrectTiles() {
        val dao = new TileDAO("target/test-temp/content/tiles/");
        dao.discoverAndLoadAll();

        val tiles = dao.getAll();
        for (val correct : CORRECT_TILES) {
            assertTrue(tiles.contains(correct));
        }
    }

    @Test
    void tileDAOHasNoNullTiles() {
        val dao = new TileDAO("target/test-temp/content/tiles/");
        dao.discoverAndLoadAll();

        for (val t : dao.getAll()) {
            assertNotNull(t);
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tryLoadThrowsWithNullPath() {
        val dao = new TileDAO("target/test-temp/content/tiles/");
        dao.discoverAndLoadAll();

        assertThrows(NullPointerException.class, () -> dao.load(null));
    }

    @Test
    void tryLoadReturnsNullOnFileThatDoesNotExist() {
        val dao = new TileDAO("target/test-temp/content/tiles/");
        dao.discoverAndLoadAll();

        assertNull(dao.load(Paths.get("No/This/Path/Does/Not/Exist/Either.nope")));
    }
}
