package com.icypunishing.event;

import com.icypunishing.config.IcyPunishingConfig;
import com.icypunishing.registry.ModItems;
import com.icypunishing.skill.SkillState;
import com.icypunishing.skill.SkillUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 经验仓、螺母包与技能点的获取途径。
 */
public class ProgressionDropHandler {

    @SubscribeEvent
    public void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!IcyPunishingConfig.ENABLE_EXP_CONTAINER_DROP.get()) {
            return;
        }
        Player killer = event.getAttackingPlayer();
        if (!(killer instanceof ServerPlayer player)) {
            return;
        }
        int xp = event.getDroppedExperience();
        if (xp <= 0) {
            return;
        }
        // 经验越多概率越高；末影龙等大量经验必掉
        double chance = Math.min(1.0, xp / (double) IcyPunishingConfig.EXP_CONTAINER_CHANCE_DIVISOR.get());
        if (player.getRandom().nextDouble() > chance) {
            return;
        }
        Item container;
        if (xp >= IcyPunishingConfig.EXP_CONTAINER_XLARGE_XP.get()) {
            container = ModItems.EXP_CONTAINER_XLARGE.get();
        } else if (xp >= IcyPunishingConfig.EXP_CONTAINER_LARGE_XP.get()) {
            container = ModItems.EXP_CONTAINER_LARGE.get();
        } else if (xp >= IcyPunishingConfig.EXP_CONTAINER_MEDIUM_XP.get()) {
            container = ModItems.EXP_CONTAINER_MEDIUM.get();
        } else {
            container = ModItems.EXP_CONTAINER_SMALL.get();
        }
        // 经验极高时额外多给几个
        int count = Math.min(IcyPunishingConfig.EXP_CONTAINER_MAX_COUNT.get(), 1 + xp / 400);
        give(player, container, count);
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!IcyPunishingConfig.ENABLE_NUT_PACK_DROP.get()) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        int armor = entity.getArmorValue();
        if (armor <= 0) {
            return;
        }
        Item pack;
        double chance;
        if (armor >= IcyPunishingConfig.NUT_PACK_XXLARGE_ARMOR.get()) {
            pack = ModItems.NUT_PACK_XXLARGE.get();
            chance = IcyPunishingConfig.NUT_PACK_XXLARGE_CHANCE.get();
        } else if (armor >= IcyPunishingConfig.NUT_PACK_XLARGE_ARMOR.get()) {
            pack = ModItems.NUT_PACK_XLARGE.get();
            chance = IcyPunishingConfig.NUT_PACK_XLARGE_CHANCE.get();
        } else if (armor >= IcyPunishingConfig.NUT_PACK_LARGE_ARMOR.get()) {
            pack = ModItems.NUT_PACK_LARGE.get();
            chance = IcyPunishingConfig.NUT_PACK_LARGE_CHANCE.get();
        } else if (armor >= IcyPunishingConfig.NUT_PACK_MEDIUM_ARMOR.get()) {
            pack = ModItems.NUT_PACK_MEDIUM.get();
            chance = IcyPunishingConfig.NUT_PACK_MEDIUM_CHANCE.get();
        } else {
            pack = ModItems.NUT_PACK_SMALL.get();
            chance = IcyPunishingConfig.NUT_PACK_SMALL_CHANCE.get();
        }
        if (player.getRandom().nextDouble() > chance) {
            return;
        }
        give(player, pack, 1);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!IcyPunishingConfig.ENABLE_SKILL_POINT_MILESTONE.get()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }
        SkillState state = SkillUtils.state(player);
        int milestone = player.experienceLevel / IcyPunishingConfig.SKILL_POINT_LEVEL_STEP.get();
        if (milestone <= state.getSkillMilestone()) {
            return;
        }
        int gained = milestone - state.getSkillMilestone();
        state.setSkillMilestone(milestone);
        give(player, ModItems.SKILL_POINT.get(), gained);
        SkillUtils.sync(player);
    }

    private static void give(ServerPlayer player, Item item, int count) {
        if (count <= 0) {
            return;
        }
        ItemStack stack = new ItemStack(item, count);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        } else {
            player.containerMenu.broadcastChanges();
        }
    }
}
