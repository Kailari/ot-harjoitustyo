package toilari.otlite.world.entities.characters;

import lombok.NonNull;
import toilari.otlite.world.entities.characters.controller.AnimalController;

public class AnimalCharacter extends AbstractCharacter {
    public AnimalCharacter(@NonNull AnimalController controller) {
        super(controller);
    }
}
