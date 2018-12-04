package toilari.otlite.view;

import lombok.Getter;

public class Camera {
    @Getter private float x = 0.0f, y = 0.0f;

    /**
     * Asettaan kameralle uuden sijainnin.
     *
     * @param x kameran uusi x-kooridnaatti
     * @param y kameran uusi y-kooridnaatti
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
