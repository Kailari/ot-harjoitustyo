package toilari.otlite.game;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.io.dao.ProfileDAO;
import toilari.otlite.io.database.Database;

import java.sql.SQLException;

/**
 * Pelin käynnistyessä avautuva tila, jossa pelaaja valitsee profiilin jolla pelataan.
 */
@Slf4j
public class ProfileSelectState extends GameState {
    private ProfileDAO profiles;
    @NonNull private final String databasePath;

    public ProfileSelectState() {
        this("data/profiles.db");
    }

    public ProfileSelectState(@NonNull String databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public boolean init() {
        try {
            initDatabaseAccess(new Database(this.databasePath));
        } catch (SQLException e) {
            LOG.error("Accessing profile database in \"{}\" failed. Falling back to an in-memory database", this.databasePath);
            LOG.error("Cause: {}", e.getMessage());
            try {
                initDatabaseAccess(new Database(":memory:"));
            } catch (SQLException e2) {
                LOG.error("Could not access in-memory database. ");
                return true;
            }
        }

        return false;
    }

    private void initDatabaseAccess(Database database) throws SQLException {
        this.profiles = new ProfileDAO(database);
    }

    @Override
    public void update() {
    }

    @Override
    public void destroy() {

    }
}
