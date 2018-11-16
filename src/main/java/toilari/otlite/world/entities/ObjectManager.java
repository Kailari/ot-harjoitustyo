package toilari.otlite.world.entities;

import lombok.Getter;
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
    @Getter @NonNull private final List<GameObject> objects = new ArrayList<>();
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
