package toilari.otlite.rendering;

import lombok.NonNull;
import toilari.otlite.rendering.lwjgl.Sprite;
import toilari.otlite.rendering.lwjgl.Texture;
import toilari.otlite.world.entities.characters.PlayerCharacter;

import static org.lwjgl.opengl.GL11.*;

public class PlayerRenderer implements IRenderer<PlayerCharacter> {
    @NonNull private final Texture texture;

    @NonNull private final Sprite sprite;

    public PlayerRenderer(@NonNull Texture texture) {
        this.texture = texture;
        this.sprite = new Sprite(texture, 0, 0, texture.getWidth() / 2, texture.getHeight(), 8, 8);
    }

    @Override
    public void draw(Camera camera, PlayerCharacter player) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.sprite.draw(camera, player.getX(), player.getY());
    }
}
