package toilari.otlite.dao.serialization;

import com.google.gson.*;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.world.level.NormalTile;
import toilari.otlite.game.world.level.Tile;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TileAdapter implements JsonDeserializer<Tile> {
    private final Map<String, Class<? extends Tile>> registeredTileTypes = new HashMap<>();

    public TileAdapter() {
        registerTileType("default", NormalTile.class);
    }

    public void registerTileType(@NonNull String key, @NonNull Class<? extends Tile> tileClass) {
        this.registeredTileTypes.put(key.toLowerCase(), tileClass);
    }

    @Override
    public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        val jsonObj = json.getAsJsonObject();
        val primitive = (JsonPrimitive) jsonObj.get("type");

        String key = null;
        if (primitive != null) {
            key = primitive.getAsString();
        }

        if (key == null) {
            key = "default";
        }

        val tileClass = this.registeredTileTypes.get(key.toLowerCase());
        Type actualType = tileClass == null ? NormalTile.class : tileClass;
        return context.deserialize(jsonObj, actualType);
    }
}
