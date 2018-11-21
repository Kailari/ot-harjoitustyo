package toilari.otlite.game.world.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import toilari.otlite.game.world.World;

import java.util.Objects;

public class GameObject {
    private static int idCounter;

    @Getter private final int id;
    @Getter private boolean spawned;
    @Getter private boolean removed;

    @Getter @Setter(AccessLevel.PACKAGE) private World world;

    @Getter @Setter private int x;
    @Getter @Setter private int y;

    /**
     * Asettaa sekä x- että y-koordinaatit uusiin arvoihin.
     *
     * @param newX uusi x-koordinaatti
     * @param newY uusi y-koordinaatti
     */
    public void setPos(int newX, int newY) {
        setX(newX);
        setY(newY);
    }

    /**
     * Merkitsee peliobjektin poistetuksi. Poistettujen objektien {@link #update()}-metodeja ei kutsuta ja
     * ne poistetaan kun kaikki objektit on päivitetty, ennen seuraavaa ruudun piirtämistä.
     */
    public void remove() {
        this.removed = true;
    }

    /**
     * Luo uuden peliobjektin ja asettaa sille yksilöllisen IDn.
     */
    public GameObject() {
        this.id = GameObject.idCounter++;
    }

    /**
     * Alustaa peliobjektin.
     */
    public void init() {
        if (this.removed) {
            throw new IllegalStateException("init called for removed object!");
        }

        if (this.spawned) {
            throw new IllegalStateException("cannot spawn the same object multiple times!");
        }

        this.spawned = true;
    }

    /**
     * Päivittää peliobjektin tilan.
     */
    public void update() {
        if (!this.spawned) {
            throw new IllegalStateException("Upadate called for object not yet spawned!");
        }

        if (this.removed) {
            throw new IllegalStateException("Update called after object was flagged for removal!");
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GameObject) {
            return ((GameObject) o).getId() == this.getId();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
