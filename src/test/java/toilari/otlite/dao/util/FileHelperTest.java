package toilari.otlite.dao.util;

import lombok.val;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testaa FileHelper-luokan toimintaa.
 */
class FileHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");
    private static final Path INVALID = ROOT.resolve("vaara.txt/");

    // NOTE: Must end to a newline in order for tests to work
    private static final String[] READABLE_CONTENTS = {"This is a test.\n", "This\nis\na\ntest\n", "Lorem ipsum etc.\n", "{\n\tfield: \"Test String\",\n\tbool: true\n}\n"};
    private static final String[] READABLE_EXTENSIONS = {"txt", "txt", "txt", "json"};

    private Path[] readableFiles;

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(INVALID);

        this.readableFiles = new Path[READABLE_CONTENTS.length];
        for (int i = 0; i < READABLE_CONTENTS.length; i++) {
            this.readableFiles[i] = Files.write(ROOT.resolve("readable_" + i + "." + READABLE_EXTENSIONS[i]),
                READABLE_CONTENTS[i].getBytes());
        }
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void discoverFilesThrowsIfParametersAreNull() {
        assertThrows(NullPointerException.class, () -> FileHelper.discoverFiles(null, ".test"));
        assertThrows(NullPointerException.class, () -> FileHelper.discoverFiles(ROOT, null));
    }

    @Test
    void discoverFilesFindsOnlyTxtFilesWhenExtensionIsTxt() {
        List<Path> files = FileHelper.discoverFiles(ROOT, "txt").collect(Collectors.toList());
        assertTrue(files.stream().allMatch(p -> p.toString().toLowerCase().endsWith(".txt")));
    }

    @Test
    void discoverFilesFindsCorrectNumberOfFiles() {
        assertEquals(READABLE_CONTENTS.length - 1, FileHelper.discoverFiles(ROOT, "txt").count());
    }

    @Test
    void discoverFilesFindsOnlyJsonFilesWhenExtensionIsJson() {
        assertTrue(FileHelper.discoverFiles(ROOT, "json").allMatch(s -> s.toString().toLowerCase().endsWith(".json")));
    }

    @Test
    void discoverFilesReturnsEmptyStreamWhenRootFileDoesNotExist() {
        val files = FileHelper.discoverFiles(Paths.get("this/path/does/hopefully/not/exist/"), "txt");
        assertNotNull(files);
        assertEquals(0, files.count());
    }

    @Test
    void discoverFilesDoesNotMatchFolders() {
        assertTrue(FileHelper.discoverFiles(ROOT, "txt").noneMatch(path -> path.toFile().isDirectory()));
    }

    @Test
    void discoverFilesThrowsIfExtensionStartsWithComma() {
        assertThrows(IllegalArgumentException.class, () -> FileHelper.discoverFiles(Paths.get("ignored"), ".extension"));
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void deleteDirectoryAndChildrenThrowsIfPathIsNull() {
        assertThrows(NullPointerException.class, () -> FileHelper.deleteDirectoryAndChildren(null));
    }

    @Test
    void deleteDirectoryAndChildrenReturnsFalseWhenTryingToRemoveSingleFile() {
        assertFalse(FileHelper.deleteDirectoryAndChildren(this.readableFiles[0]));
    }

    @Test
    void deleteDirectoryAndChildrenDoesNotRemoveSingleFile() {
        FileHelper.deleteDirectoryAndChildren(this.readableFiles[0]);
        assertTrue(Files.exists(this.readableFiles[0]));
    }

    @Test
    void deleteDirectoryAndChildrenReturnsTrueOnValidPath() {
        assertTrue(FileHelper.deleteDirectoryAndChildren(ROOT));
    }

    @Test
    void deleteDirectoryAndChildrenReallyDeletes() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
        assertFalse(Files.exists(ROOT));
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void createFileThrowsIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> FileHelper.createFile(null, "file.test"));
        assertThrows(NullPointerException.class, () -> FileHelper.createFile(ROOT, null));
        assertThrows(NullPointerException.class, () -> FileHelper.createFile(null));
    }

    @Test
    void createFileCanCreateFilesToWorkingDirectory() {
        assertTrue(FileHelper.createFile("test.file") && Files.exists(Paths.get("./test.file")));
    }

    @Test
    void createFileCreatesFile() {
        assertTrue(FileHelper.createFile(ROOT, "test.file") && Files.exists(ROOT.resolve("test.file")));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void fileExistsThrowsIfPathIsNull() {
        assertThrows(NullPointerException.class, () -> FileHelper.fileExists(null));
    }


    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(INVALID);

        for (Path path : this.readableFiles) {
            Files.deleteIfExists(path);
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            Files.deleteIfExists(Paths.get("./test.file"));
            //noinspection ResultOfMethodCallIgnored
            Files.walk(ROOT).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ignored) {
        }
    }
}