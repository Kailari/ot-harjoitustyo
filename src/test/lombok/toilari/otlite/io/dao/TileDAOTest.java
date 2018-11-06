package toilari.otlite.io.dao;

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
import toilari.otlite.io.util.FileHelper;
import toilari.otlite.world.Tile;

/**
 * Testaa TileDAO-luokan toimintaa.
 */
public class TileDAOTest {

    private static final Path ROOT = Paths.get("target/test-temp/content/tiles/").normalize();

    private static final String[][] TILE_DEFINITIONS = { { "wall.json", "{id:'wall',wall:true,symbol:'#'}" }, { "floor.json", "{id:'floor',wall:false,symbol:'.'}" }, { "hole.json", "{id:'hole',wall:false,symbol:' '}" }, { "invalid.json", "{this_is_not_a_field:wtf,symbol:,,:asd}" } };
    private static final Tile[] CORRECT_TILES = { new Tile(true, '#', "wall"), new Tile(false, '.',
            "floor"), new Tile(false, ' ', "hole") };

    private TileDAO dao;

    /**
     * Luodaan testien vaatimat hakemistot ja .json-tiedostot.
     * 
     * @throws IOException jos kansioiden tai .json tiedostojen luonti epäonnistuu
     */
    @BeforeAll
    public static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);

        for (val definition : TILE_DEFINITIONS) {
            Files.write(ROOT.resolve(definition[0]), definition[1].getBytes());
        }
    }

    /**
     * Luodaan testien vaatimat .json-tiedostot ennen kunkin testin suorittamista.
     */
    @BeforeEach
    public void beforeEach() {
        dao = new TileDAO("target/test-temp/content");
    }

    /**
     * Testaa että kaikki määritetyt ruudut löydetään.
     */
    @Test
    public void tileDAOHasAllDefinedTiles() {
        assertEquals(CORRECT_TILES.length, dao.getTiles().length);
    }

    /**
     * Testaa että kaikki määritetyt ruudut löytyvät oikeilla kenttien arvoilla.
     */
    @Test
    public void tileDAOHasCorrectTiles() {
        val tiles = Arrays.asList(dao.getTiles());
        for (int i = 0; i < CORRECT_TILES.length; i++) {
            assertTrue(tiles.contains(CORRECT_TILES[i]));
        }
    }

    /**
     * Testaa ettei DAO:sta löydy null-tilejä.
     */
    @Test
    public void tileDAOHasNoNullTiles() {
        for (val t : dao.getTiles()) {
            assertNotNull(t);
        }
    }

    /**
     * Testaa että {@link TileDAO#tryLoad(Path)} palauttaa <code>null</code> kun
     * tiedostoa ei löydy.
     */
    @Test
    public void tryLoadReturnsNullOnFileThatDoesNotExist() {
        assertNull(dao.tryLoad(Paths.get("No/This/Path/Does/Not/Exist/Either.nope")));
    }

    /**
     * Poistetaan väliaikaiset tiedostot.
     */
    @AfterAll
    public static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }
}
