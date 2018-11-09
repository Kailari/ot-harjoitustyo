package toilari.otlite.rendering;

import lombok.val;
import toilari.otlite.world.Level;

public class LevelRenderer implements IRenderer<Level> {
    @Override
    public void draw(Level level) {
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                val tile = level.getTileAt(x, y);
                System.out.printf("%c%c", tile.getSymbol(), (x == level.getWidth() - 1 ? '\n' : ' '));
            }
        }
    }
}
