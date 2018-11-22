package toilari.otlite.game.profile.tracking;

import lombok.Getter;
import lombok.NonNull;

public enum Statistics {
    KILLS("Kills", 0, 0.0),
    TILES_MOVED("Tiles Moved", 1, 0.0);

    @Getter private final String name;
    @Getter private final int id;
    @Getter private final double defaultValue;

    Statistics(@NonNull String name, int id, double defaultValue) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
    }
}
