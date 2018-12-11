package toilari.otlite.game;

import lombok.Getter;
import lombok.Setter;
import toilari.otlite.game.event.BestiaryEvent;
import toilari.otlite.game.world.entities.characters.CharacterObject;

public class BestiaryGameState extends GameState {
    @Getter @Setter private CharacterObject activeCharacter;

    @Override
    public boolean init() {
        getEventSystem().subscribeTo(BestiaryEvent.Return.class, (e) -> getGame().changeState(new MainMenuGameState()));
        return false;
    }

    @Override
    public void update(float delta) {
        if (this.activeCharacter != null && this.activeCharacter.isSpawned()) {
            this.activeCharacter.update(delta);
        }
    }

    @Override
    public void destroy() {

    }
}
