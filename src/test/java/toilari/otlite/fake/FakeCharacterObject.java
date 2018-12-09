package toilari.otlite.fake;

import lombok.val;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterObject;

public class FakeCharacterObject extends CharacterObject {
    private FakeCharacterObject() {
        this(new CharacterAttributes(1, 0, 1, 0, 10,
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

    private FakeCharacterObject(CharacterAttributes attributes) {
        super(attributes);
    }

    public static FakeCharacterObject create() {
        return new FakeCharacterObject();
    }

    public static FakeCharacterObject createWithAttributes(CharacterAttributes attrs) {
        return new FakeCharacterObject(attrs);
    }

    public static FakeCharacterObject createWithAbilities(AbilityEntry... abilities) {
        val character = new FakeCharacterObject();
        for (val entry : abilities) {
            character.getAbilities().addAbility(entry.getAbility(), entry.getComponent());
        }

        return character;
    }

    public static FakeCharacterObject createAt(int x, int y) {
        val character = create();
        character.setTilePos(x, y);
        return character;
    }

    public static FakeCharacterObject createAtWithAttributes(int x, int y, CharacterAttributes attrs) {
        val character = createWithAttributes(attrs);
        character.setTilePos(x, y);
        return character;
    }

    public static FakeCharacterObject createAtWithAbilities(int x, int y, AbilityEntry... abilities) {
        val character = createAt(x, y);
        for (val entry : abilities) {
            character.getAbilities().addAbility(entry.getAbility(), entry.getComponent());
        }

        return character;
    }
}
