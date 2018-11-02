package toilari.otlite.io.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;

/**
 * Testaa IOHelper-luokan toimintaa.
 */
public class FileHelperTest {
    private static final Path ROOT = Paths.get("target/test-temp/");

    // NOTE: Must end to a newline in order for tests to work
    private static final String[] READABLE_CONTENTS = { "This is a test.\n", "This\nis\na\ntest\n", "Lorem ipsum etc.\n", "{\n\tfield: \"Test String\",\n\tbool: true\n}\n" };
    private static final String[] READABLE_EXTENSIONS = { "txt", "txt", "txt", "json" };

    private Path[] readableFiles;

    /**
     * Luo testeissä tarvittavia tiedostoja ja hakemistoja ennen testien
     * suorittamista.
     * 
     * @throws IOException jos hakemistojen luonti epäonnistuu
     */
    @BeforeAll
    public static void beforeAll() throws IOException {
        Files.createDirectories(ROOT);
        Files.createDirectories(ROOT.resolve("vaara.txt/"));
    }

    /**
     * Luo testeissä tarvittavat väliaikaiset tiedostot.
     * 
     * @throws IOException jos tiedostoon kirjoittaminen epäonnistuu
     */
    @BeforeEach
    public void beforeEach() throws IOException {
        readableFiles = new Path[READABLE_CONTENTS.length];
        for (int i = 0; i < READABLE_CONTENTS.length; i++) {
            readableFiles[i] = Files.write(ROOT.resolve("readable_" + i + READABLE_EXTENSIONS[i]), READABLE_CONTENTS[i].getBytes());
        }
    }

    /**
     * Testaa että {@link IOHelper#discoverFiles(String, String)} palauttaa vain
     * asetetun tiedostopäätteen mukaisia polkuja kun pääte on .txt.
     */
    @Test
    public void discoverFilesFindsOnlyTxtFilesWhenExtensionIsTxt() {
        assertTrue(FileHelper.discoverFiles(ROOT, ".txt").allMatch(s -> s.endsWith(".txt")));
    }

    /**
     * Testaa että {@link IOHelper#discoverFiles(String, String)} palauttaa vain
     * asetetun tiedostopäätteen mukaisia polkuja kun pääte on .json.
     */
    @Test
    public void discoverFilesFindsOnlyJsonFilesWhenExtensionIsJson() {
        assertTrue(FileHelper.discoverFiles(ROOT, ".json").allMatch(s -> s.endsWith(".json")));
    }

    /**
     * Testaa että {@link IOHelper#discoverFiles(String, String)} palauttaa vain.
     */
    @Test
    public void discoverFilesReturnsEmptyStreamWhenRootFileDoesNotExist() {
        val files = FileHelper.discoverFiles(Paths.get("this/path/does/hopefully/not/exist/"), ".txt");
        assertNotNull(files);
        assertEquals(0, files.count());
    }

    /**
     * Testaa että {@link IOHelper#discoverFiles(String, String)} ei palauta polkuja
     * jotka viittaavat kansioihin.
     */
    @Test
    public void discoverFilesDoesNotMatchFolders() {
        assertTrue(FileHelper.discoverFiles(ROOT, ".txt").allMatch(path -> !path.toFile().isDirectory()));
    }

    /**
     * Siivoaa väliaikaiset tiedostot.
     * 
     * @throws IOException jos tiedoston poistaminen epäonnistuu
     */
    @AfterEach
    public void afterEach() throws IOException {
        for (Path path : readableFiles) {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Siivoaa luodut väliaikaiset testeissä tarvitut tiedostot.
     */
    @AfterAll
    public static void afterAll() {
        try {
            Files.walk(ROOT).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ignored) {
        }
    }
}