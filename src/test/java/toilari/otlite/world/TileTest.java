package toilari.otlite.world;

import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.world.entities.characters.AbstractCharacter;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testaa luokan Tile toimintaa.
 */
class TileTest {
    /**
     * Testaa että nullin id:n syöttäminen konstruktoriin aiheuttaa keskeytyksen.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void tileCtorNullchecks() {
        assertThrows(NullPointerException.class, () -> new Tile(true, 'x', null));
        assertDoesNotThrow(() -> new Tile(true, 'x', "xx"));
    }

    /**
     * Testaa että null character aiheuttaa keskeytyksen metodissa
     * {@link Tile#onCharacterEnter(int, int, AbstractCharacter)}.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void tileThrowsIfCharacterIsNullOnEnter() {
        val tile = new Tile(true, 'x', "xx");
        assertThrows(NullPointerException.class, () -> tile.onCharacterEnter(0, 0, null));
    }

    /**
     * Testaa että null character aiheuttaa keskeytyksen metodissa
     * {@link Tile#onCharacterExit(int, int, AbstractCharacter)}.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void tileThrowsIfCharacterIsNullOnExit() {
        val tile = new Tile(true, 'x', "xx");
        assertThrows(NullPointerException.class, () -> tile.onCharacterExit(0, 0, null));
    }

    /**
     * Testaa että hashCode palauttaa true aina kun equals palauttaa true.
     */
    @Test
    void tileEqualsAndHashCodeAreConsistent() {
        val random = new Random(715517);
        char[] c = {'a', 'b', 'c', 'd', 'e'};
        String[] id = {"aaa", "bbb", "ccc", "ddd", "eee"};
        for (int i = 0; i < 1000; i++) {
            val a = new Tile(random.nextBoolean(), c[random.nextInt(c.length)], id[random.nextInt(id.length)]);
            val b = new Tile(random.nextBoolean(), c[random.nextInt(c.length)], id[random.nextInt(id.length)]);
            assertEquals(a.equals(b), a.hashCode() == b.hashCode());
        }
    }
}
