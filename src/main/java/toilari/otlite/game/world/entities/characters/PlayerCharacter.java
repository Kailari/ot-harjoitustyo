package toilari.otlite.game.world.entities.characters;

import lombok.Getter;

public class PlayerCharacter extends AbstractCharacter {
    @Getter private long deathTime;

    public PlayerCharacter() {
        this(new CharacterAttributes(
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

    public PlayerCharacter(CharacterAttributes attributes) {
        super(attributes);
    }

    @Override
    public void remove() {
        this.deathTime = System.currentTimeMillis();
        super.remove();
    }
}
