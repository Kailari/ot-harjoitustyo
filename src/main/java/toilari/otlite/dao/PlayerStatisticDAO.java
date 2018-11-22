package toilari.otlite.dao;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.profile.tracking.PlayerStatistic;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerStatisticDAO {
    @NonNull @Getter private final Database database;

    public PlayerStatisticDAO(@NonNull Database database) throws SQLException {
        this.database = database;
        try (val connection = getDatabase().getConnection();
             val statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS PlayerStatistics (" +
                    "profile_id INTEGER," +
                    "statistic_id INTEGER," +
                    "value DOUBLE," +
                    "PRIMARY KEY (profile_id, statistic_id))");
        }
    }

    private PlayerStatistic createInstance(ResultSet result) throws SQLException {
        return new PlayerStatistic(
            result.getInt("profile_id"),
            result.getInt("statistic_id"),
            result.getDouble("value"));
    }

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

    public double get(int profileId, int statisticId) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profileId);
            statement.setInt(2, statisticId);

            val result = statement.executeQuery();
            if (!result.next()) {
                return 0.0;
            }

            return result.getDouble("value");
        }
    }

    public void increment(int statisticId, int profileId) throws SQLException {
        try (val connection = getDatabase().getConnection();
             val statement = connection.prepareStatement(
                 "UPDATE PlayerStatistics SET value = value + 1 WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profileId);
            statement.setInt(2, statisticId);
            statement.executeUpdate();
        }
    }
}
