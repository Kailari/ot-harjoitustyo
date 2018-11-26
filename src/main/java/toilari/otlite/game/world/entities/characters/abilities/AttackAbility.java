package toilari.otlite.game.world.entities.characters.abilities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AttackControllerComponent;

/**
 * Hahmon kyky hyökätä.
 */
public class AttackAbility extends AbstractAbility<AttackAbility, AttackControllerComponent> {
    /**
     * Luo uuden kyvyn.
     *
     * @param character hahmo jolle kyky lisätään
     * @param priority  kyvyn prioriteetti
     */
    public AttackAbility(CharacterObject character, int priority) {
        super(character, priority);
    }

    @Override
    public int getCost() {
        return getCharacter().getAttributes().getAttackCost();
    }

    @Override
    public int getCooldownLength() {
        return 0;
    }

    @Override
    public boolean perform(@NonNull AttackControllerComponent component) {
        val target = component.getTarget();
        if (target == null || target.isRemoved()) {
            return false;
        }

        val amount = getCharacter().getAttributes().getAttackDamage(getCharacter().getLevels());
        float current = target.getHealth();
        target.setHealth(Math.max(0, current - amount));


        if (target.isDead()) {
            target.setHealth(0.0f);
            target.remove();
        }

        getCharacter().getWorld().getObjectManager().getGameState().getEventSystem().fire(new CharacterEvent.Damage(getCharacter(), target, amount));
        return true;
    }
}
