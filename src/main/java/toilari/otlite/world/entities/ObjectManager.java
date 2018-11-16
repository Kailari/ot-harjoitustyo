package toilari.otlite.world.entities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manageri joka hallinnoi peliobjekteja. Pitää huolta että objekteja päivitetään ja että ne poistetaan
 * asianmukaisesti.
 */
public class ObjectManager {
    @NonNull private final List<GameObject> objects = new ArrayList<>();
    @NonNull private final Map<Class<? extends GameObject>, IRenderer> objectRenderers = new HashMap<>();
    private World world;

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
     */
    public void update() {
        for (val object : this.objects) {
            if (!object.isRemoved()) {
                object.update();
            }
        }

        deleteRemoved();
    }

    /**
     * Piirtää kaikki tällähetkellä olemassaolevat peliobjektit.
     * <p>
     * Varoitus "unchecked" on vaimennettu, koska metodi {@link #assignRenderer(Class, IRenderer)} pitää huolen ettei
     * tyyppiristiriitaa pääse syntymään.
     *
     * @param camera kamera jonka näkökulmasta piirretään
     * @throws NullPointerException jos kamera on <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public void draw(@NonNull Camera camera) {
        for (val object : this.objects) {
            val renderer = this.objectRenderers.get(object.getClass());
            if (renderer != null) {
                renderer.draw(camera, object);
            }
        }
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
     * Asettaa piirtäjän peliobjektityypille. Vain luokkien joille on rekisteröity piirtäjä oliot piirretään ruudulle.
     *
     * @param objectType piirrettävän objektin luokka
     * @param renderer   piirtäjä jolla luokan instanssit piirretään
     * @param <T>        piirrettävän luokan tyyppi, tulee periytyä luokasta {@link GameObject}
     * @throws NullPointerException jos objektin tyyppi tai piirtäjä on <code>null</code>
     */
    public <T extends GameObject> void assignRenderer(@NonNull Class<? extends T> objectType, @NonNull IRenderer<? extends T> renderer) {
        this.objectRenderers.put(objectType, renderer);
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

    protected void remove(@NonNull GameObject object) {
        this.objects.remove(object);
    }
}
