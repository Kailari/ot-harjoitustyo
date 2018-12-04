package toilari.otlite.view.lwjgl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * "Kamera" joka kuvaa pelimaailmaa. Sisältää apumetodeja kameran liikutteluun ja hallintaan pelimaailmassa.
 */
public class LWJGLCamera extends toilari.otlite.view.Camera {

    @NonNull private Matrix4f viewMatrix = new Matrix4f();
    @NonNull private float[] viewMatrixArr = new float[16];
    @Getter private final float pixelsPerUnit;

    private int viewportWidth;
    private int viewportHeight;

    @Getter private float zoom = 1.0f;
    private boolean viewDirty;

    /**
     * Matriisi joka sisältää tarvittavat transformaatiot pelimaailman koordinaattien projisoimiseksi
     * ruutukoordinaateiksi. Projektiomatriisi päivitetään aina kun ruudun koko muuttuu.
     *
     * @return projektiomatriisi
     */
    @NonNull @Getter(AccessLevel.PACKAGE) private Matrix4f projectionMatrix = new Matrix4f();

    /**
     * Projektiomatriisi taulukkona.
     *
     * @return 16-soluinen (4x4, riveittäin) {@link #getProjectionMatrix() projektiomatriisia} vastaava taulukko
     */
    @NonNull @Getter private float[] projectionMatrixArr = new float[16];


    public float getViewportWidth() {
        return this.viewportWidth / this.pixelsPerUnit;
    }

    public float getViewportHeight() {
        return this.viewportHeight / this.pixelsPerUnit;
    }

    /**
     * Hakee näkymämatriisin taulukkona. Taulukko on muotoa <code>float[16]</code>
     *
     * @return näkymämatriisi taulukkona.
     */
    float[] getViewMatrixArr() {
        refreshViewMatrix();
        return this.viewMatrixArr;
    }

    /**
     * Asettaaa kameran zoomin.
     *
     * @param zoom kameran zoomi
     */
    void setZoom(float zoom) {
        this.zoom = zoom;
        this.viewDirty = true;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.viewDirty = true;
    }

    /**
     * Luo uuden kameran pelimaailman tarkastelua varten.
     *
     * @param viewportWidth  ikkunan leveys
     * @param viewportHeight ikkunan korkeus
     * @param pixelsPerUnit  Pikseleiden määrä per ruutuyksikkö. Esim. jos ikkuna on 800x600 niin vaakasuunnassa
     *                       ruutuyksikköjen määrä on ppu=1.0 => 800, ppu=2.0 => 400, jne.
     */
    LWJGLCamera(int viewportWidth, int viewportHeight, float pixelsPerUnit) {
        this.pixelsPerUnit = pixelsPerUnit;
        resizeViewport(viewportWidth, viewportHeight);

        this.viewDirty = true;
        refreshViewMatrix();
    }

    /**
     * Asettaa näkymälle uuden koon. Alustaa projektiomatriisin vastaamaan ikkunan/näkymän kokoa.
     *
     * @param viewportWidth  näkymän leveys
     * @param viewportHeight näkymän korkeus
     */
    void resizeViewport(int viewportWidth, int viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        val ratio = (float) viewportWidth / viewportHeight;
        val mult = viewportHeight / this.pixelsPerUnit;
        this.projectionMatrix.setOrtho2D(0, ratio * mult, mult, 0);
        this.projectionMatrixArr = this.projectionMatrix.get(this.projectionMatrixArr);
        glViewport(0, 0, viewportWidth, viewportHeight);
    }

    private void refreshViewMatrix() {
        if (this.viewDirty) {
            this.viewMatrix
                .identity()
                .translate(getX(), getY(), 0.0f)
                .scale(this.zoom)
                .invert();
            this.viewMatrix.get(this.viewMatrixArr);

            this.viewDirty = false;
        }
    }
}
