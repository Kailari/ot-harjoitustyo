package toilari.otlite.rendering.lwjgl;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.joml.Matrix4f;
import toilari.otlite.io.util.TextFileHelper;
import toilari.otlite.rendering.Camera;

import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glDrawElements;

/**
 * Tekstuurin laajennos, jolla tekstuureja voidaan piirtää ruudulle.
 */
public class Sprite {
    private static ShaderProgram shader;

    @NonNull
    private static ShaderProgram getShader() {
        if (Sprite.shader == null) {
            try {
                val vertSrc = TextFileHelper.readFileToString("content/shaders/sprite.vert");
                val fragSrc = TextFileHelper.readFileToString("content/shaders/sprite.frag");

                val attrs = new HashMap<Integer, String>();
                attrs.put(0, "in_pos");
                attrs.put(1, "in_uv");

                val out = new HashMap<Integer, String>();
                out.put(0, "out_fragColor");

                Sprite.shader = new ShaderProgram(vertSrc, fragSrc, attrs, out);
            } catch (IOException e) {
                throw new IllegalStateException("Default sprite shader could not be loaded.");
            }
        }
        return Sprite.shader;
    }

    @NonNull private final Texture texture;
    private final int vao;

    /**
     * Luo uuden spriten.
     *
     * @param texture      tekstuuri jota tämä sprite käyttää
     * @param regionStartX tekstuurin x-koordinaatti josta piirrettävä alue alkaa (pikseleinä)
     * @param regionStartY tekstuurin y-koordinaatti josta piirrettävä alue alkaa (pikseleinä)
     * @param regionWidth  piirrettävän alueen leveys (pikseleinä)
     * @param regionHeight piirrettävän alueen korkeus (pikseleinä)
     * @param width        koko pelimaailmassa (pelimaailman yksiköissä)
     * @param height       koko pelimaailmassa (pelimaailman yksiköissä)
     * @throws NullPointerException jos tekstuuri on null
     */
    public Sprite(
        @NonNull Texture texture,
        int regionStartX,
        int regionStartY,
        int regionWidth,
        int regionHeight,
        int width,
        int height
    ) {
        this.texture = texture;

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);
        val vertices = createVertices(
            texture.getWidth(),
            texture.getHeight(),
            regionStartX,
            regionStartY,
            regionWidth,
            regionHeight,
            width,
            height);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);

        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        val indices = new int[]{
            0, 1, 2,
            2, 3, 0
        };
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    private static float[] createVertices(float textureWidth, float textureHeight, int regionStartX, int regionStartY, int regionWidth, int regionHeight, float width, float height) {
        float uMax = (regionStartX + regionWidth) / textureWidth;
        float vMax = (regionStartY + regionHeight) / textureHeight;

        float uMin = regionStartX / textureWidth;
        float vMin = regionStartY / textureHeight;

        return new float[]{
            0.0f, 0.0f, uMin, vMin,
            width, 0.0f, uMax, vMin,
            width, height, uMax, vMax,
            0.0f, height, uMin, vMax,
        };
    }

    /**
     * Piirtää spriten annettuihin koordinaatteihin.
     *
     * @param camera kamera jonka näkökulmasta piirretään
     * @param x      x-koordinaatti johon piirretään
     * @param y      y-koordinaatti johon piirretään
     * @throws NullPointerException jos kamera on <code>null</code>
     */
    public void draw(@NonNull Camera camera, int x, int y) {
        this.texture.bind();
        getShader().use();

        val uniformModel = glGetUniformLocation(getShader().getProgram(), "model");
        val uniformView = glGetUniformLocation(getShader().getProgram(), "view");
        val uniformProj = glGetUniformLocation(getShader().getProgram(), "projection");

        var model = new Matrix4f();
        model = model.translate(x, y, 0.0f);
        glUniformMatrix4fv(uniformModel, false, model.get(new float[4 * 4]));
        glUniformMatrix4fv(uniformView, false, camera.getViewMatrixArr());
        glUniformMatrix4fv(uniformProj, false, camera.getProjectionMatrixArr());

        glBindVertexArray(this.vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    /**
     * Vapauttaa spritelle varatut resurssit.
     */
    public void destroy() {
        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vao);
    }
}
