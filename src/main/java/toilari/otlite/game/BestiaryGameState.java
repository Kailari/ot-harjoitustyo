package toilari.otlite.game;

import toilari.otlite.game.event.BestiaryEvent;

public class BestiaryGameState extends GameState {
    @Override
    public boolean init() {
        getEventSystem().subscribeTo(BestiaryEvent.Return.class, (e) -> getGame().changeState(new MainMenuGameState()));
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }
}
