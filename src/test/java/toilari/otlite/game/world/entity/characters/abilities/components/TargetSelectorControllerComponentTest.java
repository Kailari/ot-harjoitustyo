package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import static org.junit.jupiter.api.Assertions.*;

class TargetSelectorControllerComponentTest {
    @Test
    void setActiveFailsIfAbilityIsNotAvailable() {
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), component));

        component.init(character);
        assertThrows(IllegalStateException.class, () -> component.setActiveTargetedAbility(FakeAttackAbility.create(0, 0)));
    }

    @Test
    void setActiveSucceedsIfAbilityIsNull() {
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), component));

        component.init(character);
        assertDoesNotThrow(() -> component.setActiveTargetedAbility(null));
    }

    @Test
    void setActiveSucceedsIfAbilityIsAvailable() {
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attackAbility = FakeAttackAbility.create(0, 0);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attackAbility, FakeAttackControllerComponent.create()));

        component.init(character);
        assertDoesNotThrow(() -> component.setActiveTargetedAbility(attackAbility));
    }

    @Test
    void isActiveReturnsTrueAfterAbilityIsSetActive() {
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attackAbility = FakeAttackAbility.create(0, 0);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attackAbility, FakeAttackControllerComponent.create()));

        component.init(character);
        component.setActiveTargetedAbility(attackAbility);
        assertTrue(component.isActive(attackAbility));
    }

    @Test
    void isActiveReturnsFalseIfAbilityIsNotActive() {
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attackAbility, attackComponent));

        component.init(character);
        assertFalse(component.isActive(attackAbility));
    }

    @Test
    void isActiveReturnsFalseIfAbilityIsNotActiveAndAbilityIsInvalid() {
        val ts = new TargetSelectorAbility();
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ts, component));

        component.init(character);
        assertFalse(component.isActive(FakeAttackAbility.create(0, 0)));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void isActiveThrowsIfAbilityIsNull() {
        val ts = new TargetSelectorAbility();
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ts, component));

        component.init(character);
        assertThrows(NullPointerException.class, () -> component.isActive(null));
    }

    @Test
    void isAvailableAbilityReturnsFalseIfAbilityIsNotTargeted() {
        val ts = new TargetSelectorAbility();
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ts, component));

        component.init(character);
        assertFalse(component.isAvailableAbility(FakeAbility.createFree()));
    }

    @Test
    void isAvailableAbilityReturnsFalseIfAbilityIsNull() {
        val ts = new TargetSelectorAbility();
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ts, component)
        );

        component.init(character);
        assertFalse(component.isAvailableAbility(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void wantsThrowsIfAbilityIsNull() {
        assertThrows(NullPointerException.class, () -> FakeTargetSelectorControllerComponent.create(null, Direction.NONE).wants(null));
    }

    @Test
    void wantsReturnsFalseWithValidAbility() {
        val ts = new TargetSelectorAbility();
        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ts, component));

        component.init(character);
        assertFalse(component.wants(ts));
    }

    @Test
    void findTargetReturnsNullIfThereIsNoActiveAbility() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component));
        world.getObjectManager().spawn(character);

        assertNull(component.findTargetInDirection(Direction.RIGHT));
    }

    @Test
    void findTargetReturnsNullIfThereIsNoTarget() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attack = FakeAttackAbility.createWithoutTargetValidation(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        world.getObjectManager().spawn(character);

        component.setActiveTargetedAbility(attack);
        assertNull(component.findTargetInDirection(Direction.RIGHT));
    }

    @Test
    void findTargetReturnsNullIfTargetIsDead() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attack = FakeAttackAbility.createWithoutTargetValidation(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        world.getObjectManager().spawn(character);

        val target = FakeCharacterObject.createAt(1, 0);
        world.getObjectManager().spawn(target);
        target.setHealth(0.0f);

        component.setActiveTargetedAbility(attack);
        assertNull(component.findTargetInDirection(Direction.RIGHT));
    }

    @Test
    void findTargetReturnsNullIfTargetIsRemoved() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attack = FakeAttackAbility.createWithoutTargetValidation(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        world.getObjectManager().spawn(character);

        val target = FakeCharacterObject.createAt(1, 0);
        world.getObjectManager().spawn(target);
        target.remove();

        component.setActiveTargetedAbility(attack);
        assertNull(component.findTargetInDirection(Direction.RIGHT));
    }

    @Test
    void findTargetReturnsNullIfTargetIsDeadAndRemoved() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attack = FakeAttackAbility.createWithoutTargetValidation(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        world.getObjectManager().spawn(character);

        val target = FakeCharacterObject.createAt(1, 0);
        world.getObjectManager().spawn(target);
        target.setHealth(0.0f);
        target.remove();

        component.setActiveTargetedAbility(attack);
        assertNull(component.findTargetInDirection(Direction.RIGHT));
    }

    @Test
    void findTargetReturnsNonNullIfTargetIsAliveAndNotRemoved() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attack = FakeAttackAbility.createWithoutTargetValidation(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        world.getObjectManager().spawn(character);

        val target = FakeCharacterObject.createAt(1, 0);
        world.getObjectManager().spawn(target);

        component.setActiveTargetedAbility(attack);
        assertNotNull(component.findTargetInDirection(Direction.RIGHT));
    }

    @Test
    void findTargetReturnsNonNullIfTargetIsNotRemovedAndNotCharacter() {
        val world = FakeWorld.create();

        val component = FakeTargetSelectorControllerComponent.create(null, Direction.NONE);
        val attack = FakeAttackAbility.createWithoutTargetValidation(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(0, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        world.getObjectManager().spawn(character);

        val target = new GameObject();
        target.setTilePos(1, 0);
        world.getObjectManager().spawn(target);

        component.setActiveTargetedAbility(attack);
        assertNotNull(component.findTargetInDirection(Direction.RIGHT));
    }
}
