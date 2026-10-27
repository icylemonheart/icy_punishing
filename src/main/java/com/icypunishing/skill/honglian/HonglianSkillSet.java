package com.icypunishing.skill.honglian;

import com.icypunishing.event.SkillCombatHandler;
import com.icypunishing.skill.CharacterSkillSet;
import com.icypunishing.skill.SignalOrb;
import com.icypunishing.skill.SkillState;
import com.icypunishing.skill.SkillUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HonglianSkillSet implements CharacterSkillSet {

    static final float[] RED_RATIO = {2.6672f, 5.3332f, 8.00f};
    static final float[] YELLOW_RATIO = {1.50f, 3.00f, 4.50f};
    static final float[] BLUE_RATIO = {1.00f, 2.00f, 3.00f};
    static final float[] BLUE_FIRE_RATIO = {0.10f, 0.15f, 0.20f};
    static final int[] BLUE_DURATION = {80, 80, 120};

    static final int ENERGY_PER_HIT = 2;
    static final int ENERGY_PER_ORB = 8;

    static final float ULT_SLASH_RATIO = 1.40f;
    static final int ULT_SLASH_COUNT = 5;
    static final float ULT_FINAL_RATIO = 10.00f;
    static final byte[] ULT_SLASH_STYLES = {0, 3, 4, 7, 0};

    static final float BURST_HIT_RATIO = 1.35f;
    static final float SS_AIRBORNE_RATIO = 0.20f;
    static final float SSS_BURST_BONUS = 0.15f;

    // 武器技能「怒火狂刃」
    static final float RAGE_NORMAL_BONUS = 0.10f;
    static final float RAGE_STACK_BONUS = 0.05f;
    static final int RAGE_MAX_STACKS = 5;
    static final int RAGE_DURATION = 40;

    static final long DODGE_COOLDOWN = 30L;
    static final float DODGE_SPEED = 1.15f;
    static final int DODGE_IFRAMES = 12;
    static final long PERFECT_DODGE_WINDOW = 8L;
    static final int HYPER_SPACE_DURATION = 60;
    static final int PERFECT_DODGE_ORB_REWARD = 2;

    private static final String TAG_PROJECTION = "icy_punishing_projection";
    private final Map<UUID, ItemStack> stashedOffhand = new ConcurrentHashMap<>();
    private final Map<UUID, UltimateTask> ultimateTasks = new ConcurrentHashMap<>();

    @Override
    public String getModelId() {
        return "honglian";
    }

    @Override
    public void onOrbConsumeKey(ServerPlayer player, int orbIndex, SkillState state) {
        SignalOrb color = state.orbAt(orbIndex);
        if (color == null) {
            return;
        }
        int consumed = state.consumeAt(orbIndex);
        if (consumed <= 0) {
            return;
        }
        int tier = Math.min(consumed, 3);
        long gameTime = player.level().getGameTime();
        boolean tripleWindow = gameTime - state.getLastTripleGameTime() <= 80;
        if (consumed >= 3) {
            state.setLastTripleGameTime(gameTime);
            tripleWindow = true;
        }
        state.addEnergy(consumed * ENERGY_PER_ORB);

        if (color == SignalOrb.RED && tripleWindow) {
            boolean wasBurst = state.getBurstTicks() > 0;
            state.setBurstTicks(80);
            if (!wasBurst) {
                equipProjection(player);
            }
        }

        switch (color) {
            case RED -> castRed(player, state, tier);
            case YELLOW -> castYellow(player, state, tier);
            case BLUE -> castBlue(player, state, tier);
        }
        SkillUtils.sync(player);
    }

    private void applyFireIfActive(ServerPlayer player, LivingEntity target, SkillState state) {
        if (state.getFireBuffTicks() > 0) {
            SkillCombatHandler.dealBonusFire(player, target, state.getFireRatio());
        }
    }

    private void sendCastVfx(ServerPlayer player, Vec3 look, byte shape, float distance,
                             float radius, float thickness, int color, int lifeMillis) {
        Vec3 eye = player.getEyePosition();
        SkillUtils.sendVfx(player, shape, eye.x + look.x * distance, eye.y + look.y * distance,
                eye.z + look.z * distance, 0.0f, radius, thickness, color, lifeMillis);
    }

    private void castRed(ServerPlayer player, SkillState state, int tier) {
        Vec3 look = player.getLookAngle().normalize();
        player.setDeltaMovement(look.scale(0.9).add(0, 0.05, 0));
        player.hurtMarked = true;
        sendCastVfx(player, look, (byte) 4, 0.9f, 4.2f, 1.15f, 0xFF2A20, 320);
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 3.5, 0.5);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target, RED_RATIO[tier - 1]);
            applyFireIfActive(player, target, state);
            Vec3 pull = player.position().subtract(target.position()).normalize().scale(0.8).add(0, 0.25, 0);
            target.setDeltaMovement(pull);
            target.hurtMarked = true;
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    private void castYellow(ServerPlayer player, SkillState state, int tier) {
        Vec3 look = player.getLookAngle().normalize();
        player.setDeltaMovement(look.scale(0.4));
        player.hurtMarked = true;
        sendCastVfx(player, look, (byte) 1, 0.5f, 2.6f, 1.1f, 0xFFB81E, 340);
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 3.0, 0.5);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target, YELLOW_RATIO[tier - 1]);
            applyFireIfActive(player, target, state);
            Vec3 cur = target.getDeltaMovement();
            target.setDeltaMovement(cur.x, 0.6, cur.z);
            target.hurtMarked = true;
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    private void castBlue(ServerPlayer player, SkillState state, int tier) {
        sendCastVfx(player, player.getLookAngle().normalize(), (byte) 5, 0.2f, 3.2f, 1.0f, 0x2A6BFF, 480);
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 3.0, 0.5);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target, BLUE_RATIO[tier - 1]);
        }
        state.setFireBuffTicks(BLUE_DURATION[tier - 1]);
        state.setFireRatio(BLUE_FIRE_RATIO[tier - 1]);
        player.level().playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void onUltimateKey(ServerPlayer player, SkillState state) {
        if (state.getEnergy() < SkillState.MAX_ENERGY) {
            player.displayClientMessage(Component.translatable("message.icy_punishing.ult_not_ready"), true);
            return;
        }
        if (ultimateTasks.containsKey(player.getUUID())) {
            return;
        }
        state.setEnergy(0);
        UltimateTask task = new UltimateTask();
        task.nextTick = player.level().getGameTime() + 4;
        ultimateTasks.put(player.getUUID(), task);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 0.8f, 1.5f);
        SkillUtils.sync(player);
    }

    @Override
    public void onMeleeHit(ServerPlayer player, LivingEntity target, float dealtDamage, SkillState state) {
        long gameTime = player.level().getGameTime();
        state.setLastCombatGameTime(gameTime);
        int asc = SkillUtils.ascension(player);

        // 怒火狂刃：每次普攻命中叠加 1 层（上限 5），刷新 2 秒持续
        state.setRageStacks(Math.min(RAGE_MAX_STACKS, state.getRageStacks() + 1));
        state.setRageTicks(RAGE_DURATION);

        if (state.isCombatMode()) {
            state.addEnergy(ENERGY_PER_HIT);
            // 爆发（双刀术）期间普攻不回复信号球
            if (state.getBurstTicks() <= 0) {
                state.addOrb(SignalOrb.random(player.getRandom()));
            }
        }

        if (asc >= 1 && !target.onGround()) {
            SkillCombatHandler.dealBonusPhysical(player, target, SS_AIRBORNE_RATIO);
        }

        if (state.getBurstTicks() > 0) {
            float ratio = BURST_HIT_RATIO * (asc >= 2 ? 1.0f + SSS_BURST_BONUS : 1.0f);
            // 主手红莲狂士斩击
            SkillCombatHandler.dealBonusPhysical(player, target, ratio);
            // 副手投影同步斩击（双刀齐击）
            SkillCombatHandler.dealBonusPhysical(player, target, ratio);
            sendDualSlash(player);
        }

        if (state.getFireBuffTicks() > 0) {
            SkillCombatHandler.dealBonusFire(player, target, state.getFireRatio());
            target.igniteForSeconds(2);
        }

        SkillUtils.sync(player);
    }

    /** 爆发期主副手同步斩击特效：左右两道镜像斜斩。 */
    private void sendDualSlash(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        double x = eye.x + look.x * 1.6;
        double y = eye.y + look.y * 1.6;
        double z = eye.z + look.z * 1.6;
        SkillUtils.sendVfx(player, (byte) 4, x, y, z, 0.0f, 2.8f, 1.0f, 0xFF2A20, 200);
        SkillUtils.sendVfx(player, (byte) 8, x, y, z, 0.0f, 2.4f, 0.55f, 0xFF6A2A, 220);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.7f, 1.3f);
    }

    @Override
    public float modifyNormalAttackDamage(ServerPlayer player, SkillState state, float damage) {
        // 怒火狂刃：普攻额外伤害 +10%，并叠加每层 +5% 物理伤害
        return damage * (1.0f + RAGE_NORMAL_BONUS + RAGE_STACK_BONUS * state.getRageStacks());
    }

    @Override
    public float modifySkillDamage(ServerPlayer player, SkillState state, float damage) {
        // 怒火狂刃的层数同时提升技能物理伤害
        return damage * (1.0f + RAGE_STACK_BONUS * state.getRageStacks());
    }

    @Override
    public void onPlayerTick(ServerPlayer player, SkillState state) {
        boolean dirty = false;
        if (state.getBurstTicks() > 0) {
            state.setBurstTicks(state.getBurstTicks() - 1);
            if (state.getBurstTicks() == 0) {
                restoreOffhand(player);
                dirty = true;
            } else {
                // 爆发期间锁定副手为投影
                enforceProjection(player);
            }
        }
        if (state.getFireBuffTicks() > 0) {
            state.setFireBuffTicks(state.getFireBuffTicks() - 1);
            if (state.getFireBuffTicks() == 0) {
                dirty = true;
            } else if (player.level().getGameTime() % 2 == 0) {
                // 蓝球余烬：刀身红色光雾
                spawnBladeEmbers(player);
            }
        }
        // 怒火狂刃层数到期清空
        if (state.getRageTicks() > 0) {
            state.setRageTicks(state.getRageTicks() - 1);
            if (state.getRageTicks() == 0) {
                state.setRageStacks(0);
            }
        }
        if (state.getDodgeCooldownTicks() > 0) {
            state.setDodgeCooldownTicks(state.getDodgeCooldownTicks() - 1);
        }
        if (dirty) {
            SkillUtils.sync(player);
        } else if (player.level().getGameTime() % 5 == 0
                && (state.getBurstTicks() > 0 || state.getFireBuffTicks() > 0
                    || state.getRageTicks() > 0 || state.getDodgeCooldownTicks() > 0)) {
            // 每 5 tick 推送一次，保证 HUD 倒计时实时跳动
            SkillUtils.sync(player);
        }

        UltimateTask task = ultimateTasks.get(player.getUUID());
        if (task != null) {
            long now = player.level().getGameTime();
            if (now >= task.nextTick) {
                if (task.slashes < ULT_SLASH_COUNT) {
                    Vec3 eye = player.getEyePosition();
                    Vec3 look = player.getLookAngle().normalize();
                    Vec3 right = look.cross(new Vec3(0, 1, 0));
                    if (right.lengthSqr() < 1.0E-6) {
                        right = new Vec3(1, 0, 0);
                    }
                    right = right.normalize();

                    int step = task.slashes;
                    float side = (step % 2 == 0) ? -1.0f : 1.0f;
                    double distance = 0.8 + (step % 2 == 0 ? 0.0 : 0.35);
                    double lateral = side * 0.35;
                    double vertical = side * 0.25;
                    SkillUtils.sendVfx(player, (byte) 1,
                            eye.x + look.x * distance + right.x * lateral,
                            eye.y + look.y * distance + vertical,
                            eye.z + look.z * distance + right.z * lateral,
                            side * 12.0f, 4.6f, 1.5f, 0xFF1E3C, 260, ULT_SLASH_STYLES[step % ULT_SLASH_COUNT]);
                    List<LivingEntity> targets = SkillUtils.sectorTargets(player, 4.0, 0.5);
                    for (LivingEntity target : targets) {
                        SkillCombatHandler.dealBonusPhysical(player, target, ULT_SLASH_RATIO);
                    }
                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.2f);
                    player.swing(player.getUsedItemHand(), true);
                    task.slashes++;
                    task.nextTick = now + 4;
                } else {
                    Vec3 eye = player.getEyePosition();
                    Vec3 look = player.getLookAngle().normalize();
                    SkillUtils.sendVfx(player, (byte) 3, eye.x + look.x * 0.6, eye.y + look.y * 0.6,
                            eye.z + look.z * 0.6, 0.0f, 5.2f, 1.7f, 0xFF0F2A, 620);
                    SkillUtils.sendVfx(player, (byte) 6, eye.x + look.x * 1.2, eye.y + look.y * 1.2,
                            eye.z + look.z * 1.2, 0.0f, 4.0f, 0.9f, 0xFF5A2A, 620);
                    SkillUtils.sendVfx(player, (byte) 7, player.getX(), player.getY(), player.getZ(),
                            0.0f, 3.0f, 0.9f, 0xFF2A20, 520);
                    List<LivingEntity> targets = SkillUtils.sectorTargets(player, 4.5, 0.5);
                    for (LivingEntity target : targets) {
                        SkillCombatHandler.dealBonusPhysical(player, target, ULT_FINAL_RATIO);
                        Vec3 cur = target.getDeltaMovement();
                        target.setDeltaMovement(cur.x, 0.5, cur.z);
                        target.hurtMarked = true;
                    }
                    player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.6f, 1.4f);
                    ultimateTasks.remove(player.getUUID());
                }
            }
            if (!player.isAlive()) {
                ultimateTasks.remove(player.getUUID());
            }
        }
    }

    /** 蓝球余烬：沿刀身生成红色粒子光雾，直观提示火伤效果仍在生效。 */
    private void spawnBladeEmbers(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 right = look.cross(new Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0E-6) {
            right = new Vec3(1, 0, 0);
        }
        right = right.normalize();
        Vec3 base = eye.add(look.scale(0.35)).add(right.scale(-0.32)).add(0, -0.28, 0);

        DustParticleOptions ember = new DustParticleOptions(new Vector3f(1.0f, 0.28f, 0.12f), 1.35f);
        DustParticleOptions core = new DustParticleOptions(new Vector3f(1.0f, 0.72f, 0.35f), 0.8f);
        var random = player.getRandom();
        for (int i = 0; i < 4; i++) {
            double t = 0.15 + 0.9 * random.nextDouble();
            Vec3 p = base.add(look.scale(t)).add(0, t * 0.22, 0)
                    .add(right.scale((random.nextDouble() - 0.5) * 0.12))
                    .add(0, (random.nextDouble() - 0.5) * 0.12, 0);
            boolean hot = random.nextFloat() < 0.35f;
            player.serverLevel().sendParticles(hot ? core : ember, p.x, p.y, p.z, 1,
                    0.02, 0.02, 0.02, 0.0);
        }
    }

    @Override
    public void onDodgeKey(ServerPlayer player, SkillState state) {
        long now = player.level().getGameTime();
        if (now - state.getLastDodgeGameTime() < DODGE_COOLDOWN) {
            return;
        }
        state.setLastDodgeGameTime(now);
        state.setDodgeCooldownTicks((int) DODGE_COOLDOWN);
        // 开启完美闪避窗口：窗口期内受击则免疫伤害并触发超算空间
        com.icylib.dodge.DodgeSystem.activate(player, PERFECT_DODGE_WINDOW, (p, src) -> {
            p.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE,
                    HYPER_SPACE_DURATION, 0, false, false, true));
            for (int i = 0; i < PERFECT_DODGE_ORB_REWARD; i++) {
                state.addOrb(SignalOrb.random(p.getRandom()));
            }
            p.level().playSound(null, p.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                    SoundSource.PLAYERS, 1.0f, 1.8f);
            SkillUtils.sendVfx(p, (byte) 2, p.getX(), p.getY() + 1.0, p.getZ(),
                    0.0f, 3.0f, 1.2f, 0x4AD8FF, 420);
            SkillUtils.sync(p);
        });
        Vec3 look = player.getLookAngle();
        double x = look.x;
        double z = look.z;
        double len = Math.sqrt(x * x + z * z);
        if (len < 1.0E-4) {
            x = 0.0f;
            z = -1.0f;
            len = 1.0f;
        }
        double dx = x / len * DODGE_SPEED;
        double dz = z / len * DODGE_SPEED;
        player.setDeltaMovement(dx, Math.max(player.getDeltaMovement().y, 0.15), dz);
        player.hurtMarked = true;
        player.invulnerableTime = DODGE_IFRAMES;
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.8f, 1.6f);
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 1.0, player.getZ(),
                0.0f, 1.8f, 0.3f, 0xFFFFFF, 200);
    }

    @Override
    public void onUnequip(ServerPlayer player, SkillState state) {
        state.setRageStacks(0);
        state.setRageTicks(0);
        if (state.getBurstTicks() > 0) {
            state.setBurstTicks(0);
            restoreOffhand(player);
            SkillUtils.sync(player);
        } else {
            // 防御性清理：若仍有残留投影则恢复
            ItemStack off = player.getOffhandItem();
            if (isProjection(off)) {
                restoreOffhand(player);
            }
        }
    }

    private boolean isProjection(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBoolean(TAG_PROJECTION);
    }

    private ItemStack createProjection(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        if (main.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = main.copy();
        CompoundTag tag = copy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(TAG_PROJECTION, true);
        copy.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        copy.setCount(1);
        return copy;
    }

    private void equipProjection(ServerPlayer player) {
        UUID id = player.getUUID();
        ItemStack currentOff = player.getOffhandItem();
        stashedOffhand.put(id, currentOff.copy());
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, createProjection(player));
    }

    private void enforceProjection(ServerPlayer player) {
        ItemStack off = player.getOffhandItem();
        if (isProjection(off)) {
            return;
        }
        // 玩家试图更换副手：把当前副手塞回背包，重新装备投影
        if (!off.isEmpty()) {
            player.getInventory().add(off);
        }
        ItemStack proj = createProjection(player);
        if (!proj.isEmpty()) {
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, proj);
        }
    }

    private void restoreOffhand(ServerPlayer player) {
        UUID id = player.getUUID();
        ItemStack stash = stashedOffhand.remove(id);
        ItemStack current = player.getOffhandItem();
        // 清除投影
        if (isProjection(current)) {
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        } else if (!current.isEmpty()) {
            player.getInventory().add(current);
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
        // 恢复原副手
        if (stash != null && !stash.isEmpty()) {
            if (player.getOffhandItem().isEmpty()) {
                player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, stash);
            } else {
                player.getInventory().add(stash);
            }
        }
    }

    private static final class UltimateTask {
        int slashes;
        long nextTick;
    }
}
