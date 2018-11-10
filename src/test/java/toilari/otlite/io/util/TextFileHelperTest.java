package toilari.otlite.io.util;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testaa että tekstitiedostojen apukomennot toimivat oikein.
 */
class TextFileHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");
    private static final List<String> EDITABLE_CONTENT = Arrays.asList(
        "This is a test.", "Every line here represents a single", "line written into the file. There is",
        "no actual reason for this text to be", "this long, but as I've been writing",
        "unit tests the whole friggin' day,", "I wanted to take a moment to write", "something completely different.",
        "ps. I'm being held hostage, please,", "HELP");

    private Path editable = ROOT.resolve("editable.txt");
    private Path empty = ROOT.resolve("empty.txt");

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
        Files.write(this.editable, EDITABLE_CONTENT, Charset.forName("UTF-8"));
    }

    /**
     * Testaa että tiedoston avaaminen lukemista varten onnistuu.
     */
    @Test
    void getReaderOpensExistingFile() {
        assertDoesNotThrow(() -> TextFileHelper.getReader(this.editable));
    }

    /**
     * Testaa että tiedoston avaaminen epäonnistuu jos tiedostoa ei ole.
     */
    @Test
    void getReaderFailsForNonexistingFile() {
        assertThrows(IOException.class, () -> TextFileHelper.getReader(this.empty));
    }

    /**
     * Testaa että tiedoston avaaminen lukemista varten onnistuu.
     */
    @Test
    void getWriterOpensExistingFile() {
        assertDoesNotThrow(() -> TextFileHelper.getWriter(this.editable));
    }

    /**
     * Testaa että tiedoston avaaminen onnistuu vaikka tiedostoa ei ole.
     */
    @Test
    void openForWritingDoesNotFailForNonexistingFile() {
        assertDoesNotThrow(() -> TextFileHelper.getWriter(this.empty));
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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void afterAll() {
        try {
            Files.walk(ROOT)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException ignored) {
        }
    }
}