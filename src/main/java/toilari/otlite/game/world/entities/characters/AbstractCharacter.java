package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;
import toilari.otlite.game.world.entities.characters.controller.CharacterController;
import toilari.otlite.game.world.level.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Hahmo pelimaailmassa.
 */
@Slf4j
public abstract class AbstractCharacter extends GameObject {
    @Getter @Setter private transient float health;
    @Getter private transient CharacterController controller;
    @Getter private transient long lastAttackTime;
    @Getter private transient float lastAttackAmount;
    @Getter private transient AbstractCharacter lastAttackTarget;

    @Getter private final CharacterAttributes attributes;
    @Getter private final CharacterLevels levels;
    @Getter private final List<IAbility> abilities;

    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> void addAbility(A ability, IControllerComponent<A> component) {
        this.abilities.add(ability);
        this.controller.registerComponent(ability, component);
    }

    protected AbstractCharacter(@NonNull CharacterAttributes attributes) {
        this.attributes = attributes;
        this.abilities = new ArrayList<>();
        this.levels = new CharacterLevels();
        this.controller = new CharacterController(this);
    }

    @Override
    public void init() {
        super.init();
        this.health = getAttributes().getMaxHealth(this.levels);
    }

    /**
     * Tarkistaa onvatko hahmon terveyspisteet nollassa.
     *
     * @return <code>true</code> jos hahmon terveyspisteet ovat likimain nolla
     */
    public boolean isDead() {
        return this.health < 0.000001f;
    }

    /**
     * Päivitysrutiini jota kutsutaan vain hahmon omalla vuorolla.
     *
     * @param turnManager objekti-/vuoromanageri jolla hahmojen vuoroja hallinnoidaan
     * @throws NullPointerException jos vuoromanageri on <code>null</code>
     */
    public void updateOnTurn(@NonNull TurnObjectManager turnManager) {
        if (this.controller == null) {
            return;
        }

        this.abilities.stream()
            .sorted(Comparator.comparingInt(IAbility::getPriority))
            .forEach((a) -> handleAbility(turnManager, a));
    }

    public void updateAfterTurn() {
        for (val ability : this.abilities) {
            if (ability.isOnCooldown()) {
                ability.reduceCooldownTimer();
            }
        }
    }

    private <A extends IAbility<A, C>, C extends IControllerComponent<A>> void handleAbility(@NonNull TurnObjectManager turnManager, @NonNull A ability) {
        if (ability.isOnCooldown()) {
            return;
        }

        val component = this.controller.getComponentFor(ability);
        component.updateInput();

        val cost = ability.getCost();
        if (component.wants(ability) && canAfford(turnManager, cost)) {
            if (ability.perform(this.controller, component)) {
                turnManager.spendActionPoints(cost);
                ability.setOnCooldown();
            }
        }
    }

    private boolean canAfford(TurnObjectManager turnManager, int cost) {
        return turnManager.getRemainingActionPoints() >= cost;
    }


    /**
     * Yrittää hyökätä annettuun suuntaan.
     *
     * @param dx siirtymä x-akselilla
     * @param dy siirtymä y-akselilla
     * @return <code>true</code> jos hyökkäys tapahtui, muulloin <code>false</code>
     */
    private boolean attack(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = getX() / Tile.SIZE_IN_WORLD + dx;
        int newY = getY() / Tile.SIZE_IN_WORLD + dy;

        if (!canAttack(newX, newY)) {
            return false;
        }

        attack((AbstractCharacter) getWorld().getObjectAt(newX, newY), this.getAttributes().getAttackDamage(this.levels));
        return true;
    }

    /**
     * Testaa voiko hahmo hyökätä annettuun suuntaan. Hyökkääminen onnistuu jos koordinaateista löytyy toinen hahmo,
     * jota ei vielä ole poistettu.
     *
     * @param x tarkistettava x-koordinaatti
     * @param y tarkistettava y-koordinaatti
     * @return <code>true</code> jos voidaan hyökätä, muulloin <code>false</code>
     */
    public boolean canAttack(int x, int y) {
        val objectAtTarget = getWorld().getObjectAt(x, y);
        return objectAtTarget instanceof AbstractCharacter && !objectAtTarget.isRemoved();

    }

    /**
     * Tekee kohteeseen annetun määrän vahinkopisteitä.
     *
     * @param target kohde jota vahingoitetaan
     * @param amount vahingon määrä
     */
    protected void attack(@NonNull AbstractCharacter target, float amount) {
        if (target.isRemoved()) {
            return;
        }

        float current = target.getHealth();
        target.setHealth(Math.max(0, current - amount));

        this.lastAttackTime = System.currentTimeMillis();
        this.lastAttackAmount = current - target.getHealth();
        this.lastAttackTarget = target;

        if (target.isDead()) {
            target.setHealth(0.0f);
            target.remove();
        }
    }
}