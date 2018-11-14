package toilari.otlite.rendering;

import lombok.extern.slf4j.Slf4j;
import toilari.otlite.game.Game;

/**
 * Piirtää pelin.
 */
@Slf4j
public class GameRenderer implements IRenderer<Game> {
    @Override
    public void draw(Camera camera, Game game) {
        game.getCurrentGameState().draw(camera);
    }
}
