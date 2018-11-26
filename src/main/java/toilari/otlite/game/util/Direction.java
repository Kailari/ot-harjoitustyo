package toilari.otlite.game.util;

import lombok.Getter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Suunta pelimaailmassa.
 */
public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0),
    NONE(0, 0);

    @Getter private final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Palauttaa suunnat iteroitavana järjestyksessä <code>ylös, oikealle, alas, vasempaan</code>.
     *
     * @return suunnat iteroitavana
     */
    public static Iterable<Direction> asIterable() {
        return new IterableDirection();
    }

    private static final class IterableDirection implements Iterator<Direction>, Iterable<Direction> {
        private Direction current = NONE;

        @Override
        public Iterator<Direction> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return this.current != LEFT;
        }

        @Override
        public Direction next() {
            switch (this.current) {
                case UP:
                    return this.current = RIGHT;
                case RIGHT:
                    return this.current = DOWN;
                case DOWN:
                    return this.current = LEFT;
                case NONE:
                    return this.current = UP;
                case LEFT:
                default:
                    throw new NoSuchElementException();
            }
        }
    }
}
