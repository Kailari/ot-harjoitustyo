package toilari.otlite.rendering;

import lombok.NonNull;
import toilari.otlite.rendering.lwjgl.Sprite;
import toilari.otlite.rendering.lwjgl.Texture;
import toilari.otlite.world.entities.characters.AbstractCharacter;

import static org.lwjgl.opengl.GL11.*;

public class CharacterRenderer implements IRenderer<AbstractCharacter> {
    @NonNull private final Texture texture;

    @NonNull private final Sprite sprite;

    public CharacterRenderer(@NonNull Texture texture, int frames) {
        this.texture = texture;
        this.sprite = new Sprite(texture, 0, 0, texture.getWidth() / frames, texture.getHeight(), 8, 8);
    }

    @Override
    public void draw(Camera camera, AbstractCharacter character) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        this.sprite.draw(camera, character.getX(), character.getY());
    }

    @Override
    public void destroy(AbstractCharacter character) {
        this.texture.destroy();
    }
}
