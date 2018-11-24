package toilari.otlite.view.lwjgl.renderer;

import toilari.otlite.game.GameState;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.renderer.IGameStateRenderer;

public interface ILWJGLGameStateRenderer<T extends GameState> extends IGameStateRenderer<T, LWJGLCamera> {
}
