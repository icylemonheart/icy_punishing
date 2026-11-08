package com.icypunishing.skill.hongying;

import com.icypunishing.event.SkillCombatHandler;
import com.icypunishing.skill.CharacterSkillSet;
import com.icypunishing.skill.SignalOrb;
import com.icypunishing.skill.SkillState;
import com.icypunishing.skill.SkillUpgradeHelper;
import com.icypunishing.skill.SkillUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 红樱（露西亚·深红之渊）技能组，物理属性机体。 */
public class HongyingSkillSet implements CharacterSkillSet {

    // 赤渊·烨华（红球）
    static final float[] RED_RATIO = {0.25f, 0.50f, 0.75f};
    static final int RED_HITS = 3;
    static final float RED_TRIPLE_EXTRA = 0.75f;

    // 赤渊·惶乱（黄球）
    static final float[] YELLOW_RATIO = {2.50f, 5.00f, 7.50f};

    // 赤渊·碎光散（蓝球）
    static final float[] BLUE_SPEED = {0.20f, 0.30f, 0.50f};
    static final int BLUE_DURATION = 100;
    static final int BLUE_DURATION_UPGRADED = 200;
    static final float[] ENHANCE_EXTRA = {0.04f, 0.08f, 0.12f};
    static final float[] ENHANCE_SWORD_EXTRA = {0.08f, 0.16f, 0.24f};

    // 剑气球
    static final float SWORD_ORB_RATIO = 3.40f;

    static final int ENERGY_PER_ORB = 8;
    static final int ENERGY_PER_HIT = 2;

    // 必杀·渊裂之术（太刀连续斩）
    static final int ULT_COST = 85;
    static final float ULT_RATIO = 26.00f;
    /** 时间轴与节拍统一由 UltTimeline 提供；锁定覆盖整段演出。 */
    static final int ULT_LOCK_TICKS = UltTimeline.END_TICK + 4;
    static final float ULT_SLASH_RATIO = 1.60f;
    static final float ULT_FINISH_RATIO = 10.00f;
    static final int COLOR_ULT_TRAIL = 0xFFFFFF;

    // 核心被动·赤渊-刀光意
    static final int AURA_DURATION = 100;
    static final int AURA_DURATION_UPGRADED = 200;
    static final int BLUE_TRIPLE_WINDOW = 160;
    static final int MAX_SWORD_STACKS = 2;

    // 升阶
    static final float CRIMSON_BONUS = 0.10f;
    static final int CRIMSON_DURATION = 80;
    static final int CRIMSON_DURATION_UPGRADED = 160;
    static final float SSS_EXTRA_BONUS = 1.05f;
    static final float SSS_PLUS_BLUE_BONUS = 0.20f;
    static final float SSS_PLUS_BLUE_BONUS_UPGRADED = 0.30f;

    // 武器技能·落樱散华
    static final int ORB_BUFF_MAX = 10;
    static final float ORB_BUFF_BONUS = 0.02f;
    static final int ORB_BUFF_DURATION = 100;
    static final int AURA_ORB_COOLDOWN = 200;

    // 跃升（三个节点各 18 级，总等级 1-54）
    static final int LEAP_RUSH = 1;
    static final int LEAP_BLUE_DURATION = 9;
    static final int LEAP_RUSH_UPGRADE = 18;
    static final int LEAP_NAITO = 19;
    static final int LEAP_AURA_ENERGY = 27;
    static final int LEAP_NAITO_UPGRADE = 36;
    static final int LEAP_ULT_SWORD = 37;
    static final int LEAP_ULT_SWORD_UPGRADE = 45;
    static final int LEAP_BACKLINE_UPGRADE = 54;
    static final float RUSH_RATIO = 3.00f;
    static final float NAITO_RATIO = 6.00f;
    static final float ULT_SWORD_RATIO = 3.50f;
    static final float ULT_SWORD_EXTRA = 0.50f;
    static final float ULT_SWORD_EXTRA_UPGRADED = 1.00f;
    static final int LEAP_AURA_ENERGY_VALUE = 15;
    static final int LEAP_DEPLOY_ENERGY = 35;

    static final int HOLD_MIN_TICKS = 10;
    static final int HOLD_NAITO_WINDOW = 12;
    static final int ULT_SWORD_WINDOW = 200;
    static final long DODGE_COOLDOWN = 10L;
    static final int PERFECT_DODGE_WINDOW = 10;
    static final int PERFECT_DODGE_ORB_REWARD = 2;
    static final float DODGE_SPEED = 1.15f;
    static final int DODGE_IFRAMES = 12;

    // 红球：Z 字向前闪身（三段斜向突进，共约 3 个身位）
    static final int[] RED_DASH_TICKS = {0, 3, 6};
    static final double RED_DASH_SPEED = 0.55;
    static final double RED_DASH_SIDE = 0.45;
    static final int RED_SUPER_ARMOR_TICKS = 14;
    static final int SWORD_SUPER_ARMOR_TICKS = 14;

    // 黄球：向前突击穿身 + 沿途水平环形斩（四段，共约 2 个身位）
    static final int[] YELLOW_SLASH_TICKS = {0, 2, 4, 6};
    static final int YELLOW_SLASH_SEGMENTS = 4;
    static final double YELLOW_RUSH_SPEED = 0.30;

