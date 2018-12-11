package toilari.otlite.game.profile.statistics;

import lombok.Getter;
import lombok.NonNull;

public enum Statistics {
    KILLS("Kills", 0, 0.0),
    ATTACKS_PERFORMED("Attacks Performed", 4, 0.0),
    DAMAGE_DEALT("Damage Dealt", 3, 0.0),

    TILES_MOVED("Tiles Moved", 1, 0.0),
    TURNS_PLAYED("Turns Played", 2, 0.0),

    FLOORS_CLEARED("Floors Cleared", 5, 0.0),
    BUTTONS_CLICKED("UI Buttons clicked", 6, 0.0);

    @Getter private final String name;
    @Getter private final int id;
    @Getter private final double defaultValue;

    Statistics(@NonNull String name, int id, double defaultValue) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
    }
}
