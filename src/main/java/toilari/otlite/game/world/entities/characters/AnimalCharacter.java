package toilari.otlite.game.world.entities.characters;

import lombok.NonNull;

public class AnimalCharacter extends AbstractCharacter {
    @Override
    public boolean attack(@NonNull AbstractCharacter target, float amount) {
        return false;
    }
}
