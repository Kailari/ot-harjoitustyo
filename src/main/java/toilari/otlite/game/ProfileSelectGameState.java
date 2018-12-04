package toilari.otlite.game;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.event.MenuEvent;
import toilari.otlite.game.event.ProfileMenuEvent;

import java.sql.SQLException;

/**
 * Pelin käynnistyessä avautuva tila, jossa pelaaja valitsee profiilin jolla pelataan.
 */
@Slf4j
public class ProfileSelectGameState extends GameState {
    @Override
    public boolean init() {
        getEventSystem().subscribeTo(MenuEvent.Quit.class, (e) -> getGame().setRunning(false));
        getEventSystem().subscribeTo(ProfileMenuEvent.Add.class, this::onAdd);
        getEventSystem().subscribeTo(ProfileMenuEvent.Remove.class, this::onRemove);
        getEventSystem().subscribeTo(ProfileMenuEvent.Select.class, this::onSelect);

        try {
            for (val profile : getGame().getProfileDao().findAll()) {
                getGame().getStatistics().startTrackingProfile(profile.getId());
            }
        } catch (SQLException e) {
            LOG.error("Error updating statistics entries for existing profiles.");
            LOG.error("Cause: {}", e.getMessage());
            return true;
        }

        return false;
    }

    @Override
    public void update() {
    }

    @Override
    public void destroy() {
    }

    private void onAdd(@NonNull ProfileMenuEvent.Add event) {
        try {
            if (getGame().getProfileDao().profileWithNameExists(event.getName())) {
                ProfileSelectGameState.LOG.warn("Profile with given name already exists!");
                getEventSystem().fire(new ProfileMenuEvent.InvalidName());
                return;
            }

            val profile = getGame().getProfileDao().createNew(event.getName());
            if (profile != null) {
                getGame().getStatistics().startTrackingProfile(profile.getId());
                getEventSystem().fire(new ProfileMenuEvent.Added(profile));
            }
        } catch (SQLException e) {
            ProfileSelectGameState.LOG.error("Creating profile failed, trying to shut down gracefully.");
            ProfileSelectGameState.LOG.error("Cause: {}", e.getMessage());

            getGame().setRunning(false);
        }
    }

    private void onSelect(@NonNull ProfileMenuEvent.Select event) {
        try {
            val profile = getGame().getProfileDao().findById(event.getProfile().getId());

            getGame().setActiveProfile(profile);
            getGame().changeState(new MainMenuGameState());
        } catch (SQLException e) {
            ProfileSelectGameState.LOG.error("Selecting profile failed, trying to shut down gracefully.");
            ProfileSelectGameState.LOG.error("Cause: {}", e.getMessage());

            getGame().setRunning(false);
        }
    }

    private void onRemove(@NonNull ProfileMenuEvent.Remove event) {
        try {
            getGame().getProfileDao().remove(event.getProfile());
            getEventSystem().fire(new ProfileMenuEvent.Removed(event.getProfile()));
        } catch (SQLException e) {
            ProfileSelectGameState.LOG.error("Creating profile failed, trying to shut down gracefully.");
            ProfileSelectGameState.LOG.error("Cause: {}", e.getMessage());

            getGame().setRunning(false);
        }
    }
}
