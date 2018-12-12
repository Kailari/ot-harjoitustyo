package toilari.otlite.dao.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.util.Color;

import java.lang.reflect.Type;

/**
 * Gson-sarjoitusadapteri värien sarjoitukseen.
 */
@Slf4j
public class ColorAdapter implements JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        val array = json.getAsJsonArray();
        if (array == null || array.size() < 3) {
            LOG.warn("Invalid color definition, using black color.");
            return Color.BLACK;
        }

        float[] components = new float[3];
        for (int i = 0; i < 3; i++) {
            val primitive = array.get(i).getAsJsonPrimitive();
            if (primitive == null) {
                LOG.warn("Invalid color definition, using black color.");
                return Color.BLACK;
            }

            components[i] = primitive.getAsFloat();
        }

        return new Color(components[0], components[1], components[2]);
    }
}
