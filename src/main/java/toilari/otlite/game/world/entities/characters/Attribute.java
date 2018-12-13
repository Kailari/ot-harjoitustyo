package toilari.otlite.game.world.entities.characters;

import lombok.NonNull;
import lombok.val;

public enum Attribute {
    STRENGTH,
    ENDURANCE,
    VITALITY,
    DEXTERITY,
    CHARISMA,
    INTELLIGENCE,
    WISDOM,
    LUCK,
    MAX;

    public static class Strength {
        private static final float[] DAMAGE_MODIFIER = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1.0f};
        private static final float[] KNOCKBACK_RESISTANCE = {0.0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f, 0.55f, 0.75f};

        private static final int[] KICK_KNOCKBACK_MIN = {-1, 1, 1, 1, 1, 1, 2, 2, 2, 3};
        private static final int[] KICK_KNOCKBACK_MAX = {-1, 1, 1, 1, 2, 2, 2, 2, 3, 4};
        private static final int[] KICK_COST = {-1, 2, 2, 2, 2, 2, 2, 2, 2, 1};
        private static final int[] KICK_COOLDOWN = {-1, 6, 6, 5, 5, 4, 4, 3, 3, 2};


        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getDamageModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(STRENGTH);
            validateLevel(level);

            return Strength.DAMAGE_MODIFIER[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getKnockbackResistance(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(STRENGTH);
            validateLevel(level);

            return Strength.KNOCKBACK_RESISTANCE[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getKickKnockbackMin(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(STRENGTH);
            validateLevel(level);

            return Strength.KICK_KNOCKBACK_MIN[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getKickKnockbackMax(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(STRENGTH);
            validateLevel(level);

            return Strength.KICK_KNOCKBACK_MAX[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getKickCost(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(STRENGTH);
            validateLevel(level);

            return Strength.KICK_COST[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getKickCooldown(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(STRENGTH);
            validateLevel(level);

            return Strength.KICK_COOLDOWN[level - 1];
        }
    }

    public static class Endurance {
        private static final float[] ARMOR_MODIFIER = {0.0f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.55f, 0.75f};
        private static final float[] HEALTH_MODIFIER = {0.0f, 0.025f, 0.05f, 0.075f, 0.01f, 0.0125f, 0.015f, 0.0175f, 0.012f, 0.013f};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getArmorModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(ENDURANCE);
            validateLevel(level);

            return Endurance.ARMOR_MODIFIER[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getHealthModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(ENDURANCE);
            validateLevel(level);

            return Endurance.HEALTH_MODIFIER[level - 1];
        }
    }

    public static class Vitality {
        private static final float[] HEALTH_MODIFIER = {0.0f, 0.05f, 0.1f, 0.15f, 0.20f, 0.25f, 0.3f, 0.35f, 0.40f, 0.50f};
        private static final float[] REGEN_MODIFIER = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1.0f};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getHealthModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(VITALITY);
            validateLevel(level);

            return Vitality.HEALTH_MODIFIER[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getHealthRegenModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(VITALITY);
            validateLevel(level);

            return Vitality.REGEN_MODIFIER[level - 1];
        }
    }

    public static class Dexterity {
        private static final int[] ACTION_POINTS = {0, 0, 1, 1, 2, 2, 2, 3, 3, 4};
        private static final int[] LEAP_RANGE = {-1, -1, -1, 2, 2, 2, 3, 3, 3, 4};
        private static final int[] LEAP_COST = {-1, -1, -1, 2, 2, 3, 3, 3, 2, 2};
        private static final int[] LEAP_COOLDOWN = {-1, -1, -1, 4, 4, 2, 3, 3, 3, 2};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getActionPoints(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(DEXTERITY);
            validateLevel(level);

            return Dexterity.ACTION_POINTS[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getLeapRange(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(DEXTERITY);
            validateLevel(level);

            return Dexterity.LEAP_RANGE[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getLeapCost(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(DEXTERITY);
            validateLevel(level);

            return Dexterity.LEAP_COST[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getLeapCooldown(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(DEXTERITY);
            validateLevel(level);

            return Dexterity.LEAP_COOLDOWN[level - 1];
        }
    }

    public static class Charisma {
        private static final float[] FEAR_RESISTANCE_MODIFIER = {0.0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f, 0.5f, 0.75f};
        private static final int[] WARCRY_RANGE = {-1, -1, -1, 2, 2, 3, 3, 3, 3, 4};
        private static final int[] WARCRY_COST = {-1, -1, -1, 2, 2, 2, 1, 1, 1, 1};
        private static final int[] WARCRY_COOLDOWN = {-1, -1, -1, 6, 5, 5, 4, 4, 4, 4};
        private static final float[] WARCRY_FEAR_CHANCE = {-1, -1, -1, 0.1f, 0.25f, 0.35f, 0.45f, 0.50f, 0.65f, 0.9f};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getFearResistanceModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(CHARISMA);
            validateLevel(level);

            return Charisma.FEAR_RESISTANCE_MODIFIER[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getWarcryRange(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(CHARISMA);
            validateLevel(level);

            return Charisma.WARCRY_RANGE[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getWarcryCost(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(CHARISMA);
            validateLevel(level);

            return Charisma.WARCRY_COST[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static int getWarcryCooldown(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(CHARISMA);
            validateLevel(level);

            return Charisma.WARCRY_COOLDOWN[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getWarcryFearChance(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(CHARISMA);
            validateLevel(level);

            return Charisma.WARCRY_FEAR_CHANCE[level - 1];
        }
    }

    public static class Intelligence {
        private static final float[] POTION_POTENCY_MODIFIER = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1.0f};
        private static final float[] XP_BONUS = {0.0f, 0.05f, 0.075f, 0.1f, 0.1025f, 0.105f, 0.1075f, 0.11f, 0.12f, 0.2f};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getPotionPotencyModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(INTELLIGENCE);
            validateLevel(level);

            return Intelligence.POTION_POTENCY_MODIFIER[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getXpBonus(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(INTELLIGENCE);
            validateLevel(level);

            return Intelligence.XP_BONUS[level - 1];
        }
    }

    public static class Wisdom {
        private static final float[] FEAR_RESISTANCE = {0.0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f, 0.5f, 0.75f};
        private static final float[] CRITICAL_HIT_DAMAGE_MODIFIER = {0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f, 0.5f, 0.7f, 0.9f, 1.5f};
        private static final int[] ACTION_POINTS = {-1, -1, -1, -1, -1, -1, -1, -1, 1, 2};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getFearResistance(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(WISDOM);
            validateLevel(level);

            return Wisdom.FEAR_RESISTANCE[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getCriticalHitDamageModifier(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(WISDOM);
            validateLevel(level);

            return Wisdom.CRITICAL_HIT_DAMAGE_MODIFIER[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        static int getActionPoints(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(WISDOM);
            validateLevel(level);

            return Wisdom.ACTION_POINTS[level - 1];
        }
    }

    public static class Luck {
        private static final float[] CRITICAL_HIT_CHANCE = {0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.075f, 0.1f, 0.125f, 0.15f, 0.3333f};
        private static final float[] EVASION = {0.0f, 0.01f, 0.02f, 0.03f, 0.045f, 0.07f, 0.09f, 0.12f, 0.15f, 0.20f};

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getCriticalHitChance(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(LUCK);
            validateLevel(level);

            return Luck.CRITICAL_HIT_CHANCE[level - 1];
        }

        /**
         * Hakee attribuutin tasoa vastaavan bonuksen arvon.
         *
         * @param levels attribuuttien tasot
         * @return tasoa vastaava bonus
         */
        public static float getEvasion(@NonNull CharacterLevels levels) {
            val level = levels.getAttributeLevel(LUCK);
            validateLevel(level);

            return Luck.EVASION[level - 1];
        }
    }

    private static void validateLevel(int level) {
        if (level < 1 || level > 10) {
            throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
        }
    }
}
