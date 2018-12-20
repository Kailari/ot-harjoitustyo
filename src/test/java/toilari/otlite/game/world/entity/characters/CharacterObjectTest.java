package toilari.otlite.game.world.entity.characters;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.AbilityEntry;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterInfo;
import toilari.otlite.game.world.entities.characters.CharacterLevels;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbstractAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AIRandomRoamMoveControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.AbstractControllerComponent;
import toilari.otlite.game.world.level.Level;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;
import toilari.otlite.game.world.level.TileMapping;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CharacterObjectTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void constructorThrowsIfParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new CharacterObject(null, null));

        assertThrows(NullPointerException.class, () -> new CharacterObject(null, new CharacterLevels(), new CharacterInfo(), null));
        assertThrows(NullPointerException.class, () -> new CharacterObject(new CharacterAttributes(), null, new CharacterInfo(), null));
        assertThrows(NullPointerException.class, () -> new CharacterObject(new CharacterAttributes(), new CharacterLevels(), null, null));
        assertThrows(NullPointerException.class, () -> new CharacterObject(null, null, new CharacterInfo(), null));
        assertThrows(NullPointerException.class, () -> new CharacterObject(new CharacterAttributes(), null, null, null));
        assertThrows(NullPointerException.class, () -> new CharacterObject(null, null, null, null));
    }

    @Test
    void characterIsAliveWhenSpawned() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);
        assertFalse(character.isDead());
    }

    @Test
    void healthIsMaxedAtSpawn() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);
        assertEquals(10.0f, character.getHealth());
    }

    @Test
    void characterIsDeadWhenHealthIsZero() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);
        character.setHealth(0.0f);

        assertTrue(character.isDead());
    }

    @Test
    void panickingCharacterSkipsUpdatingAbilities() {
        val world = FakeWorld.create();

        val ability = new TestAbility();
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, new TestControllerComponent())
        );
        world.getObjectManager().spawn(character);

        character.panic(0, 0);
        character.updateOnTurn(world.getObjectManager());

        assertFalse(ability.isOnCooldown());
    }

    @Test
    void panickingCharacterUpdatesMoveAbility() {
        val world = FakeWorld.createWithLevel(createLevel());

        val ability = new MoveAbility() {
            @Override
            public int getCooldownLength() {
                return 10;
            }
        };
        val character = FakeCharacterObject.createAtWithAbilities(2, 2,
            new AbilityEntry<>(0, ability, new AIRandomRoamMoveControllerComponent())
        );
        world.getObjectManager().spawn(character);
        character.panic(0, 0);

        character.updateOnTurn(world.getObjectManager());

        assertTrue(ability.isOnCooldown());
    }

    @Test
    void getStateReturnsIdleWhenNotCharactersTurn() {
        val world = FakeWorld.create();

        val other = FakeCharacterObject.create();
        world.getObjectManager().spawn(other);

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        assumeFalse(world.getObjectManager().isCharactersTurn(character));
        assertEquals("idle", character.getState());
    }

    @Test
    void getStateReturnsIdleWhenOutOfAP() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);
        world.getObjectManager().spendActionPoints(world.getObjectManager().getRemainingActionPoints());

        assumeTrue(world.getObjectManager().isCharactersTurn(character));
        assertEquals("idle", character.getState());
    }

    @Test
    void getStateReturnsActiveWhenCharactersTurnAndHasAPRemaining() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        assumeTrue(world.getObjectManager().isCharactersTurn(character));
        assertEquals("active", character.getState());
    }

    @Test
    void getStateReturnsOverrideWhenSet() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);
        character.setStateOverride("override");

        assumeTrue(world.getObjectManager().isCharactersTurn(character));
        assertEquals("override", character.getState());
    }

    @Test
    void healAddsCorrectAmount() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        character.setHealth(1.0f);
        character.heal(5.0f);

        assertEquals(6.0f, character.getHealth());
    }

    @Test
    void healCannotOverheal() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        character.setHealth(1.0f);
        character.heal(9001.0f);

        assertEquals(character.getAttributes().getMaxHealth(), character.getHealth());
    }

    @Test
    void healThrowsIfAmountIsNegative() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        character.setHealth(1.0f);
        assertThrows(IllegalArgumentException.class, () -> character.heal(-5.0f));
    }

    @Test
    void removeThrowsIfNotSpawned() {
        assertThrows(IllegalStateException.class, () -> FakeCharacterObject.create().remove());
    }

    @Test
    void panickingEndsAfterSomeTime() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create(new Random(1337));
        world.getObjectManager().spawn(character);

        character.panic(0, 0);
        for (int i = 0; i < 2; i++) {
            character.endTurn();
        }

        assertFalse(character.isPanicking());
    }

    @Test
    void panickingDoesNotAlwaysEndOnFirstEndTurn() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create(new Random(1234));
        world.getObjectManager().spawn(character);

        character.panic(0, 0);
        character.endTurn();

        assertTrue(character.isPanicking());
    }

    @Test
    void abilityCooldownsGoDownOnTurnEnd() {
        val world = FakeWorld.create();

        val ability = new TestAbility() {
            @Override
            public int getCooldownLength() {
                return 10;
            }
        };
        val character = FakeCharacterObject.createWithAbilities(
            new AbilityEntry<>(0, ability, new TestControllerComponent())
        );
        world.getObjectManager().spawn(character);

        ability.putOnCooldown();
        character.endTurn();

        assertTrue(ability.isOnCooldown());
        assertEquals(9, ability.getRemainingCooldown());
    }


    private static Level createLevel() {
        val tileMappings = new TileMapping(() -> Arrays.asList(new Tile[]{
            new NormalTile(true, false, 0, "wall"),
            new NormalTile(false, false, 1, "floor"),
            new NormalTile(false, true, 2, "hole"),
        }));

        val w = tileMappings.getIndex("wall");
        val f = tileMappings.getIndex("floor");
        val h = tileMappings.getIndex("hole");

        val indices = new byte[]{
            f, w, w, w, w, w, w, w,
            w, f, f, f, f, f, f, w,
            w, f, f, f, w, w, f, w,
            w, h, h, f, f, f, w, w,
            w, h, f, f, f, f, f, w,
            w, f, f, h, h, f, f, w,
            w, f, f, f, f, f, f, w,
            w, w, w, w, w, w, w, f};

        return new Level(8, 8, tileMappings, indices);
    }

    private class TestAbility extends AbstractAbility<TestAbility, TestControllerComponent> {
        TestAbility() {
            super("test");
        }

        @Override
        public int getCost() {
            return 0;
        }

        @Override
        public int getCooldownLength() {
            return 69;
        }

        @Override
        public boolean perform(@NonNull TestControllerComponent component) {
            return true;
        }
    }

    private class TestControllerComponent extends AbstractControllerComponent<TestAbility> {
        @Override
        public boolean wants(@NonNull TestAbility ability) {
            return true;
        }

        @Override
        public void updateInput(@NonNull TestAbility ability) {
        }

        @Override
        public void abilityPerformed(TestAbility ability) {
        }

        @Override
        public void reset() {
        }
    }
}
