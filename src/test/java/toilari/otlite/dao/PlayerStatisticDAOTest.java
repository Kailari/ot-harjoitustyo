package toilari.otlite.dao;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.database.Database;
import toilari.otlite.dao.util.FileHelper;
import toilari.otlite.game.profile.tracking.Statistics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStatisticDAOTest {
    private static final Path PERSISTENT_ROOT = Paths.get("src/test/resources/");
    private static final Path ROOT = Paths.get("target/test-temp/");

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(ROOT);
        Files.copy(PERSISTENT_ROOT.resolve("test.db"), ROOT.resolve("test.db"), StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterEach
    void afterEach() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsIfDatabaseIsNull() {
        assertThrows(NullPointerException.class, () -> new PlayerStatisticDAO(null));
    }

    @Test
    void constructorCreatesRequiredTables() throws SQLException {
        val database = new Database(ROOT.resolve("does_not_exist.db").toString());
        new PlayerStatisticDAO(database);

        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT EXISTS(SELECT 1 FROM sqlite_master WHERE tbl_name = 'PlayerStatistics' LIMIT 1)")) {
            val result = statement.executeQuery();
            assertTrue(result.next() && result.getInt(1) == 1);
        }
    }

    @Test
    void addButDoNotReplaceInsertsToEmptyTable() throws SQLException {
        val database = new Database(ROOT.resolve("does_not_exist.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).createNew("TestProfile");
        val dao = new PlayerStatisticDAO(database);

        dao.addButDoNotReplace(profile.getId(), Statistics.KILLS.getId(), 10.0);

        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profile.getId());
            statement.setInt(2, Statistics.KILLS.getId());
            val result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(10.0, result.getDouble("value"));
        }
    }

    @Test
    void addButDoNotReplaceDoesNotOverrideExistingData() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        dao.addButDoNotReplace(profile.getId(), Statistics.KILLS.getId(), 10.0);

        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profile.getId());
            statement.setInt(2, Statistics.KILLS.getId());
            val result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(2.0, result.getDouble("value"));
        }
    }

    @Test
    void updateChangesValues() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        dao.update(profile.getId(), Statistics.KILLS.getId(), 9001.0);
        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profile.getId());
            statement.setInt(2, Statistics.KILLS.getId());
            val result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(9001.0, result.getDouble("value"));
        }
    }

    @Test
    void updateDoesNotTouchOtherProfilesValues() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        dao.update(profile.getId(), Statistics.KILLS.getId(), 9001.0);
        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = 2 AND statistic_id = ?")) {
            statement.setInt(1, Statistics.KILLS.getId());
            val result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(1.0, result.getDouble("value"));
        }
    }

    @Test
    void getReturnsZeroIfEntryDoesNotExist() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        assertEquals(0.0, dao.get(profile.getId(), 715517));
    }

    @Test
    void getReturnsCorrectValueIfEntryExists() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        assertEquals(2.0, dao.get(profile.getId(), Statistics.KILLS.getId()));
    }

    @Test
    void incrementIncreasesValueByExactlyOne() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        dao.increment(profile.getId(), Statistics.KILLS.getId());
        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value FROM PlayerStatistics WHERE profile_id = ? AND statistic_id = ?")) {
            statement.setInt(1, profile.getId());
            statement.setInt(2, Statistics.KILLS.getId());
            val result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(3.0, result.getDouble("value"));
        }
    }

    @Test
    void incrementDoesNotTouchOtherProfilesValues() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val profile = new ProfileDAO(database, new SettingsDAO(ROOT.toString())).findByName("Kissa");
        val dao = new PlayerStatisticDAO(database);

        dao.increment(profile.getId(), Statistics.KILLS.getId());
        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT value AS value_koira FROM PlayerStatistics WHERE profile_id = 2 AND statistic_id = ?")) {
            statement.setInt(1, Statistics.KILLS.getId());
            val result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals(1.0, result.getDouble("value_koira"));
        }
    }
}
