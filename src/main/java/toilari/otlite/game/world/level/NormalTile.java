package toilari.otlite.game.world.level;

import lombok.NonNull;

public class NormalTile extends Tile {
    public NormalTile(boolean wall, boolean dangerous, int tileIndex, @NonNull String id) {
        super(wall, dangerous, tileIndex, id);
    }
}
