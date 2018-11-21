package toilari.otlite.dao.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 * Apuluokka I/O streamisen avaamiseen tiedostojen lukemista ja kirjoittamista
 * varten. Abstraktoi tiedoston alkuperän (levy, verkko, classpath), jolloin
 * virtoja voidaan avata ilman että kutsujan tarvitsee tietää mistä data on
 * peräisin.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileStreamHelper {
    /**
     * Avaa tiedoston lukemista varten. Luo {@link InputStream InputSteramin} jolla
     * tiedostoa voidaan lukea.
     *
     * @param path    Tiedoston polku
     * @param options Lista asetuksista joiden mukaan tiedosto avataan
     * @return InputStream jonka avulla tiedosto voidaan lukea
     * @throws IOException          jos tiedoston avaaminen epäonnistuu
     * @throws NullPointerException jos polku tai asetukset on <code>null</code>
     */
    @NonNull
    public static InputStream openForReading(@NonNull Path path, @NonNull OpenOption... options) throws IOException {
        return Files.newInputStream(path, options);
    }

    /**
     * Avaa tiedoston kirjoittamista varten. Luo {@link OutputStream OutputStreamin}
     * jolla tiedostoon voidaan kirjoittaa.
     *
     * @param path    Tiedoston polku
     * @param options Lista asetuksista joiden mukaan tiedosto avataan
     * @return OutputStream jonka avulla tiedostoon voidaan kirjoittaa
     * @throws IOException          jos tiedoston avaaminen epäonnistuu
     * @throws NullPointerException jos polku tai asetukset on <code>null</code>
     */
    @NonNull
    public static OutputStream openForWriting(@NonNull Path path, @NonNull OpenOption... options) throws IOException {
        return Files.newOutputStream(path, options);
    }
}