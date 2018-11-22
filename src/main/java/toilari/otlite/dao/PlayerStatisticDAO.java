package toilari.otlite.dao;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.database.Database;

import java.sql.SQLException;

/**
 * Lukee pelaajaprofiilien statistiikkadataa tietokannasta.
 */
@Slf4j
public class PlayerStatisticDAO {
    @NonNull @Getter private final Database database;

    /**
     * Luo uuden DAOn statistiikkadatan lukemiseen. Luo tarvittavat tietokantataulut jos niitä ei vielä ole.
     *
     * @param database tietokanta jota luetaan
     * @throws SQLException         jos tietokannan käsittelyssä tapahtuu virhe
     * @throws NullPointerException jos tietokanta on <code>null</code>
     */
    public PlayerStatisticDAO(@NonNull Database database) throws SQLException {
        this.database = database;

        try (val connection = getDatabase().getConnection();
             val statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS PlayerStatistics (" +
                    "profile_id INTEGER," +
                    "statistic_id INTEGER," +
                    "value DOUBLE," +
                    "PRIMARY KEY (profile_id, statistic_id)," +
                    "FOREIGN KEY (profile_id) REFERENCES Profiles(id))");
        }
    }

    /**
     * Lisää pelaajalle statistiikan tietokantatauluun, muttei korvaa tietoa jos taulussa on jo rivi tälle pelaajalle,
     * tälle kyseiselle statistiikalle.
     *
     * @param profileId   pelaajaprofiili jonka tietoja päivitetään
     * @param statisticId päivitettävän statistiikan ID
     * @param value       arvo
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public void addButDoNotReplace(int profileId, int statisticId, double value) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "INSERT INTO PlayerStatistics(profile_id, statistic_id, value)" +
                     "SELECT ?,?,? WHERE NOT EXISTS(SELECT 1 FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?)")) {
            statement.setInt(1, profileId);
            statement.setInt(2, statisticId);
            statement.setDouble(3, value);
            statement.setInt(4, profileId);
            statement.setInt(5, statisticId);
            statement.executeUpdate();
        }
    }

    /**
     * Päivittää pelaajan statistiikkoja.
     *
     * @param profileId   pelaajaprofiili jonka tietoja päivitetään
     * @param statisticId päivitettävän statistiikan ID
     * @param value       uusi arvo
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public void update(int profileId, int statisticId, double value) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "UPDATE PlayerStatistics SET value = ? WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setDouble(1, value);
            statement.setInt(2, profileId);
            statement.setInt(3, statisticId);
            statement.executeUpdate();
        }
    }

    /**
     * Hakee pelaajan statistiikkatiedon.
     *
     * @param profileId   pelaajaprofiili jonka tietoja haetaan
     * @param statisticId noudettavan statistiikan ID
     * @return statistiikan nykyinen arvo
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public double get(int profileId, int statisticId) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profileId);
            statement.setInt(2, statisticId);

            val result = statement.executeQuery();
            if (!result.next()) {
                LOG.error("No entry for statistic {} for profile {}", statisticId, profileId);
                return 0.0;
            }

            return result.getDouble("value");
        }
    }

    /**
     * Kasvattaa pelaajan statistiikkatietoa yhdellä.
     *
     * @param profileId   pelaajaprofiili jonka tietoja muokataan
     * @param statisticId kasvatettavan statistiikan ID
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public void increment(int profileId, int statisticId) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "UPDATE PlayerStatistics SET value = value + 1 WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profileId);
            statement.setInt(2, statisticId);
            statement.executeUpdate();
        }
    }
}
