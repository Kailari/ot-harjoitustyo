package toilari.otlite.dao.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Apuluokka tekstipohjaisten tiedostojen käsittelyyn.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextFileHelper {
    /**
     * Avaa puskuroidun lukijan tekstipohjaisen tiedoston lukemista varten.
     *
     * @param path    Luettavan tekstitiedoston polku
     * @param options Lista asetuksista joiden mukaan tiedosto avataan
     * @return Lukijan, jolla tiedostoa voidaan lukea
     * @throws IOException          jos tiedoston avaaminen epäonnistuu
     * @throws NullPointerException jos polku tai asetukset on <code>null</code>
     */
    @NonNull
    public static BufferedReader getReader(@NonNull Path path, @NonNull OpenOption... options) throws IOException {
        return new BufferedReader(
            new InputStreamReader(FileStreamHelper.openForReading(path, options), Charset.forName("UTF-8")));
    }

    /**
     * Avaa puskuroidun kirjoittajan tekstipohjaisen tiedostoon kirjoittamista
     * varten.
     *
     * @param path    Kirjoitettavan tekstitiedoston polku
     * @param options Lista asetuksista joiden mukaan tiedosto avataan
     * @return Kirjoittajan, jolla tiedostoa voidaan lukea
     * @throws IOException          jos tiedoston avaaminen epäonnistuu
     * @throws NullPointerException jos polku tai asetukset on <code>null</code>
     */
    @NonNull
    public static BufferedWriter getWriter(@NonNull Path path, @NonNull OpenOption... options) throws IOException {
        return new BufferedWriter(
            new OutputStreamWriter(FileStreamHelper.openForWriting(path, options), Charset.forName("UTF-8")));
    }

    /**
     * Lukee tiedoston annetusta polusta merkkijonoksi.
     *
     * @param path tiedoston polku
     * @return merkkijono jossa tiedoston sisältö. <code>null</code> jos tiedostoa ei löydy
     * @throws IOException          jos tiedoston avaaminen epäonnistuu
     * @throws NullPointerException jos polku on <code>null</code>
     */
    @NonNull
    public static String readFileToString(@NonNull String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), Charset.forName("UTF-8"));
    }
}
