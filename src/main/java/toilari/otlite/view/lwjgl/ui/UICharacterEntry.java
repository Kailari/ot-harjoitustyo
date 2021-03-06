package toilari.otlite.view.lwjgl.ui;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.dao.serialization.IGetByIDDao;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.view.lwjgl.LWJGLCamera;
import toilari.otlite.view.lwjgl.batch.SpriteBatch;
import toilari.otlite.view.lwjgl.renderer.CharacterRenderer;
import toilari.otlite.view.lwjgl.renderer.ILWJGLRenderer;
import toilari.otlite.view.renderer.IRenderer;

/**
 * Käyttöliittymäkomponentti hahmon tietojen näyttämiseen osana käyttöliittymää.
 */
public class UICharacterEntry {
    @Getter @NonNull private final CharacterObject character;
    private final CharacterRenderer renderer;


    /**
     * Luo uuden käyttöliittymäkomponentin hahmon tietojen näyttämiseen.
     *
     * @param renderers    dao jolla voidaan hakea hahmolle piirtäjä
     * @param previewWorld maailma jossa esikatseltaav hahmo on
     * @param character    hahmo jonka tietoja tämä komponentti näyttää
     */
    public UICharacterEntry(@NonNull IGetByIDDao<ILWJGLRenderer> renderers, @NonNull World previewWorld, @NonNull CharacterObject character) {
        this.character = previewWorld.getObjectManager().spawnTemplate(character);
        this.renderer = (CharacterRenderer) renderers.getByID(this.character.getRendererID());
    }

    /**
     * Piirtää hahmon "muotokuvan" annettuihin koordinaatteihin.
     *
     * @param camera kamera jonka näkökulmasta piiretään
     * @param batch  sarjapiirtäjä jonka jonoon piirtokomennot asetetaan
     * @param x      x-koordinaatti johon piirretään
     * @param y      y-koordinaatti johon piirretään
     */
    public void drawPortrait(@NonNull LWJGLCamera camera, @NonNull SpriteBatch batch, float x, float y) {
        this.character.setPos(Math.round(x), Math.round(y));
        this.renderer.draw(camera, this.character, batch);
    }
}
