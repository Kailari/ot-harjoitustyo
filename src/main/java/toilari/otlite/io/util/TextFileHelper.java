package toilari.otlite.io.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Apuluokka tekstipohjaisten tiedostojen käsittelyyn.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextFileHelper {
    /**
     * Avaa puskuroidun lukijan tekstipohjaisen tiedoston lukemista varten.
     * 
     * @param path Luettavan tekstitiedoston polku
     * @param options Lista asetuksista joiden mukaan tiedosto avataan
     * @return Lukijan, jolla tiedostoa voidaan lukea
     * @throws IOException jos tiedoston avaaminen epäonnistuu
     */
    public static BufferedReader getReader(Path path, OpenOption... options) throws IOException {
        return new BufferedReader(new InputStreamReader(FileStreamHelper.openForReading(path)));
    }

    /**
     * Avaa puskuroidun kirjoittajan tekstipohjaisen tiedostoon kirjoittamista varten.
     * 
     * @param path Kirjoitettavan tekstitiedoston polku
     * @param options Lista asetuksista joiden mukaan tiedosto avataan
     * @return Kirjoittajan, jolla tiedostoa voidaan lukea
     * @throws IOException jos tiedoston avaaminen epäonnistuu
     */
    public static BufferedWriter getWriter(Path path, OpenOption... options) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(FileStreamHelper.openForWriting(path, options)));
    }
}
