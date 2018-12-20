package toilari.otlite.game.world.entity;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeCharacterObject;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterObject;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TurnObjectManagerTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void initThrowsIfWorldIsNull() {
        assertThrows(NullPointerException.class, () -> new TurnObjectManager().init(null));
    }

    @Test
    void getRemainingActionPointsRetunsInitiallyCorrectAmount() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.createWithAttributes(new CharacterAttributes(
            null,
            1,
            0,
            1,
            0,
            10,
            1337, // <---
            0,
            0.1f,
            0.0f,
            0.001f,
            0.0f,
            0.0f,
            1.0f,
            0.1f,
            0.0f,
            0.1f,
            10.0f,
            0.1f,
            0.5f,
            0.001f
        ));
        world.getObjectManager().spawn(character);

        assertEquals(1337, world.getObjectManager().getRemainingActionPoints());
    }

    @Test
    void spendActionPointsThrowsIfRemainingGoesNegative() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        assertThrows(IllegalArgumentException.class, () -> world.getObjectManager().spendActionPoints(100));
    }

    @Test
    void spendActionPointsThrowsIfAmountIsNegative() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        assertThrows(IllegalArgumentException.class, () -> world.getObjectManager().spendActionPoints(-1));
    }

    @Test
    void spendActionPointsReducesRemainingPointsAndPointsCanReachZero() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        world.getObjectManager().spendActionPoints(1);
        assertEquals(1, world.getObjectManager().getRemainingActionPoints());
        world.getObjectManager().spendActionPoints(1);
        assertEquals(0, world.getObjectManager().getRemainingActionPoints());
    }

    @Test
    void isCharactersTurnRetunsFalseWhenThereAreNoCharacters() {
        assertFalse(new TurnObjectManager().isCharactersTurn(FakeCharacterObject.create()));
    }

    @Test
    void getActiveCharacterRetunsNullWhenThereAreNoCharacters() {
        assertNull(new TurnObjectManager().getActiveCharacter());
    }

    @Test
    void firstSpawnedCharacterGetsTurnEvenIfNextTurnIsCalledBeforeSpawning() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().nextTurn();
        world.getObjectManager().nextTurn();
        world.getObjectManager().nextTurn();
        world.getObjectManager().spawn(character);

        assertEquals(character, world.getObjectManager().getActiveCharacter());
        assertTrue(world.getObjectManager().isCharactersTurn(character));
    }

    @Test
    void getActiveCharacterRetunsOnlyCharacterWhenThereIsOnlyOne() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        assertEquals(character, world.getObjectManager().getActiveCharacter());
    }

    @Test
    void onlyCharacterGetsTurnsInfinitelyTest100Cycles() {
        val world = FakeWorld.create();

        val character = FakeCharacterObject.create();
        world.getObjectManager().spawn(character);

        for (int i = 0; i < 100; i++) {
            assertTrue(world.getObjectManager().isCharactersTurn(character));
            world.getObjectManager().nextTurn();
        }
    }

    @Test
    void turnsRepeatAfterFullRoundTest100CyclesWithMultipleCharacters() {
        val world = FakeWorld.create();

        val a = FakeCharacterObject.create();
        val b = FakeCharacterObject.create();
        val c = FakeCharacterObject.create();
        world.getObjectManager().spawn(a);
        world.getObjectManager().spawn(b);
        world.getObjectManager().spawn(c);

        for (int i = 0; i < 100; i++) {
            assertTrue(world.getObjectManager().isCharactersTurn(a));

            world.getObjectManager().nextTurn();
            assertTrue(world.getObjectManager().isCharactersTurn(b));

            world.getObjectManager().nextTurn();
            assertTrue(world.getObjectManager().isCharactersTurn(c));

            world.getObjectManager().nextTurn();
        }
    }

    @Test
    void charactersTakeTurnsInTheOrderTheyAreSpawned() {
        val world = FakeWorld.create();

        val a = FakeCharacterObject.create();
        val b = FakeCharacterObject.create();
        val c = FakeCharacterObject.create();
        world.getObjectManager().spawn(a);
        world.getObjectManager().spawn(b);
        world.getObjectManager().spawn(c);

        assertTrue(world.getObjectManager().isCharactersTurn(a));

        world.getObjectManager().nextTurn();
        assertTrue(world.getObjectManager().isCharactersTurn(b));

        world.getObjectManager().nextTurn();
        assertTrue(world.getObjectManager().isCharactersTurn(c));
    }

    @Test
    void removingCharactersDoesNotBreakTheOrderingWithRemovedObjectsNotCleanedUp() {
        val world = FakeWorld.create();

        val characters = new FakeCharacterObject[1000];
        int nRemoved = 0;
        for (int i = 0; i < characters.length; i++) {
            characters[i] = FakeCharacterObject.create();
            world.getObjectManager().spawn(characters[i]);
        }

        int i = 0;
        int j = 0;
        val rand = new Random(1337);
        while (nRemoved < characters.length * 0.9f) {
            assertTrue(world.getObjectManager().isCharactersTurn(characters[j]));
            if (!characters[i].isRemoved()) {
                characters[i].remove();
                nRemoved++;
            }
            i += rand.nextInt(characters.length - nRemoved);
            if (i >= characters.length) {
                i -= characters.length;
            }

            do {
                j++;

                if (j == characters.length) {
                    j = 0;
                }
            } while (characters[j].isRemoved());
            world.getObjectManager().nextTurn();
        }
    }

    @Test
    void removingCharactersDoesNotBreakTheOrderingWithRemovedObjectsCleanedUpByUpdatingRemovalOccuringAfterTurnChange() {
        val world = FakeWorld.create();

        val characters = new FakeCharacterObject[1000];
        int nRemoved = 0;
        for (int i = 0; i < characters.length; i++) {
            characters[i] = FakeCharacterObject.create();
            world.getObjectManager().spawn(characters[i]);
        }

        int i = 0;
        int j = 0;
        val rand = new Random(1337);
        while (nRemoved < characters.length * 0.9f) {
            assertTrue(world.getObjectManager().isCharactersTurn(characters[j]));

            world.getObjectManager().nextTurn();

            if (!characters[i].isRemoved()) {
                characters[i].remove();
                nRemoved++;
            }


            i += rand.nextInt(characters.length - nRemoved);
            if (i >= characters.length) {
                i -= characters.length;
            }

            do {
                j++;

                if (j == characters.length) {
                    j = 0;
                }
            } while (characters[j].isRemoved());


            world.getObjectManager().update(1.0f);
        }
    }

    @Test
    void removingCharactersDoesNotBreakTheOrderingWithRemovedObjectsCleanedUpByUpdatingRemovalOccuringDuringTurn() {
        val world = FakeWorld.create();

        val characters = new FakeCharacterObject[1000];
        int nRemoved = 0;
        for (int i = 0; i < characters.length; i++) {
            characters[i] = FakeCharacterObject.create();
            world.getObjectManager().spawn(characters[i]);
        }

        int i = 0;
        int j = 0;
        val rand = new Random(1337);
        while (nRemoved < characters.length * 0.9f) {
            assertTrue(world.getObjectManager().isCharactersTurn(characters[j]));

            if (!characters[i].isRemoved()) {
                characters[i].remove();
                nRemoved++;
            }

            world.getObjectManager().nextTurn();

            i += rand.nextInt(characters.length - nRemoved);
            if (i >= characters.length) {
                i -= characters.length;
            }

            do {
                j++;

                if (j == characters.length) {
                    j = 0;
                }
            } while (characters[j].isRemoved());


            world.getObjectManager().update(1.0f);
        }
    }

    @Test
    void updateSkipsToNextTurnIfCharacterIsRemoved() {
        val world = FakeWorld.create();

        CharacterObject a, b;
        world.getObjectManager().spawn(a = FakeCharacterObject.create());
        world.getObjectManager().spawn(b = FakeCharacterObject.create());
        a.remove();

        world.getObjectManager().update(1.0f);

        assertTrue(world.getObjectManager().isCharactersTurn(b));
    }
}
