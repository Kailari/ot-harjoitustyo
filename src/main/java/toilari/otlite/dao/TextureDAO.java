package toilari.otlite.dao;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lwjgl.system.MemoryStack;
import toilari.otlite.dao.util.FileStreamHelper;
import toilari.otlite.view.lwjgl.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

/**
 * DAO tekstuurien lataamiseen kuvatiedostoista.
 */
@Slf4j
public class TextureDAO extends CachingDAO<Texture, String> {
    @NonNull private final Path root;

    /**
     * Luo uuden DAOn. Tekstuureja etsitään polusta joka annetaan parametrilla <code>root</code>
     *
     * @param root juurihakemisto josta tekstuureja etsitään
     * @throws NullPointerException jos juurihakemisto on <code>null</code>
     */
    public TextureDAO(@NonNull String root) {
        this.root = Paths.get(root);
    }

    /**
     * Lataa tekstuurin kuvatiedostosta.
     *
     * @param path polku josta kuvatiedostoa etsitään
     * @return <code>null</code> jos kuvatiedostoa ei löydy, muulloin ladatusta kuvatiedostosta luotu tekstuuri
     * @throws NullPointerException jos polku on <code>null</code>
     */
    @Override
    protected Texture load(@NonNull String path) {
        int handle = generateGLTexture();

        int width, height;
        try (val is = FileStreamHelper.openForReading(this.root.resolve(path)); val stack = MemoryStack.stackPush()) {
            val image = ImageIO.read(is);
            width = image.getWidth();
            height = image.getHeight();
            val buffer = loadImageData(stack, image, width, height);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } catch (IOException e) {
            LOG.warn("Kuvatiedostoa \"{}\" ei löytynyt tai sitä ei voitu ladata!");
            return null;
        }

        return new Texture(width, height, handle);
    }

    @Override
    public Texture get(String key) {
        val texture = super.get(key);
        texture.init();
        return texture;
    }

    private static int generateGLTexture() {
        val handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        return handle;
    }

    private ByteBuffer loadImageData(@NonNull MemoryStack stack, @NonNull BufferedImage image, int width, int height) {
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        val buffer = stack.malloc(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));  // R
                buffer.put((byte) ((pixel >> 8) & 0xFF));   // G
                buffer.put((byte) (pixel & 0xFF));          // B
                buffer.put((byte) ((pixel >> 24) & 0xFF));  // A
            }
        }

        buffer.flip();

        return buffer;
    }
}
