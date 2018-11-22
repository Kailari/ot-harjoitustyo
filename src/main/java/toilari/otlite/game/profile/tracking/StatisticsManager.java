package toilari.otlite.game.profile.tracking;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.PlayerStatisticDAO;
import toilari.otlite.dao.database.Database;

import java.sql.SQLException;

@Slf4j
public class StatisticsManager {
    private final PlayerStatisticDAO playerStatistics;

    public StatisticsManager(@NonNull Database database) throws SQLException {
        this.playerStatistics = new PlayerStatisticDAO(database);
    }

    public void startTrackingProfile(int profileId) throws SQLException {
        for (val stat : Statistics.values()) {
            this.playerStatistics.addButDoNotReplace(profileId, stat.getId(), stat.getDefaultValue());
        }
    }

    public long getLong(Statistics key, int profileId) {
        return (long) getDouble(key, profileId);
    }

    public double getDouble(Statistics key, int profileId) {
        try {
            return this.playerStatistics.get(profileId, key.getId());
        } catch (SQLException e) {
            LOG.warn("Could not get statistic {} for profile {}", key.getName(), profileId);
            return Double.NaN;
        }
    }

    public void set(Statistics key, int profileId, double value) {
        try {
            this.playerStatistics.update(key.getId(), profileId, value);
        } catch (SQLException e) {
            LOG.warn("Could not update statistic {} for profile {}", key.getName(), profileId);
        }
    }

    public void increment(Statistics key, int profileId) {
        try {
            this.playerStatistics.increment(key.getId(), profileId);
        } catch (SQLException e) {
            LOG.warn("Could not increment statistic {} for profile {}", key.getName(), profileId);
        }
    }
}
