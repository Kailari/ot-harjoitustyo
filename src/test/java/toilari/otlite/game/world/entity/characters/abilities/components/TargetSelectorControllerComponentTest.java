package toilari.otlite.game.world.entity.characters.abilities.components;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeAbility;
import toilari.otlite.fake.FakeAttackAbility;
import toilari.otlite.fake.FakeAttackControllerComponent;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.TargetSelectorControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class TargetSelectorControllerComponentTest {
    @Test
    void setActiveFailsIfAbilityIsNotAvailable() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        character.getAbilities().addAbility(ts, component);
        component.init(character);
        assertThrows(IllegalStateException.class, () -> component.setActiveTargetedAbility(FakeAttackAbility.create(0, 0)));
    }

    @Test
    void setActiveSucceedsIfAbilityIsNull() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        character.getAbilities().addAbility(ts, component);
        component.init(character);
        assertDoesNotThrow(() -> component.setActiveTargetedAbility(null));
    }

    @Test
    void setActiveSucceedsIfAbilityIsAvailable() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);
        assertDoesNotThrow(() -> component.setActiveTargetedAbility(attackAbility));
    }

    @Test
    void isActiveReturnsTrueAfterAbilityIsSetActive() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);

        component.setActiveTargetedAbility(attackAbility);
        assertTrue(component.isActive(attackAbility));
    }

    @Test
    void isActiveReturnsFalseIfAbilityIsNotActive() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);

        assertFalse(component.isActive(attackAbility));
    }

    @Test
    void isActiveReturnsFalseIfAbilityIsNotActiveAndAbilityIsInvalid() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);

        assertFalse(component.isActive(FakeAttackAbility.create(0, 0)));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void isActiveThrowsIfAbilityIsNull() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);

        assertThrows(NullPointerException.class, () -> component.isActive(null));
    }

    @Test
    void isAvailableAbilityReturnsFalseIfAbilityIsNotTargeted() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);

        assertFalse(component.isAvailableAbility(FakeAbility.createFree()));
    }

    @Test
    void isAvailableAbilityReturnsFalseIfAbilityIsNull() {
        val ts = new TargetSelectorAbility();
        val component = new TestTargetSelectorControllerComponent();
        val character = new FakeCharacterObject();

        val attackAbility = FakeAttackAbility.create(0, 0);
        val attackComponent = FakeAttackControllerComponent.create();

        character.getAbilities().addAbility(attackAbility, attackComponent);
        character.getAbilities().addAbility(ts, component);
        component.init(character);

        assertFalse(component.isAvailableAbility(null));
    }

    private static class TestTargetSelectorControllerComponent extends TargetSelectorControllerComponent {
        @Override
        public void updateInput(@NonNull TargetSelectorAbility ability) {

        }
    }
}
