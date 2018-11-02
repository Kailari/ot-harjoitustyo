package toilari.otlite.io.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Apuluokka tiedostojen luomista, poistamista ja etsimistä varten.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHelper {
    /**
     * Etsii annetusta polusta kaikki tiedostot joilla on annettu pääte ja palauttaa
     * ne {@link Stream streamina}.
     * 
     * @param path Polku josta etsitään
     * @param extension  tiedostopääte
     * @return <code>Stream&lt;Path&gt;</code> jossa löydettyjen tiedostojen polut.
     *         Tyhjä jos tiedostoja ei löytynyt
     */
    public static Stream<Path> discoverFiles(Path path, String extension) {
        try {
            return Files.find(path, 0, (p, attr) -> FileHelper.extensionFilter(extension, p, attr));
        } catch (IOException e) {
            LOG.error("Could not access path %s: %s", path, e.getMessage());
            return Arrays.stream(new Path[0]);
        }
    }

    /**
     * Luo tiedoston jonka polkuna on <code>pathString</code> ja nimenä
     * <code>filename</code>. Tarvittavat hakemistot luodaan automaattisesti.
     * 
     * @param path polku kansioon johon tiedosto luodaan
     * @param filename   tiedoston nimi
     * @return <code>true</code> kun tiedoston luonti onnistuu, muutoin
     *         <code>false</code>
     */
    public static boolean createFile(Path path, String filename) {
        Path filePath = path.resolve(filename);

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LOG.error("Could not create required directories: " + e.getMessage());
            return false;
        }

        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            LOG.error("Could not create file: " + e.getMessage());
            return false;
        }

        return true;
    }

    private static boolean extensionFilter(String extension, Path path, BasicFileAttributes attributes) {
        return !attributes.isDirectory() && path.endsWith("." + extension);
    }
}