package toilari.otlite.dao.util;

import lombok.val;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFileHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");
    private static final List<String> EDITABLE_CONTENT = Arrays.asList(
        "This is a test.", "Every line here represents a single", "line written into the file. There is",
        "no actual reason for this text to be", "this long, but as I've been writing",
        "unit tests the whole friggin' day,", "I wanted to take a moment to write", "something completely different.",
        "ps. I'm being held hostage, please,", "HELP");

    private Path editable = ROOT.resolve("editable.txt");
    private Path empty = ROOT.resolve("empty.txt");

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Files.write(this.editable, EDITABLE_CONTENT, Charset.forName("UTF-8"));
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void getReaderThrowsIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> TextFileHelper.getReader(null));
        assertThrows(NullPointerException.class, () -> TextFileHelper.getReader(this.editable, (OpenOption[]) null));
    }

    @Test
    void getReaderOpensExistingFile() {
        assertDoesNotThrow(() -> TextFileHelper.getReader(this.editable));
    }

    @Test
    void getReaderFailsForNonexistingFile() {
        assertThrows(IOException.class, () -> TextFileHelper.getReader(this.empty));
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void getWriterThrowsIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> TextFileHelper.getWriter(null));
        assertThrows(NullPointerException.class, () -> TextFileHelper.getWriter(this.editable, (OpenOption[]) null));
    }

    @Test
    void getWriterOpensExistingFile() {
        assertDoesNotThrow(() -> TextFileHelper.getWriter(this.editable));
    }

    @Test
    void openForWritingDoesNotFailForNonexistingFile() {
        assertDoesNotThrow(() -> TextFileHelper.getWriter(this.empty));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void readFileToStringThrowsIfPathIsNull() {
        assertThrows(NullPointerException.class, () -> TextFileHelper.readFileToString(null));
    }

    @Test
    void readFileToStringReturnsCorrectStringForExistingFile() throws IOException {
        val string = TextFileHelper.readFileToString(this.editable.toString());
        val split = string.split("\n");
        for (int i = 0; i < Math.min(split.length, EDITABLE_CONTENT.size()); i++) {
            assertEquals(EDITABLE_CONTENT.get(i), split[i]);
        }
    }


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