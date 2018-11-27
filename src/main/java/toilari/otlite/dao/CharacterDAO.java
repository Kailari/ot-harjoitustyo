package toilari.otlite.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.dao.serialization.CharacterAdapter;
import toilari.otlite.dao.util.TextFileHelper;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AttackAbility;
import toilari.otlite.game.world.entities.characters.abilities.EndTurnAbility;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.AttackControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.EndTurnControllerComponent;
import toilari.otlite.game.world.entities.characters.abilities.components.MoveControllerComponent;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

@Slf4j
public class CharacterDAO extends AutoDiscoverFileDAO<CharacterObject> {
    private static final String[] EXTENSIONS = {"json", "char"};

    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(CharacterObject.class, constructCharacterAdapter())
        .create();

    private static CharacterAdapter constructCharacterAdapter() {
        val adapter = new CharacterAdapter();
        adapter.registerAbility("move", MoveAbility.class)
            .addComponent("player", MoveControllerComponent.Player.class)
            .addComponent("animal", MoveControllerComponent.AI.class);

        adapter.registerAbility("end_turn", EndTurnAbility.class)
            .addComponent("player", EndTurnControllerComponent.Player.class)
            .addComponent("ai", EndTurnControllerComponent.AI.class);

        adapter.registerAbility("attack", AttackAbility.class)
            .addComponent("player", AttackControllerComponent.Player.class);

        return adapter;
    }

    /**
     * Luo uuden DAO:n hahmojen lataamiseksi määritystiedostoista.
     *
     * @param root juurihakemisto josta hahmojen määrityksiä etsitään
     */
    public CharacterDAO(@NonNull String root) {
        super(root);
    }

    @NonNull
    @Override
    protected String[] getFileExtensions() {
        return CharacterDAO.EXTENSIONS;
    }

    @Override
    protected CharacterObject load(Path path) {
        try (Reader reader = TextFileHelper.getReader(path)) {
            return this.gson.fromJson(reader, CharacterObject.class);
        } catch (JsonSyntaxException e) {
            LOG.warn("Json syntax-error in file {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOG.warn("Error reading from file {}", path);
        }

        return null;
    }
}
