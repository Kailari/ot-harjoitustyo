package toilari.otlite.game.world.entities.characters;

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

        private static final int[] KICK_KNOCKBACK_MIN = {-1, 0, 0, 0, 0, 0, 1, 1, 1, 2};
        private static final int[] KICK_KNOCKBACK_MAX = {-1, 1, 1, 1, 2, 2, 2, 2, 3, 4};
        private static final int[] KICK_COST = {-1, 1, 1, 2, 2, 2, 2, 2, 2, 2};
        private static final int[] KICK_COOLDOWN = {-1, 4, 4, 3, 3, 3, 3, 2, 2, 1};


        public static float getDamageModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Strength.DAMAGE_MODIFIER[level - 1];
        }

        public static float getKnockbackResistance(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Strength.KNOCKBACK_RESISTANCE[level - 1];
        }

        public static int getKickKnockbackMin(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Strength.KICK_KNOCKBACK_MIN[level - 1];
        }

        public static int getKickKnockbackMax(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Strength.KICK_KNOCKBACK_MAX[level - 1];
        }

        public static int getKickCost(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Strength.KICK_COST[level - 1];
        }

        public static int getKickCooldown(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Strength.KICK_COOLDOWN[level - 1];
        }
    }

    public static class Endurance {
        private static final float[] ARMOR_MODIFIER = {0.0f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.55f, 0.75f};
        private static final float[] HEALTH_MODIFIER = {0.0f, 0.025f, 0.05f, 0.075f, 0.01f, 0.0125f, 0.015f, 0.0175f, 0.012f, 0.013f};

        public static float getArmorModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Endurance.ARMOR_MODIFIER[level - 1];
        }

        public static float getHealthModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Endurance.HEALTH_MODIFIER[level - 1];
        }
    }

    public static class Vitality {
        private static final float[] HEALTH_MODIFIER = {0.0f, 0.05f, 0.1f, 0.15f, 0.20f, 0.25f, 0.3f, 0.35f, 0.40f, 0.50f};
        private static final float[] REGEN_MODIFIER = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1.0f};
        private static final int[] REGEN_DELAY = {5, 5, 4, 4, 4, 4, 3, 3, 3, 2};

        public static float getHealthModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Vitality.HEALTH_MODIFIER[level - 1];
        }

        public static float getHealthRegenModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Vitality.REGEN_MODIFIER[level - 1];
        }

        public static int getHealthRegenDelay(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Vitality.REGEN_DELAY[level - 1];
        }
    }

    public static class Dexterity {
        private static final int[] ACTION_POINTS = {1, 1, 2, 2, 2, 3, 3, 3, 3, 4};
        private static final int[] LEAP_RANGE = {-1, -1, -1, 2, 2, 2, 3, 3, 3, 4};
        private static final int[] LEAP_COST = {-1, -1, -1, 2, 2, 3, 3, 3, 2, 2};
        private static final int[] LEAP_COOLDOWN = {-1, -1, -1, 4, 4, 2, 3, 3, 3, 2};

        public static int getActionPoints(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Dexterity.ACTION_POINTS[level - 1];
        }

        public static int getLeapRange(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Dexterity.LEAP_RANGE[level - 1];
        }

        public static int getLeapCost(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Dexterity.LEAP_COST[level - 1];
        }

        public static int getLeapCooldown(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Dexterity.LEAP_COOLDOWN[level - 1];
        }
    }

    public static class Charisma {
        private static final float[] FEAR_RESISTANCE_MODIFIER = {0.0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f, 0.5f, 0.75f};
        private static final int[] WARCRY_RANGE = {-1, -1, -1, 2, 2, 3, 3, 3, 3, 4};
        private static final int[] WARCRY_COST = {-1, -1, -1, 2, 2, 2, 1, 1, 1, 1};
        private static final int[] WARCRY_COOLDOWN = {-1, -1, -1, 6, 5, 5, 4, 4, 4, 4};
        private static final float[] WARCRY_FEAR_CHANCE = {-1, -1, -1, 0.1f, 0.15f, 0.2f, 0.25f, 0.35f, 0.45f, 0.6f};

        public static float getFearResistanceModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Charisma.FEAR_RESISTANCE_MODIFIER[level - 1];
        }

        public static int getWarcryRange(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Charisma.WARCRY_RANGE[level - 1];
        }

        public static int getWarcryCost(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Charisma.WARCRY_COST[level - 1];
        }

        public static int getWarcryCooldown(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Charisma.WARCRY_COOLDOWN[level - 1];
        }

        public static float getWarcryFearChance(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Charisma.WARCRY_FEAR_CHANCE[level - 1];
        }
    }

    public static class Intelligence {
        private static final float[] POTION_POTENCY_MODIFIER = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1.0f};
        private static final float[] XP_BONUS = {0.0f, 0.05f, 0.075f, 0.1f, 0.1025f, 0.105f, 0.1075f, 0.11f, 0.12f, 0.2f};

        public static float getPotionPotencyModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Intelligence.POTION_POTENCY_MODIFIER[level - 1];
        }

        public static float getXpBonus(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Intelligence.XP_BONUS[level - 1];
        }
    }

    public static class Wisdom {
        private static final float[] FEAR_RESISTANCE = {0.0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f, 0.5f, 0.75f};
        private static final float[] CRITICAL_HIT_DAMAGE_MODIFIER = {0.5f, 0.55f, 0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.9f, 1.0f, 2.0f};
        private static final int[] ACTION_POINTS = {-1, -1, -1, -1, -1, -1, -1, -1, 1, 2};

        public static float getFearResistance(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Wisdom.FEAR_RESISTANCE[level - 1];
        }

        public static float getCriticalHitDamageModifier(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Wisdom.CRITICAL_HIT_DAMAGE_MODIFIER[level - 1];
        }

        public static int getActionPoints(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Wisdom.ACTION_POINTS[level - 1];
        }
    }

    public static class Luck {
        private static final float[] CRITICAL_HIT_CHANCE = {0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.075f, 0.1f, 0.125f, 0.15f, 0.3333f};
        private static final float[] EVASION = {0.05f, 0.025f, 0.05f, 0.075f, 0.1f, 0.125f, 0.15f, 0.2f, 0.3f, 0.45f};

        public static float getCriticalHitChance(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Luck.CRITICAL_HIT_CHANCE[level - 1];
        }

        public static float getEvasion(int level) {
            if (level < 1 || level > 10) {
                throw new IllegalArgumentException("Attribute level must be within bounds [1,10]");
            }

            return Luck.EVASION[level - 1];
        }
    }
}
