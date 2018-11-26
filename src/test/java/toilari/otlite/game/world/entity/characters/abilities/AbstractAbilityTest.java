package toilari.otlite.game.world.entity.characters.abilities;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class AbstractAbilityTest {
    @Test
    void constructorThrowsIfCharacterIsNull() {
        assertThrows(NullPointerException.class, () -> new TestAbility(null, 0));
    }

    @Test
    void abilityIsNotOnCooldownByDefault() {
        val character = new FakeCharacterObject();
        assertFalse(new TestAbility(character, 0).isOnCooldown());
    }

    @Test
    void abilityOnCooldownAfterBeingPutOnCooldown() {
        val character = new FakeCharacterObject();
        val ability = new TestAbility(character, 0);

        ability.putOnCooldown();
        assertTrue(ability.isOnCooldown());
    }

    @Test
    void abilityCooldownIsExpectedNumberOfTurnsInitially() {
        val character = new FakeCharacterObject();
        val ability = new TestAbility(character, 0);

        ability.putOnCooldown();
        assertEquals(10, ability.getRemainingCooldown());
    }

    @Test
    void abilityCooldownIsExpectedNumberOfTurnsAfterReducingCooldown() {
        val character = new FakeCharacterObject();
        val ability = new TestAbility(character, 0);

        ability.putOnCooldown();
        ability.reduceCooldownTimer();
        assertEquals(9, ability.getRemainingCooldown());
        ability.reduceCooldownTimer();
        assertEquals(8, ability.getRemainingCooldown());
        ability.reduceCooldownTimer();
        assertEquals(7, ability.getRemainingCooldown());
        ability.reduceCooldownTimer();
        assertEquals(6, ability.getRemainingCooldown());
        ability.reduceCooldownTimer();
        assertEquals(5, ability.getRemainingCooldown());
    }

    @Test
    void abilityCooldownCanReachZero() {
        val character = new FakeCharacterObject();
        val ability = new TestAbility(character, 0);

        ability.putOnCooldown();
        for (int i = 0; i < 10; i++) {
            ability.reduceCooldownTimer();
        }
        assertEquals(0, ability.getRemainingCooldown());
    }

    @Test
    void reduceCooldownThrowsIfTimerWouldGoNegative() {
        val character = new FakeCharacterObject();
        val ability = new TestAbility(character, 0);

        ability.putOnCooldown();
        for (int i = 0; i < 10; i++) {
            ability.reduceCooldownTimer();
        }
        assertThrows(IllegalStateException.class, ability::reduceCooldownTimer);
    }


    private class TestAbility extends AbstractAbility<TestAbility, AbstractControllerComponent<TestAbility>> {
        TestAbility(CharacterObject character, int priority) {
            super(character, priority);
        }

        @Override
        public int getCooldownLength() {
            return 10;
        }

        @Override
        public int getCost() {
            fail("TestAbility::getCost() is a mock method and should not be called.");
            return 0;
        }

        @Override
        public boolean perform(@NonNull AbstractControllerComponent<TestAbility> component) {
            fail("TestAbility::perform() is a mock method and should not be called.");
            return false;
        }
    }
}
