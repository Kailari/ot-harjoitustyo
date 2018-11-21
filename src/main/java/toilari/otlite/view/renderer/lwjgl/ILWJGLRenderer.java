package toilari.otlite.view.renderer.lwjgl;

import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.renderer.IRenderer;

/**
 * Piirtäjän erikoistapaus LWJGL-pohjaiseen piirtämiseen.
 *
 * @param <T> piirrettävän objektin tyyppi
 */
public interface ILWJGLRenderer<T> extends IRenderer<T, LWJGLCamera> {
}
