package toilari.otlite.view;

import lombok.Getter;
import lombok.NonNull;
import org.joml.Vector2f;

public class Camera {
    @NonNull @Getter private Vector2f position = new Vector2f(0.0f, 0.0f);

    /**
     * Asettaan kameralle uuden sijainnin.
     *
     * @param x kameran uusi x-kooridnaatti
     * @param y kameran uusi y-kooridnaatti
     */
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }
}
