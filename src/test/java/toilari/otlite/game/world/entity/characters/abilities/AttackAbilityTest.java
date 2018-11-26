package toilari.otlite.game.world.entity.characters.abilities;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AttackControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class AttackAbilityTest {
    @Test
    void getCooldownLengthMatchesCharacterAttributes() {
        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        assertEquals(character.getAttributes().getAttackCooldown(), ability.getCooldownLength());
    }

    @Test
    void getCostMatchesCharacterAttributes() {
        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        assertEquals(character.getAttributes().getAttackCost(), ability.getCost());
    }

    @Test
    void canAttackReturnsFalseIfAttackingSelf() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        assertFalse(ability.canAttack(character));
    }

    @Test
    void canAttackReturnsFalseIfAttackingNull() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        assertFalse(ability.canAttack(null));
    }

    @Test
    void canAttackReturnsFalseIfAttackingRemovedObject() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new GameObject();
        manager.spawn(other);
        other.remove();

        assertFalse(ability.canAttack(other));
    }

    @Test
    void canAttackReturnsFalseIfAttackingNonCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        assertFalse(ability.canAttack(new GameObject()));
    }

    @Test
    void canAttackReturnsFalseIfAttackingDeadCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);
        other.setHealth(0.0f);

        assertFalse(ability.canAttack(other));
    }

    @Test
    void canAttackReturnsFalseIfAttackingRemovedDeadCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);
        other.setHealth(0.0f);
        other.remove();

        assertFalse(ability.canAttack(other));
    }

    @Test
    void canAttackReturnsTrueIfAttackingValidNotDeadNotRemovedCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);

        assertTrue(ability.canAttack(other));
    }

    @Test
    void canAttackReturnsFalseIfAttackingOwnCoordinates() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        assertFalse(ability.canAttack(6, 9));
    }

    @Test
    void canAttackReturnsFalseIfAttackingEmptyCoordinates() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        assertFalse(ability.canAttack(9, 6));
    }

    @Test
    void canAttackReturnsFalseIfAttackingCoordinatesWithRemovedObject() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new GameObject();
        other.setTilePos(9, 6);
        manager.spawn(other);
        other.remove();

        assertFalse(ability.canAttack(9, 6));
    }

    @Test
    void canAttackReturnsFalseIfAttackingCoordinatesWithDeadCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        other.setTilePos(9, 6);
        manager.spawn(other);
        other.setHealth(0.0f);

        assertFalse(ability.canAttack(9, 6));
    }

    @Test
    void canAttackReturnsFalseIfAttackingCoordinatesWithDeadRemovedCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        other.setTilePos(9, 6);
        manager.spawn(other);
        other.setHealth(0.0f);
        other.remove();

        assertFalse(ability.canAttack(9, 6));
    }

    @Test
    void canAttackReturnsTrueIfAttackingCoordinatesWithValidNotDeadNotRemovedCharacter() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        character.setTilePos(6, 9);
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        other.setTilePos(9, 6);
        manager.spawn(other);

        assertTrue(ability.canAttack(9, 6));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void performThrowsIfComponentIsNull() {
        val character = new FakeCharacterObject();
        assertThrows(NullPointerException.class,
            () -> new AttackAbility(character, 0).perform(null));
    }

    @Test
    void performReturnsFalseIfTargetIsNull() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val component = new TestAttackControllerComponent(character, null);
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new GameObject();
        manager.spawn(other);
        other.remove();

        val component = new TestAttackControllerComponent(character, other);
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsDead() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);
        other.setHealth(0.0f);

        val component = new TestAttackControllerComponent(character, other);
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsFalseIfTargetIsDeadAndRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);
        other.setHealth(0.0f);
        other.remove();

        val component = new TestAttackControllerComponent(character, other);
        assertFalse(ability.perform(component));
    }

    @Test
    void performReturnsTrueIfTargetIsNotDeadAndNotRemoved() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);

        val component = new TestAttackControllerComponent(character, other);
        assertTrue(ability.perform(component));
    }

    @Test
    void performReducesTargetHealth() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);

        val component = new TestAttackControllerComponent(character, other);
        ability.perform(component);

        assertEquals(9.0f, other.getHealth());
    }

    @Test
    void performFailsAfterTargetsHealthReachesZeroAfterMultipleAttacks() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new FakeCharacterObject();
        val ability = new AttackAbility(character, 0);
        manager.spawn(character);

        val other = new FakeCharacterObject();
        manager.spawn(other);

        val component = new TestAttackControllerComponent(character, other);
        for (int i = 0; i < 10; i++) {
            ability.perform(component);
        }

        assertFalse(ability.perform(component));
    }


    private class TestAttackControllerComponent extends AttackControllerComponent {
        private GameObject target;

        private TestAttackControllerComponent(CharacterObject character, GameObject target) {
            super(character);
            this.target = target;
        }

        @Override
        public GameObject getTarget() {
            return this.target;
        }

        @Override
        public boolean wants(@NonNull AttackAbility ability) {
            return true;
        }

        @Override
        public void updateInput(@NonNull AttackAbility ability) {
        }
    }
}
