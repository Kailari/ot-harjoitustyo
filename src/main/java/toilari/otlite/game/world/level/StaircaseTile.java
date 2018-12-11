package toilari.otlite.game.world.level;

import lombok.NonNull;
import toilari.otlite.game.world.entities.characters.CharacterObject;

/**
 * Ruututyyppi johon siirtymällä pelaaja pääsee seuraavaan kerrokseen.
 */
public class StaircaseTile extends Tile {
    static String nextLevel;

    /**
     * Luo uuden portaikkoruututyypin.
     *
     * @param wall      kohdellaanko ruutua seinänä
     * @param dangerous kohdellaanko ruutua vaarallisena
     * @param tileIndex ruudun ulkonäön indeksi
     * @param id        ruudun tunniste
     * @throws NullPointerException jos id on <code>null</code>
     */
    public StaircaseTile(boolean wall, boolean dangerous, int tileIndex, @NonNull String id) {
        super(wall, dangerous, tileIndex, id);
    }

    @Override
    public void onCharacterEnter(int x, int y, @NonNull CharacterObject character) {
        if (!character.getWorld().getObjectManager().getPlayer().equals(character) || StaircaseTile.nextLevel == null) {
            return;
        }

        character.getWorld().changeLevel(StaircaseTile.nextLevel);
    }
}
