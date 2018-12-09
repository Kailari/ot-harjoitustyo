package toilari.otlite.game.world.entity.characters.abilities;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;

import static org.junit.jupiter.api.Assertions.*;

class AbstractAttackAbilityTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void canPerformOnThrowsIfDirectionIsNull() {
        assertThrows(NullPointerException.class, () -> FakeAttackAbility.create(1, 0).canPerformOn(FakeCharacterObject.create(), null));
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingSelf() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        assertFalse(ability.canPerformOn(character, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(character, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingNull() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        assertFalse(ability.canPerformOn(null, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(null, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingRemovedObject() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        val other = new GameObject();
        manager.spawn(other);
        other.remove();

        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(other, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingNonCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        val nonCharacterObject = new GameObject();
        manager.spawn(nonCharacterObject);
        assertFalse(ability.canPerformOn(nonCharacterObject, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(nonCharacterObject, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingDeadCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        val other = FakeCharacterObject.createAt(7, 9);
        manager.spawn(other);

        other.setHealth(0.0f);
        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(other, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingRemovedDeadCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        val other = FakeCharacterObject.createAt(7, 9);
        manager.spawn(other);

        other.setHealth(0.0f);
        other.remove();
        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(other, direction));
        }
    }

    @Test
    void canPerformOnReturnsTrueIfAttackingValidNotDeadNotRemovedCharacterInAnyDirectionExceptNone() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        manager.spawn(character);

        val other = FakeCharacterObject.createAt(7, 9);
        manager.spawn(other);

        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertTrue(ability.canPerformOn(other, direction));
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void performThrowsIfComponentIsNull() {
        assertThrows(NullPointerException.class, () -> new AttackAbility().perform(null));
    }

    @Test
    void performReturnsFalseIfTargetIsNull() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val other = FakeCharacterObject.create();
        manager.spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);

        other.remove();
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsDead() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val other = FakeCharacterObject.create();
        manager.spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);

        other.setHealth(0.0f);

        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsDeadAndRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val other = FakeCharacterObject.create();
        manager.spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);

        other.setHealth(0.0f);
        other.remove();

        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsTrueIfTargetIsNotDeadAndNotRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val other = FakeCharacterObject.create();
        manager.spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);

        assertTrue(ability.perform(component));
    }

    @Test
    void performReducesTargetHealth() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val other = FakeCharacterObject.create();
        manager.spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);

        ability.perform(component);
        assertEquals(9.0f, other.getHealth());
    }

    @Test
    void performFailsAfterTargetsHealthReachesZeroAfterMultipleAttacks() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val other = FakeCharacterObject.create();
        manager.spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        manager.spawn(character);
        for (int i = 0; i < 10; i++) {
            ability.perform(component);
        }

        assertFalse(ability.perform(component));
    }
}
