package toilari.otlite.game.world.entity.characters.abilities;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeTargetSelectorControllerComponent;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.abilities.KickAbility;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.KickControllerComponent;
import toilari.otlite.game.world.level.KillTile;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.TileMapping;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class KickAbilityTest {
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getCooldownLengthMatchesCharacterAttributes(int strengthLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, strengthLevel);
        val ability = new KickAbility();
        ability.init(character);
        assertEquals(Attribute.Strength.getKickCooldown(character.getLevels()), ability.getCooldownLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getCostMatchesCharacterAttributes(int strengthLevel) {
        val character = FakeCharacterObject.create();
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, strengthLevel);
        val ability = new KickAbility();
        ability.init(character);
        assertEquals(Attribute.Strength.getKickCost(character.getLevels()), ability.getCost());
    }

    @Test
    void kickingKnocksBackTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = FakeCharacterObject.createAt(2, 0);
        world.getObjectManager().spawn(target);

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertEquals(1, target.getTileX());
    }

    @Test
    void kickingDoesNotMoveTargetIfThereIsAWallBehindTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = FakeCharacterObject.createAt(1, 0);
        world.getObjectManager().spawn(target);

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(2, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertEquals(1, target.getTileX());
    }

    @Test
    void kickingDoesNotMoveTargetIfThereIsAnotherObjectBehindTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val obstacle = FakeCharacterObject.createAt(1, 0);
        world.getObjectManager().spawn(obstacle);

        val target = FakeCharacterObject.createAt(2, 0);
        world.getObjectManager().spawn(target);

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertEquals(2, target.getTileX());
    }

    @Test
    void cannotPerformOnDeadTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = FakeCharacterObject.createAt(2, 0);
        world.getObjectManager().spawn(target);
        target.setHealth(0.0f);

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertFalse(kick.perform(component));
        assertEquals(2, target.getTileX());
    }

    @Test
    void cannotPerformOnRemovedTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = FakeCharacterObject.createAt(2, 0);
        world.getObjectManager().spawn(target);
        target.remove();

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertFalse(kick.perform(component));
        assertEquals(2, target.getTileX());
    }

    @Test
    void cannotPerformOnDeadRemovedTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = FakeCharacterObject.createAt(2, 0);
        world.getObjectManager().spawn(target);
        target.setHealth(0.0f);
        target.remove();

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        assertFalse(kick.perform(component));
        assertEquals(2, target.getTileX());
    }

    @Test
    void kickingTargetToPitKillsThem() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = FakeCharacterObject.createAt(4, 0);
        world.getObjectManager().spawn(target);

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.RIGHT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertTrue(target.isDead());
    }

    @Test
    void kickingNonCharacterSuccesfullyMovesTarget() {
        val world = FakeWorld.createWithLevel(createLevel());

        val target = new GameObject();
        target.setTilePos(2, 0);
        world.getObjectManager().spawn(target);

        val ts = FakeTargetSelectorControllerComponent.create(target, Direction.LEFT);
        val kick = new KickAbility();
        val component = new KickControllerComponent.AI();
        val character = FakeCharacterObject.createAtWithAbilities(3, 0,
            new AbilityEntry<>(0, new TargetSelectorAbility(), ts),
            new AbilityEntry<>(1, kick, component)
        );
        world.getObjectManager().spawn(character);

        character.getLevels().rewardExperience(1000);
        character.getLevels().setAttributeLevel(Attribute.STRENGTH, 2);

        kick.perform(component);

        assertEquals(1, target.getTileX());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void performThrowsWithNullArguments() {
        assertThrows(NullPointerException.class, () -> new KickAbility().perform(null));
    }

    private Level createLevel() {
        val tiles = Arrays.asList(
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 0, "floor"),
            new KillTile(false, true, 0, "hole")
        );
        val mapping = new HashMap<String, Byte>();
        mapping.put("wall", (byte) 0);
        mapping.put("floor", (byte) 1);
        mapping.put("hole", (byte) 2);

        return new Level(8, 1, new TileMapping(() -> tiles, mapping), new byte[]{
            0, 1, 1, 1, 1, 2, 1, 0
        });
    }
}
