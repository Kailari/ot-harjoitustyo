package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.io.dao.ProfileDAO;
import toilari.otlite.io.database.Database;
import toilari.otlite.menu.EventSystem;
import toilari.otlite.menu.IEvent;
import toilari.otlite.menu.Profile;

import java.sql.SQLException;
import java.util.List;

/**
 * Pelin käynnistyessä avautuva tila, jossa pelaaja valitsee profiilin jolla pelataan.
 */
@Slf4j
public class ProfileSelectState extends GameState {
    @Getter @NonNull private final EventSystem eventSystem = new EventSystem();
    @NonNull private final String databasePath;

    private ProfileDAO profiles;

    /**
     * Hakee listan saatavilla olevista profiileista.
     *
     * @return saatavilla olevat profiilit, tyhjä lista jos haku epäonnistuu.
     * @throws SQLException jos haku tietokannasta epäonnistuu
     */
    public List<Profile> getProfiles() throws SQLException {
        return this.profiles.findAll();
    }

    public ProfileSelectState(@NonNull String databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public boolean init() {
        if (tryInitDatabaseAccess()) {
            return true;
        }

        this.eventSystem.subscribeTo(QuitEvent.class, this::onQuit);

        return false;
    }

    private boolean tryInitDatabaseAccess() {
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

    private void onQuit(@NonNull QuitEvent event) {
        getGame().setRunning(false);
    }

    public static class QuitEvent implements IEvent {
    }
}
