package toilari.otlite.game.world.entity;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;

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
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 1337));
        manager.spawn(character);

        assertEquals(1337, manager.getRemainingActionPoints());
    }

    @Test
    void spendActionPointsThrowsIfRemainingGoesNegative() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
        manager.spawn(character);

        assertThrows(IllegalArgumentException.class, () -> manager.spendActionPoints(3));
    }

    @Test
    void spendActionPointsThrowsIfAmountIsNegative() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
        manager.spawn(character);

        assertThrows(IllegalArgumentException.class, () -> manager.spendActionPoints(-1));
    }

    @Test
    void spendActionPointsReducesRemainingPoints() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 3));
        manager.spawn(character);

        manager.spendActionPoints(1);
        assertEquals(2, manager.getRemainingActionPoints());
    }

    @Test
    void spendActionPointsCanReachZero() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter(new CharacterAttributes(10.0f, 1, 1, 2));
        manager.spawn(character);

        manager.spendActionPoints(2);
        assertEquals(0, manager.getRemainingActionPoints());
    }

    @Test
    void isCharactersTurnRetunsFalseWhenThereAreNoCharacters() {
        assertFalse(new TurnObjectManager().isCharactersTurn(new TestCharacter()));
    }

    @Test
    void getActiveCharacterRetunsNullWhenThereAreNoCharacters() {
        assertNull(new TurnObjectManager().getActiveCharacter());
    }

    @Test
    void firstSpawnedCharacterGetsTurnEvenIfNextTurnIsCalledBeforeSpawning() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter();
        manager.nextTurn();
        manager.nextTurn();
        manager.nextTurn();
        manager.spawn(character);

        assertEquals(character, manager.getActiveCharacter());
        assertTrue(manager.isCharactersTurn(character));
    }

    @Test
    void getActiveCharacterRetunsOnlyCharacterWhenThereIsOnlyOne() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter();
        manager.spawn(character);

        assertEquals(character, manager.getActiveCharacter());
    }

    @Test
    void onlyCharacterGetsTurnsInfinitelyTest100Cycles() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val character = new TestCharacter();
        manager.spawn(character);

        for (int i = 0; i < 100; i++) {
            assertTrue(manager.isCharactersTurn(character));
            manager.nextTurn();
        }
    }

    @Test
    void turnsRepeatAfterFullRoundTest100CyclesWithMultipleCharacters() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val a = new TestCharacter();
        val b = new TestCharacter();
        val c = new TestCharacter();
        manager.spawn(a);
        manager.spawn(b);
        manager.spawn(c);

        for (int i = 0; i < 100; i++) {
            assertTrue(manager.isCharactersTurn(a));

            manager.nextTurn();
            assertTrue(manager.isCharactersTurn(b));

            manager.nextTurn();
            assertTrue(manager.isCharactersTurn(c));

            manager.nextTurn();
        }
    }

    @Test
    void charactersTakeTurnsInTheOrderTheyAreSpawned() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val a = new TestCharacter();
        val b = new TestCharacter();
        val c = new TestCharacter();
        manager.spawn(a);
        manager.spawn(b);
        manager.spawn(c);

        assertTrue(manager.isCharactersTurn(a));

        manager.nextTurn();
        assertTrue(manager.isCharactersTurn(b));

        manager.nextTurn();
        assertTrue(manager.isCharactersTurn(c));
    }

    @Test
    void removingCharactersDoesNotBreakTheOrderingWithRemovedObjectsNotCleanedUp() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val characters = new TestCharacter[1000];
        int nRemoved = 0;
        for (int i = 0; i < characters.length; i++) {
            characters[i] = new TestCharacter();
            manager.spawn(characters[i]);
        }

        int i = 0;
        int j = 0;
        val rand = new Random(1337);
        while (nRemoved < characters.length * 0.9f) {
            assertTrue(manager.isCharactersTurn(characters[j]));
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
            manager.nextTurn();
        }
    }

    @Test
    void removingCharactersDoesNotBreakTheOrderingWithRemovedObjectsCleanedUpByUpdatingRemovalOccuringAfterTurnChange() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val characters = new TestCharacter[1000];
        int nRemoved = 0;
        for (int i = 0; i < characters.length; i++) {
            characters[i] = new TestCharacter();
            manager.spawn(characters[i]);
        }

        int i = 0;
        int j = 0;
        val rand = new Random(1337);
        while (nRemoved < characters.length * 0.9f) {
            assertTrue(manager.isCharactersTurn(characters[j]));

            manager.nextTurn();

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


            manager.update();
        }
    }

    @Test
    void removingCharactersDoesNotBreakTheOrderingWithRemovedObjectsCleanedUpByUpdatingRemovalOccuringDuringTurn() {
        val manager = new TurnObjectManager();
        val world = new World(manager);
        world.init();

        val characters = new TestCharacter[1000];
        int nRemoved = 0;
        for (int i = 0; i < characters.length; i++) {
            characters[i] = new TestCharacter();
            manager.spawn(characters[i]);
        }

        int i = 0;
        int j = 0;
        val rand = new Random(1337);
        while (nRemoved < characters.length * 0.9f) {
            assertTrue(manager.isCharactersTurn(characters[j]));

            if (!characters[i].isRemoved()) {
                characters[i].remove();
                nRemoved++;
            }

            manager.nextTurn();

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


            manager.update();
        }
    }


    private class TestCharacter extends AbstractCharacter {
        public TestCharacter(@NonNull CharacterAttributes attributes) {
            super(attributes);
        }

        TestCharacter() {
            super(new CharacterAttributes(10f, 1, 1, 1));
        }
    }
}
