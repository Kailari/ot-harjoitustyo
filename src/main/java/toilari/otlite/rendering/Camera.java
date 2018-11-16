package toilari.otlite.rendering;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * "Kamera" joka kuvaa pelimaailmaa. Sisältää apumetodeja kameran liikutteluun ja hallintaan pelimaailmassa.
 */
public class Camera {
    @NonNull @Getter private Matrix4f projectionMatrix;
    @NonNull @Getter private float[] projectionMatrixArr = new float[16];
    @NonNull private Matrix4f viewMatrix;
    @NonNull private float[] viewMatrixArr = new float[16];
    private boolean viewDirty;


    @NonNull @Getter private Vector2f position = new Vector2f(0.0f, 0.0f);
    @Getter private float zoom = 1.0f;

    /**
     * Pikseleiden määrä per ruutuyksikkö.
     * Esim. jos ikkuna on 800x600 niin 1.0 = leveyssuunnassa 800 pikseliä, 2.0 = 400 pikseliä jne.
     * TODO: Toteuta getter/setter siten että ne päivittävät projektiomatriisin
     *
     * @param pixelsPerUnit uusi pikseleiden määrä ruutuyksikköä kohden
     * @return pikseleiden määrä yhtä ruutuyksikköä kohden
     */
    @Getter @Setter private float pixelsPerUnit = 8.0f;

    /**
     * Luo uuden kameran pelimaailman tarkastelua varten.
     *
     * @param viewportWidth  ikkunan leveys
     * @param viewportHeight ikkunan korkeus
     */
    public Camera(int viewportWidth, int viewportHeight) {
        this.projectionMatrix = new Matrix4f();
        this.viewDirty = true;
        resizeViewport(viewportWidth, viewportHeight);
        refreshViewMatrix();
    }

    /**
     * Laskee näkymämatriisin. Metodi laskee matriisin ainoastaan jos kameran sijainti on muuttunut.
     *
     * @return näkymämatriisi
     */
    @NonNull
    public Matrix4f getViewMatrix() {
        refreshViewMatrix();
        return this.viewMatrix;
    }

    /**
     * Hakee näkymämatriisin taulukkona. Taulukko on muotoa <code>float[16]</code>
     *
     * @return näkymämatriisi taulukkona.
     */
    public float[] getViewMatrixArr() {
        refreshViewMatrix();
        return this.viewMatrixArr;
    }

    private void refreshViewMatrix() {
        if (this.viewDirty) {
            this.viewMatrix = new Matrix4f();
            this.viewMatrix
                .translate(this.position.x, this.position.y, 0.0f)
                .scale(this.zoom);
            this.viewMatrix.get(this.viewMatrixArr);

            this.viewDirty = false;
        }
    }


    /**
     * Asettaan kameralle uuden sijainnin.
     *
     * @param x kameran uusi x-kooridnaatti
     * @param y kameran uusi y-kooridnaatti
     */
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
        this.viewDirty = true;
    }

    /**
     * Asettaaa kameran suurennoksen.
     *
     * @param zoom kameran zoomi
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
        this.viewDirty = true;
    }


    /**
     * Asettaa näkymälle uuden koon. Alustaa projektiomatriisin vastaamaan ikkunan/näkymän kokoa.
     *
     * @param viewportWidth  näkymän leveys
     * @param viewportHeight näkymän korkeus
     */
    public void resizeViewport(int viewportWidth, int viewportHeight) {
        val ratio = (float) viewportWidth / viewportHeight;
        val mult = viewportHeight / this.pixelsPerUnit;
        this.projectionMatrix.setOrtho2D(0, ratio * mult, mult, 0);
        this.projectionMatrixArr = this.projectionMatrix.get(this.projectionMatrixArr);
    }
}
