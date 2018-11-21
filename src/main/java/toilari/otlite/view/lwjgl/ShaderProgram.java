package toilari.otlite.view.lwjgl;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

/**
 * Apuluokka sävytinohjelmien käsittelyyn.
 */
@Slf4j
public class ShaderProgram {
    private final int vertexShader;
    private final int fragmentShader;
    @Getter private final int program;

    /**
     * Luo uuden sävyttimen.
     *
     * @param vertexShaderSource   verteksisävyttimen lähdekoodi
     * @param fragmentShaderSource fragmenttisävyttimen lähdekoodi
     * @param attributeLocations   verteksisävyttimen attribuuttien indeksit
     * @param outLocations         fragmenttiatribuuttien indeksit
     * @throws NullPointerException jos mikään parametreista on <code>null</code>
     */
    public ShaderProgram(
        @NonNull String vertexShaderSource,
        @NonNull String fragmentShaderSource,
        @NonNull Map<Integer, String> attributeLocations,
        @NonNull Map<Integer, String> outLocations
    ) {
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

        for (val entry : attributeLocations.entrySet()) {
            glBindAttribLocation(this.program, entry.getKey(), entry.getValue());
        }

        for (val entry : outLocations.entrySet()) {
            glBindFragDataLocation(this.program, entry.getKey(), entry.getValue());
        }

        glLinkProgram(this.program);

        if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
            LOG.error(glGetProgramInfoLog(this.program));
        }
    }

    /**
     * Asettaa sävytinohjelman käyttöön.
     */
    public void use() {
        glUseProgram(this.program);
    }

    /**
     * Vapauttaa sävytinohjelmalle varatut resurssit.
     */
    public void destroy() {
        glDeleteShader(this.vertexShader);
        glDeleteShader(this.fragmentShader);
        glDeleteProgram(this.program);
    }
}
