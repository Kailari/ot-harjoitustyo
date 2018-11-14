package toilari.otlite.rendering.lwjgl;

import lombok.Getter;
import toilari.otlite.io.dao.TextureDAO;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
    @Getter private final int width;
    @Getter private final int height;

    private final int handle;

    /**
     * Luo uuden tekstuurin. Älä kutsu tätä konstruktoria suoraan, vaan käytä {@link TextureDAO}
     * -luokan tarjoamaa toiminnallisuutta tekstuurien luomiseen.
     *
     * @param width  tekstuurin leveys
     * @param height tekstuurin korkeus
     * @param handle tekstuuria vastaavan OpenGL-tekstuurin kahva/tunniste
     */
    public Texture(int width, int height, int handle) {
        this.width = width;
        this.height = height;
        this.handle = handle;
    }

    /**
     * Asettaa tekstuurin aktiiviseksi jotta piirtokomennot voivat käyttää sitä.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.handle);
    }

    /**
     * Jos tämä tekstuuri on aktiivinen, vapauttaa sen. Tämän metodin kutsumisen jälkeen mikään tekstuuri
     * ei ole liitettynä/aktiivisena.
     */
    public void release() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Vapauttaa tekstuurille varatut resurssit.
     */
    public void destroy() {
        glDeleteTextures(this.handle);
    }
}
