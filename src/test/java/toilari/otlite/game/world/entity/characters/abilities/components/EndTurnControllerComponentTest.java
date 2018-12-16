package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndTurnControllerComponentTest {
    @Test
    void doesNotWantIfCharacterIsAliveAndNotRemoved() {
        val world = new World(new TurnObjectManager());
        world.init();

        val ability = new EndTurnAbility();
        val component = new EndTurnControllerComponent() {
        };
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component)
        );
        world.getObjectManager().spawn(character);

        component.updateInput(ability);
        assertFalse(component.wants(ability));
    }

    @Test
    void wantsIfCharacterIsDead() {
        val world = new World(new TurnObjectManager());
        world.init();

        val ability = new EndTurnAbility();
        val component = new EndTurnControllerComponent() {
        };
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component)
        );
        world.getObjectManager().spawn(character);
        character.setHealth(0.0f);

        component.updateInput(ability);
        assertTrue(component.wants(ability));
    }

    @Test
    void wantsIfCharacterIsRemoved() {
        val world = new World(new TurnObjectManager());
        world.init();

        val ability = new EndTurnAbility();
        val component = new EndTurnControllerComponent() {
        };
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component)
        );
        world.getObjectManager().spawn(character);
        character.remove();

        component.updateInput(ability);
        assertTrue(component.wants(ability));
    }

    @Test
    void wantsIfCharacterIsDeadAndRemoved() {
        val world = new World(new TurnObjectManager());
        world.init();

        val ability = new EndTurnAbility();
        val component = new EndTurnControllerComponent() {
        };
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, component)
        );
        world.getObjectManager().spawn(character);
        character.setHealth(0.0f);
        character.remove();

        component.updateInput(ability);
        assertTrue(component.wants(ability));
    }
}
