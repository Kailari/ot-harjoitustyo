package toilari.otlite.game.profile.statistics;

import lombok.extern.slf4j.Slf4j;

/**
 * Seurattava statistiikka. Esim. liikuttujen ruutujen määrä, lyötyjen mörköjen lukumäärä, yms.
 * <p>
 * Koska erillisten statistiikkaluokan lisääminen ja käsitteleminen sekä kokonaisluvuille että liukuluvuille aiheuttaisi
 * enemmän päänvaivaa kuin mitä siitä on iloa, varastoidaan kaikki statistiikka <code>double</code>-tietotyyppinä.
 * Tästä seuraa että kasvavia kokonaislukuja voidaan varastoida tarkasti vain <code>2^53</code> asti, jonka jälkeen
 * signifikandin tarkkuus loppuu kesken ja tarkkuutta aletaan menettää (pienillä arvoilla kasvattaminen ei enää onnistu,
 * yms.).
 * </p>
 * <p>
 * Luku <code>2^53</code> on kuitenkin todennäköisesti enemmänkin kuin kyllin suuri kaikkien tarvittavien
 * statistiikkojen seurantaan, joten luvut <code>-1 * 2^53...2^53</code> varastoidaan normaalisti, ja lukualueen
 * ulkopuolelle jäävien lukujen varastoinnin yrittäminen kirjaa lokiin varoituksen.
 * </p>
 */
@Slf4j
public class PlayerStatistic {
    private static final long MAX_ACCURATE_VALUE = 2L << 53;

    private final int profileId;
    private final int statisticId;

    private final double value;

    public PlayerStatistic(int profileId, int statisticId, double value) {
        this.statisticId = statisticId;
        this.value = value;
        this.profileId = profileId;
    }

    public synchronized double getValue() {
        return this.value;
    }

    private static void checkAccuracy(double value) {
        if (value > MAX_ACCURATE_VALUE || value < -MAX_ACCURATE_VALUE) {
            LOG.warn("Losing statistic precision due to storage limitations!");
        }
    }
}
