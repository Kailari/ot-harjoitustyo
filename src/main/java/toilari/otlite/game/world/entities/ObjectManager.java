package toilari.otlite.game.world.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import toilari.otlite.game.GameState;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.game.event.EventSystem;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterInfo;
import toilari.otlite.game.world.entities.characters.CharacterLevels;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.level.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Manageri joka hallinnoi peliobjekteja. Pitää huolta että objekteja päivitetään ja että ne poistetaan
 * asianmukaisesti.
 */
public class ObjectManager {
    @Getter @NonNull private final List<GameObject> objects = new ArrayList<>();
    @Getter @Setter private CharacterObject player;
    @Getter private GameState gameState;

    private EventSystem eventSystem = null;

    private World world;

    /**
     * Hakee viestinvälitysjärjestelmän. Jos managerille ei ole asetettu pelitilaa, luodaan oma
     * viestinvälitysjärjestelmä, muulloin käytetään pelitilan järjestelmää.
     *
     * @return käytettävä viestinvälitysjärjestelmä
     */
    public EventSystem getEventSystem() {
        if (this.gameState == null) {
            return this.eventSystem == null ? this.eventSystem = new EventSystem() : this.eventSystem;
        }

        return this.gameState.getEventSystem();
    }

    /**
     * Asettaa aktiivisen pelitilan. Voidaan asettaa vain kerran, uudelleenkutsu aiheuttaa keskeytyksen.
     * Kutsutaan {@link PlayGameState} konstruktorissa.
     *
     * @param state aktiivinen pelitila
     */
    public void setGameState(@NonNull GameState state) {
        if (this.gameState != null) {
            throw new IllegalStateException("Trying to re-set containing GameState!");
        }
        this.gameState = state;
    }

    /**
     * Tarkistaa onko annetuissa koordinaateissa objektia ja palauttaa sen jos sellainen löytyy. Palauttaa objektin
     * vaikka se olisi merkitty poistetuksi.
     *
     * @param x x-koordinaatti josta etsitään
     * @param y y-koordinaatti josta etsitään
     * @return <code>null</code> jos koordinaateissa ei ole objektia, muulloin löydetty objekti
     */
    public GameObject getObjectAt(int x, int y) {
        return this.objects.stream()
            .filter(o -> o.getX() / Tile.SIZE_IN_WORLD == x && o.getY() / Tile.SIZE_IN_WORLD == y)
            .findFirst()
            .orElse(null);
    }

    /**
     * Alustaa objektimanagerin.
     *
     * @param world pelimaailma jonka objekteja hallinnoidaan
     * @throws NullPointerException jos world on null
     */
    public void init(@NonNull World world) {
        this.world = world;
    }

    /**
     * Päivittää kaikki olemassaolevat objektit.
     *
     * @param delta viimeisimmästä päivityksestä kulunut aika
     */
    public void update(float delta) {
        for (val object : this.objects) {
            if (!object.isRemoved()) {
                object.update(delta);
            }
        }

        deleteRemoved();
    }

    /**
     * Lisää peliobjektin pelimaailmaan.
     *
     * @param object objekti joka lisätään
     * @throws NullPointerException jos objekti on <code>null</code>
     */
    public void spawn(@NonNull GameObject object) {
        if (this.world == null) {
            throw new IllegalStateException("object manager has null-world, have you called .init()?");
        }

        if (this.objects.contains(object)) {
            throw new IllegalArgumentException("object with ID=" + object.getId() + " already exists!");
        }

        this.objects.add(object);
        object.setWorld(this.world);
        object.init();
    }

    /**
     * Lisää kopion peliobjektista pelimaailmaan.
     *
     * @param template kopioitava peliobjekti
     * @return lisätty peliobjekti
     * @throws NullPointerException jos templaatti on <code>null</code>
     */
    public CharacterObject spawnTemplate(@NonNull CharacterObject template) {
        val attributes = new CharacterAttributes(template.getAttributes());
        val levels = new CharacterLevels(template.getLevels());
        val info = new CharacterInfo(template.getInfo());
        val instance = new CharacterObject(attributes, levels, info, new Random());
        instance.getAbilities().cloneAbilitiesFrom(template.getAbilities());
        instance.setRendererID(template.getRendererID());
        instance.setHealth(template.getHealth());
        spawn(instance);
        return instance;
    }

    /**
     * Lisää kopion peliobjektista pelimaailmaan ja asettaa sille sijainnin.
     *
     * @param template kopioitava peliobjekti
     * @param x        uuden objekin x-koordinaatti
     * @param y        uuden objekin y-koordinaatti
     * @return uusi objekti
     * @throws NullPointerException jos templaatti on <code>null</code>
     */
    public CharacterObject spawnTemplateAt(@NonNull CharacterObject template, int x, int y) {
        val spawned = spawnTemplate(template);
        spawned.setTilePos(x, y);
        return spawned;
    }

    /**
     * Läpikäy objektit ja tuhoaa kaikki jotka on merkattu poistetuiksi.
     */
    private void deleteRemoved() {
        val removed = this.objects.stream()
            .filter(GameObject::isRemoved)
            .collect(Collectors.toList());

        for (val obj : removed) {
            remove(obj);
        }
    }

    protected void remove(GameObject object) {
        this.objects.remove(object);
    }

    /**
     * Poistaa kaikki paitsi pelaajaobjektin.
     */
    public void clearAllNonPlayerObjects() {
        this.objects.clear();
        if (this.player != null) {
            this.objects.add(this.player);
        }
    }
}
