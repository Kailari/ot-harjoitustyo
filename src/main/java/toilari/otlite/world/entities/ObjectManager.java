package toilari.otlite.world.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.io.dao.TextureDAO;
import toilari.otlite.rendering.Camera;
import toilari.otlite.rendering.IRenderer;
import toilari.otlite.rendering.PlayerRenderer;
import toilari.otlite.world.entities.characters.PlayerCharacter;

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
    @Getter private final List<GameObject> objects = new ArrayList<>();
    private final Map<Class<? extends GameObject>, IRenderer> objectRenderers = new HashMap<>();

    /**
     * Lisää peliobjektin pelimaailmaan.
     *
     * @param object objekti joka lisätään
     */
    public void spawn(@NonNull GameObject object) {
        if (this.objects.contains(object)) {
            throw new IllegalArgumentException("object with ID=" + object.getId() + " already exists!");
        }

        this.objects.add(object);
        object.init();
    }

    /**
     * Piirtää kaikki tällähetkellä olemassaolevat peliobjektit.
     * <p>
     * Varoitus "unchecked" on vaimennettu, koska metodi {@link #assignRenderer(Class, IRenderer)} pitää huolen ettei
     * tyyppiristiriitaa pääse syntymään.
     *
     * @param camera kamera jonka näkökulmasta piirretään
     */
    @SuppressWarnings("unchecked")
    public void draw(Camera camera) {
        for (val object : this.objects) {
            val renderer = this.objectRenderers.get(object.getClass());
            if (renderer != null) {
                renderer.draw(camera, object);
            }
        }
    }

    /**
     * Läpikäy objektit ja tuhoaa kaikki jotka on merkattu poistetuiksi.
     */
    public void deleteRemoved() {
        val removed = this.objects.stream()
            .filter(GameObject::isRemoved)
            .collect(Collectors.toList());

        for (val obj : removed) {
            this.objects.remove(obj);
        }
    }

    public <T extends GameObject> void assignRenderer(@NonNull Class<T> objectType, @NonNull IRenderer<T> renderer) {
        this.objectRenderers.put(objectType, renderer);
    }
}
