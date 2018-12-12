package toilari.otlite.game;

import toilari.otlite.game.event.MainMenuEvent;
import toilari.otlite.game.event.MenuEvent;

/**
 * Päävalikon pelitila.
 */
public class MainMenuGameState extends GameState {
    @Override
    public boolean init() {
        getEventSystem().subscribeTo(MenuEvent.Quit.class, (e) -> getGame().setRunning(false));
        getEventSystem().subscribeTo(
            MainMenuEvent.NewGame.class,
            (e) -> getGame().changeState(new PlayGameState(
                getGame().getNewObjectManager(),
                getGame().getTiles(),
                getGame().getCharacters(),
                getGame().getLevels())
            )
        );
        getEventSystem().subscribeTo(MainMenuEvent.Bestiary.class, (e) -> getGame().changeState(new BestiaryGameState()));
        return false;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void destroy() {

    }
}
