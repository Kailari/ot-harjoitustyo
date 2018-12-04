package toilari.otlite.game;

import toilari.otlite.game.event.MainMenuEvent;
import toilari.otlite.game.event.MenuEvent;
import toilari.otlite.game.world.entities.TurnObjectManager;

public class MainMenuGameState extends GameState {
    @Override
    public boolean init() {
        getEventSystem().subscribeTo(MenuEvent.Quit.class, (e) -> getGame().setRunning(false));
        getEventSystem().subscribeTo(MainMenuEvent.NewGame.class, (e) -> getGame().changeState(new PlayGameState(new TurnObjectManager())));
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }
}
