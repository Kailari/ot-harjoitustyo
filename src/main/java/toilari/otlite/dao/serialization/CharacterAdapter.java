package toilari.otlite.dao.serialization;

import com.google.gson.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterLevels;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Gson adapteri hahmojen ja hahmojen komponenttien sarjoittamiseen.
 */
@Slf4j
public class CharacterAdapter implements JsonDeserializer<CharacterObject> {
    private final Map<String, Entry<?, ?>> entries = new HashMap<>();

    @Override
    public CharacterObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        val jsonObj = json.getAsJsonObject();
        val abilities = jsonObj.getAsJsonObject("abilities");

        val resultPairs = new ArrayList<Pair>();

        for (val key : abilities.keySet()) {
            val abilityObj = abilities.getAsJsonObject(key);
            val abilityEntry = this.entries.get(key);

            if (abilityObj == null || abilityEntry == null) {
                LOG.warn("Unknown ability class \"{}\"", key);
                continue;
            }

            val componentObj = (JsonObject) abilityObj.remove("component");
            if (componentObj == null) {
                LOG.warn("Could not find component-tag for ability \"{}\"", key);
                continue;
            }

            val componentClassPrimitive = componentObj.getAsJsonPrimitive("class");
            if (componentClassPrimitive == null) {
                LOG.warn("Could not find class-primitive for component in \"{}\"", key);
                continue;
            }

            val componentClass = abilityEntry.getComponentClasses().get(componentClassPrimitive.getAsString());

            val ability = (IAbility<?, ?>) context.deserialize(abilityObj, abilityEntry.getAbilityClass());
            val component = (IControllerComponent<?>) context.deserialize(componentObj, componentClass);
            resultPairs.add(new Pair(ability, component));
        }

        val characterAttributes = (CharacterAttributes) context.deserialize(jsonObj.getAsJsonObject("attributes"), CharacterAttributes.class);
        val characterLevels = (CharacterLevels) context.deserialize(jsonObj.getAsJsonObject("levels"), CharacterLevels.class);

        val character = new CharacterObject(characterAttributes == null ? new CharacterAttributes() : characterAttributes, characterLevels == null ? new CharacterLevels() : characterLevels);

        val rendererIDPrimitive = jsonObj.getAsJsonPrimitive("rendererID");
        if (rendererIDPrimitive != null) {
            character.setRendererID(rendererIDPrimitive.getAsString());
        }

        for (val pair : resultPairs) {
            character.addAbility(pair.ability, pair.component);
        }

        return character;
    }

    public <A extends IAbility<A, C>, C extends IControllerComponent<A>> Entry<A, C> registerAbility(
        @NonNull String key,
        @NonNull Class<? extends A> abilityClass) {

        val entry = new Entry<A, C>(abilityClass);
        this.entries.put(key, entry);
        return entry;
    }

    public static class Entry<A extends IAbility<A, C>, C extends IControllerComponent<A>> {
        @Getter private final Class<? extends A> abilityClass;
        @Getter private final Map<String, Class<? extends C>> componentClasses = new HashMap<>();

        public Entry(@NonNull Class<? extends A> abilityClass) {
            this.abilityClass = abilityClass;
        }

        public Entry<A, C> addComponent(String key, Class<? extends C> componentClass) {
            this.componentClasses.put(key, componentClass);
            return this;
        }
    }

    private static class Pair<A extends IAbility<A, C>, C extends IControllerComponent<A>> {
        A ability;
        C component;

        public Pair(A ability, C component) {
            this.ability = ability;
            this.component = component;
        }
    }
}
