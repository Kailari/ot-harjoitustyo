package toilari.otlite.game.world.level;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;

/**
 * Ruutu jonka päälle astuminen tappaa pelaajan.
 */
public class KillTile extends Tile {
    /**
     * Luo uuden tappavan ruututyypin.
     *
     * @param wall      kohdellaanko ruutua seinänä
     * @param dangerous kohdellaanko ruutua vaarallisena
     * @param tileIndex ruudun ulkonäön indeksi
     * @param id        ruudun tunniste
     * @throws NullPointerException jos id on <code>null</code>
     */
    public KillTile(boolean wall, boolean dangerous, int tileIndex, @NonNull String id) {
        super(wall, dangerous, tileIndex, id);
    }

    @Override
    public void onCharacterEnter(int x, int y, @NonNull CharacterObject character) {
        character.setHealth(0.0f);
        character.remove();
    }
}
