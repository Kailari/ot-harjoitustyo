package toilari.otlite.view.lwjgl.batch;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joml.Matrix4f;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.util.Color;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.ShaderProgram;
import toilari.otlite.view.lwjgl.Texture;

import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Batch Renderer eli "Sarjapiirtäjä"/joukkopiirtäjä. Piirtäjä joka kokoaa useiden piirrettävien tekstuurien
 * piirtokäskyt yhteen ja suorittaa ne kerralla. Säästää huomattavan määrän resursseja.
 */
@Slf4j
public class SpriteBatch {
    private static final int MAX_SPRITES = 512;
    private static final int ATTRIBUTES_PER_SPRITE = 4 * (2 + 2 + 3); // 2 pos, 2 uv, 3 color
    private static ShaderProgram shader;
    private float[] vertices;
    private Matrix4f model;

    @NonNull
    private static ShaderProgram getShader() {
        if (shader == null) {
            try {
                val vertSrc = TextFileHelper.readFileToString("content/shaders/sprite.vert");
                val fragSrc = TextFileHelper.readFileToString("content/shaders/sprite.frag");

                val attrs = new HashMap<Integer, String>();
                attrs.put(0, "in_pos");
                attrs.put(1, "in_uv");
                attrs.put(2, "in_tint");

                val out = new HashMap<Integer, String>();
                out.put(0, "out_fragColor");

                shader = new ShaderProgram(vertSrc, fragSrc, attrs, out);
            } catch (IOException e) {
                throw new IllegalStateException("Default sprite shader could not be loaded.");
            }
        }
        return shader;
    }


    private int vao = -1;
    private int vbo = -1;
    private int ebo = -1;

    private boolean beginCalled;
    private Texture active;
    private int nSpritesInBatch;
    private int nVertices;

    /**
     * Alustaa piirtäjän.
     */
    public void init() {
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        this.vertices = new float[MAX_SPRITES * ATTRIBUTES_PER_SPRITE]; // 7 float fields
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        val indices = new int[MAX_SPRITES * 6];
        int j = 0;
        for (int i = 0; i < indices.length; i += 6, j += 4) {
            indices[i + 0] = j + 0;
            indices[i + 1] = j + 1;
            indices[i + 2] = j + 2;
            indices[i + 3] = j + 2;
            indices[i + 4] = j + 3;
            indices[i + 5] = j + 0;
        }
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    /**
     * Vapauttaa kaikki varatut resurssit.
     */
    public void destroy() {
        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);
    }

    /**
     * Avaa piirtojonon. Kutsuttava aina ennen kuin yhtään <code>queue(...)</code>-metodia voidaan kutsua.
     */
    public void begin() {
        if (this.vao == -1) {
            throw new IllegalStateException("VAO is not initialized!");
        }

        if (this.beginCalled) {
            throw new IllegalStateException("Call SpriteBatch.end() before .begin() for second time!");
        }

        this.model = new Matrix4f().identity();
        this.beginCalled = true;
        this.nVertices = 0;
        this.nSpritesInBatch = 0;
    }

    /**
     * Lopettaa piirtämisen ja tyhjentää jonon piirtämällä kaikki loput jonossa olevat kuvat näytölle.
     *
     * @param camera kamera jonka näkökulmasta piiretään
     */
    public void end(@NonNull LWJGLCamera camera) {
        if (!this.beginCalled) {
            throw new IllegalStateException("Call SpriteBatch.begin() before .end()!");
        }

        flush(camera);

        this.beginCalled = false;
    }

