package toilari.otlite.game.world.entities.characters.abilities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import toilari.otlite.game.event.EventSystem;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractControllerComponent;

public abstract class AbstractAbility<A extends AbstractAbility<A, C>, C extends AbstractControllerComponent<A>> implements IAbility<A, C> {
    @Getter(AccessLevel.PROTECTED) private CharacterObject character;
    @Getter @Setter private int priority = 0;
    private int cooldownTimer;

    @Override
    public void init(@NonNull CharacterObject character, int priority) {
        this.priority = priority;
        this.character = character;
    }

    @Override
    public boolean isOnCooldown() {
        return this.cooldownTimer > 0;
    }

    @Override
    public int getRemainingCooldown() {
        return this.cooldownTimer;
    }

    @Override
    public void putOnCooldown() {
        this.cooldownTimer = this.getCooldownLength();
    }

    @Override
    public void reduceCooldownTimer() {
        if (this.cooldownTimer == 0) {
            throw new IllegalStateException("Cooldown cannot go negative!");
        }
        this.cooldownTimer--;
    }

    protected boolean hasEventSystem() {
        return getCharacter().getWorld().getObjectManager().getGameState() != null;
    }

    protected EventSystem getEventSystem() {
        return getCharacter().getWorld().getObjectManager().getGameState().getEventSystem();
    }
}
