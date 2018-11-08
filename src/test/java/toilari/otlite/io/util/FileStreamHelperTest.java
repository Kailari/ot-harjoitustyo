package toilari.otlite.io.util;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testaa että tiedostovirtojen apukomennot toimivat oikein tiedostoilla.
 */
class FileStreamHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");

    private Path editable = ROOT.resolve("editable.file");
    private Path empty = ROOT.resolve("empty.file");

    /**
     * Luo juurihakemiston johon testien tarvitsemat tiedostot voidaan luoda.
     *
     * @throws IOException Jos hakemiston luominen epäonnistuu
     */
    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
    }

    /**
     * Luo testien tarvitsemat väliaikaiset tiedostot.
     *
     * @throws IOException Jos tiedostojen luonti epäonnistuu
     */
    @BeforeEach
    void beforeEach() throws IOException {
        Files.createFile(this.editable);
    }

    /**
     * Testaa että tiedoston avaaminen lukemista varten onnistuu.
     */
    @Test
    void openForReadingOpensExistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForReading(this.editable));
    }

    /**
     * Testaa että tiedoston avaaminen epäonnistuu jos tiedostoa ei ole.
     */
    @Test
    void openForReadingFailsForNonexistingFile() {
        assertThrows(IOException.class, () -> FileStreamHelper.openForReading(this.empty));
    }

    /**
     * Testaa että tiedoston avaaminen lukemista varten onnistuu.
     */
    @Test
    void openForWritingOpensExistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForWriting(this.editable));
    }

    /**
     * Testaa että tiedoston avaaminen onnistuu vaikka tiedostoa ei ole.
     */
    @Test
    void openForWritingDoesNotFailForNonexistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForWriting(this.empty, StandardOpenOption.CREATE));
    }


    /**
     * Poistaa testien tarvitsemat väliaikaiset tiedostot.
     *
     * @throws IOException Jos tiedostojen poisto epäonnistuu
     */
    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(this.editable);
        Files.deleteIfExists(this.empty);
    }

    /**
     * Siivoaa luodut väliaikaiset testeissä tarvitut tiedostot.
     */
    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }
}