package toilari.otlite.dao;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.profile.Profile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO jolla voidaan ladata pelaajaprofiilit levyltä.
 */
public class ProfileDAO {
    @NonNull @Getter private final Database database;

    /**
     * Luo uuden profiili DAOn, joka lukee annettua tietokantaa.
     * Luo tarvittavat taulut automaattisesti.
     *
     * @param database tietokanta josta pelaajaprofiilit löytyvät.
     * @throws SQLException         jos profiilitaulun lisäämisessä tietokantaan tapahtuu virhe
     * @throws NullPointerException jos database on <code>null</code>
     */
    public ProfileDAO(@NonNull Database database) throws SQLException {
        this.database = database;

        try (val connection = getDatabase().getConnection();
             val statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS Profiles (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "hasSave BOOLEAN)");
        }
    }

    private Profile createInstance(ResultSet result) throws SQLException {
        return new Profile(
            result.getInt("id"),
            result.getString("name"),
            result.getBoolean("hasSave")
        );
    }

    /**
     * Hakee kaikki tallennetut objektit.
     *
     * @return lista tallennetuista objekteista
     * @throws SQLException jos tietokannan lukemisessa tapahtuu virhe
     */
    @NonNull
    public List<Profile> findAll() throws SQLException {
        val allFound = new ArrayList<Profile>();
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement("SELECT * FROM Profiles")) {
            val result = statement.executeQuery();

            while (result.next()) {
                allFound.add(createInstance(result));
            }
        }

        return allFound;
    }


    /**
     * Etsii objektin sen ID:n perusteella.
     *
     * @param id etsittävän objektin ID
     * @return <code>null</code> jos objektia ei ole, muulloin objekti jolla oli annettu id
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public Profile findById(int id) throws SQLException {
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT * FROM Profiles WHERE id = ?")) {
            statement.setInt(1, id);

            val result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }

            return createInstance(result);
        }
    }

    /**
     * Poistaa objektin sen ID:n perusteella.
     *
     * @param id poistettavan objektin ID
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public void removeById(int id) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "DELETE FROM PlayerStatistics WHERE profile_id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "DELETE FROM Profiles WHERE id = ?")) {
            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }

    /**
     * Hakee annetunnimisen pelaajaprofiilin.
     *
     * @param name profiilin nimi
     * @return <code>null</code> jos profiilia ei ole, muulloin profiili jolla oli annettu nimi
     * @throws SQLException         jos tietokannan käsittelyssä tapahtuu virhe
     * @throws NullPointerException jos name on <code>null</code>
     */
    public Profile findByName(@NonNull String name) throws SQLException {
        try (val connection = getDatabase().getConnection();
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

    /**
     * Luo uuden pelaajaprofiilin.
     *
     * @param name profiilin nimi
     * @return luotu profiili
     * @throws SQLException             jos tietokannan käsittelyssä tapahtuu virhe
     * @throws IllegalArgumentException jos tietokannassa on jo annetunniminen profiili
     * @throws NullPointerException     jos nimi on <code>null</code>
     */
    public Profile createNew(@NonNull String name) throws SQLException {
        if (profileWithNameExists(name)) {
            throw new IllegalStateException("Profile with name \"" + name + "\" already exists");
        }

        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "INSERT INTO Profiles (name, hasSave) VALUES (?,FALSE)")) {

            statement.setString(1, name);
            statement.executeUpdate();
        }

        return findByName(name);
    }

    /**
     * Tarkistaa onko annetunnimistä pelaajaprofiilia jo olemassa.
     *
     * @param name profiilin nimi
     * @return <code>true</code> jos profiili löytyy, <code>false</code> muulloin
     * @throws SQLException         jos tietokannan käsittelyssä tapahtuu virhe
     * @throws NullPointerException jos nimi on <code>null</code>
     */
    public boolean profileWithNameExists(@NonNull String name) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "SELECT EXISTS(SELECT 1 FROM Profiles WHERE name = ? LIMIT 1)")) {
            statement.setString(1, name);

            val result = statement.executeQuery();
            return result.next() && result.getInt(1) == 1;
        }
    }

}
