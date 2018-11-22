package toilari.otlite.dao;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toilari.otlite.dao.database.Database;
import toilari.otlite.dao.util.FileHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Testaa että ProfileDAO toimii kuten oletettu.
 */
class ProfileDAOTest {
    private static final Path PERSISTENT_ROOT = Paths.get("src/test/resources/");
    private static final Path ROOT = Paths.get("target/test-temp/");

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(ROOT);
        Files.copy(PERSISTENT_ROOT.resolve("test.db"), ROOT.resolve("test.db"), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Testaa että konstruktori luo tarvittavat tietokantataulut jos niitä ei vielä ole.
     */
    @Test
    void constructorCreatesTableIfItDoesNotExist() throws SQLException {
        val database = new Database(ROOT.resolve("does_not_exist.db").toString());
        new ProfileDAO(database);

        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT EXISTS(SELECT 1 FROM sqlite_master WHERE tbl_name = 'Profiles' LIMIT 1)")) {
            val result = statement.executeQuery();
            assertTrue(result.next() && result.getInt(1) == 1);
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsWithNullDatabase() {
        assertThrows(NullPointerException.class, () -> new ProfileDAO(null));
    }

    /**
     * Tarkistaa että DAO löytää olemassaolevat rivit testitietokannasta.
     */
    @Test
    void findAllFindsExistingEntries() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        val all = dao.findAll();
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("Kissa")));
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("Koira")));
    }

    @Test
    void findByNameFindsNonNullForExistingEntry() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertNotNull(dao.findByName("Kissa"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void findByNameThrowsForNullName() {
        assertThrows(NullPointerException.class, () -> {
            val database = new Database(ROOT.resolve("test.db").toString());
            val dao = new ProfileDAO(database);

            assertNull(dao.findByName(null));
        });
    }

    @Test
    void findByNameFindsNonNullForNonExistentEntry() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertNull(dao.findByName("EnOleOlemassa"));
    }

    @Test
    void findByIDFindsNonNullForExistingEntry() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertNotNull(dao.findById(1));
    }

    @Test
    void findByIDFindsNonNullForNonExistentEntry() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertNull(dao.findById(1337));
    }

    @Test
    void removeByIDRemovesOneWhenGivenValidID() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        val size = dao.findAll().size();
        dao.removeById(2);
        assertEquals(size - 1, dao.findAll().size());
    }

    @Test
    void removeByIDDoesNotLeaveStatisticsBehind() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);
        dao.removeById(2);

        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT COUNT(*) AS count FROM PlayerStatistics WHERE profile_id = 2")) {
            val result = statement.executeQuery();

            assumeTrue(result.next());
            assertEquals(0, result.getInt("count"));
        }
    }

    @Test
    void removeByIDRemovesNothingWhenGivenInvalidID() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        val size = dao.findAll().size();
        dao.removeById(1337);
        assertEquals(size, dao.findAll().size());
    }

    @Test
    void removeByIDDoesNotTouchStatisticsWhenGivenInvalidID() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        int count;
        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT COUNT(*) AS count FROM PlayerStatistics")) {
            val result = statement.executeQuery();

            assumeTrue(result.next());
            count = result.getInt("count");
        }

        dao.removeById(1337);

        try (val connection = database.getConnection();
             val statement = connection.prepareStatement(
                 "SELECT COUNT(*) AS count FROM PlayerStatistics")) {
            val result = statement.executeQuery();

            assumeTrue(result.next());
            assertEquals(count, result.getInt("count"));
        }
    }

    @Test
    void profileWithNameExistsReturnsTrueForExistingName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertTrue(dao.profileWithNameExists("Kissa"));
        assertTrue(dao.profileWithNameExists("Koira"));
    }

    @Test
    void profileWithNameExistsReturnsFalseForInvalidName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertFalse(dao.profileWithNameExists("IDoNotExist"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void profileWithNameExistsThrowsWithNullName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertThrows(NullPointerException.class, () -> dao.profileWithNameExists(null));
    }

    @Test
    void createNewThrowsWithDuplicateName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertThrows(IllegalArgumentException.class, () -> dao.createNew("Kissa"));
    }

    @Test
    void createNewCreatesNonNUllWithValidName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertNotNull(dao.createNew("NewProfile"));
    }

    @Test
    void createNewCreatesExpectedWithValidName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertEquals("NewProfile", dao.createNew("NewProfile").getName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void createNewThrowsWithNullName() throws SQLException {
        val database = new Database(ROOT.resolve("test.db").toString());
        val dao = new ProfileDAO(database);

        assertThrows(NullPointerException.class, () -> dao.createNew(null));
    }


    @AfterEach
    void afterEach() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }

    @AfterAll
    static void afterAll() {
        FileHelper.deleteDirectoryAndChildren(ROOT);
    }
}
