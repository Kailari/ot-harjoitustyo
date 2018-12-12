package toilari.otlite.view.lwjgl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import toilari.otlite.game.util.Color;

/**
 * Spriten laajennos joka tarjoaa helpon tavan animaatioiden käsittelyyn.
 */
@Slf4j
public class AnimatedSprite {
    private final Sprite[] frames;

    /**
     * Luo uuden animoidun spriten.
     *
     * @param texture spriten tekstuuri
     * @param nFrames montako framea spritessä on
     * @param width   spriten leveys ruudulla (pelin yksiköissä)
     * @param height  spriten korkeus ruudulla (pelin yksiköissä)
     */
    public AnimatedSprite(@NonNull Texture texture, int nFrames, int width, int height) {
        int frameWidth = texture.getWidth() / nFrames;
        int frameHeight = texture.getHeight();

        this.frames = new Sprite[nFrames];
        for (int i = 0; i < nFrames; i++) {
            this.frames[i] = new Sprite(
                texture,
                i * frameWidth,
                0, frameWidth,
                frameHeight,
                width,
                height
            );
        }
    }

    /**
     * Piirtää yhden spriten framen ruudulle.
     *
     * @param camera kamera jonka näkökulmasta piirretään
     * @param x      x-koordinaatti johon piirretään (pelimaailmassa)
     * @param y      y-koordinaatti johon piirretään (pelimaailmassa)
     * @param frame  frame joka piiretään
     * @param color  värisävy
     * @throws NullPointerException jos kamera on <code>null</code>
     */
    public void draw(@NonNull LWJGLCamera camera, float x, float y, int frame, @NonNull Color color) {
        if (frame < 0 || frame >= this.frames.length) {
            LOG.warn("Frame index {} is out of bounds, check your renderer definitions!", frame);
            frame = 0;
        }
        this.frames[frame].draw(camera, x, y, color);
    }

    /**
     * Vapauttaa ladatut resurssit.
     */
    public void destroy() {
        for (Sprite frame : this.frames) {
            frame.destroy();
        }
    }
}
