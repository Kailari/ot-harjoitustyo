package toilari.otlite.dao.serialization;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.characters.CharacterAbilities;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterLevels;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Gson adapteri hahmojen ja hahmojen komponenttien sarjoittamiseen.
 */
@Slf4j
public class CharacterAdapter implements JsonDeserializer<CharacterObject> {
    @Override
    public CharacterObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        val jsonObj = json.getAsJsonObject();

        val characterAttributes = getAttributes(context, jsonObj);
        val characterLevels = getLevels(context, jsonObj);
        val character = new CharacterObject(characterAttributes, characterLevels);

        val abilities = jsonObj.getAsJsonObject("abilities");
        if (abilities != null) {
            deserializeAbilities(context, abilities, character);
        }

        val rendererIDPrimitive = jsonObj.getAsJsonPrimitive("rendererID");
        if (rendererIDPrimitive != null) {
            character.setRendererID(rendererIDPrimitive.getAsString());
        }

        return character;
    }

    private CharacterLevels getLevels(JsonDeserializationContext context, JsonObject jsonObj) {
        CharacterLevels levels = context.deserialize(jsonObj.getAsJsonObject("levels"), CharacterLevels.class);
        return levels == null ? new CharacterLevels() : levels;
    }

    private CharacterAttributes getAttributes(JsonDeserializationContext context, JsonObject jsonObj) {
        CharacterAttributes attributes = context.deserialize(jsonObj.getAsJsonObject("attributes"), CharacterAttributes.class);
        return attributes == null ? new CharacterAttributes() : attributes;
    }

    private void deserializeAbilities(JsonDeserializationContext context, JsonObject abilities, CharacterObject character) {
        val resultPairs = new ArrayList<Pair>();
        for (val key : abilities.keySet()) {
            val abilityObj = abilities.getAsJsonObject(key);
            val abilityEntry = CharacterAbilities.getAbilityEntries().get(key);

            if (abilityObj == null || abilityEntry == null) {
                LOG.warn("Unknown ability class \"{}\"", key);
                continue;
            }

            IControllerComponent component = deserializeComponent(key, abilityObj, abilityEntry, context);
            if (component == null) {
                continue;
            }

            val ability = (IAbility<?, ?>) context.deserialize(abilityObj, abilityEntry.getAbilityClass());
            resultPairs.add(new Pair(ability, component));
        }

        for (val pair : resultPairs) {
            character.addAbility(pair.ability, pair.component);
        }
    }

    private IControllerComponent deserializeComponent(String key, JsonObject abilityObj, AbilityComponentEntry<?, ?> abilityEntry, JsonDeserializationContext context) {
        val componentObj = (JsonObject) abilityObj.remove("component");
        if (componentObj == null) {
            LOG.warn("Could not find component-tag for ability \"{}\"", key);
            return null;
        }

        val componentClassPrimitive = componentObj.getAsJsonPrimitive("class");
        if (componentClassPrimitive == null) {
            LOG.warn("Could not find class-primitive for component in \"{}\"", key);
            return null;
        }

        val componentClass = abilityEntry.getComponentClasses().get(componentClassPrimitive.getAsString());
        return context.deserialize(componentObj, componentClass);
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
