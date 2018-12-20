package toilari.otlite.dao.serialization;

import com.google.gson.GsonBuilder;
import lombok.val;
import org.junit.jupiter.api.Test;
import toilari.otlite.fake.FakeWorld;
import toilari.otlite.game.world.entities.characters.Attribute;
import toilari.otlite.game.world.entities.characters.CharacterObject;
import toilari.otlite.game.world.entities.characters.abilities.TargetSelectorAbility;
import toilari.otlite.game.world.entities.characters.abilities.components.PlayerTargetSelectorControllerComponent;

import static org.junit.jupiter.api.Assertions.*;

class CharacterAdapterTest {
    private static final String VALID_INPUT = "{\n" +
        "  \"rendererID\": \"player\",\n" +
        "  \"info\": {\n" +
        "    \"name\": \"Hero (you!)\"\n" +
        "  },\n" +
        "  \"attributes\": {\n" +
        "    \"moveCost\": 1,\n" +
        "    \"moveCooldown\": 0,\n" +
        "    \"attackCost\": 1,\n" +
        "    \"attackCooldown\": 0,\n" +
        "    \"baseActionPoints\": 2,\n" +
        "    \"baseHealth\": 100.0,\n" +
        "    \"healthGain\": 1.5,\n" +
        "    \"baseHealthRegen\": 5.0,\n" +
        "    \"baseHealthRegenGain\": 1.5,\n" +
        "    \"baseArmor\": 0,\n" +
        "    \"armorGain\": 0.1,\n" +
        "    \"baseEvasion\": 0.01,\n" +
        "    \"evasionGain\": 0.001125,\n" +
        "    \"baseKnockbackResistance\": 0.0,\n" +
        "    \"baseFearResistance\": 0.0,\n" +
        "    \"baseAttackDamage\": 10.0,\n" +
        "    \"baseAttackDamageGain\": 0.1875,\n" +
        "    \"baseCriticalHitChance\": 0.025,\n" +
        "    \"baseCriticalHitDamage\": 1.0\n" +
        "  },\n" +
        "  \"levels\": {\n" +
        "    \"experience\": 200,\n" +
        "    \"attributeLevels\": [\n" +
        "      2, 1, 1, 1, 1, 1, 1, 1\n" +
        "    ],\n" +
        "    \"experiencePerFloor\": 15\n" +
        "  },\n" +
        "  \"abilities\": {\n" +
        "    \"target_selector\": {\n" +
        "      \"priority\": 0,\n" +
        "      \"component\": {\n" +
        "        \"class\": \"player\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"move\": {\n" +
        "      \"priority\": 1,\n" +
        "      \"component\": {\n" +
        "        \"class\": \"player\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"attack\": {\n" +
        "      \"priority\": 2,\n" +
        "      \"name\": \"Basic\\nAttack\",\n" +
        "      \"component\": {\n" +
        "        \"visibleOnHud\": true,\n" +
        "        \"class\": \"player\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"block\": {\n" +
        "      \"priority\": 3,\n" +
        "      \"name\": \"Block/\\nHunker Down\",\n" +
        "      \"component\": {\n" +
        "        \"visibleOnHud\": true,\n" +
        "        \"class\": \"player\",\n" +
        "        \"stateOverride\": \"block\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"kick\": {\n" +
        "      \"priority\": 4,\n" +
        "      \"name\": \"Kick\",\n" +
        "      \"component\": {\n" +
        "        \"visibleOnHud\": true,\n" +
        "        \"class\": \"player\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"warcry\": {\n" +
        "      \"priority\": 5,\n" +
        "      \"name\": \"Warcry\",\n" +
        "      \"component\": {\n" +
        "        \"visibleOnHud\": true,\n" +
        "        \"class\": \"player\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"end_turn\": {\n" +
        "      \"priority\": 99,\n" +
        "      \"component\": {\n" +
        "        \"class\": \"player\"\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}";

    @Test
    void deserializationSucceedsWithValidInput() {
        val world = FakeWorld.create();

        val gson = new GsonBuilder()
            .registerTypeAdapter(CharacterObject.class, new CharacterAdapter())
            .create();

        val player = gson.fromJson(VALID_INPUT, CharacterObject.class);
        world.getObjectManager().spawn(player);

        assertEquals("player", player.getRendererID());
        assertEquals(2, player.getLevels().getAttributeLevel(Attribute.STRENGTH));
        assertEquals(200, player.getLevels().getExperience());
        assertEquals(101.5f, player.getAttributes().getMaxHealth());
        assertEquals(0.035f, player.getAttributes().getCriticalHitChance());
        assertTrue(player.getAbilities().getComponent(TargetSelectorAbility.class) instanceof PlayerTargetSelectorControllerComponent);
        assertEquals("Hero (you!)", player.getInfo().getName());
    }

    @Test
    void deserializationSucceedsEvenIfTagsAreMissing() {
        val world = FakeWorld.create();

        val gson = new GsonBuilder()
            .registerTypeAdapter(CharacterObject.class, new CharacterAdapter())
            .create();

        val player = gson.fromJson("{}", CharacterObject.class);
        world.getObjectManager().spawn(player);

        assertNull(player.getRendererID());
        assertEquals(1, player.getLevels().getAttributeLevel(Attribute.STRENGTH));
        assertEquals(0, player.getLevels().getExperience());
        assertEquals(0.0f, player.getAttributes().getMaxHealth());
        assertEquals(0.01f, player.getAttributes().getCriticalHitChance());
        assertNull(player.getAbilities().getComponent(TargetSelectorAbility.class));
        assertEquals("Unnamed", player.getInfo().getName());
    }
}
