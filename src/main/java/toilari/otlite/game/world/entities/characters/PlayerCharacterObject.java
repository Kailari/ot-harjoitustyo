package toilari.otlite.game.world.entities.characters;

import lombok.Getter;

public class PlayerCharacterObject extends CharacterObject {

    /**
     * TODO: Hankkiudu tästä luokasta eroon kunhan hahmojen lukeminen tiedostosta onnistuu.
     */
    public PlayerCharacterObject() {
        super(new CharacterAttributes(
            1,
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
