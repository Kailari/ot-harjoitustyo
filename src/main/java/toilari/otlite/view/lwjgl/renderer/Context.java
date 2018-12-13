package toilari.otlite.view.lwjgl.renderer;

import toilari.otlite.game.util.Color;

import java.util.HashMap;
import java.util.Map;

public class Context {
    public String texture = "sheep.png";
    public int nFrames = 1;
    public Map<String, int[]> states = new HashMap<>();
    public float framesPerSecond = 1.0f;

    public Color color = new Color(1.0f, 1.0f, 1.0f);

    public int width = 8;
    public int height = 8;
}
