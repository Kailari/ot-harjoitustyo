package toilari.otlite.dao.util.database;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.database.Database;
import toilari.otlite.dao.util.FileHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testaa että tietokanta toimii odotetulla tavalla.
 */
class DatabaseTest {
    private static final Path PERSISTENT_ROOT = Paths.get("src/test/resources/");
    private static final Path ROOT = Paths.get("target/test-temp/");

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(ROOT);
        Files.copy(PERSISTENT_ROOT.resolve("test.db"), ROOT.resolve("test.db"), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Testaa että uuden tietokannan luominen luo tiedoston tietokannalle jos sitä ei ole jo olemassa.
     */
    @Test
    void databaseConstructorCreatesFileIfItDoesNotExist() {
        val path = ROOT.resolve("does_not_exist.db").toString();

        assertDoesNotThrow(() -> new Database(path));
        assertTrue(FileHelper.fileExists(path));
    }

    /**
     * Testaa että olemassaolevan tietokannan avaaminen onnistuu.
     */
    @Test
    void databaseConstructorDoesNotThrowForFileThatExists() {
        val path = ROOT.resolve("test.db").toString();
        assertDoesNotThrow(() -> new Database(path));
    }

    /**
     * Testaa että olemassaolevalle tiedostolle luotu tietokanta palauttaa validin yhteyden.
     */
    @Test
    void succesfullyConstructedDatabaseReturnsValidConnection() {
        val path = ROOT.resolve("test.db").toString();
        assertDoesNotThrow(() -> {
                val db = new Database(path);
                val conn = db.getConnection();
                assertNotNull(conn);
            }
        );
    }

    @AfterEach
    void afterEach() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }
}
