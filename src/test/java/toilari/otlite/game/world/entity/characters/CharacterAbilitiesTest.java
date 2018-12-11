package toilari.otlite.game.world.entity.characters;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeAbility;
import toilari.otlite.fake.FakeAttackAbility;
import toilari.otlite.fake.FakeAttackControllerComponent;
import toilari.otlite.fake.FakeControllerComponent;
import toilari.otlite.game.world.entities.characters.CharacterAbilities;
import toilari.otlite.game.world.entities.characters.abilities.AbilityRegistry;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;
import toilari.otlite.game.world.entities.characters.abilities.WarcryAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.KickControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.WarcryControllerComponent;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class CharacterAbilitiesTest {
    @BeforeAll
    static void beforeAll() {
        AbilityRegistry.register("fake", FakeAbility.class, FakeAbility::createFree)
            .registerComponent("fake", FakeControllerComponent.class, FakeControllerComponent::create);
        AbilityRegistry.register("fakeAttack", FakeAttackAbility.class, FakeAttackAbility::createFree)
            .registerComponent("fake", FakeAttackControllerComponent.class, FakeAttackControllerComponent::create);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void addAbilityThrowsIfArgumentsAreNull() {
        val abilities = new CharacterAbilities();
        assertThrows(NullPointerException.class, () -> abilities.addAbility(null, FakeControllerComponent.create(false)));
        assertThrows(NullPointerException.class, () -> abilities.addAbility(FakeAbility.createFree(), null));
        assertThrows(NullPointerException.class, () -> abilities.addAbility(null, null));
    }

    @Test
    void addAbilityDoesNotThrowIfGivenValidArguments() {
        val abilities = new CharacterAbilities();
        assertDoesNotThrow(() -> abilities.addAbility(FakeAbility.createFree(), FakeControllerComponent.create(false)));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getComponentThrowsIfClassIsNull() {
        val abilities = new CharacterAbilities();
        assertThrows(NullPointerException.class, () -> abilities.getComponent(null));
    }

    @Test
    void getComponentReturnsNullIfAbilityIsUnregistered() {
        val abilities = new CharacterAbilities();
        assertNull(abilities.getComponent(FakeAbility.class));
    }

    @Test
    void getComponentReturnsValidIfAbilityIsRegistered() {
        val abilities = new CharacterAbilities();
        val component = FakeControllerComponent.create(false);
        abilities.addAbility(FakeAbility.createFree(), component);
        assertEquals(component, abilities.getComponent(FakeAbility.class));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getAbilityThrowsIfClassIsNull() {
        val abilities = new CharacterAbilities();
        assertThrows(NullPointerException.class, () -> abilities.getAbility(null));
    }

    @Test
    void getAbilityReturnsNullIfAbilityIsUnregistered() {
        val abilities = new CharacterAbilities();
        assertNull(abilities.getAbility(FakeAbility.class));
    }

    @Test
    void getAbilityReturnsValidIfAbilityIsRegistered() {
        val abilities = new CharacterAbilities();
        val ability = FakeAbility.createFree();
        abilities.addAbility(ability, FakeControllerComponent.create(false));
        assertEquals(ability, abilities.getAbility(FakeAbility.class));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getComponentResponsibleForThrowsIfInstanceIsNull() {
        val abilities = new CharacterAbilities();
        assertThrows(NullPointerException.class, () -> abilities.getComponentResponsibleFor(null));
    }

    @Test
    void getComponentResponsibleForThrowsIfInstanceIsUnregistered() {
        val abilities = new CharacterAbilities();
        assertThrows(IllegalStateException.class, () -> abilities.getComponentResponsibleFor(FakeAbility.createFree()));
    }

    @Test
    void getComponentResponsibleForReturnsValidIfInstanceValid() {
        val abilities = new CharacterAbilities();
        val ability = FakeAbility.createFree();
        val component = FakeControllerComponent.create(false);
        abilities.addAbility(ability, component);
        assertEquals(component, abilities.getComponentResponsibleFor(ability));
    }

    @Test
    void getAbilitiesSortedByPriorityReturnsEmptyWhenThereAreNoAbilitiesRegistered() {
        val abilities = new CharacterAbilities();
        assertFalse(abilities.getAbilitiesSortedByPriority().iterator().hasNext());
    }

    @Test
    void getAbilitiesSortedByPriorityReturnsAllAbilities() {
        val abilities = new CharacterAbilities();
        val aa = FakeAbility.createFree();
        val ac = FakeControllerComponent.create(false);
        aa.setPriority(0);

        val ba = FakeAttackAbility.create(0, 0);
        val bc = FakeAttackControllerComponent.create();
        ba.setPriority(3);

        val ca = new KickAbility();
        val cc = new KickControllerComponent.Player();
        ca.setPriority(1);

        val da = new WarcryAbility();
        val dc = new WarcryControllerComponent.Player();
        da.setPriority(2);

        abilities.addAbility(aa, ac);
        abilities.addAbility(ba, bc);
        abilities.addAbility(ca, cc);
        abilities.addAbility(da, dc);

        assertEquals(
            4,
            StreamSupport.stream(abilities.getAbilitiesSortedByPriority().spliterator(), false)
                .count());
    }

    @Test
    void getAbilitiesSortedByPriorityHasCorrectOrder() {
        val abilities = new CharacterAbilities();
        val aa = FakeAbility.createFree();
        val ac = FakeControllerComponent.create(false);
        aa.setPriority(0);

        val ba = FakeAttackAbility.create(0, 0);
        val bc = FakeAttackControllerComponent.create();
        ba.setPriority(3);

        val ca = new KickAbility();
        val cc = new KickControllerComponent.Player();
        ca.setPriority(1);

        val da = new WarcryAbility();
        val dc = new WarcryControllerComponent.Player();
        da.setPriority(2);

        abilities.addAbility(aa, ac);
        abilities.addAbility(ba, bc);
        abilities.addAbility(ca, cc);
        abilities.addAbility(da, dc);

        val iterator = abilities.getAbilitiesSortedByPriority().iterator();
        assertEquals(aa, iterator.next());
        assertEquals(ca, iterator.next());
        assertEquals(da, iterator.next());
        assertEquals(ba, iterator.next());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void cloneAbilitiesFromThrowsIfTemplateIsNull() {
        val abilities = new CharacterAbilities();
        assertThrows(NullPointerException.class, () -> abilities.cloneAbilitiesFrom(null));
    }

    @Test
    void cloneAbilitiesFromClonesCorrectNumberOfAbilities() {
        val abilities = new CharacterAbilities();
        val aa = FakeAbility.createFree();
        val ac = FakeControllerComponent.create(false);
        aa.setPriority(0);

        val ba = FakeAttackAbility.create(0, 0);
        val bc = FakeAttackControllerComponent.create();
        ba.setPriority(3);

        val ca = new KickAbility();
        val cc = new KickControllerComponent.Player();
        ca.setPriority(1);

        val da = new WarcryAbility();
        val dc = new WarcryControllerComponent.Player();
        da.setPriority(2);

        abilities.addAbility(aa, ac);
        abilities.addAbility(ba, bc);
        abilities.addAbility(ca, cc);
        abilities.addAbility(da, dc);

        val cloned = new CharacterAbilities();
        cloned.cloneAbilitiesFrom(abilities);

        assertEquals(
            4,
            StreamSupport.stream(cloned.getAbilitiesSortedByPriority().spliterator(), false)
                .count());
    }

    @Test
    void cloneAbilitiesFromClonedAbilitiesMatchTemplate() {
        val abilities = new CharacterAbilities();
        val aa = FakeAbility.createFree();
        val ac = FakeControllerComponent.create(false);
        aa.setPriority(0);

        val ba = FakeAttackAbility.create(0, 0);
        val bc = FakeAttackControllerComponent.create();
        ba.setPriority(3);

        val ca = new KickAbility();
        val cc = new KickControllerComponent.Player();
        ca.setPriority(1);

        val da = new WarcryAbility();
        val dc = new WarcryControllerComponent.Player();
        da.setPriority(2);

        abilities.addAbility(aa, ac);
        abilities.addAbility(ba, bc);
        abilities.addAbility(ca, cc);
        abilities.addAbility(da, dc);

        val cloned = new CharacterAbilities();
        cloned.cloneAbilitiesFrom(abilities);

        val iterator = cloned.getAbilitiesSortedByPriority().iterator();

        val cloneaa = iterator.next();
        assertEquals(aa.getPriority(), cloneaa.getPriority());
        assertTrue(cloneaa instanceof FakeAbility);

        val cloneca = iterator.next();
        assertEquals(ca.getPriority(), cloneca.getPriority());
        assertTrue(cloneca instanceof KickAbility);

        val cloneda = iterator.next();
        assertEquals(da.getPriority(), cloneda.getPriority());
        assertTrue(cloneda instanceof WarcryAbility);

        val cloneba = iterator.next();
        assertEquals(ba.getPriority(), cloneba.getPriority());
        assertTrue(cloneba instanceof FakeAttackAbility);
    }
}
