package toilari.otlite.game.world.entities.characters;

import lombok.NonNull;

public class AnimalCharacter extends AbstractCharacter {
    public AnimalCharacter() {
        this(new CharacterAttributes(
            1,
            2,
            0,
            0.1f,
            0.1f,
            0.001f,
            0.0f,
            0.0f,
            0.1f,
            0.01f,
            0.0f,
            0.1f,
            5.0f,
            0.1f,
            0.5f,
            0.001f
        ));
    }

    public AnimalCharacter(@NonNull CharacterAttributes attributes) {
        super(attributes);
    }
}
