package toilari.otlite.world.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import toilari.otlite.rendering.IRenderer;

import java.util.Objects;

public class GameObject {
    private static int idCounter;

    @Getter private final int id;
    @Getter private boolean removed;

    @Getter private int x;
    @Getter private int y;

    @Getter @Setter(AccessLevel.PROTECTED) private boolean dirty;

    /**
     * Asettaa objektin x-koordinaatin.
     *
     * @param x uusi x-koordinaatti
     */
    public void setX(int x) {
        this.x = x;
        setDirty(true);
    }

    /**
     * Asettaa objektin y-koordinaatin.
     *
     * @param y uusi y-koordinaatti
     */
    public void setY(int y) {
        this.y = y;
        setDirty(true);
    }

    /**
     * Merkitsee peliobjektin poistetuksi. Poistettujen objektien {@link #update()}-metodeja ei kutsuta ja
     * ne poistetaan kun kaikki objektit on päivitetty, ennen seuraavaa ruudun piirtämistä.
     */
    public void remove() {
        this.removed = true;
        setDirty(true);
    }

    protected GameObject() {
        this.id = GameObject.idCounter++;
    }

    /**
     * Alustaa peliobjektin.
     */
    public void init() {
        if (this.removed) {
            throw new IllegalStateException("init called for removed object!");
        }
    }

    /**
     * Päivittää peliobjektin tilan.
     */
    public void update() {
        if (this.removed) {
            throw new IllegalStateException("Update called after object was flagged for removal!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GameObject) {
            return ((GameObject) o).id == this.id;
        }

        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
