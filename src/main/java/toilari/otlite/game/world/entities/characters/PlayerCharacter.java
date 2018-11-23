package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.profile.tracking.Statistics;

public class PlayerCharacter extends AbstractCharacter {
    @Getter private long deathTime;

    public PlayerCharacter() {
        super(new CharacterAttributes(10.0f, 1, 1, 2));
    }

    @Override
    public boolean move(int dx, int dy) {
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

        if (target.isDead()) {
            val game = getWorld().getObjectManager().getGameState().getGame();
            game.getStatistics().increment(Statistics.KILLS, game.getActiveProfile().getId());
        }
    }

    @Override
    public void remove() {
        this.deathTime = System.currentTimeMillis();
        super.remove();
    }
}
