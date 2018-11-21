package toilari.otlite.dao;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.profile.Profile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO jolla voidaan ladata pelaajaprofiilit levyltä.
 */
public class ProfileDAO {
    @NonNull private final Database database;

    /**
     * Luo uuden profiili DAOn, joka lukee annettua tietokantaa.
     * Luo tarvittavat taulut automaattisesti.
     *
     * @param database tietokanta josta pelaajaprofiilit löytyvät.
     * @throws SQLException jos profiilitaulun lisäämisessä tietokantaan tapahtuu virhe
     */
    public ProfileDAO(@NonNull Database database) throws SQLException {
        this.database = database;

        try (val connection = this.database.getConnection();
             val statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS Profiles (" +
                    "id INTEGER PRIMARY KEY," +
                    "name VARCHAR(30)," +
                    "hasSave BOOLEAN)");
        }
    }

    /**
     * Hakee kaikki tallennetut profiilit.
     *
     * @return lista tallennetuista profiileista
     * @throws SQLException jos tietokannan lukemisessa tapahtuu virhe
     */
    @NonNull
    public List<Profile> findAll() throws SQLException {
        val profiles = new ArrayList<Profile>();
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement("SELECT * FROM Profiles")) {
            val result = statement.executeQuery();

            while (result.next()) {
                profiles.add(new Profile(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getBoolean("hasSave")
                ));
            }
        }

        return profiles;
    }

    /**
     * Luo uuden pelaajaprofiilin.
     *
     * @param name profiilin nimi
     * @return luotu profiili
     * @throws SQLException             jos tietokannan käsittelyssä tapahtuu virhe
     * @throws IllegalArgumentException jos tietokannassa on jo annetunniminen profiili
     */
    public Profile createNew(@NonNull String name) throws SQLException {
        if (profileWithNameExists(name)) {
            throw new IllegalStateException("Profile with name \"" + name + "\" already exists");
        }

        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "INSERT INTO Profiles (name, hasSave) VALUES (?,FALSE)")) {

            statement.setString(1, name);
        }

        return findProfileByName(name);
    }

    /**
     * Tarkistaa onko annetunnimistä pelaajaprofiilia jo olemassa.
     *
     * @param name profiilin nimi
     * @return <code>true</code> jos profiili löytyy, <code>false</code> muulloin
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public boolean profileWithNameExists(@NonNull String name) throws SQLException {
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT COUNT(name) AS count FROM Profiles WHERE name = ?")) {
            statement.setString(1, name);

            val result = statement.executeQuery();
            return result.next() && result.getInt("count") > 0;
        }
    }

    /**
     * Hakee annetunnimisen pelaajaprofiilin.
     *
     * @param name profiilin nimi
     * @return <code>null</code> jos profiilia ei ole, muulloin profiili jolla oli annettu nimi
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public Profile findProfileByName(@NonNull String name) throws SQLException {
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT * FROM Profiles WHERE name = ?")) {
            statement.setString(1, name);

            val result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Profile(
                result.getInt("id"),
                result.getString("name"),
                result.getBoolean("hasSave")
            );
        }
    }
}
