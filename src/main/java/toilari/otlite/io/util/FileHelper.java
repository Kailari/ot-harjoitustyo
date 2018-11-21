package toilari.otlite.io.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

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
     * @param path      Polku josta etsitään
     * @param extension tiedostopääte
     * @return <code>Stream&lt;Path&gt;</code> jossa löydettyjen tiedostojen polut.
     * Tyhjä jos tiedostoja ei löytynyt
     * @throws NullPointerException jos polku tai tiedostopääte on <code>null</code>
     */
    public static Stream<Path> discoverFiles(@NonNull Path path, @NonNull String extension) {
        if (extension.startsWith(".")) {
            throw new IllegalArgumentException("omit the initial '.' of the extension. (e.g. \"txt\", not \".txt\")");
        }

        try {
            return Files.find(path, 1, (p, attr) -> FileHelper.extensionFilter(extension, p, attr));
        } catch (IOException e) {
            LOG.error("Could not access path {}", path);
            return Arrays.stream(new Path[0]);
        }
    }

    /**
     * Luo tiedoston jonka polkuna on <code>pathString</code> ja nimenä
     * <code>filename</code>. Tarvittavat hakemistot luodaan automaattisesti.
     *
     * @param path     polku kansioon johon tiedosto luodaan
     * @param filename tiedoston nimi
     * @return <code>true</code> kun tiedoston luonti onnistuu, muutoin
     * <code>false</code>
     * @throws NullPointerException jos polku tai tiedostonimi on <code>null</code>
     */
    public static boolean createFile(@NonNull Path path, @NonNull String filename) {
        Path filePath = path.resolve(filename);

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LOG.error("Could not create required directories: {}", e.getMessage());
            return false;
        }

        try {
            Files.createFile(filePath);
            return true;
        } catch (IOException e) {
            LOG.error("Could not create file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Luo tyhjän tiedoston joka löytyy annetusta polusta.
     *
     * @param filename polku johon tiedosto luodaan.
     * @return <code>true</code> jos tiedoston luonti onnistuu, <code>false</code> muulloin
     */
    public static boolean createFile(@NonNull String filename) {
        val path = Paths.get(filename);
        val parent = path.getParent();
        return createFile(parent == null ? Paths.get(".") : parent, path.getFileName().toString());
    }

    /**
     * Poistaa tiedoston ja kaikki sen sisältämät alihakemistot ja tiedostot.
     *
     * @param path Poistettavan hakemiston polku
     * @return <code>true</code> jos poistaminen onnistuu, muutoin
     * <code>false</code>
     * @throws NullPointerException jos polku on <code>null</code>
     */
    public static boolean deleteDirectoryAndChildren(@NonNull Path path) {
        if (!Files.isDirectory(path)) {
            LOG.error("Path to be deleted should point to a directory!");
            return false;
        }

        try {
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
            return true;
        } catch (IOException e) {
            LOG.error("Could not delete file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Tarkistaa onko annetunnimistä tiedostoa olemassa.
     *
     * @param path tiedoston polku
     * @return <code>true</code> jos tiedosto on olemassa, <code>false</code> jos tiedostoa ei ole.
     */
    public static boolean fileExists(@NonNull String path) {
        return new File(path).exists();
    }

    private static boolean extensionFilter(String extension, Path path, BasicFileAttributes attributes) {
        return attributes.isRegularFile() && path.normalize().toString().toLowerCase().endsWith("." + extension);
    }
}