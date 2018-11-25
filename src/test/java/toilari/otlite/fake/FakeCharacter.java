package toilari.otlite.fake;

import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;

public class FakeCharacter extends AbstractCharacter {
    public FakeCharacter(CharacterAttributes attributes) {
        super(attributes);
    }

    public FakeCharacter() {
        super(new CharacterAttributes(
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
