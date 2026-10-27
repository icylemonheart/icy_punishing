package com.icypunishing.skill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public interface CharacterSkillSet {
    String getModelId();

    void onOrbConsumeKey(ServerPlayer player, int orbIndex, SkillState state);

    void onUltimateKey(ServerPlayer player, SkillState state);

    void onMeleeHit(ServerPlayer player, LivingEntity target, float dealtDamage, SkillState state);

    void onPlayerTick(ServerPlayer player, SkillState state);

    default void onDodgeKey(ServerPlayer player, SkillState state) {
    }

    default void onUnequip(ServerPlayer player, SkillState state) {
    }

    /** 普通攻击（左键）伤害修正。 */
    default float modifyNormalAttackDamage(ServerPlayer player, SkillState state, float damage) {
        return damage;
    }

    /** 技能附加伤害修正。 */
    default float modifySkillDamage(ServerPlayer player, SkillState state, float damage) {
        return damage;
    }
}
