package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.*;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.KickControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.PerformIfNothingElseToDoEndTurnControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponentTest {
    @Test
    void targetIsNullAfterUpdateInputIfCharacterHasNoAbilities() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component)
        );
        world.getObjectManager().spawn(character);

        component.updateInput(ability);
        assertNull(component.getTarget());
        assertEquals(Direction.NONE, component.getTargetDirection());
    }

    @Test
    void targetIsNullAfterUpdateInputIfCharacterHasNoTargetedAbilities() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component),
            new AbilityEntry<>(1, new EndTurnAbility(), new PerformIfNothingElseToDoEndTurnControllerComponent())

        );
        world.getObjectManager().spawn(character);

        component.updateInput(ability);
        assertNull(component.getTarget());
        assertEquals(Direction.NONE, component.getTargetDirection());
    }

    @Test
    void targetIsNullAfterUpdateInputIfCharacterHasTargetedAbilityWhichIsLocked() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component),
            // Kick ability requires lvl 2 STRENGTH, so it will start as locked
            new AbilityEntry<>(1, new KickAbility(), new KickControllerComponent.AI())
        );
        world.getObjectManager().spawn(character);

        component.updateInput(ability);
        assertNull(component.getTarget());
        assertEquals(Direction.NONE, component.getTargetDirection());
    }

    @Test
    void targetIsNullAfterUpdateInputIfCharacterHasTargetedAbilityWhichIsOnCooldown() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 10);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation())
        );
        world.getObjectManager().spawn(character);

        attack.putOnCooldown();

        component.updateInput(ability);
        assertNull(component.getTarget());
        assertEquals(Direction.NONE, component.getTargetDirection());
    }

    @Test
    void targetIsNullAfterUpdateInputIfCharacterHasTargetedAbilityWhichCostsTooMuch() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(100, 10);
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation())
        );
        world.getObjectManager().spawn(character);

        component.updateInput(ability);
        assertNull(component.getTarget());
        assertEquals(Direction.NONE, component.getTargetDirection());
    }

    @Test
    void targetIsNullAfterUpdateInputIfCharacterHasTargetedAbilityWhichTheyCanUseAndPlayerIsTooFar() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(2, 2,
            new AbilityEntry<>(0, ability, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation())
        );
        world.getObjectManager().spawn(character);

        for (val direction : Direction.asIterable()) {
            player.setTilePos(character.getTileX() + direction.getDx() * 2, character.getTileY() + direction.getDy() * 2);
            component.updateInput(ability);
            assertNull(component.getTarget());
            assertEquals(Direction.NONE, component.getTargetDirection());
        }
    }

    @Test
    void targetIsCorrectAfterUpdateInputIfCharacterHasTargetedAbilityWhichTheyCanUseAndPlayerIsAdjacent() {
        val world = FakeWorld.create();

        val player = FakeCharacterObject.create();
        world.getObjectManager().spawn(player);
        world.getObjectManager().setPlayer(player);

        val ability = new TargetSelectorAbility();
        val component = new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ability, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation())
        );
        world.getObjectManager().spawn(character);

        for (val direction : Direction.asIterable()) {
            player.setTilePos(character.getTileX() + direction.getDx(), character.getTileY() + direction.getDy());
            component.updateInput(ability);
            assertEquals(player, component.getTarget());
            assertEquals(direction, component.getTargetDirection());
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void updateInputThrowsIfAbilityIsNull() {
        assertThrows(NullPointerException.class, () -> new AlwaysAttackAdjacentIfPossibleTargetSelectorControllerComponent().updateInput(null));
    }
}
