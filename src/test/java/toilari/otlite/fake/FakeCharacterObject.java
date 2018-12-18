package toilari.otlite.fake;

import lombok.val;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.TurnObjectManager;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterObject;

import java.util.HashMap;
import java.util.Random;

public class FakeCharacterObject extends CharacterObject {
    private FakeCharacterObject() {
        this(new CharacterAttributes(null, 1, 0, 1, 0, 10,
            2,
            0,
            0.1f,
            0.05f,
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
        super(attributes, new Random());
    }

    public FakeCharacterObject(Random random) {
        super(new CharacterAttributes(null, 1, 0, 1, 0, 10,
            2,
            0,
            0.1f,
            0.05f,
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
        ), random);
    }

    public static FakeCharacterObject createImmortalWithAbilities(AbilityEntry... abilities) {
        val character = createWithAttributes(new CharacterAttributes(null, 1, 0, 1, 0, 10,
            2,
            0,
            0.1f,
            0.05f,
            0.001f,
            0.0f,
            0.0f,
            1.0f,
            0.1f,
            0.0f,
            0.1f,
            Float.POSITIVE_INFINITY,
            0.1f,
            0.5f,
            0.001f
        ));

        for (val entry : abilities) {
            character.getAbilities().addAbility(entry.getAbility(), entry.getComponent());
        }

        return character;
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

    public static HashMap<Direction, CharacterObject> createAround(TurnObjectManager manager, CharacterObject character) {
        val map = new HashMap<Direction, CharacterObject>();
        map.put(Direction.LEFT, createAt(character.getTileX() - 1, character.getTileY()));
        map.put(Direction.RIGHT, createAt(character.getTileX() + 1, character.getTileY()));
        map.put(Direction.UP, createAt(character.getTileX(), character.getTileY() - 1));
        map.put(Direction.DOWN, createAt(character.getTileX(), character.getTileY() + 1));
        manager.spawn(map.get(Direction.LEFT));
        manager.spawn(map.get(Direction.RIGHT));
        manager.spawn(map.get(Direction.UP));
        manager.spawn(map.get(Direction.DOWN));
        return map;
    }

    public static FakeCharacterObject create(Random random) {
        return new FakeCharacterObject(random);
    }
}
