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

    public static final ModConfigSpec.BooleanValue ENABLE_EXP_CONTAINER_DROP;
    public static final ModConfigSpec.IntValue EXP_CONTAINER_CHANCE_DIVISOR;
    public static final ModConfigSpec.IntValue EXP_CONTAINER_MEDIUM_XP;
    public static final ModConfigSpec.IntValue EXP_CONTAINER_LARGE_XP;
    public static final ModConfigSpec.IntValue EXP_CONTAINER_XLARGE_XP;
    public static final ModConfigSpec.IntValue EXP_CONTAINER_MAX_COUNT;

    public static final ModConfigSpec.BooleanValue ENABLE_NUT_PACK_DROP;
    public static final ModConfigSpec.IntValue NUT_PACK_MEDIUM_ARMOR;
    public static final ModConfigSpec.IntValue NUT_PACK_LARGE_ARMOR;
    public static final ModConfigSpec.IntValue NUT_PACK_XLARGE_ARMOR;
    public static final ModConfigSpec.IntValue NUT_PACK_XXLARGE_ARMOR;
    public static final ModConfigSpec.DoubleValue NUT_PACK_SMALL_CHANCE;
    public static final ModConfigSpec.DoubleValue NUT_PACK_MEDIUM_CHANCE;
    public static final ModConfigSpec.DoubleValue NUT_PACK_LARGE_CHANCE;
    public static final ModConfigSpec.DoubleValue NUT_PACK_XLARGE_CHANCE;
    public static final ModConfigSpec.DoubleValue NUT_PACK_XXLARGE_CHANCE;

    public static final ModConfigSpec.BooleanValue ENABLE_SKILL_POINT_MILESTONE;
    public static final ModConfigSpec.IntValue SKILL_POINT_LEVEL_STEP;

    static {
        DISABLE_MOB_DROP = BUILDER
                .comment("是否关闭生物掉落意识饰品。默认为false。 / Whether to disable awareness accessories dropping from mobs. Default: false.")
                .define("disableMobDrop", false);

        BUILDER.comment("意识饰品掉落配置 / Awareness accessory drop settings").push("awareness_drop");

        TWO_STAR_DROP_HEALTH = BUILDER
                .comment("掉落2星意识所需的最低怪物血量。默认为100。 / Minimum mob health required to drop 2-star awareness. Default: 100.")
                .defineInRange("twoStarDropHealth", 100.0, 0.0, 2147483647.0);

        THREE_STAR_DROP_HEALTH = BUILDER
                .comment("掉落3星意识所需的最低怪物血量。默认为500。 / Minimum mob health required to drop 3-star awareness. Default: 500.")
                .defineInRange("threeStarDropHealth", 500.0, 0.0, 2147483647.0);

        FOUR_STAR_DROP_HEALTH = BUILDER
                .comment("掉落4星意识所需的最低怪物血量。默认为1000。 / Minimum mob health required to drop 4-star awareness. Default: 1000.")
                .defineInRange("fourStarDropHealth", 1000.0, 0.0, 2147483647.0);

        FIVE_STAR_DROP_HEALTH = BUILDER
                .comment("掉落5星意识所需的最低怪物血量。默认为5000。 / Minimum mob health required to drop 5-star awareness. Default: 5000.")
                .defineInRange("fiveStarDropHealth", 5000.0, 0.0, 2147483647.0);

        SIX_STAR_DROP_HEALTH = BUILDER
                .comment("掉落6星意识所需的最低怪物血量。默认为10000。 / Minimum mob health required to drop 6-star awareness. Default: 10000.")
                .defineInRange("sixStarDropHealth", 10000.0, 0.0, 2147483647.0);

        BUILDER.pop();

        BUILDER.comment("意识饰品掉落概率配置 / Awareness accessory drop chance settings").push("drop_chance");

        TWO_STAR_DROP_CHANCE = BUILDER
                .comment("2星意识掉落概率。默认为0.5（50%）。 / Drop chance for 2-star awareness. Default: 0.5 (50%).")
                .defineInRange("twoStarDropChance", 0.5, 0.0, 1.0);

        THREE_STAR_DROP_CHANCE = BUILDER
                .comment("3星意识掉落概率。默认为0.3（30%）。 / Drop chance for 3-star awareness. Default: 0.3 (30%).")
                .defineInRange("threeStarDropChance", 0.3, 0.0, 1.0);

        FOUR_STAR_DROP_CHANCE = BUILDER
                .comment("4星意识掉落概率。默认为0.2（20%）。 / Drop chance for 4-star awareness. Default: 0.2 (20%).")
                .defineInRange("fourStarDropChance", 0.2, 0.0, 1.0);

        FIVE_STAR_DROP_CHANCE = BUILDER
                .comment("5星意识掉落概率。默认为0.1（10%）。 / Drop chance for 5-star awareness. Default: 0.1 (10%).")
                .defineInRange("fiveStarDropChance", 0.1, 0.0, 1.0);

        SIX_STAR_DROP_CHANCE = BUILDER
                .comment("6星意识掉落概率。默认为0.05（5%）。 / Drop chance for 6-star awareness. Default: 0.05 (5%).")
                .defineInRange("sixStarDropChance", 0.05, 0.0, 1.0);

        BUILDER.pop();

        BUILDER.comment("经验仓掉落配置 / EXP container drop settings").push("exp_container_drop");

        ENABLE_EXP_CONTAINER_DROP = BUILDER
                .comment("是否启用击杀生物掉落经验仓。默认为true。 / Whether mobs drop EXP containers on death. Default: true.")
                .define("enableExpContainerDrop", true);

        EXP_CONTAINER_CHANCE_DIVISOR = BUILDER
                .comment("掉落概率 = 怪物掉落经验 / 该值，上限100%。默认为15（即15点经验必定掉落）。 / Drop chance = experience dropped by the mob / this value, capped at 100%. Default: 15 (i.e. 15 experience guarantees a drop).")
                .defineInRange("expContainerChanceDivisor", 15, 1, 100000);

        EXP_CONTAINER_MEDIUM_XP = BUILDER
                .comment("掉落中型经验仓所需的最低经验量。默认为10。 / Minimum experience required to drop a medium EXP container. Default: 10.")
                .defineInRange("expContainerMediumXp", 10, 0, 100000);

        EXP_CONTAINER_LARGE_XP = BUILDER
                .comment("掉落大型经验仓所需的最低经验量。默认为30。 / Minimum experience required to drop a large EXP container. Default: 30.")
                .defineInRange("expContainerLargeXp", 30, 0, 100000);

        EXP_CONTAINER_XLARGE_XP = BUILDER
                .comment("掉落特大型经验仓所需的最低经验量。默认为60。 / Minimum experience required to drop an X-Large EXP container. Default: 60.")
                .defineInRange("expContainerXLargeXp", 60, 0, 100000);

        EXP_CONTAINER_MAX_COUNT = BUILDER
                .comment("单次掉落经验仓的最大数量。默认为3。 / Maximum number of EXP containers dropped at once. Default: 3.")
                .defineInRange("expContainerMaxCount", 3, 1, 64);

        BUILDER.pop();

        BUILDER.comment("螺母包掉落配置（按怪物护甲值分档） / Nut pack drop settings (tiered by mob armor)").push("nut_pack_drop");

        ENABLE_NUT_PACK_DROP = BUILDER
                .comment("是否启用击杀生物掉落螺母包。默认为true。 / Whether mobs drop nut packs on death. Default: true.")
                .define("enableNutPackDrop", true);

        NUT_PACK_MEDIUM_ARMOR = BUILDER
                .comment("掉落中型螺母包所需的最低护甲值。默认为5。 / Minimum armor required to drop a medium nut pack. Default: 5.")
                .defineInRange("nutPackMediumArmor", 5, 1, 1000);

        NUT_PACK_LARGE_ARMOR = BUILDER
                .comment("掉落大型螺母包所需的最低护甲值。默认为10。 / Minimum armor required to drop a large nut pack. Default: 10.")
                .defineInRange("nutPackLargeArmor", 10, 1, 1000);

        NUT_PACK_XLARGE_ARMOR = BUILDER
                .comment("掉落特大型螺母包所需的最低护甲值。默认为15。 / Minimum armor required to drop an X-Large nut pack. Default: 15.")
                .defineInRange("nutPackXLargeArmor", 15, 1, 1000);

        NUT_PACK_XXLARGE_ARMOR = BUILDER
                .comment("掉落超大型螺母包所需的最低护甲值。默认为20。 / Minimum armor required to drop an XX-Large nut pack. Default: 20.")
                .defineInRange("nutPackXXLargeArmor", 20, 1, 1000);

        NUT_PACK_SMALL_CHANCE = BUILDER
                .comment("小型螺母包掉落概率。默认为0.5（50%）。 / Drop chance for a small nut pack. Default: 0.5 (50%).")
                .defineInRange("nutPackSmallChance", 0.5, 0.0, 1.0);

        NUT_PACK_MEDIUM_CHANCE = BUILDER
                .comment("中型螺母包掉落概率。默认为0.3（30%）。 / Drop chance for a medium nut pack. Default: 0.3 (30%).")
                .defineInRange("nutPackMediumChance", 0.3, 0.0, 1.0);

        NUT_PACK_LARGE_CHANCE = BUILDER
                .comment("大型螺母包掉落概率。默认为0.2（20%）。 / Drop chance for a large nut pack. Default: 0.2 (20%).")
                .defineInRange("nutPackLargeChance", 0.2, 0.0, 1.0);

        NUT_PACK_XLARGE_CHANCE = BUILDER
                .comment("特大型螺母包掉落概率。默认为0.1（10%）。 / Drop chance for an X-Large nut pack. Default: 0.1 (10%).")
                .defineInRange("nutPackXLargeChance", 0.1, 0.0, 1.0);

        NUT_PACK_XXLARGE_CHANCE = BUILDER
                .comment("超大型螺母包掉落概率。默认为0.05（5%）。 / Drop chance for an XX-Large nut pack. Default: 0.05 (5%).")
                .defineInRange("nutPackXXLargeChance", 0.05, 0.0, 1.0);

        BUILDER.pop();

        BUILDER.comment("技能点获取配置 / Skill point acquisition settings").push("skill_point");

        ENABLE_SKILL_POINT_MILESTONE = BUILDER
                .comment("是否启用经验等级里程碑发放技能点。默认为true。 / Whether to grant skill points at experience level milestones. Default: true.")
                .define("enableSkillPointMilestone", true);

        SKILL_POINT_LEVEL_STEP = BUILDER
                .comment("每提升多少级原版经验等级获得1个技能点。默认为10。 / Number of vanilla experience levels needed per skill point. Default: 10.")
                .defineInRange("skillPointLevelStep", 10, 1, 1000);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}