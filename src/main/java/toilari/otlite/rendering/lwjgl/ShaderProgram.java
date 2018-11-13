package toilari.otlite.rendering.lwjgl;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

@Slf4j
public class ShaderProgram {
    private final int vertexShader;
    private final int fragmentShader;
    @Getter private final int program;

    public ShaderProgram(@NonNull String vertexShaderSource, @NonNull String fragmentShaderSource) {
        this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(this.vertexShader, vertexShaderSource);
        glCompileShader(this.vertexShader);

        this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(this.fragmentShader, fragmentShaderSource);
        glCompileShader(this.fragmentShader);

        if (glGetShaderi(this.vertexShader, GL_COMPILE_STATUS) != GL_TRUE) {
            LOG.error("Error compiling vertex shader: {}", glGetShaderInfoLog(this.vertexShader));
        }

        if (glGetShaderi(this.fragmentShader, GL_COMPILE_STATUS) != GL_TRUE) {
            LOG.error("Error compiling fragment shader: {}", glGetShaderInfoLog(this.fragmentShader));
        }

        this.program = glCreateProgram();
        glAttachShader(this.program, this.vertexShader);
        glAttachShader(this.program, this.fragmentShader);

        glBindAttribLocation(this.program, 0, "in_pos");
        glBindAttribLocation(this.program, 1, "in_uv");
        glBindFragDataLocation(this.program, 0, "out_fragColor");
        glLinkProgram(this.program);

        if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
            LOG.error(glGetProgramInfoLog(this.program));
        }
    }

    public void use() {
        glUseProgram(this.program);
    }

    public void destroy() {
        glDeleteShader(this.vertexShader);
        glDeleteShader(this.fragmentShader);
        glDeleteProgram(this.program);
    }
}
