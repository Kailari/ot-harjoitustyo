package toilari.otlite.dao.database;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.util.FileHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Apuluokka tietokantayhteyden hallinnointiin.
 */
@Slf4j
public class Database {
    @NonNull private final String databaseUrl;

    /**
     * Luo uuden tietokannan annettuun polkuun. Jos tietokantaa ei ole olemassa, sellainen luodaan.
     *
     * @param databasePath polku josta tietokantaa etsitään/johon uusi tietokanta luodaan
     * @throws SQLException jos tietokannan luonti epäonnistuu tai olemassaolevaa tietokantaa ei voida lukea
     */
    public Database(@NonNull String databasePath) throws SQLException {
        this.databaseUrl = "jdbc:sqlite:" + databasePath;

        if (!FileHelper.fileExists(databasePath)) {
            FileHelper.createFile(databasePath);
        }

        try (val connection = getConnection()) {
            if (connection != null) {
                LOG.info("Connected to the database at \"{}\"", databasePath);
            }
        }
    }

    /**
     * Hakee yhteyden tietokantaan kyselyitä varten.
     *
     * @return yhteys tietokantaan
     * @throws SQLException jos yhteyden luominen epäonnistuu
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.databaseUrl);
    }
}
