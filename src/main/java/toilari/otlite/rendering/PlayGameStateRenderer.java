package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.game.PlayGameState;
import toilari.otlite.io.dao.TextureDAO;
import toilari.otlite.io.util.TextFileHelper;
import toilari.otlite.rendering.lwjgl.ShaderProgram;

import java.io.IOException;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class PlayGameStateRenderer implements IRenderer<PlayGameState> {
    private final TextureDAO textures = new TextureDAO("content/textures/");
    private LevelRenderer levelRenderer;

    private ShaderProgram program;
    private int vao;
    private int vbo;
    private int ebo;

    @Override
    public boolean init(PlayGameState playGameState) {
        val tileset = this.textures.load("tileset.png");
        this.levelRenderer = new LevelRenderer(tileset, 8, 8);

        float[] square = {
            -0.5f, -0.5f, 0f, 0f,
            0.5f, -0.5f, 1f, 0f,
            0.5f, 0.5f, 1f, 1f,
            -0.5f, 0.5f, 0f, 1f
        };

        try {
            this.program = new ShaderProgram(
                TextFileHelper.readFileToString("content/shaders/sprite.vert"),
                TextFileHelper.readFileToString("content/shaders/sprite.frag")
            );
        } catch (
            IOException e) {
            e.printStackTrace();
        }

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, square, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);
        glEnableVertexAttribArray(1);


        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        val indices = new int[]{0, 1, 2, 2, 3, 0};
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);


        return true;
    }

    @Override
    public void draw(PlayGameState playGameState) {
        val world = playGameState.getWorld();
        this.levelRenderer.draw(world.getCurrentLevel());



        /*this.program.use();
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);*/
    }

    @Override
    public void destroy(PlayGameState playGameState) {

    }
}
