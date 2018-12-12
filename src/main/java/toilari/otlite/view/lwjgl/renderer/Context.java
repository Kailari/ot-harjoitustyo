package toilari.otlite.view.lwjgl.renderer;

import toilari.otlite.game.util.Color;

public class Context {
    public String texture = "sheep.png";
    public int nFrames = 1;
    public int[] idleFrames = {0};
    public int[] walkFrames = {0};
    public float framesPerSecond = 1.0f;

    public Color color = new Color(1.0f, 1.0f, 1.0f);

    public int width = 8;
    public int height = 8;
}
