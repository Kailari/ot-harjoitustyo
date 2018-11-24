package toilari.otlite.game.world.entities.characters.abilities;

import lombok.AccessLevel;
import lombok.Getter;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractControllerComponent;

public abstract class AbstractAbility<A extends AbstractAbility<A, C>, C extends AbstractControllerComponent<A>> implements IAbility<A, C> {
    @Getter(AccessLevel.PROTECTED) private final AbstractCharacter character;
    @Getter private final int priority;
    private int cooldownTimer;


    protected AbstractAbility(AbstractCharacter character, int priority) {
        this.character = character;
        this.priority = priority;
    }

    @Override
    public boolean isOnCooldown() {
        return this.cooldownTimer > 0;
    }

    @Override
    public void setOnCooldown() {
        this.cooldownTimer = this.getCooldownLength();
    }

    @Override
    public void reduceCooldownTimer() {
        if (this.cooldownTimer == 0) {
            throw new IllegalStateException("Cooldown cannot go negative!");
        }
    }
}
