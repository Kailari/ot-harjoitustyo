package toilari.otlite.io.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testaa että tiedostovirtojen apukomennot toimivat oikein tiedostoilla.
 */
public class FileStreamHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");

    private Path editable = ROOT.resolve("editable.file");
    private Path empty = ROOT.resolve("empty.file");

    /**
     * Luo juurihakemiston johon testien tarvitsemat tiedostot voidaan luoda.
     * 
     * @throws IOException Jos hakemiston luominen epäonnistuu
     */
    @BeforeAll
    public static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
    }

    /**
     * Luo testien tarvitsemat väliaikaiset tiedostot.
     * 
     * @throws IOException Jos tiedostojen luonti epäonnistuu
     */
    @BeforeEach
    public void beforeEach() throws IOException {
        Files.createFile(editable);
    }

    /**
     * Testaa että tiedoston avaaminen lukemista varten onnistuu.
     */
    @Test
    public void openForReadingOpensExistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForReading(editable));
    }

    /**
     * Testaa että tiedoston avaaminen epäonnistuu jos tiedostoa ei ole.
     */
    @Test
    public void openForReadingFailsForNonexistingFile() {
        assertThrows(IOException.class, () -> FileStreamHelper.openForReading(empty));
    }

    /**
     * Testaa että tiedoston avaaminen lukemista varten onnistuu.
     */
    @Test
    public void openForWritingOpensExistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForWriting(editable));
    }

    /**
     * Testaa että tiedoston avaaminen onnistuu vaikka tiedostoa ei ole.
     */
    @Test
    public void openForWritingDoesNotFailForNonexistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForWriting(empty, StandardOpenOption.CREATE));
    }



    /**
     * Poistaa testien tarvitsemat väliaikaiset tiedostot.
     * 
     * @throws IOException Jos tiedostojen poisto epäonnistuu
     */
    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(editable);
        Files.deleteIfExists(empty);
    }

    /**
     * Siivoaa luodut väliaikaiset testeissä tarvitut tiedostot.
     */
    @AfterAll
    public static void afterAll() {
        try {
            Files.walk(ROOT)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException ignored) {
        }
    }
}