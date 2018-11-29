package toilari.otlite.game.world.entities.characters.abilities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.IHealthHandler;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.components.AttackControllerComponent;

/**
 * Hahmon kyky hyökätä.
 */
@NoArgsConstructor
public class AttackAbility extends AbstractAbility<AttackAbility, AttackControllerComponent> {
    @Getter private boolean lastAttackKill;
    @Getter private float lastAttackDamage;

    /**
     * Testaa voiko hahmo hyökätä annettuihin koordinaatteihin.
     *
     * @param x tarkistettava x-koordinaatti
     * @param y tarkistettava y-koordinaatti
     * @return <code>true</code> jos voidaan hyökätä, muulloin <code>false</code>
     */
    public boolean canAttack(int x, int y) {
        val objectAtTarget = getCharacter().getWorld().getObjectAt(x, y);
        return canAttack(objectAtTarget);
    }

    /**
     * Testaa voiko hahmo hyökätä annetun objektin kimppuun. Hyökkääminen onnistuu jos kohde ei ole null,
     * sitä ei ole poistettu, se on {@link CharacterObject hahmo} ja se ei ole kuollut
     *
     * @param target kohde jonka kimppuun hyökätään
     * @return <code>true</code> jos voidaan hyökätä, muulloin <code>false</code>
     */
    public boolean canAttack(GameObject target) {
        if (!(target instanceof CharacterObject) || target.isRemoved()) {
            return false;
        }

        val character = (CharacterObject) target;
        return !character.isDead() && !getCharacter().equals(character);
    }

    @Override
    public int getCost() {
        return getCharacter().getAttributes().getAttackCost();
    }

    @Override
    public int getCooldownLength() {
        return getCharacter().getAttributes().getAttackCooldown();
    }

    @Override
    public boolean perform(@NonNull AttackControllerComponent component) {
        val target = component.getTarget();
        if (!canAttack(target)) {
            return false;
        }

        this.lastAttackKill = false;
        this.lastAttackDamage = 0.0f;

        val amount = calculateDamage();
        if (target instanceof IHealthHandler) {
            this.lastAttackDamage = amount;
            dealDamage(target, (IHealthHandler) target, amount);
        }

        if (hasEventSystem()) {
            getEventSystem().fire(new CharacterEvent.Damage(getCharacter(), target, amount));
        }
        return true;
    }

    protected float calculateDamage() {
        return getCharacter().getAttributes().getAttackDamage(getCharacter().getLevels());
    }

    private void dealDamage(GameObject target, IHealthHandler targetWithHealth, float amount) {
        float current = targetWithHealth.getHealth();
        targetWithHealth.setHealth(Math.max(0, current - amount));

        if (targetWithHealth.isDead()) {
            targetWithHealth.setHealth(0.0f);
            target.remove();
            this.lastAttackKill = true;
        }
    }
}
