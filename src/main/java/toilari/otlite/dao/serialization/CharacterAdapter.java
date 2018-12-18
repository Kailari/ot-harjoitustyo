package toilari.otlite.dao.serialization;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.world.entities.characters.CharacterAttributes;
import toilari.otlite.game.world.entities.characters.CharacterInfo;
import toilari.otlite.game.world.entities.characters.CharacterLevels;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.AbilityRegistry;
import toilari.otlite.game.world.entities.characters.abilities.IAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.IControllerComponent;

import java.lang.reflect.Type;
import java.util.Random;

/**
 * Gson adapteri hahmojen ja hahmojen komponenttien sarjoittamiseen.
 */
@Slf4j
public class CharacterAdapter implements JsonDeserializer<CharacterObject> {
    @Override
    public CharacterObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        val jsonObj = json.getAsJsonObject();

        val characterAttributes = deserializeOrCreateNewAttributes(context, jsonObj);
        val characterLevels = deserializeOrCreateNewLevels(context, jsonObj);
        val characterInfo = deserializeOrCreateNewInfo(context, jsonObj);

        val character = new CharacterObject(characterAttributes, characterLevels, characterInfo, new Random());

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

    private CharacterLevels deserializeOrCreateNewLevels(JsonDeserializationContext context, JsonObject jsonObj) {
        CharacterLevels levels = context.deserialize(jsonObj.getAsJsonObject("levels"), CharacterLevels.class);
        return levels == null ? new CharacterLevels() : levels;
    }

    private CharacterAttributes deserializeOrCreateNewAttributes(JsonDeserializationContext context, JsonObject jsonObj) {
        CharacterAttributes attributes = context.deserialize(jsonObj.getAsJsonObject("attributes"), CharacterAttributes.class);
        return attributes == null ? new CharacterAttributes() : attributes;
    }

    private CharacterInfo deserializeOrCreateNewInfo(JsonDeserializationContext context, JsonObject jsonObj) {
        CharacterInfo info = context.deserialize(jsonObj.getAsJsonObject("info"), CharacterInfo.class);
        return info == null ? new CharacterInfo() : info;
    }

    private void deserializeAbilities(JsonDeserializationContext context, JsonObject abilities, CharacterObject character) {
        for (val abilityKey : abilities.keySet()) {
            val abilityObj = abilities.getAsJsonObject(abilityKey);
            if (abilityObj == null || abilityKey == null) {
                LOG.warn("Error reading ability \"{}\"", abilityKey);
                continue;
            }

            val componentObj = (JsonObject) abilityObj.remove("component");
            if (componentObj == null) {
                LOG.warn("Could not find component-tag for ability \"{}\"", abilityKey);
                continue;
            }

            val componentKey = resolveComponentKey(componentObj);
            if (componentKey == null) {
                LOG.warn("Could not find valid class-tag for component on ability \"{}\"", abilityKey);
                continue;
            }

            deserializeAbility(context, character, abilityKey, abilityObj, componentObj, componentKey);
        }
    }

    private void deserializeAbility(JsonDeserializationContext context, CharacterObject character, String abilityKey, JsonObject abilityObj, JsonObject componentObj, String componentKey) {
        val abilityClass = AbilityRegistry.getAbilityClass(abilityKey);
        if (abilityClass == null) {
            LOG.warn("Unknown ability class \"{}\"", abilityKey);
            return;
        }

        val componentClass = AbilityRegistry.getComponentClass(abilityKey, componentKey);
        if (componentClass == null) {
            LOG.warn("Unknown component class \"{}\"", componentKey);
            return;
        }

        IAbility ability = context.deserialize(abilityObj, abilityClass);
        IControllerComponent component = context.deserialize(componentObj, componentClass);
        if (ability == null || component == null) {
            return;
        }

        // Types for these objects came from AbilityRegistry, which can guarantee that for given ability key, all
        // components get via getComponentClass(...) must be compatible.
        // noinspection unchecked
        character.getAbilities().addAbility(ability, component);
    }

    private String resolveComponentKey(JsonObject componentObj) {
        val componentClassPrimitive = componentObj.getAsJsonPrimitive("class");
        if (componentClassPrimitive == null) {
            return null;
        }

        return componentClassPrimitive.getAsString();
    }
}