    /**
     * Asettaa kuvan jonoon piirettäväksi.
     *
     * @param camera       kamera jonka näkökulmasta piiretään
     * @param texture      piirettävä kuva
     * @param color        värisävy
     * @param x            x-koordinaatti pelimaailmassa
     * @param y            y-koordinaatti pelimaailmassa
     * @param w            leveys pelimaailmassa
     * @param h            korkeus pelimaailmassa
     * @param regionStartX kuvasta piirettävän alueen vasemman yläkulman x-koordinaatti
     * @param regionStartY kuvasta piirettävän alueen vasemman yläkulman y-koordinaatti
     * @param regionWidth  kuvasta piirettävän alueen leveys
     * @param regionHeight kuvasta piirettävän alueen korkeus
     */
    public void queue(@NonNull LWJGLCamera camera, @NonNull Texture texture, @NonNull Color color, float x, float y, float w, float h, int regionStartX, int regionStartY, int regionWidth, int regionHeight) {
        val textureWidth = texture.getWidth();
        val textureHeight = texture.getHeight();
        val u1 = (regionStartX + regionWidth) / (float) textureWidth;
        val v1 = (regionStartY + regionHeight) / (float) textureHeight;

        val u0 = regionStartX / (float) textureWidth;
        val v0 = regionStartY / (float) textureHeight;

        queue(camera, texture, color, x, y, w, h, u0, v0, u1, v1);
    }

    /**
     * Asettaa kuvan jonoon piirrettäväksi.
     *
     * @param camera  kamera jonka näkökulmasta piiretään
     * @param texture piirettävä kuva
     * @param color   värisävy
     * @param x       x-koordinaatti pelimaailmassa
     * @param y       y-koordinaatti pelimaailmassa
     * @param w       leveys pelimaailmassa
     * @param h       korkeus pelimaailmassa
     * @param u0      vasemman yläkulman u-koordinaatti
     * @param v0      vasemman yläkulman v-koordinaatti
     * @param u1      oikean alakulman u-koordinaatti
     * @param v1      oikean alakulman v-koordinaatti
     */
    public void queue(@NonNull LWJGLCamera camera, @NonNull Texture texture, @NonNull Color color, float x, float y, float w, float h, float u0, float v0, float u1, float v1) {
        if (!this.beginCalled) {
            throw new IllegalStateException("Call SpriteBatch.begin() before queuing data!");
        }

        if (this.active != null && (!texture.equals(this.active) || this.nSpritesInBatch >= MAX_SPRITES)) {
            flush(camera);
        }

        this.active = texture;
        queueVertex(color, x + 0, y + 0, u0, v0);
        queueVertex(color, x + w, y + 0, u1, v0);
        queueVertex(color, x + w, y + h, u1, v1);
        queueVertex(color, x + 0, y + h, u0, v1);

        this.nSpritesInBatch++;
    }

    private void queueVertex(@NonNull Color color, float x, float y, float u, float v) {
        this.vertices[this.nVertices++] = x;
        this.vertices[this.nVertices++] = y;
        this.vertices[this.nVertices++] = u;
        this.vertices[this.nVertices++] = v;
        this.vertices[this.nVertices++] = color.getR();
        this.vertices[this.nVertices++] = color.getG();
        this.vertices[this.nVertices++] = color.getB();
    }

    private void flush(@NonNull LWJGLCamera camera) {
        this.active.bind();
        prepareShader(camera);
        constructVBO();

        glDrawElements(GL_TRIANGLES, this.nSpritesInBatch * 6, GL_UNSIGNED_INT, 0);

        this.nSpritesInBatch = 0;
        this.nVertices = 0;
    }

    private void prepareShader(@NonNull LWJGLCamera camera) {
        getShader().use();

        val uniformModel = glGetUniformLocation(getShader().getProgram(), "model");
        val uniformView = glGetUniformLocation(getShader().getProgram(), "view");
        val uniformProj = glGetUniformLocation(getShader().getProgram(), "projection");

        glUniformMatrix4fv(uniformModel, false, this.model.get(new float[4 * 4]));
        glUniformMatrix4fv(uniformView, false, camera.getViewMatrixArr());
        glUniformMatrix4fv(uniformProj, false, camera.getProjectionMatrixArr());
    }

    private void constructVBO() {
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);

        // vertex position
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 7, 0);

        // vertex uv
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 7, 2 * 4);

        // vertex color
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 4 * 7, 4 * 4);
    }
}
