package toilari.otlite.game.profile.statistics;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.PlayerStatisticDAO;

import java.sql.SQLException;

/**
 * Statistiikkamanageri. Hallinnoi pelaajaprofiilien statistiikkatietoja
 */
@Slf4j
public class StatisticsManager {
    private final PlayerStatisticDAO playerStatistics;

    /**
     * Luo uuden statistiikkamanagerin käyttäen annettua tietokantaa tiedon tallennukseen.
     *
     * @param statistics dao jolla pelaajan statistiikkoihin pääsee käsiksi
     */
    public StatisticsManager(@NonNull PlayerStatisticDAO statistics) {
        this.playerStatistics = statistics;
    }

    /**
     * Lisää pelaajaprofiilin tietokantaan jos sitä ei vielä ole siellä.
     *
     * @param profileId lisättävän profiilin ID
     *
     * @throws SQLException jos tietokannan käsittelyssä tapahtuu virhe
     */
    public void startTrackingProfile(int profileId) throws SQLException {
        for (val stat : Statistics.values()) {
            this.playerStatistics.addButDoNotReplace(profileId, stat.getId(), stat.getDefaultValue());
        }
    }

    /**
     * Hakee statistiikkatiedon kokonaislukuna.
     *
     * @param key       haettava tieto
     * @param profileId profiili jonka tietoa haetaan
     *
     * @return statistiikan nykyinen arvo tai -1 jos hakeminen ei onnistunut
     *
     * @throws NullPointerException jos haettava statistiikka on <code>null</code>
     */
    public long getLong(@NonNull Statistics key, int profileId) {
        val d = getDouble(key, profileId);
        return Double.isNaN(d) ? -1 : (long) d;
    }

    /**
     * Hakee statistiikkatiedon liukulukuna.
     *
     * @param key       haettava tieto
     * @param profileId profiili jonka tietoa haetaan
     *
     * @return statistiikan nykyinen arvo tai <code>NaN</code> jos hakeminen ei onnistu
     *
     * @throws NullPointerException jos haettava statistiikka on <code>null</code>
     */
    public double getDouble(@NonNull Statistics key, int profileId) {
        try {
            return this.playerStatistics.get(profileId, key.getId());
        } catch (SQLException e) {
            LOG.warn("Could not get statistic {} for profile {}", key.getName(), profileId);
            return Double.NaN;
        }
    }

    /**
     * Asettaa statistiikkatiedolle uuden arvon.
     *
     * @param key       tieto jonka arvo asetetaan
     * @param profileId profiili jonka tietoa päivitetään
     * @param value     uusi arvo
     *
     * @throws NullPointerException jos haettava statistiikka on <code>null</code>
     */
    public void set(@NonNull Statistics key, int profileId, double value) {
        try {
            this.playerStatistics.update(key.getId(), profileId, value);
        } catch (SQLException e) {
            LOG.warn("Could not update statistic {} for profile {}", key.getName(), profileId);
        }
    }

    /**
     * Kasvattaa statistiikkatiedon arvoa yhdellä.
     *
     * @param key       tieto jonka arvoa kasvatetaan
     * @param profileId profiili jonka tietoa päivitetään
     *
     * @throws NullPointerException jos haettava statistiikka on <code>null</code>
     */
    public void increment(@NonNull Statistics key, int profileId) {
        incrementBy(key, 1.0, profileId);
    }

    /**
     * Kasvattaa statistiikkatiedon arvoa annetulla arvolla.
     *
     * @param key       tieto jonka arvoa kasvatetaan
     * @param amount    kuinka paljon arvoa kasvatetaan
     * @param profileId profiili jonka tietoa päivitetään
     *
     * @throws NullPointerException jos haettava statistiikka on <code>null</code>
     */
    public void incrementBy(Statistics key, double amount, int profileId) {
        try {
            this.playerStatistics.incrementBy(profileId, key.getId(), amount);
        } catch (SQLException e) {
            LOG.warn("Could not increment statistic {} for profile {}", key.getName(), profileId);
        }
    }
}
