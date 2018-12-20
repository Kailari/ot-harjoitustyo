package toilari.otlite.game.world.entity.characters.abilities;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.*;
import toilari.otlite.game.event.CharacterEvent;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.BlockAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.BlockControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class AbstractAttackAbilityTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void canPerformOnThrowsIfDirectionIsNull() {
        assertThrows(NullPointerException.class, () -> FakeAttackAbility.create(1, 0).canPerformOn(FakeCharacterObject.create(), null));
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingNull() {
        val world = FakeWorld.create();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        world.getObjectManager().spawn(character);

        assertFalse(ability.canPerformOn(null, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(null, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingRemovedObject() {
        val world = FakeWorld.create();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        world.getObjectManager().spawn(character);

        val other = new GameObject();
        world.getObjectManager().spawn(other);
        other.remove();

        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(other, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingDeadCharacter() {
        val world = FakeWorld.create();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        world.getObjectManager().spawn(character);

        val other = FakeCharacterObject.createAt(7, 9);
        world.getObjectManager().spawn(other);

        other.setHealth(0.0f);
        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(other, direction));
        }
    }

    @Test
    void canPerformOnReturnsFalseIfAttackingRemovedDeadCharacter() {
        val world = FakeWorld.create();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        world.getObjectManager().spawn(character);

        val other = FakeCharacterObject.createAt(7, 9);
        world.getObjectManager().spawn(other);

        other.setHealth(0.0f);
        other.remove();
        assertFalse(ability.canPerformOn(other, Direction.NONE));
        for (val direction : Direction.asIterable()) {
            assertFalse(ability.canPerformOn(other, direction));
        }
    }

    @Test
    void canPerformOnReturnsTrueIfAttackingValidNotDeadNotRemovedCharacterInAnyDirectionExceptNone() {
        val world = FakeWorld.create();

        val ability = FakeAttackAbility.create(1, 0);
        val character = FakeCharacterObject.createAtWithAbilities(6, 9,
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, FakeAttackControllerComponent.create()));
        ability.init(character);
        world.getObjectManager().spawn(character);

        val other = FakeCharacterObject.createAt(7, 9);
        world.getObjectManager().spawn(other);

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
        val world = FakeWorld.create();

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsRemoved() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        other.remove();
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsDead() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        other.setHealth(0.0f);

        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsDeadAndRemoved() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        other.setHealth(0.0f);
        other.remove();

        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsTrueIfTargetIsNotDeadAndNotRemoved() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        assertTrue(ability.perform(component));
    }

    @Test
    void performReturnsTrueForDummyNonCharacterNonHealthHandlerTarget() {
        val world = FakeWorld.create();

        val other = new GameObject();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        assertTrue(ability.perform(component));
    }

    // "variables used in lambda expressions need to be final or effectively final" - so we need to use fields instead.
    private int blockedAttacks = 0;
    private int evadedAttacks = 0;

    @Test
    void targetEvasionAndBlockingProcs() {
        val world = FakeWorld.create();

        this.blockedAttacks = 0;
        this.evadedAttacks = 0;
        world.getObjectManager().getEventSystem().subscribeTo(CharacterEvent.BlockedAttack.class, (e) -> this.blockedAttacks++);
        world.getObjectManager().getEventSystem().subscribeTo(CharacterEvent.MissedAttack.class, (e) -> this.evadedAttacks++);

        val block = new BlockAbility();
        val blockComponent = new BlockControllerComponent() {
            @Override
            protected void doUpdateInput(@NonNull BlockAbility ability) {
            }
        };
        val other = FakeCharacterObject.createImmortalWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(null, Direction.NONE)),
            new AbilityEntry<>(1, block, blockComponent)
        );
        world.getObjectManager().spawn(other);
        other.getLevels().rewardExperience(1000);
        other.getLevels().setAttributeLevel(Attribute.ENDURANCE, 2);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val ts = FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        for (int i = 0; i < 10000; i++) {
            ts.setTarget(other, Direction.RIGHT);
            if (i % 5 == 0) {
                block.perform(blockComponent);
            }
            ability.perform(component);
        }

        assertEquals(2000, this.blockedAttacks);
        assertEquals(419, this.evadedAttacks);
    }

    @Test
    void performReducesTargetHealth() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);

        ability.perform(component);
        assertEquals(9.0f, other.getHealth());
    }

    @Test
    void performFailsAfterTargetsHealthReachesZeroAfterMultipleAttacks() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val ability = FakeAttackAbility.create(1, 0);
        val component = FakeAttackControllerComponent.create();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(other, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));

        world.getObjectManager().spawn(character);
        for (int i = 0; i < 10; i++) {
            ability.perform(component);
        }

        assertFalse(ability.perform(component));
    }
}
