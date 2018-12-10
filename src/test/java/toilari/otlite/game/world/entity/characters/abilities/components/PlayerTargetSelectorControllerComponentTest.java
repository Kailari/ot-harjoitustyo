package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.KickControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerTargetSelectorControllerComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlayerTargetSelectorControllerComponentTest {
    @Test
    void cycleTargetsSelectsCorrectAfterSettingTargetManually() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val charactersAround = FakeCharacterObject.createAround(manager, character);

        component.setActiveTargetedAbility(attack);
        component.setTarget(charactersAround.get(Direction.DOWN), Direction.DOWN);
        component.cycleTargets();
        assertEquals(charactersAround.get(Direction.LEFT), component.getTarget());
        assertEquals(Direction.LEFT, component.getTargetDirection());
    }

    @Test
    void cycleTargetsDoesNotChangeTargetIfOnlyOneIsAvailable() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val target = FakeCharacterObject.createAt(1, 2);
        manager.spawn(target);

        component.setActiveTargetedAbility(attack);
        component.setTarget(target, Direction.DOWN);
        component.cycleTargets();
        assertEquals(target, component.getTarget());
        assertEquals(Direction.DOWN, component.getTargetDirection());
    }

    @Test
    void cycleTargetsSetsTargetToNullIfNoTargetsAreAvailable() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val target = FakeCharacterObject.createAt(1, 1337);
        manager.spawn(target);

        component.setActiveTargetedAbility(attack);
        component.setTarget(target, Direction.DOWN);
        component.cycleTargets();
        assertNull(component.getTarget());
        assertEquals(Direction.NONE, component.getTargetDirection());
    }

    @Test
    void pressingAbilityKeyDoesNothingIfCharacterHasNoSuchAbility() {
        val input = new FakeInputHandler(Key.TWO);
        Input.init(input);
        input.update();

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        FakeCharacterObject.createAround(manager, character);

        component.updateInput(ts);
        assertNull(component.getTarget());
        assertNull(component.getActive());
    }

    @Test
    void pressingAbilityKeySetsMatchingAbilityAsActive() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val charactersAround = FakeCharacterObject.createAround(manager, character);

        component.updateInput(ts);
        assertEquals(attack, component.getActive());
        assertEquals(Direction.UP, component.getTargetDirection());
        assertEquals(charactersAround.get(Direction.UP), component.getTarget());
    }

    @Test
    void pressingAbilityKeyDoesNothingIfAbilityIsOnCooldown() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 9001);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        FakeCharacterObject.createAround(manager, character);

        attack.putOnCooldown();
        component.updateInput(ts);
        assertNull(component.getActive());
        assertNull(component.getTarget());
    }

    @Test
    void pressingAbilityKeyDoesNothingIfCannotAffordAbility() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(9001, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        FakeCharacterObject.createAround(manager, character);

        component.updateInput(ts);
        assertNull(component.getActive());
        assertNull(component.getTarget());
    }

    @Test
    void pressingAbilityKeyCyclesTargetsWhenActiveAbilityIsSet() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val charactersAround = FakeCharacterObject.createAround(manager, character);

        for (val direction : Direction.asIterable()) {
            component.updateInput(ts);
            assertEquals(attack, component.getActive());
            assertEquals(direction, component.getTargetDirection());
            assertEquals(charactersAround.get(direction), component.getTarget());
        }
    }

    @Test
    void targetDoesNotChangeIfChangingAbilityWhileSelectingTarget() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val attack2 = new KickAbility();
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()),
            new AbilityEntry<>(2, attack2, new KickControllerComponent.Player()));
        manager.spawn(character);

        val charactersAround = FakeCharacterObject.createAround(manager, character);

        component.updateInput(ts);
        input.setPressedKeys(Key.TWO);
        component.updateInput(ts);
        assertEquals(attack2, component.getActive());
        assertEquals(Direction.UP, component.getTargetDirection());
        assertEquals(charactersAround.get(Direction.UP), component.getTarget());
    }

    @Test
    void arrowKeysSelectCorrectTarget() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val charactersAround = FakeCharacterObject.createAround(manager, character);

        component.updateInput(ts);

        int i = 0;
        Key[] keys = {Key.LEFT, Key.DOWN, Key.UP, Key.RIGHT};
        Direction[] directions = {Direction.LEFT, Direction.DOWN, Direction.UP, Direction.RIGHT};
        for (val direction : directions) {
            input.setPressedKeys(keys[i++]);
            component.updateInput(ts);
            assertEquals(attack, component.getActive());
            assertEquals(direction, component.getTargetDirection());
            assertEquals(charactersAround.get(direction), component.getTarget());
        }
    }

    @Test
    void arrowKeysDoNothingfThereIsNoTargetInDirection() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val target = FakeCharacterObject.createAt(2, 1);
        manager.spawn(target);
        component.updateInput(ts);

        Key[] keys = {Key.LEFT, Key.DOWN, Key.UP};
        for (val key : keys) {
            input.setPressedKeys(key);
            component.updateInput(ts);
            assertEquals(attack, component.getActive());
            assertEquals(target, component.getTarget());
            assertEquals(Direction.RIGHT, component.getTargetDirection());
        }
    }

    @Test
    void findingTargetStopsIfAllTargetsMoveOutOfRange() {
        val input = new FakeInputHandler(Key.ONE);
        Input.init(input);

        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val ts = new TargetSelectorAbility();
        val component = new PlayerTargetSelectorControllerComponent();
        val attack = FakeAttackAbility.createWithoutTargetValidation(0, 0);
        val character = FakeCharacterObject.createAtWithAbilities(1, 1,
            new AbilityEntry<>(0, ts, component),
            new AbilityEntry<>(1, attack, FakeAttackControllerComponent.createWithoutTargetValidation()));
        manager.spawn(character);

        val charactersAround = FakeCharacterObject.createAround(manager, character);
        component.updateInput(ts);

        for (val direction : Direction.asIterable()) {
            val c = charactersAround.get(direction);
            c.setTilePos(c.getTileX() + direction.getDx(), c.getTileY() + direction.getDy());
        }

        component.updateInput(ts);
        assertNull(component.getActive());
    }
}
