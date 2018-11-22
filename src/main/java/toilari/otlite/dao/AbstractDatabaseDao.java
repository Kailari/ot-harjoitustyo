package toilari.otlite.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabaseDao<T> {
    @NonNull @Getter private final Database database;
    @NonNull @Getter(AccessLevel.PROTECTED) private final String tableName;

    AbstractDatabaseDao(@NonNull Database database, String tableName) {
        this.database = database;
        this.tableName = tableName;
    }

    protected abstract T createInstance(ResultSet result) throws SQLException;

    /**
     * Hakee kaikki tallennetut objektit.
     *
     * @return lista tallennetuista objekteista
     * @throws SQLException jos tietokannan lukemisessa tapahtuu virhe
     */
    @NonNull
    public List<T> findAll() throws SQLException {
        val allFound = new ArrayList<T>();
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement("SELECT * FROM " + this.tableName)) {
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
    public T findById(int id) throws SQLException {
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT * FROM " + this.tableName + " WHERE id = ?")) {
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
        try (val connection = this.database.getConnection();
             val statement = connection.prepareStatement(
                 "DELETE FROM " + tableName + " WHERE id = ?")) {
            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }
}
