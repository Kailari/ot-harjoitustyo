package toilari.otlite.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.ProfileDAO;
import toilari.otlite.dao.database.Database;
import toilari.otlite.game.event.IEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.world.entities.TurnObjectManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Pelin käynnistyessä avautuva tila, jossa pelaaja valitsee profiilin jolla pelataan.
 */
@Slf4j
public class ProfileSelectState extends GameState {
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

        getEventSystem().subscribeTo(QuitEvent.class, this::onQuit);
        getEventSystem().subscribeTo(AddEvent.class, this::onAdd);
        getEventSystem().subscribeTo(RemoveEvent.class, this::onRemove);
        getEventSystem().subscribeTo(SelectEvent.class, this::onSelect);

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

    private void onAdd(@NonNull AddEvent event) {
        try {
            if (this.profiles.profileWithNameExists(event.getName())) {
                LOG.warn("Profile with given name already exists!");
                getEventSystem().fire(new InvalidNameEvent());
                return;
            }

            this.profiles.createNew(event.getName());
        } catch (SQLException e) {
            LOG.error("Creating profile failed, trying to shut down gracefully.");
            LOG.error("Cause: {}", e.getMessage());

            getGame().setRunning(false);
        }
    }

    private void onSelect(@NonNull SelectEvent event) {
        try {
            val profile = this.profiles.findProfileById(event.getId());

            getGame().setActiveProfile(profile);
            getGame().changeState(new PlayGameState(new TurnObjectManager()));
        } catch (SQLException e) {
            LOG.error("Selecting profile failed, trying to shut down gracefully.");
            LOG.error("Cause: {}", e.getMessage());

            getGame().setRunning(false);
        }
    }

    private void onRemove(@NonNull RemoveEvent event) {
        try {
            this.profiles.removeById(event.getId());
        } catch (SQLException e) {
            LOG.error("Creating profile failed, trying to shut down gracefully.");
            LOG.error("Cause: {}", e.getMessage());

            getGame().setRunning(false);
        }
    }

    public static class QuitEvent implements IEvent {
    }

    public static class AddEvent implements IEvent {
        @NonNull @Getter private final String name;

        public AddEvent(@NonNull String name) {
            this.name = name;
        }
    }

    public static class InvalidNameEvent implements IEvent {
    }

    public static class InvalidIdEvent implements IEvent {
    }

    public static class RemoveEvent implements IEvent {
        @Getter private final int id;

        public RemoveEvent(int id) {
            this.id = id;
        }
    }

    public static class SelectEvent implements IEvent {
        @Getter private final int id;

        public SelectEvent(int id) {
            this.id = id;
        }
    }
}
