package toilari.otlite.io.util;

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

    /**
     * Luo testeissä tarvittavia tiedostoja ja hakemistoja ennen testien
     * suorittamista.
     *
     * @throws IOException jos hakemistojen luonti epäonnistuu
     */
    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
    }

    /**
     * Luo testeissä tarvittavat väliaikaiset tiedostot.
     *
     * @throws IOException jos tiedostoon kirjoittaminen epäonnistuu
     */
    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(INVALID);

        this.readableFiles = new Path[READABLE_CONTENTS.length];
        for (int i = 0; i < READABLE_CONTENTS.length; i++) {
            this.readableFiles[i] = Files.write(ROOT.resolve("readable_" + i + "." + READABLE_EXTENSIONS[i]),
                    READABLE_CONTENTS[i].getBytes());
        }
    }

    /**
     * Testaa että {@link FileHelper#discoverFiles(Path, String)} palauttaa vain
     * asetetun tiedostopäätteen mukaisia polkuja kun pääte on .txt.
     */
    @Test
    void discoverFilesFindsOnlyTxtFilesWhenExtensionIsTxt() {
        List<Path> files = FileHelper.discoverFiles(ROOT, "txt").collect(Collectors.toList());
        assertTrue(files.stream().allMatch(p -> p.toString().toLowerCase().endsWith(".txt")));
    }

    /**
     * Testaa että {@link FileHelper#discoverFiles(Path, String)} löytää oikean
     * määrän tiedostoja.
     */
    @Test
    void discoverFilesFindsCorrectNumberOfFiles() {
        assertEquals(READABLE_CONTENTS.length - 1, FileHelper.discoverFiles(ROOT, "txt").count());
    }

    /**
     * Testaa että {@link FileHelper#discoverFiles(Path, String)} palauttaa vain
     * asetetun tiedostopäätteen mukaisia polkuja kun pääte on .json.
     */
    @Test
    void discoverFilesFindsOnlyJsonFilesWhenExtensionIsJson() {
        assertTrue(FileHelper.discoverFiles(ROOT, "json").allMatch(s -> s.toString().toLowerCase().endsWith(".json")));
    }

    /**
     * Testaa että {@link FileHelper#discoverFiles(Path, String)} palauttaa vain.
     */
    @Test
    void discoverFilesReturnsEmptyStreamWhenRootFileDoesNotExist() {
        val files = FileHelper.discoverFiles(Paths.get("this/path/does/hopefully/not/exist/"), "txt");
        assertNotNull(files);
        assertEquals(0, files.count());
    }

    /**
     * Testaa että {@link FileHelper#discoverFiles(Path, String)} ei palauta
     * polkuja jotka viittaavat kansioihin.
     */
    @Test
    void discoverFilesDoesNotMatchFolders() {
        assertTrue(FileHelper.discoverFiles(ROOT, "txt").noneMatch(path -> path.toFile().isDirectory()));
    }

    /**
     * Testaa että {@link FileHelper#discoverFiles(Path, String)} heittää virheen
     * jos tiedostopääte alkaa pisteellä.
     */
    @Test
    void discoverFilesThrowsIfExtensionStartsWithComma() {
        assertThrows(IllegalArgumentException.class, () -> FileHelper.discoverFiles(Paths.get("ignored"), ".extension"));
    }

    /**
     * Testaa että {@link FileHelper#deleteDirectoryAndChildren(Path)} palauttaa
     * false kun parametri ei osoita kansioon.
     */
    @Test
    void deleteDirectoryAndChildrenCannotRemoveSingleFile() {
        assertFalse(FileHelper.deleteDirectoryAndChildren(this.readableFiles[0]));
    }

    /**
     * Testaa että {@link FileHelper#deleteDirectoryAndChildren(Path)} hyväksyy
     * parametriksi vain kansioita. (palauttaa false)
     */
    @Test
    void deleteDirectoryAndChildrenFileNotRemoved() {
        FileHelper.deleteDirectoryAndChildren(this.readableFiles[0]);
        assertTrue(Files.exists(this.readableFiles[0]));
    }

    /**
     * Testaa että {@link FileHelper#deleteDirectoryAndChildren(Path)} palauttaa
     * true kun poistettava kansio on olemassa.
     */
    @Test
    void deleteDirectoryAndChildrenReturnsTrueOnValidPath() {
        assertTrue(FileHelper.deleteDirectoryAndChildren(ROOT));
    }

    /**
     * Testaa että {@link FileHelper#deleteDirectoryAndChildren(Path)} poistaa
     * annetun hakemiston.
     */
    @Test
    void deleteDirectoryAndChildrenReallyDeletes() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
        assertFalse(Files.exists(ROOT));
    }

    /**
     * Testaa että {@link FileHelper#createFile(Path, String)} luo tiedoston
     * onnistuneesti.
     */
    @Test
    void createFileCreatesFile() {
        assertTrue(FileHelper.createFile(ROOT, "test.file") && Files.exists(ROOT.resolve("test.file")));
    }

    /**
     * Siivoaa väliaikaiset tiedostot.
     *
     * @throws IOException jos tiedoston poistaminen epäonnistuu
     */
    @AfterEach
    void afterEach() throws IOException {
        Files.deleteIfExists(INVALID);

        for (Path path : this.readableFiles) {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Siivoaa luodut väliaikaiset testeissä tarvitut tiedostot.
     */
    @AfterAll
    static void afterAll() {
        try {
            //noinspection ResultOfMethodCallIgnored
            Files.walk(ROOT).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ignored) {
        }
    }
}