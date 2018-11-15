package toilari.otlite.world.entities.characters;

import lombok.NonNull;
import toilari.otlite.world.entities.characters.controller.PlayerController;

public class PlayerCharacter extends AbstractCharacter {
    public PlayerCharacter(@NonNull PlayerController controller) {
        super(controller);
    }
}
