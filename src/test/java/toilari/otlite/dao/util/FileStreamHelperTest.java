package toilari.otlite.dao.util;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStreamHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");

    private Path editable = ROOT.resolve("editable.file");
    private Path empty = ROOT.resolve("empty.file");

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createFile(this.editable);
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void openForReadingThrowsIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> FileStreamHelper.openForReading(null));
        assertThrows(NullPointerException.class, () -> FileStreamHelper.openForReading(this.editable, (OpenOption[]) null));
    }

    @Test
    void openForReadingOpensExistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForReading(this.editable));
    }

    @Test
    void openForReadingFailsForNonexistingFile() {
        assertThrows(IOException.class, () -> FileStreamHelper.openForReading(this.empty));
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void openForWritingThrowsIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> FileStreamHelper.openForWriting(null));
        assertThrows(NullPointerException.class, () -> FileStreamHelper.openForWriting(this.editable, (OpenOption[]) null));
    }

    @Test
    void openForWritingOpensExistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForWriting(this.editable));
    }

    @Test
    void openForWritingDoesNotFailForNonexistingFile() {
        assertDoesNotThrow(() -> FileStreamHelper.openForWriting(this.empty, StandardOpenOption.CREATE));
    }


    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(this.editable);
        Files.deleteIfExists(this.empty);
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }
}