package toilari.otlite.game.world.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.level.Tile;

import java.util.Objects;

public class GameObject {
    private static int idCounter;
    @Getter private transient float timeAlive;

    @Getter @Setter private String rendererID;

    @Getter private final int id;
    @Getter private boolean spawned;
    @Getter private boolean removed;

    @Getter @Setter(AccessLevel.PACKAGE) private World world;

    @Getter @Setter private int x;
    @Getter @Setter private int y;

    /**
     * Hakee objektin x-ruutukoordinaatin. Jokaisessa ruudussa on {@link Tile#SIZE_IN_WORLD} koordinaattiyksikköä,
     * joten kartan käsittelyyn tarvitaan epätarkempia <i>ruutukoordinaatteja</i>
     *
     * @return objektin x-koordinaatti ruutuina
     */
    public int getTileX() {
        return this.x / Tile.SIZE_IN_WORLD;
    }

    /**
     * Hakee objektin y-ruutukoordinaatin. Jokaisessa ruudussa on {@link Tile#SIZE_IN_WORLD} koordinaattiyksikköä,
     * joten kartan käsittelyyn tarvitaan epätarkempia <i>ruutukoordinaatteja</i>
     *
     * @return objektin y-koordinaatti ruutuina
     */
    public int getTileY() {
        return this.y / Tile.SIZE_IN_WORLD;
    }

    /**
     * Asettaa sekä x- että y-koordinaatit uusiin arvoihin.
     * <b>HUOM: EI KUTSU {@link Tile#onCharacterEnter(int, int, CharacterObject)} tai
     * {@link Tile#onCharacterExit(int, int, CharacterObject)} vaan ne täytyy kutsua manuaalisesti</b>
     *
     * @param newX uusi x-koordinaatti
     * @param newY uusi y-koordinaatti
     */
    public void setPos(int newX, int newY) {
        setX(newX);
        setY(newY);
    }

    /**
     * Asettaa sekä x- että y-koordinaatit uusiin arvoihin. Sijainti on ruutukoordinaatteina.
     * <b>HUOM: EI KUTSU {@link Tile#onCharacterEnter(int, int, CharacterObject)} tai
     * {@link Tile#onCharacterExit(int, int, CharacterObject)} vaan ne täytyy kutsua manuaalisesti</b>
     *
     * @param newX uusi x-ruutukoordinaatti
     * @param newY uusi y-ruutukoordinaatti
     */
    public void setTilePos(int newX, int newY) {
        setPos(newX * Tile.SIZE_IN_WORLD, newY * Tile.SIZE_IN_WORLD);
    }

    /**
     * Merkitsee peliobjektin poistetuksi. Poistettujen objektien {@link #update(float)}-metodeja ei kutsuta ja
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
     * @param delta viimeisimmästä päivityksestä kulunut aika
     */
    public void update(float delta) {
        if (!this.spawned) {
            throw new IllegalStateException("Upadate called for object not yet spawned!");
        }

        if (this.removed) {
            throw new IllegalStateException("Update called after object was flagged for removal!");
        }

        this.timeAlive += delta;
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
