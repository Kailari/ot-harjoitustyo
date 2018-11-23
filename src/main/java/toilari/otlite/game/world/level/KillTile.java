package toilari.otlite.game.world.level;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;

public class KillTile extends Tile {
    public KillTile(boolean wall, boolean dangerous, int tileIndex, @NonNull String id) {
        super(wall, dangerous, tileIndex, id);
    }

    @Override
    public void onCharacterEnter(int x, int y, @NonNull AbstractCharacter character) {
        character.remove();
    }
}
