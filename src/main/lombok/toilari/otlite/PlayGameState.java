package toilari.otlite;

import toilari.otlite.world.World;

/**
 * Pelin varsinainen pelillinen osuus.
 */
public class PlayGameState extends GameState {
    private World world;

    /**
     * Luo uuden pelitila-instanssin.
     */
    public PlayGameState() {
        super("Game");
        world = new World();
    }

    @Override
    public void init() {
        
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {

    }

    @Override
    public void destroy() {

    }
}
