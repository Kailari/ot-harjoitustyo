package toilari.otlite.rendering.lwjgl;

import lombok.NonNull;
import toilari.otlite.rendering.Camera;

public class AnimatedSprite {
    private final Sprite[] frames;

    public AnimatedSprite(@NonNull Texture texture, int nFrames, int width, int height) {
        int frameWidth = texture.getWidth() / nFrames;
        int frameHeight = texture.getHeight();

        this.frames = new Sprite[nFrames];
        for (int i = 0; i < nFrames; i++) {
            this.frames[i] = new Sprite(
                texture,
                i * frameWidth,
                0, frameWidth,
                frameHeight,
                width,
                height
            );
        }
    }

    public void draw(@NonNull Camera camera, int x, int y, int frame, float r, float g, float b) {
        this.frames[frame].draw(camera, x, y, r, g, b);
    }
}
