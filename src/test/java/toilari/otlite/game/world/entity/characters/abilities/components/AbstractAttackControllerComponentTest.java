package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractAttackControllerComponent;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class AbstractAttackControllerComponentTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void abilityPerformedThrowsWithIllegalParameters() {
        assertThrows(NullPointerException.class, () -> new TestAttackControllerComponent().abilityPerformed(null));
    }

    @Test
    void abilityPerformedRewardsExperienceIfTargetIsCharacterWhichDies() {
        val world = new World(new TurnObjectManager());
        world.init();

        val target = FakeCharacterObject.create();
        world.getObjectManager().spawn(target);
        target.setHealth(0.1f);

        val ability = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val component = FakeAttackControllerComponent.createWithoutTargetValidation();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(target, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));
        world.getObjectManager().spawn(character);

        if (ability.perform(component)) {
            component.abilityPerformed(ability);
        }
        assumeTrue(ability.isLastAttackKill());
        assertEquals(10, character.getLevels().getExperience());
    }

    @Test
    void abilityPerformedDoesNotRewardExperienceIfTargetIsCharacterWhichDoesNotDie() {
        val world = new World(new TurnObjectManager());
        world.init();

        val target = FakeCharacterObject.create();
        world.getObjectManager().spawn(target);

        val ability = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val component = FakeAttackControllerComponent.createWithoutTargetValidation();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(target, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));
        world.getObjectManager().spawn(character);

        if (ability.perform(component)) {
            component.abilityPerformed(ability);
        }
        assumeFalse(ability.isLastAttackKill());
        assertEquals(0, character.getLevels().getExperience());
    }

    @Test
    void abilityPerformedDoesNotTouchExperienceIfTargetIsNotCharacter() {
        val world = new World(new TurnObjectManager());
        world.init();

        val target = new GameObject();
        world.getObjectManager().spawn(target);

        val ability = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val component = FakeAttackControllerComponent.createWithoutTargetValidation();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, new TargetSelectorAbility(), FakeTargetSelectorControllerComponent.create(target, Direction.RIGHT)),
            new AbilityEntry<>(1, ability, component));
        world.getObjectManager().spawn(character);

        if (ability.perform(component)) {
            component.abilityPerformed(ability);
        }
        assertEquals(0, character.getLevels().getExperience());
    }

    @Test
    void wantsPerformOnDoesNotThrowDueToNullParameters() {
        assertDoesNotThrow(() -> new TestAttackControllerComponent().wantsPerformOn(new GameObject(), null));
        assertDoesNotThrow(() -> new TestAttackControllerComponent().wantsPerformOn(null, Direction.RIGHT));
        assertDoesNotThrow(() -> new TestAttackControllerComponent().wantsPerformOn(null, null));
    }

    @Test
    void wantsPerformOnReturnsTrueForValidTarget() {
        assertTrue(new TestAttackControllerComponent().wantsPerformOn(new GameObject(), Direction.RIGHT));
    }

    @Test
    void wantsPerformOnReturnsFalseForDirectionNONE() {
        assertFalse(new TestAttackControllerComponent().wantsPerformOn(new GameObject(), Direction.NONE));
    }

    private static class TestAttackControllerComponent extends AbstractAttackControllerComponent<FakeAttackAbility> {
        @Override
        protected void doUpdateInput(FakeAttackAbility ability) {
        }
    }
}
