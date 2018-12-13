package toilari.otlite.view.lwjgl;

import lombok.NonNull;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;

/**
 * Pelimaailmaan piirettävä kaksiuloitteinen kuva.
 */
public class Sprite {
    @NonNull private final Texture texture;
    private final int regionStartX;
    private final int regionStartY;
    private final int regionWidth;
    private final int regionHeight;

    /**
     * Luo uuden spriten.
     *
     * @param texture      tekstuuri jota tämä sprite käyttää
     * @param regionStartX tekstuurin x-koordinaatti josta piirrettävä alue alkaa (pikseleinä)
     * @param regionStartY tekstuurin y-koordinaatti josta piirrettävä alue alkaa (pikseleinä)
     * @param regionWidth  piirrettävän alueen leveys (pikseleinä)
     * @param regionHeight piirrettävän alueen korkeus (pikseleinä)
     *
     * @throws NullPointerException jos tekstuuri on null
     */
    public Sprite(
        @NonNull Texture texture,
        int regionStartX,
        int regionStartY,
        int regionWidth,
        int regionHeight
    ) {
        this.texture = texture;

        this.regionStartX = regionStartX;
        this.regionStartY = regionStartY;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
    }

    /**
     * Piirtää spriten annettuihin koordinaatteihin.
     *
     * @param camera kamera jonka näkökulmasta piirretään
     * @param batch  sarjapiirtäjä jonka jonoon piirto-operaatio asetetaan
     * @param x      x-koordinaatti johon piirretään
     * @param y      y-koordinaatti johon piirretään
     * @param w      piirrettävän spriten leveys
     * @param h      piirrettävän spriten korkeus
     * @param color  värisävy
     *
     * @throws NullPointerException jos kamera on <code>null</code>
     */
    public void draw(@NonNull LWJGLCamera camera, @NonNull SpriteBatch batch, float x, float y, float w, float h, @NonNull Color color) {
        batch.queue(camera, this.texture, color, x, y, w, h, this.regionStartX, this.regionStartY, this.regionWidth, this.regionHeight);
    }
}
