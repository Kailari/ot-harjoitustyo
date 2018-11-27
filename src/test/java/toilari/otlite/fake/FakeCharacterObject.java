package toilari.otlite.fake;

import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterObject;

public class FakeCharacterObject extends CharacterObject {
    public FakeCharacterObject(CharacterAttributes attributes) {
        super(attributes);
    }

    public FakeCharacterObject() {
        super(new CharacterAttributes(1, 0, 1, 0, 10,
            2,
            2,
            0,
            0.1f,
            0.0f,
            0.001f,
            0.0f,
            0.0f,
            1.0f,
            0.1f,
            0.0f,
            0.1f,
            10.0f,
            0.1f,
            0.5f,
            0.001f
        ));
    }
}
