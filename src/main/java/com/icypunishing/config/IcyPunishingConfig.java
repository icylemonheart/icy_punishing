package com.icypunishing.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class IcyPunishingConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue DISABLE_MOB_DROP;

    public static final ModConfigSpec.DoubleValue TWO_STAR_DROP_HEALTH;
    public static final ModConfigSpec.DoubleValue THREE_STAR_DROP_HEALTH;
    public static final ModConfigSpec.DoubleValue FOUR_STAR_DROP_HEALTH;
    public static final ModConfigSpec.DoubleValue FIVE_STAR_DROP_HEALTH;
    public static final ModConfigSpec.DoubleValue SIX_STAR_DROP_HEALTH;

    public static final ModConfigSpec.DoubleValue TWO_STAR_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue THREE_STAR_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue FOUR_STAR_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue FIVE_STAR_DROP_CHANCE;
    public static final ModConfigSpec.DoubleValue SIX_STAR_DROP_CHANCE;

    static {
        DISABLE_MOB_DROP = BUILDER
                .comment("是否关闭生物掉落意识饰品。默认为false。")
                .define("disableMobDrop", false);

        BUILDER.comment("意识饰品掉落配置").push("awareness_drop");

        TWO_STAR_DROP_HEALTH = BUILDER
                .comment("掉落2星意识所需的最低怪物血量。默认为100。")
                .defineInRange("twoStarDropHealth", 100.0, 0.0, 2147483647.0);

        THREE_STAR_DROP_HEALTH = BUILDER
                .comment("掉落3星意识所需的最低怪物血量。默认为500。")
                .defineInRange("threeStarDropHealth", 500.0, 0.0, 2147483647.0);

        FOUR_STAR_DROP_HEALTH = BUILDER
                .comment("掉落4星意识所需的最低怪物血量。默认为1000。")
                .defineInRange("fourStarDropHealth", 1000.0, 0.0, 2147483647.0);

        FIVE_STAR_DROP_HEALTH = BUILDER
                .comment("掉落5星意识所需的最低怪物血量。默认为5000。")
                .defineInRange("fiveStarDropHealth", 5000.0, 0.0, 2147483647.0);

        SIX_STAR_DROP_HEALTH = BUILDER
                .comment("掉落6星意识所需的最低怪物血量。默认为10000。")
                .defineInRange("sixStarDropHealth", 10000.0, 0.0, 2147483647.0);

        BUILDER.pop();

        BUILDER.comment("意识饰品掉落概率配置").push("drop_chance");

        TWO_STAR_DROP_CHANCE = BUILDER
                .comment("2星意识掉落概率。默认为0.02（2%）。")
                .defineInRange("twoStarDropChance", 0.02, 0.0, 1.0);

        THREE_STAR_DROP_CHANCE = BUILDER
                .comment("3星意识掉落概率。默认为0.005（0.5%）。")
                .defineInRange("threeStarDropChance", 0.005, 0.0, 1.0);

        FOUR_STAR_DROP_CHANCE = BUILDER
                .comment("4星意识掉落概率。默认为0.001（0.1%）。")
                .defineInRange("fourStarDropChance", 0.001, 0.0, 1.0);

        FIVE_STAR_DROP_CHANCE = BUILDER
                .comment("5星意识掉落概率。默认为0.0002（0.02%）。")
                .defineInRange("fiveStarDropChance", 0.0002, 0.0, 1.0);

        SIX_STAR_DROP_CHANCE = BUILDER
                .comment("6星意识掉落概率。默认为0.0001（0.01%）。")
                .defineInRange("sixStarDropChance", 0.0001, 0.0, 1.0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}