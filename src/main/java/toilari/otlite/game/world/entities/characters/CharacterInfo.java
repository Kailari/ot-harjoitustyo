package toilari.otlite.game.world.entities.characters;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Hahmon tiedot.
 */
@NoArgsConstructor
public class CharacterInfo {
    @Getter private String name = "Unnamed";

    /**
     * Kopioi hahmon tiedot templaatista.
     *
     * @param template tiedot joista kopioidaan
     */
    public CharacterInfo(CharacterInfo template) {
        this.name = template.name;
    }
}
