package toilari.otlite.game.world.entity.characters.abilities;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeAbility;
import toilari.otlite.fake.FakeControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.AbilityRegistry;
import toilari.otlite.game.world.entities.characters.abilities.BlockAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.BlockControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class AbilityRegistryTest {
    @Test
    void getAbilityClassReturnsCorrectWithValidID() {
        val clazz = AbilityRegistry.getAbilityClass("block");
        assertNotNull(clazz);
        assertTrue(clazz.isAssignableFrom(BlockAbility.class));
    }

    @Test
    void getAbilityClassReturnsNullWithInvalidID() {
        assertNull(AbilityRegistry.getAbilityClass("this_key_does_not_exist :c"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getAbilityClassThrowsIfIDIsNull() {
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getAbilityClass(null));
    }

    @Test
    void getComponentClassReturnsCorrectWithValidID() {
        val clazz = AbilityRegistry.getComponentClass("block", "player");
        assertNotNull(clazz);
        assertTrue(clazz.isAssignableFrom(BlockControllerComponent.Player.class));
    }

    @Test
    void getComponentClassReturnsNullWithInvalidID() {
        assertNull(AbilityRegistry.getComponentClass("block", "this_key_does_not_exist :c"));
    }

    @Test
    void getComponentClassReturnsNullWithInvalidAbility() {
        assertNull(AbilityRegistry.getComponentClass("does_not_exist", "this_key_does_not_exist :c"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getComponentClassThrowsIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getComponentClass(null, "does_not_exist"));
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getComponentClass("does_not_exist", null));
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getComponentClass(null, null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void instanceFactoryGettersThrowIfParamsAreNull() {
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getAbilityInstanceFactory(null));

        assertThrows(NullPointerException.class, () -> AbilityRegistry.getComponentInstanceFactory(null, FakeControllerComponent.create(true)));
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getComponentInstanceFactory(FakeAbility.createFree(), null));
        assertThrows(NullPointerException.class, () -> AbilityRegistry.getComponentInstanceFactory(null, null));
    }
}
