package toilari.otlite.rendering.lwjgl;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import toilari.otlite.io.util.TextFileHelper;
import toilari.otlite.rendering.Texture;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glDrawElements;

/**
 * Tekstuurin laajennos, jolla tekstuureja voidaan piirtää ruudulle.
 */
public class Sprite {
    private static ShaderProgram shader;

    private static ShaderProgram getShader() {
        if (Sprite.shader == null) {
            try {
                val vertSrc = TextFileHelper.readFileToString("content/shaders/sprite.vert");
                val fragSrc = TextFileHelper.readFileToString("content/shaders/sprite.frag");
                Sprite.shader = new ShaderProgram(vertSrc, fragSrc);
            } catch (IOException e) {
                throw new IllegalStateException("Default sprite shader could not be loaded.");
            }
        }
        return Sprite.shader;
    }

    @NonNull private final Texture texture;

    private final int vao;
    private final int vbo;
    private final int ebo;

    public Sprite(@NonNull Texture texture, int regionStartX, int regionStartY, int regionWidth, int regionHeight) {
        this.texture = texture;

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);
        try (val stack = MemoryStack.stackPush()) {
            val vertices = createVertices(
                stack,
                texture.getWidth(),
                texture.getHeight(),
                regionStartX,
                regionStartY,
                regionWidth,
                regionHeight);

            this.vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);

            this.ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
            val indices = stack.mallocInt(2 * 3);
            indices.put(0).put(1).put(2);
            indices.put(2).put(3).put(0);
            indices.flip();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            getShader();
        }
    }

    private static FloatBuffer createVertices(MemoryStack stack, float width, float height, int regionStartX, int regionStartY, int regionWidth, int regionHeight) {
        float uMax = (regionStartX + regionWidth) / width;
        float vMax = (regionStartY + regionHeight) / height;

        float uMin = regionStartX / width;
        float vMin = regionStartY / height;


        // xy   => 2
        // uv   => 2
        // 4 corners * (2 + 2) => 16
        val vertices = stack.mallocFloat(4 * 4);
        vertices.put(0.0f).put(0.0f)
            .put(uMin).put(vMin);
        vertices.put(regionWidth).put(0.0f)
            .put(uMax).put(vMin);
        vertices.put(regionWidth).put(regionHeight)
            .put(uMax).put(vMax);
        vertices.put(0.0f).put(regionHeight)
            .put(uMin).put(vMax);

        vertices.flip();

        return vertices;
    }


    public void draw(int x, int y) {
        float[] tmp1 = new float[4 * 4];
        float[] tmp2 = new float[4 * 4];
        float[] tmp3 = new float[4 * 4];



        this.texture.bind();
        getShader().use();

        val uniformModel = glGetUniformLocation(getShader().getProgram(), "model");
        var model = new Matrix4f();
        model = model.translate(x, y, 0.0f);
        glUniformMatrix4fv(uniformModel, false, model.get(tmp1));

        val uniformView = glGetUniformLocation(getShader().getProgram(), "view");
        var view = new Matrix4f();
        view = view.identity();
        glUniformMatrix4fv(uniformView, false, view.get(tmp2));

        val uniformProj = glGetUniformLocation(getShader().getProgram(), "projection");
        var proj = new Matrix4f();

        float ratio = 800f / 600f;
        float zoom = 100.0f;
        proj = proj.setOrtho2D(-ratio * zoom, ratio * zoom, -1f * zoom, 1f * zoom);
        glUniformMatrix4fv(uniformProj, false, proj.get(tmp3));

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    public void destroy() {
        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vao);
    }
}
