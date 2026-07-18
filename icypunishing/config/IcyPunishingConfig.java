package com.icypunishing.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class IcyPunishingConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    public static final ForgeConfigSpec.DoubleValue TWO_STAR_DROP_HEALTH;
    public static final ForgeConfigSpec.DoubleValue THREE_STAR_DROP_HEALTH;
    public static final ForgeConfigSpec.DoubleValue FOUR_STAR_DROP_HEALTH;
    public static final ForgeConfigSpec.DoubleValue FIVE_STAR_DROP_HEALTH;
    
    public static final ForgeConfigSpec.DoubleValue TWO_STAR_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue THREE_STAR_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue FOUR_STAR_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue FIVE_STAR_DROP_CHANCE;
    
    static {
        BUILDER.comment("Awareness accessory drop configuration").push("awareness_drop");
        
        TWO_STAR_DROP_HEALTH = BUILDER
                .comment("Minimum monster health for 2-star awareness drop. Default is 100.")
                .defineInRange("twoStarDropHealth", 100.0, 0.0, 10000.0);
        
        THREE_STAR_DROP_HEALTH = BUILDER
                .comment("Minimum monster health for 3-star awareness drop. Default is 500.")
                .defineInRange("threeStarDropHealth", 500.0, 0.0, 10000.0);
        
        FOUR_STAR_DROP_HEALTH = BUILDER
                .comment("Minimum monster health for 4-star awareness drop. Default is 1000.")
                .defineInRange("fourStarDropHealth", 1000.0, 0.0, 10000.0);
        
        FIVE_STAR_DROP_HEALTH = BUILDER
                .comment("Minimum monster health for 5-star awareness drop. Default is 5000.")
                .defineInRange("fiveStarDropHealth", 5000.0, 0.0, 10000.0);
        
        BUILDER.pop();
        
        BUILDER.comment("Awareness accessory drop chance configuration").push("drop_chance");
        
        TWO_STAR_DROP_CHANCE = BUILDER
                .comment("2-star awareness drop chance. Default is 0.02 (2%).")
                .defineInRange("twoStarDropChance", 0.02, 0.0, 1.0);
        
        THREE_STAR_DROP_CHANCE = BUILDER
                .comment("3-star awareness drop chance. Default is 0.005 (0.5%).")
                .defineInRange("threeStarDropChance", 0.005, 0.0, 1.0);
        
        FOUR_STAR_DROP_CHANCE = BUILDER
                .comment("4-star awareness drop chance. Default is 0.001 (0.1%).")
                .defineInRange("fourStarDropChance", 0.001, 0.0, 1.0);
        
        FIVE_STAR_DROP_CHANCE = BUILDER
                .comment("5-star awareness drop chance. Default is 0.0002 (0.02%).")
                .defineInRange("fiveStarDropChance", 0.0002, 0.0, 1.0);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
}