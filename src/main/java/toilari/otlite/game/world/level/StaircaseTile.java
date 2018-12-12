package toilari.otlite.game.world.level;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.profile.statistics.Statistics;
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
        val player = character.getWorld().getObjectManager().getPlayer();
        if (!player.equals(character) || StaircaseTile.nextLevel == null) {
            return;
        }

        val healthRegen = player.getAttributes().getHealthRegen();
        player.heal(healthRegen);
        player.getWorld().getObjectManager().getEventSystem().fire(new CharacterEvent.Heal(player, healthRegen));

        val state = character.getWorld().getObjectManager().getGameState();
        if (state != null) {
            state.getGame().getStatistics().increment(Statistics.FLOORS_CLEARED, state.getGame().getActiveProfile().getId());
        }
        character.getWorld().changeLevel(StaircaseTile.nextLevel);
    }
}
