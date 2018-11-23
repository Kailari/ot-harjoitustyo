package toilari.otlite.game.world.entities.characters;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.profile.tracking.Statistics;

public class PlayerCharacter extends AbstractCharacter {
    @Override
    protected boolean move(int dx, int dy) {
        if (super.move(dx, dy)) {
            val game = getWorld().getObjectManager().getGameState().getGame();
            game.getStatistics().increment(Statistics.TILES_MOVED, game.getActiveProfile().getId());
            return true;
        }

        return false;
    }

    @Override
    public void attack(@NonNull AbstractCharacter target, float amount) {
        super.attack(target, amount);

        if (target.getHealth() < 0.000001) {
            val game = getWorld().getObjectManager().getGameState().getGame();
            game.getStatistics().increment(Statistics.KILLS, game.getActiveProfile().getId());
        }
    }
}
