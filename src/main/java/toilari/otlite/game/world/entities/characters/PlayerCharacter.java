package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.profile.tracking.Statistics;

public class PlayerCharacter extends AbstractCharacter {
    @Getter private long deathTime;

    public PlayerCharacter() {
        this(new CharacterAttributes(
            2,
            2,
            0,
            0.1f,
            0.0f,
            0.001f,
            0.0f,
            0.0f,
            1.0f,
            0.1f,
            0.0f,
            0.1f,
            10.0f,
            0.1f,
            0.5f,
            0.001f
        ));
    }

    public PlayerCharacter(CharacterAttributes attributes) {
        super(attributes);
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
