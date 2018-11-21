package toilari.otlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;
import toilari.otlite.dao.util.FileHelper;
import toilari.otlite.game.world.Tile;

/**
 * Testaa TileDAO-luokan toimintaa.
 */
class TileDAOTest {

    private static final Path ROOT = Paths.get("target/test-temp/content/tiles/").normalize();

    private static final String[][] TILE_DEFINITIONS = { { "wall.json", "{\"id\":\"wall\",\"wall\":true,\"symbol\":\"#\"}" }, { "floor.json", "{\"id\":\"floor\",\"wall\":false,\"symbol\":\".\"}" }, { "hole.json", "{\"id\":\"hole\",\"wall\":false,\"symbol\":\" \"}" }, { "invalid.json", "{this_is_not_a_field:wtf,\"wall\":true,symbol:,,:asd}" } };
    private static final Tile[] CORRECT_TILES = { new Tile(true, '#', "wall"), new Tile(false, '.',
            "floor"), new Tile(false, ' ', "hole") };

    private TileDAO dao;

    /**
     * Luodaan testien vaatimat hakemistot ja .json-tiedostot.
     * 
     * @throws IOException jos kansioiden tai .json tiedostojen luonti epäonnistuu
     */
    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);

        for (val definition : TILE_DEFINITIONS) {
            Files.write(ROOT.resolve(definition[0]), definition[1].getBytes());
        }
    }

    /**
     * Luodaan testien vaatimat .json-tiedostot ennen kunkin testin suorittamista.
     */
    @BeforeEach
    void beforeEach() {
        this.dao = new TileDAO("target/test-temp/content/tiles/");
        this.dao.discoverAndLoadAll();
    }

    /**
     * Testaa että kaikki määritetyt ruudut löydetään.
     */
    @Test
    void tileDAOHasAllDefinedTiles() {
        assertEquals(CORRECT_TILES.length, this.dao.getTiles().length);
    }

    /**
     * Testaa että kaikki määritetyt ruudut löytyvät oikeilla kenttien arvoilla.
     */
    @Test
    void tileDAOHasCorrectTiles() {
        val tiles = Arrays.asList(this.dao.getTiles());
        for (val correct : CORRECT_TILES) {
            assertTrue(tiles.contains(correct));
        }
    }

    /**
     * Testaa ettei DAO:sta löydy null-tilejä.
     */
    @Test
    void tileDAOHasNoNullTiles() {
        for (val t : this.dao.getTiles()) {
            assertNotNull(t);
        }
    }

    /**
     * Testaa että {@link TileDAO#tryLoad(Path)} palauttaa <code>null</code> kun
     * tiedostoa ei löydy.
     */
    @Test
    void tryLoadReturnsNullOnFileThatDoesNotExist() {
        assertNull(this.dao.tryLoad(Paths.get("No/This/Path/Does/Not/Exist/Either.nope")));
    }

    /**
     * Poistetaan väliaikaiset tiedostot.
     */
    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }
}
