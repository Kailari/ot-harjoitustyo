package toilari.otlite.game.world.level;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.dao.IGetAllDAO;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.world.entities.ObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class LevelData {
    @Getter private int width = 0, height = 0;
    @Getter private Map<String, Byte> mapping = new HashMap<>();
    @Getter private byte[] tiles = new byte[0];

    @SerializedName("characters")
    @Getter private List<CharacterEntry> characterEntries = new ArrayList<>();

    /**
     * Muuttaa datan konkreettiseksi kartaksi.
     *
     * @param tiles ruututyyppien hakemiseen käytettävä DAO
     * @return kartta joka on rakennettu datasta
     * @throws NullPointerException jos ruutudao on <code>null</code>
     */
    public Level asLevel(@NonNull IGetAllDAO<Tile> tiles) {
        return new Level(this.width, this.height, new TileMapping(tiles, this.mapping), this.tiles);
    }

    /**
     * Lisää kaikki kartan mukana ladatut hahmot pelimaailmaan.
     *
     * @param characters hahmo-dao jolla saadaan ladattua hahmojen ID mukaiset templaatit
     * @param manager    objektimanageri jolla hahmot lisätään pelimaailmaan
     */
    public void spawn(@NonNull IGetByIDDao<CharacterObject> characters, @NonNull ObjectManager manager) {
        for (val entry : this.characterEntries) {
            if (entry.id.equals("player")) {
                val player = manager.getPlayer();
                player.setTilePos(entry.x, entry.y);
            } else {
                val template = characters.getByID(entry.id);
                manager.spawnTemplateAt(template, entry.x, entry.y);
            }
        }
    }

    private static class CharacterEntry {
        int x, y;
        String id;
    }
}
