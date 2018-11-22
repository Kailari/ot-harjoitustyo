package toilari.otlite.dao;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.profile.Profile;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO jolla voidaan ladata pelaajaprofiilit levyltä.
 */
public class ProfileDAO extends AbstractDatabaseDao<Profile> {
    /**
     * Luo uuden profiili DAOn, joka lukee annettua tietokantaa.
     * Luo tarvittavat taulut automaattisesti.
     *
     * @param database tietokanta josta pelaajaprofiilit löytyvät.
     * @throws SQLException         jos profiilitaulun lisäämisessä tietokantaan tapahtuu virhe
     * @throws NullPointerException jos database on <code>null</code>
     */
    public ProfileDAO(@NonNull Database database) throws SQLException {
        super(database, "Profiles");

        try (val connection = getDatabase().getConnection();
             val statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "hasSave BOOLEAN)");
        }
    }

    @Override
    protected Profile createInstance(ResultSet result) throws SQLException {
        return new Profile(
            result.getInt("id"),
            result.getString("name"),
            result.getBoolean("hasSave")
        );
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
                 "SELECT * FROM " + getTableName() + " WHERE name = ?")) {
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
                 "INSERT INTO " + getTableName() + " (name, hasSave) VALUES (?,FALSE)")) {

            statement.setString(1, name);
            statement.executeUpdate();
        }

        return findByName(name);
    }

    @Override
    public void removeById(int id) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "DELETE FROM PlayerStatistics WHERE profile_id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

        super.removeById(id);
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
                 "SELECT EXISTS(SELECT 1 FROM " + getTableName() + " WHERE name = ? LIMIT 1)")) {
            statement.setString(1, name);

            val result = statement.executeQuery();
            return result.next() && result.getInt(1) == 1;
        }
    }

}
