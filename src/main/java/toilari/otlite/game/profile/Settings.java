package toilari.otlite.game.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Pelaajaprofiilikohtaiset asetukset.
 */
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    @Getter private boolean autoEndTurn = false;
}
