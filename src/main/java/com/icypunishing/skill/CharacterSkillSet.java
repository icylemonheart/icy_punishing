package com.icypunishing.skill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public interface CharacterSkillSet {
    String getModelId();

    void onOrbConsumeKey(ServerPlayer player, int orbIndex, SkillState state);

    void onUltimateKey(ServerPlayer player, SkillState state);

    void onMeleeHit(ServerPlayer player, LivingEntity target, float dealtDamage, SkillState state);

    void onPlayerTick(ServerPlayer player, SkillState state);

    /** 长按/松开左键（仅深红之渊用于逆闪切、纳刀斩与剑气爆发）。 */
    default void onAttackHold(ServerPlayer player, SkillState state, boolean pressed) {
    }

    default void onDodgeKey(ServerPlayer player, SkillState state) {
    }

    /** 释放必杀技所需的能量。 */
    default int ultimateEnergyCost() {
        return SkillState.MAX_ENERGY;
    }

    /** SSS+ 常态普攻额外伤害加成（黎明改为必杀后触发，故返回 0）。 */
    default float sssPlusNormalBonus() {
        return 0.15f;
    }

    default void onUnequip(ServerPlayer player, SkillState state) {
    }

    default float modifyNormalAttackDamage(ServerPlayer player, SkillState state, float damage) {
        return damage;
    }

    default float modifySkillDamage(ServerPlayer player, SkillState state, float damage) {
        return damage;
    }
}
