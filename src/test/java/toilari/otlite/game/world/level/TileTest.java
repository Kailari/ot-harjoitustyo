package toilari.otlite.game.world.level;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    void tileCtorNullchecks() {
        assertThrows(NullPointerException.class, () -> new NormalTile(true, false, 0, null));
        assertDoesNotThrow(() -> new NormalTile(true, false, 0, "xx"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileThrowsIfCharacterIsNullOnEnter() {
        val tile = new NormalTile(true, false, 0, "xx");
        assertThrows(NullPointerException.class, () -> tile.onCharacterEnter(0, 0, null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void tileThrowsIfCharacterIsNullOnExit() {
        val tile = new NormalTile(true, false, 0, "xx");
        assertThrows(NullPointerException.class, () -> tile.onCharacterExit(0, 0, null));
    }

    @Test
    void tileEqualsAndHashCodeAreConsistent() {
        val random = new Random(715517);
        String[] id = {"aaa", "bbb", "ccc", "ddd", "eee"};
        for (int i = 0; i < 1000; i++) {
            val a = new NormalTile(random.nextBoolean(), random.nextBoolean(), random.nextInt(5), id[random.nextInt(id.length)]);
            val b = new NormalTile(random.nextBoolean(), random.nextBoolean(), random.nextInt(5), id[random.nextInt(id.length)]);
            assertEquals(a.equals(b), a.hashCode() == b.hashCode());
        }
    }
}