    static final int COLOR_CRIMSON = 0xD22A3C;
    static final int COLOR_SWORD = 0xFF6A80;
    static final int COLOR_ENHANCE = 0x4FA8FF;
    static final int COLOR_ULT = 0xFFE1E6;
    static final int COLOR_GRID = 0x66F0FF;

    private static final ResourceLocation ENHANCE_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "hongying_enhance_speed");

    private static final Map<UUID, Long> HOLD_START = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> NAITO_READY = new ConcurrentHashMap<>();
    private static final java.util.Set<UUID> HOLD_FIRED = ConcurrentHashMap.newKeySet();

    @Override
    public String getModelId() {
        return "hongying";
    }

    @Override
    public int ultimateEnergyCost() {
        return ULT_COST;
    }

    @Override
    public void onOrbConsumeKey(ServerPlayer player, int orbIndex, SkillState state) {
        SignalOrb color = state.orbAt(orbIndex);
        if (color == null) {
            return;
        }
        // 剑气球没有二消/三消，一次只消耗 1 个
        int consumed = color == SignalOrb.SWORD ? state.consumeOne(orbIndex) : state.consumeAt(orbIndex);
        if (consumed <= 0) {
            return;
        }
        int tier = Math.min(consumed, 3);
        state.addEnergy(consumed * ENERGY_PER_ORB);

        // 武器技能「落樱散华」：每消除 1 球 +2% 物理伤害，5 秒，最多 10 层
        state.setOrbBuffStacks(state.getOrbBuffStacks() + consumed);
        state.setOrbBuffTicks(ORB_BUFF_DURATION);

        // 核心被动：三消蓝球后，下一次任意三消进入剑气状态
        boolean enterAura = tier >= 3 && state.getBlueTripleTicks() > 0;
        if (color == SignalOrb.BLUE && tier >= 3) {
            state.setBlueTripleTicks(BLUE_TRIPLE_WINDOW);
        }
        if (enterAura) {
            enterSwordAura(player, state);
        }

        switch (color) {
            case RED -> castRed(player, state, tier);
            case YELLOW -> castYellow(player, state, tier);
            case BLUE -> castBlue(player, state, tier);
            case SWORD -> castSwordOrb(player, state);
        }
        SkillUtils.sync(player);
    }

    @Override
    public void onDodgeKey(ServerPlayer player, SkillState state) {
        long now = player.level().getGameTime();
        if (now - state.getLastDodgeGameTime() < DODGE_COOLDOWN) {
            return;
        }
        state.setLastDodgeGameTime(now);
        state.setDodgeCooldownTicks((int) DODGE_COOLDOWN);
        boolean aura = state.getSwordAuraTicks() > 0;
        com.icylib.dodge.DodgeSystem.activate(player, PERFECT_DODGE_WINDOW, (p, src) -> {
            SkillState st = SkillUtils.state(p);
            for (int i = 0; i < PERFECT_DODGE_ORB_REWARD; i++) {
                st.addOrb(aura ? SignalOrb.SWORD : SignalOrb.random(p.getRandom()));
            }
            p.level().playSound(null, p.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                    SoundSource.PLAYERS, 1.0f, 1.8f);
            SkillUtils.sendVfx(p, (byte) 2, p.getX(), p.getY() + 1.0, p.getZ(),
                    0.0f, 3.2f, 1.2f, aura ? COLOR_SWORD : COLOR_CRIMSON, 420);
            SkillUtils.sync(p);
        });
        Vec3 look = player.getLookAngle();
        double x = look.x, z = look.z;
        double len = Math.sqrt(x * x + z * z);
        if (len < 1.0E-4) {
            x = 0.0;
            z = -1.0;
            len = 1.0;
        }
        player.setDeltaMovement(x / len * DODGE_SPEED, Math.max(player.getDeltaMovement().y, 0.15),
                z / len * DODGE_SPEED);
        player.hurtMarked = true;
        player.invulnerableTime = DODGE_IFRAMES;
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.6f, 1.6f);
        SkillUtils.sync(player);
    }

    @Override
    public void onUltimateKey(ServerPlayer player, SkillState state) {
        if (state.getEnergy() < ULT_COST) {
            player.displayClientMessage(Component.translatable("message.icy_punishing.ult_not_ready"), true);
            return;
        }
        state.setEnergy(state.getEnergy() - ULT_COST);
        int leap = SkillUtils.leap(player);
        // 跃升节点三：剑气状态下点击必杀后可长按普攻释放剑气爆发
        if (state.getSwordAuraTicks() > 0 && leap >= LEAP_ULT_SWORD) {
            state.setUltSwordTicks(ULT_SWORD_WINDOW);
        }

        double fx = player.getX(), fy = player.getY(), fz = player.getZ();
        float yaw = player.getYRot();
        Vec3 flat = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0, Math.cos(Math.toRadians(yaw))).normalize();
        Vec3 right = new Vec3(-flat.z, 0.0, flat.x);

        // 锁定输入：整段演出期间禁止移动/跳跃/左键，并通知客户端驱动本体模型动画
        state.startUltLock(ULT_LOCK_TICKS, fx, fy, fz);
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtMarked = true;
        SkillUtils.sendUltStart(player, ULT_LOCK_TICKS);

        // 起势：地面青色网格 + 脚下白环
        SkillUtils.sendVfx(player, (byte) 12, fx, fy + 0.02, fz, 0.0f, 6.5f, 0.06f, COLOR_GRID, 1200);
        SkillUtils.sendVfx(player, (byte) 2, fx, fy + 0.05, fz, 0.0f, 2.6f, 0.12f, COLOR_ULT, 500);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 1.0f, 1.2f);

        // 十次斩击：上一道刀光转红，再生成新的白色刀光
        for (int i = 0; i < UltTimeline.SLASH_TICKS.length; i++) {
            final int index = i;
            SkillUtils.schedule(player, UltTimeline.SLASH_TICKS[i], p -> {
                SkillUtils.sendUltTrail(p, com.icypunishing.network.ClientboundUltTrailPayload.ACTION_RECOLOR);
                fireUltSlash(p, index, flat, right);
            });
        }

        // 蓄力停顿：残留刀光全部转红，能量向刀身收束
        SkillUtils.schedule(player, UltTimeline.CHARGE_TICK, p -> {
            SkillUtils.sendUltTrail(p, com.icypunishing.network.ClientboundUltTrailPayload.ACTION_RECOLOR);
            Vec3 eye = p.getEyePosition();
            SkillUtils.sendVfx(p, (byte) 6, eye.x, eye.y, eye.z, yaw, p.getXRot(), 3.2f, 0.06f, COLOR_ULT, 620, (byte) 0);
            SkillUtils.sendVfx(p, (byte) 5, eye.x, eye.y, eye.z, yaw, p.getXRot(), 2.4f, 0.10f, COLOR_CRIMSON, 500, (byte) 0);
        });

        // 终结斩：清除全部残留刀光，全屏刀光爆发
        SkillUtils.schedule(player, UltTimeline.FINISH_TICK, p -> {
            SkillUtils.sendUltTrail(p, com.icypunishing.network.ClientboundUltTrailPayload.ACTION_CLEAR);
            Vec3 eye = p.getEyePosition();
            Vec3 dir = p.getLookAngle().normalize();
            Vec3 c = eye.add(dir.scale(3.0));
            float pyaw = p.getYRot();
            SkillUtils.sendVfx(p, (byte) 8, eye.x, eye.y - 0.6, eye.z, 0.0f, 11.0f, 0.18f, COLOR_ULT, 700);
            SkillUtils.sendVfx(p, (byte) 13, c.x, c.y, c.z, pyaw, p.getXRot(), 8.0f, 0.10f, COLOR_CRIMSON, 900, (byte) 0);
            SkillUtils.sendVfx(p, (byte) 10, c.x, c.y, c.z, pyaw, p.getXRot(), 9.0f, 0.12f, COLOR_ULT, 600, (byte) 12);
            SkillUtils.sendVfx(p, (byte) 20, c.x, c.y, c.z, pyaw, 0.0f, 6.5f, 0.90f, COLOR_ULT, 900, (byte) 3);
            for (LivingEntity target : SkillUtils.sectorTargets(p, 12.0, -0.2)) {
                SkillCombatHandler.dealBonusPhysical(p, target,
                        ULT_FINISH_RATIO * SkillUpgradeHelper.ultMultiplier(p));
                Vec3 push = target.position().subtract(p.position()).normalize().scale(0.8);
                target.setDeltaMovement(push.x, 0.55, push.z);
                target.hurtMarked = true;
            }
            p.level().playSound(null, p.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL,
                    SoundSource.PLAYERS, 0.8f, 1.4f);
        });

        SkillUtils.schedule(player, UltTimeline.END_TICK, p -> SkillUtils.state(p).clearUltLock());
        SkillUtils.sync(player);
    }

    /** 单次斩击：楔形扫掠判定 + 正前方带偏移的白色刀光，几何与渲染层一致。 */
    private void fireUltSlash(ServerPlayer player, int index, Vec3 flat, Vec3 right) {
        Vec3 eye = player.getEyePosition();
        float yaw = player.getYRot();
        int kind = UltTimeline.slashKind(index);
        // 挥砍中心位置阶梯 + 左右交替，最终叠成刀光网
        double lateral = kind == 2
                ? -0.95
                : ((index % 2 == 0) ? 1.0 : -1.0) * (0.16 + (index % 4) * 0.12);
        double height = (index % 3) * 0.26 - 0.42;
        double forward = 0.35 + (index % 5) * 0.10;
        Vec3 origin = eye.add(0.0, -0.45 + height, 0.0).add(flat.scale(forward)).add(right.scale(lateral));
        float radius = 3.2f + (index % 3) * 0.25f;
        SkillUtils.sendVfx(player, (byte) 20, origin.x, origin.y, origin.z,
                yaw, 0.0f, radius, 0.30f, COLOR_ULT_TRAIL, 6000, (byte) kind);
        // 斜面切片判定：贴着刀光走向，越远越薄
        for (LivingEntity target : bladeSlabTargets(player, origin, yaw, kind, radius + 2.5)) {
            SkillCombatHandler.dealBonusPhysical(player, target,
                    ULT_SLASH_RATIO * SkillUpgradeHelper.ultMultiplier(player));
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.9f, 0.9f + index * 0.04f);
    }

    /** 楔形命中判定：目标投影到刀光平面，检查「扫掠角度 × 厚度 × 半径」。 */
    private static List<LivingEntity> bladeSlabTargets(ServerPlayer player, Vec3 origin, float yaw,
                                                       int kind, double reach) {
        double angle = Math.toRadians(UltTimeline.SWEEP_ANGLE[Math.floorMod(kind, UltTimeline.SWEEP_ANGLE.length)]);
        Vec3 fwd = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0, Math.cos(Math.toRadians(yaw))).normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = fwd.cross(up).normalize();
        up = right.cross(fwd).normalize();
        Vec3 u = right.scale(Math.cos(angle)).add(up.scale(Math.sin(angle))).normalize();
        Vec3 w = right.scale(-Math.sin(angle)).add(up.scale(Math.cos(angle))).normalize();
        Vec3 v = fwd.scale(Math.cos(UltTimeline.BEND)).add(w.scale(Math.sin(UltTimeline.BEND))).normalize();
        // 弧带两端各留 10° 余量，避免擦边漏判
        double half = Math.toRadians(UltTimeline.HALF_ANGLE + 10.0);
        // 刀刃厚度：刀光本身是面，判定给它 1.15 格容错，命中手感更接近"砍到"
        double slab = 1.15;

        AABB box = new AABB(origin, origin).inflate(reach + 2.0);
        List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (entity == player || !entity.isAlive()) {
                continue;
            }
            Vec3 rel = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(origin);
            double plane = rel.dot(w);
            if (Math.abs(plane) > slab) {
                continue;
            }
            Vec3 proj = rel.subtract(w.scale(plane));
            double along = proj.dot(v);
            double side = proj.dot(u);
            if (Math.sqrt(along * along + side * side) > reach) {
                continue;
            }
            double ang = Math.atan2(side, along);
            if (ang < -half || ang > half) {
                continue;
            }
            result.add(entity);
        }
        return result;
    }

    @Override
    public void onMeleeHit(ServerPlayer player, LivingEntity target, float dealtDamage, SkillState state) {
        long gameTime = player.level().getGameTime();
        state.setLastCombatGameTime(gameTime);
        if (state.isCombatMode()) {
            state.addEnergy(ENERGY_PER_HIT);
            // 剑气状态下，普攻获得的信号球也转化为剑气球
            state.addOrb(state.getSwordAuraTicks() > 0
                    ? SignalOrb.SWORD
                    : SignalOrb.random(player.getRandom()));
        }
        if (state.getEnhanceTicks() > 0) {
            // 强化状态：攻击时额外附加 2 次物理伤害
            int tier = Math.max(1, Math.min(3, state.getEnhanceTier()));
            for (int i = 0; i < 2; i++) {
                SkillCombatHandler.dealBonusPhysical(player, target, ENHANCE_EXTRA[tier - 1]);
            }
        }
        SkillUtils.sync(player);
    }

    @Override
    public void onAttackHold(ServerPlayer player, SkillState state, boolean pressed) {
        UUID id = player.getUUID();
        long now = player.level().getGameTime();
        if (pressed) {
            HOLD_START.put(id, now);
            HOLD_FIRED.remove(id);
            NAITO_READY.remove(id);
            return;
        }
        HOLD_START.remove(id);
        boolean fired = HOLD_FIRED.remove(id);
        Long ready = NAITO_READY.remove(id);
        // 剑气状态下长按已打出逆闪切后，松手接纳刀斩
        if (fired && ready != null && now <= ready
                && state.getSwordAuraTicks() > 0 && state.getSwordStacks() > 0
                && SkillUtils.leap(player) >= LEAP_NAITO) {
            castNaito(player, state, SkillUtils.leap(player));
            SkillUtils.sync(player);
        }
    }

    /** 长按达到阈值时在服务端结算逆闪切/剑气爆发。 */
    private boolean tickHoldActions(ServerPlayer player, SkillState state, int leap) {
        UUID id = player.getUUID();
        Long start = HOLD_START.get(id);
        if (start == null || HOLD_FIRED.contains(id)) {
            return false;
        }
        long now = player.level().getGameTime();
        if (now - start < HOLD_MIN_TICKS) {
            return false;
        }
        HOLD_FIRED.add(id);

        // 剑气爆发优先：剑气状态 + 必杀窗口 + 心剑满层
        if (state.getUltSwordTicks() > 0 && state.getSwordAuraTicks() > 0
                && state.getSwordStacks() >= MAX_SWORD_STACKS && leap >= LEAP_ULT_SWORD) {
            castUltSwordBurst(player, state, leap);
            return true;
        }
        if (leap >= LEAP_RUSH && state.getSwordStacks() > 0) {
            boolean aura = state.getSwordAuraTicks() > 0;
            castRushSlash(player, state, leap);
            if (aura && leap >= LEAP_NAITO && state.getSwordStacks() > 0) {
                NAITO_READY.put(id, now + HOLD_NAITO_WINDOW);
            } else if (!aura) {
                // 非剑气状态：随机获得 1 个信号球并重新排列
                state.addOrb(SignalOrb.random(player.getRandom()));
                state.sortGrouped();
            }
        }
        return true;
    }

    @Override
    public void onPlayerTick(ServerPlayer player, SkillState state) {
        boolean dirty = false;
        int leap = SkillUtils.leap(player);

        if (tickHoldActions(player, state, leap)) {
            dirty = true;
        }
        Long naitoReady = NAITO_READY.get(player.getUUID());
        if (naitoReady != null && player.level().getGameTime() > naitoReady) {
            NAITO_READY.remove(player.getUUID());
        }

        if (state.getSwordAuraTicks() > 0) {
            state.setSwordAuraTicks(state.getSwordAuraTicks() - 1);
            if (state.getSwordAuraTicks() == 0) {
                exitSwordAura(player, state, leap);
                dirty = true;
            }
        }
        if (state.getEnhanceTicks() > 0) {
            state.setEnhanceTicks(state.getEnhanceTicks() - 1);
            if (state.getEnhanceTicks() == 0) {
                removeSpeed(player);
                dirty = true;
            }
        }
        if (state.getCrimsonTicks() > 0) {
            state.setCrimsonTicks(state.getCrimsonTicks() - 1);
        }
        if (state.getSuperArmorTicks() > 0) {
            state.setSuperArmorTicks(state.getSuperArmorTicks() - 1);
        }
        if (state.getOrbBuffTicks() > 0) {
            state.setOrbBuffTicks(state.getOrbBuffTicks() - 1);
            if (state.getOrbBuffTicks() == 0) {
                state.setOrbBuffStacks(0);
                dirty = true;
            }
        }
        if (state.getUltSwordTicks() > 0) {
            state.setUltSwordTicks(state.getUltSwordTicks() - 1);
        }
        if (state.getBlueTripleTicks() > 0) {
            state.setBlueTripleTicks(state.getBlueTripleTicks() - 1);
        }
        // 跃升 18 级：首次出场获得 35 点必杀能量
        if (leap >= LEAP_RUSH_UPGRADE && state.isCombatMode() && !state.isDeployEnergyGranted()) {
            state.addEnergy(LEAP_DEPLOY_ENERGY);
            state.setDeployEnergyGranted(true);
            dirty = true;
        }
        if (dirty || player.tickCount % 5 == 0) {
            SkillUtils.sync(player);
        }
    }

    @Override
    public float modifyNormalAttackDamage(ServerPlayer player, SkillState state, float damage) {
        return damage * physicalMultiplier(player, state);
    }

    @Override
    public float modifySkillDamage(ServerPlayer player, SkillState state, float damage) {
        float mult = physicalMultiplier(player, state);
        // SSS「赤红之息」：额外伤害 +5%
        if (SkillUtils.ascension(player) >= 2) {
            mult *= SSS_EXTRA_BONUS;
        }
        return damage * mult;
    }

    @Override
    public void onUnequip(ServerPlayer player, SkillState state) {
        HOLD_START.remove(player.getUUID());
        HOLD_FIRED.remove(player.getUUID());
        NAITO_READY.remove(player.getUUID());
        removeSpeed(player);
        state.setSwordAuraTicks(0);
        state.setSwordStacks(0);
        state.setEnhanceTicks(0);
        state.setEnhanceTier(0);
        state.setCrimsonTicks(0);
        state.setOrbBuffStacks(0);
        state.setOrbBuffTicks(0);
        state.setUltSwordTicks(0);
        state.setBlueTripleTicks(0);
        state.setDeployEnergyGranted(false);
    }

    /** 物理伤害倍率：落樱散华 + 赤鞘出 + SSS+ 蓝球状态。 */
    private float physicalMultiplier(ServerPlayer player, SkillState state) {
        float mult = 1.0f + ORB_BUFF_BONUS * state.getOrbBuffStacks();
        if (state.getCrimsonTicks() > 0) {
            mult += CRIMSON_BONUS;
        }
        int asc = SkillUtils.ascension(player);
        if (asc >= 3 && state.getEnhanceTicks() > 0) {
            int leap = SkillUtils.leap(player);
            mult += leap >= LEAP_ULT_SWORD_UPGRADE ? SSS_PLUS_BLUE_BONUS_UPGRADED : SSS_PLUS_BLUE_BONUS;
        }
        return mult;
    }

    private void castRed(ServerPlayer player, SkillState state, int tier) {
        // 霸体：Z 字闪身全程免疫击退
        state.setSuperArmorTicks(RED_SUPER_ARMOR_TICKS);
        // 总倍率按段数均分
        float perHit = RED_RATIO[tier - 1] / RED_DASH_TICKS.length
                * SkillUpgradeHelper.orbMultiplier(player, SignalOrb.RED);
        double[] sideSigns = {1.0, -1.0, 1.0};
        // 三段 Z 字闪身：前冲 + 左右斜切，每段对前方目标多段伤害
        for (int seg = 0; seg < RED_DASH_TICKS.length; seg++) {
            final int index = seg;
            final double sideSign = sideSigns[seg];
            SkillUtils.schedule(player, RED_DASH_TICKS[seg], p -> {
                Vec3 flat = flatLook(p);
                Vec3 side = new Vec3(-flat.z, 0.0, flat.x).scale(sideSign * RED_DASH_SIDE);
                // 速度冲量由客户端执行（服务端 move 会被客户端位置包覆盖）
                p.setDeltaMovement(flat.scale(RED_DASH_SPEED).add(side).add(0.0, 0.02, 0.0));
                p.hurtMarked = true;
                for (LivingEntity target : SkillUtils.sectorTargets(p, 4.2, 0.25)) {
                    for (int i = 0; i < RED_HITS; i++) {
                        SkillCombatHandler.dealBonusPhysical(p, target, perHit);
                    }
                }
                SkillUtils.sendVfx(p, (byte) 18, p.getX(), p.getY() + 1.0, p.getZ(),
                        0.0f, 3.4f + index * 0.3f, 0.12f,
                        index % 2 == 0 ? COLOR_SWORD : COLOR_CRIMSON, 240, (byte) 0);
                p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.PLAYERS, 0.7f, 1.4f + index * 0.1f);
            });
        }
        // 三消：最后一段额外物理伤害并击飞
        if (tier >= 3) {
            SkillUtils.schedule(player, 9, p -> {
                Vec3 eye = p.getEyePosition();
                Vec3 dir = p.getLookAngle().normalize();
                for (LivingEntity target : SkillUtils.sectorTargets(p, 4.5, 0.25)) {
                    SkillCombatHandler.dealBonusPhysical(p, target,
                            RED_TRIPLE_EXTRA * SkillUpgradeHelper.orbMultiplier(p, SignalOrb.RED));
                    target.setDeltaMovement(target.getDeltaMovement().add(0, 0.75, 0));
                    target.hurtMarked = true;
                }
                SkillUtils.sendVfx(p, (byte) 14, eye.x + dir.x * 2.5, eye.y - 0.4, eye.z + dir.z * 2.5,
                        p.getYRot(), p.getXRot(), 4.0f, 0.5f, COLOR_CRIMSON, 500, (byte) 0);
                p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.PLAYERS, 1.2f, 1.2f);
                triggerCrimson(p, state);
            });
        }
        if (tier >= 3 && SkillUtils.leap(player) >= LEAP_RUSH) {
            state.setSwordStacks(state.getSwordStacks() + 1);
        }
        SkillUtils.sync(player);
    }

    private static Vec3 flatLook(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0.0, look.z);
        if (flat.lengthSqr() < 1.0E-6) {
            return new Vec3(0.0, 0.0, -1.0);
        }
        return flat.normalize();
    }

    private void castYellow(ServerPlayer player, SkillState state, int tier) {
        Vec3 flat = flatLook(player);
        player.setDeltaMovement(flat.scale(YELLOW_RUSH_SPEED).add(0.0, 0.02, 0.0));
        player.hurtMarked = true;
        float perSlash = YELLOW_RATIO[tier - 1] / YELLOW_SLASH_SEGMENTS
                * SkillUpgradeHelper.orbMultiplier(player, SignalOrb.YELLOW);
        for (int i = 0; i < YELLOW_SLASH_TICKS.length; i++) {
            final int index = i;
            SkillUtils.schedule(player, YELLOW_SLASH_TICKS[i], p -> {
                Vec3 dir = flatLook(p);
                p.setDeltaMovement(dir.scale(YELLOW_RUSH_SPEED).add(0.0, 0.02, 0.0));
                p.hurtMarked = true;
                double radius = 2.4 + index * 0.35;
                for (LivingEntity target : SkillUtils.sectorTargets(p, radius, -1.0)) {
                    SkillCombatHandler.dealBonusPhysical(p, target, perSlash);
                }
                SkillUtils.sendVfx(p, (byte) 17, p.getX(), p.getY() + 1.0, p.getZ(),
                        0.0f, (float) radius, 0.25f, COLOR_SWORD, 240, (byte) 0);
                p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.PLAYERS, 0.8f, 1.3f + index * 0.1f);
            });
        }
        // 跃升 18 级：三消黄球也可触发赤鞘出
        if (tier >= 3 && SkillUtils.leap(player) >= LEAP_RUSH_UPGRADE) {
            triggerCrimson(player, state);
        }
        if (tier >= 3 && SkillUtils.leap(player) >= LEAP_RUSH) {
            state.setSwordStacks(state.getSwordStacks() + 1);
        }
        // 黄球三消终结：空间虫洞
        if (tier >= 3) {
            SkillUtils.schedule(player, 8, p -> {
                Vec3 dir = p.getLookAngle();
                SkillUtils.sendVfx(p, (byte) 15, p.getX() + dir.x * 2.0, p.getY() + 2.0, p.getZ() + dir.z * 2.0,
                        p.getYRot(), p.getXRot(), 3.8f, 0.4f, COLOR_CRIMSON, 700, (byte) 0);
                p.level().playSound(null, p.blockPosition(), SoundEvents.TRIDENT_THUNDER.value(),
                        SoundSource.PLAYERS, 0.8f, 1.6f);
            });
        }
        SkillUtils.sync(player);
    }

    private void castBlue(ServerPlayer player, SkillState state, int tier) {
        int leap = SkillUtils.leap(player);
        int duration = leap >= LEAP_BLUE_DURATION ? BLUE_DURATION_UPGRADED : BLUE_DURATION;
        state.setEnhanceTicks(duration);
        state.setEnhanceTier(tier);
        applySpeed(player, BLUE_SPEED[tier - 1]);
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 0.2, player.getZ(),
                0.0f, 2.6f, 0.6f, COLOR_ENHANCE, 380);
        // 蓝球三消终结：吸聚漩涡
        if (tier >= 3) {
            SkillUtils.schedule(player, 6, p -> {
                SkillUtils.sendVfx(p, (byte) 16, p.getX(), p.getY() + 0.4, p.getZ(),
                        p.getYRot(), p.getXRot(), 3.6f, 0.35f, COLOR_ENHANCE, 700, (byte) 0);
                p.level().playSound(null, p.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS, 1.0f, 0.7f);
            });
        }
        SkillUtils.schedule(player, 6, p -> {
            SkillUtils.sendVfx(p, (byte) 3, p.getX(), p.getY() + 0.4, p.getZ(),
                    0.0f, 2.2f, 0.7f, COLOR_ENHANCE, 320);
            p.level().playSound(null, p.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS, 0.8f, 1.3f);
        });
    }

    /** 剑气球：向前释放两道剑气（无二消/三消，一次只消耗 1 个球）。 */
    private void castSwordOrb(ServerPlayer player, SkillState state) {
        state.setSuperArmorTicks(SWORD_SUPER_ARMOR_TICKS);
        Vec3 flat = flatLook(player);
        player.setDeltaMovement(flat.scale(0.45).add(0.0, 0.04, 0.0));
        player.hurtMarked = true;
        boolean enhance = state.getEnhanceTicks() > 0;
        int enhanceTier = Math.max(1, Math.min(3, state.getEnhanceTier()));
        for (int i = 0; i < 2; i++) {
            final int index = i;
            SkillUtils.schedule(player, i * 3, p -> fireSwordQi(p, index, enhance, enhanceTier));
        }
    }

    /** 单道剑气：沿朝向直飞 9 段，两道通过 style 让月牙特效整体侧弯交叉成扁平 X。 */
    private void fireSwordQi(ServerPlayer player, int index, boolean enhance, int enhanceTier) {
        Vec3 flat = flatLook(player);
        Vec3 right = new Vec3(-flat.z, 0.0, flat.x);
        Vec3 start = new Vec3(player.getX(), player.getY() + 1.1, player.getZ())
                .add(right.scale((index == 0 ? -1.0 : 1.0) * 0.55))
                .add(flat.scale(0.9));
        float yaw = (float) Math.toDegrees(Math.atan2(-flat.x, flat.z));
        int color = index == 0 ? COLOR_ULT : COLOR_SWORD;
        // style 决定月牙整体侧弯方向：<10 右偏，>=10 左偏 → 两道交叉
        byte style = (byte) (index == 0 ? 5 : 13);
        float perHit = SWORD_ORB_RATIO * 0.5f;
        java.util.Set<UUID> hit = new java.util.HashSet<>();
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 1.0f, 1.45f);
        for (int step = 0; step < 9; step++) {
            final int s = step;
            SkillUtils.schedule(player, step, p -> {
                Vec3 pos = start.add(flat.scale(s * 1.5));
                SkillUtils.sendVfx(p, (byte) 19, pos.x, pos.y, pos.z, yaw, 0.0f,
                        1.8f + s * 0.09f, 0.11f, color, 220, style);
                net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(
                        pos.x - 1.1, pos.y - 1.4, pos.z - 1.1,
                        pos.x + 1.1, pos.y + 1.4, pos.z + 1.1);
                for (LivingEntity target : p.level().getEntitiesOfClass(LivingEntity.class, box,
                        e -> e != p && e.isAlive())) {
                    if (!hit.add(target.getUUID())) {
                        continue;
                    }
                    SkillCombatHandler.dealBonusPhysical(p, target, perHit);
                    if (enhance) {
                        SkillCombatHandler.dealBonusPhysical(p, target, ENHANCE_SWORD_EXTRA[enhanceTier - 1]);
                    }
                }
            });
        }
    }

    /** 进入剑气状态：全部信号球化为剑气球，额外 +2。 */
    private void enterSwordAura(ServerPlayer player, SkillState state) {
        int leap = SkillUtils.leap(player);
        state.setSwordAuraTicks(leap >= LEAP_BLUE_DURATION ? AURA_DURATION_UPGRADED : AURA_DURATION);
        state.convertAllOrbs(SignalOrb.SWORD);
        state.addOrb(SignalOrb.SWORD);
        state.addOrb(SignalOrb.SWORD);
        if (leap >= LEAP_AURA_ENERGY) {
            // 跃升 9 级：进入剑气回复 15 点必杀能量（原 QTE 唤醒）
            state.addEnergy(LEAP_AURA_ENERGY_VALUE);
        }
        // 武器技能：进入剑气额外获得 1 个剑气球，10 秒冷却
        long now = player.level().getGameTime();
        if (now >= state.getAuraOrbReadyTime()) {
            state.addOrb(SignalOrb.SWORD);
            state.setAuraOrbReadyTime(now + AURA_ORB_COOLDOWN);
        }
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 0.3, player.getZ(),
                0.0f, 3.4f, 0.8f, COLOR_SWORD, 460);
        SkillUtils.sendVfx(player, (byte) 5, player.getX(), player.getY() + 0.9, player.getZ(),
                0.0f, 2.2f, 0.6f, COLOR_CRIMSON, 400);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    /** 退出剑气状态：SSS 额外 2 个信号球，跃升 18 级后改为 2 个蓝球。 */
    private void exitSwordAura(ServerPlayer player, SkillState state, int leap) {
        if (leap >= LEAP_NAITO_UPGRADE) {
            state.addOrb(SignalOrb.BLUE);
            state.addOrb(SignalOrb.BLUE);
        } else if (SkillUtils.ascension(player) >= 2) {
            state.addOrb(SignalOrb.random(player.getRandom()));
            state.addOrb(SignalOrb.random(player.getRandom()));
        }
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 0.3, player.getZ(),
                0.0f, 2.8f, 0.5f, COLOR_SWORD, 320);
    }

    private void triggerCrimson(ServerPlayer player, SkillState state) {
        if (SkillUtils.ascension(player) < 1) {
            return;
        }
        int leap = SkillUtils.leap(player);
        state.setCrimsonTicks(leap >= LEAP_RUSH_UPGRADE ? CRIMSON_DURATION_UPGRADED : CRIMSON_DURATION);
    }

    /** 逆闪切：消耗 1 层心剑。 */
    private void castRushSlash(ServerPlayer player, SkillState state, int leap) {
        state.setSwordStacks(state.getSwordStacks() - 1);
        float ratio = RUSH_RATIO * (leap >= LEAP_RUSH_UPGRADE ? 2.0f : 1.0f);
        Vec3 look = player.getLookAngle().normalize();
        player.setDeltaMovement(look.scale(1.2).add(0, 0.05, 0));
        player.hurtMarked = true;
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 3.5, 0.3);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target, ratio);
        }
        SkillUtils.sendVfx(player, (byte) 0, player.getX(), player.getY() + 0.4, player.getZ(),
                0.0f, 3.8f, 1.0f, COLOR_SWORD, 300);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 1.0f, 1.4f);
    }

    /** 纳刀斩：消耗剩余心剑，每层 600%，期间无敌。 */
    private void castNaito(ServerPlayer player, SkillState state, int leap) {
        int stacks = state.getSwordStacks();
        state.setSwordStacks(0);
        float ratio = NAITO_RATIO * (leap >= LEAP_NAITO_UPGRADE ? 2.0f : 1.0f) * Math.max(1, stacks);
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 4.0, 0.3);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target, ratio);
        }
        player.invulnerableTime = 10;
        SkillUtils.sendVfx(player, (byte) 4, player.getX(), player.getY() + 0.5, player.getZ(),
                0.0f, 2.8f, 1.1f, COLOR_CRIMSON, 420);
        SkillUtils.schedule(player, 4, p -> {
            SkillUtils.sendVfx(p, (byte) 8, p.getX(), p.getY() + 0.2, p.getZ(),
                    0.0f, 4.2f, 0.9f, COLOR_SWORD, 420);
            p.level().playSound(null, p.blockPosition(), SoundEvents.TRIDENT_THUNDER.value(),
                    SoundSource.PLAYERS, 0.8f, 1.6f);
        });
    }

    /** 剑气爆发：消耗 2 层心剑与全部剑气球，每个额外剑气球提升基础伤害。 */
    private void castUltSwordBurst(ServerPlayer player, SkillState state, int leap) {
        int swordOrbs = 0;
        var orbs = state.getOrbs();
        for (int i = orbs.size() - 1; i >= 0; i--) {
            if (orbs.get(i) == SignalOrb.SWORD) {
                orbs.remove(i);
                swordOrbs++;
            }
        }
        if (swordOrbs <= 0) {
            return;
        }
        state.setSwordStacks(state.getSwordStacks() - MAX_SWORD_STACKS);
        state.setUltSwordTicks(0);
        float extra = ULT_SWORD_EXTRA;
        if (leap >= LEAP_ULT_SWORD_UPGRADE) {
            extra = ULT_SWORD_EXTRA_UPGRADED;
        }
        float ratio = ULT_SWORD_RATIO + extra * (swordOrbs - 1);
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 4.5, 0.25);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target, ratio);
        }
        SkillUtils.sendVfx(player, (byte) 4, player.getX(), player.getY() + 0.5, player.getZ(),
                0.0f, 5.0f, 1.4f, COLOR_CRIMSON, 520);
        SkillUtils.schedule(player, 3, p -> {
            SkillUtils.sendVfx(p, (byte) 8, p.getX(), p.getY() + 0.2, p.getZ(),
                    0.0f, 5.4f, 1.2f, COLOR_ULT, 520);
            p.level().playSound(null, p.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL,
                    SoundSource.PLAYERS, 0.7f, 1.8f);
        });
    }

    private void applySpeed(ServerPlayer player, float bonus) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance == null) {
            return;
        }
        instance.removeModifier(ENHANCE_SPEED_ID);
        instance.addTransientModifier(new AttributeModifier(ENHANCE_SPEED_ID, bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private void removeSpeed(ServerPlayer player) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null && instance.getModifier(ENHANCE_SPEED_ID) != null) {
            instance.removeModifier(ENHANCE_SPEED_ID);
        }
    }
}
