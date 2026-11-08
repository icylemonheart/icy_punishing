package com.icypunishing.skill;

import com.icypunishing.item.ObjKatanaItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 技能点伤害倍率：等级存于主手武器 NBT（skill_levels），0红 1黄 2蓝 3必杀，5-7 跃升每级 +2%。 */
public final class SkillUpgradeHelper {
    public static final int SKILL_RED = 0;
    public static final int SKILL_YELLOW = 1;
    public static final int SKILL_BLUE = 2;
    public static final int SKILL_ULT = 3;
    public static final int SKILL_PASSIVE = 4;
    public static final int SKILL_LEAP_1 = 5;
    public static final int SKILL_LEAP_2 = 6;
    public static final int SKILL_LEAP_3 = 7;

    private SkillUpgradeHelper() {
    }

    /** 指定技能倍率：1 级为 18 级的一半，线性增长。 */
    public static float skillMultiplier(ServerPlayer player, int skillIndex) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ObjKatanaItem katana) {
            return katana.skillMultiplier(stack, skillIndex);
        }
        return 1.0f;
    }

    public static float orbMultiplier(ServerPlayer player, SignalOrb orb) {
        return switch (orb) {
            case RED -> skillMultiplier(player, SKILL_RED);
            case YELLOW -> skillMultiplier(player, SKILL_YELLOW);
            case BLUE -> skillMultiplier(player, SKILL_BLUE);
            case SWORD -> 1.0f; // 剑气球无独立技能位
        };
    }

    public static float ultMultiplier(ServerPlayer player) {
        return skillMultiplier(player, SKILL_ULT);
    }

    /** 跃升 1/2/3：每级全技能 +2%（1 级为基准）。 */
    public static float leapMultiplier(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ObjKatanaItem katana) {
            int levels = Math.max(0, katana.getSkillLevel(stack, SKILL_LEAP_1) - 1)
                    + Math.max(0, katana.getSkillLevel(stack, SKILL_LEAP_2) - 1)
                    + Math.max(0, katana.getSkillLevel(stack, SKILL_LEAP_3) - 1);
            return 1.0f + 0.02f * levels;
        }
        return 1.0f;
    }
}
