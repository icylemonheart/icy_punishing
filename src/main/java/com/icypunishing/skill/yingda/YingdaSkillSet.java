package com.icypunishing.skill.yingda;

import com.icypunishing.event.SkillCombatHandler;
import com.icypunishing.skill.CharacterSkillSet;
import com.icypunishing.skill.SignalOrb;
import com.icypunishing.skill.SkillState;
import com.icypunishing.skill.SkillUpgradeHelper;
import com.icypunishing.skill.SkillUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/** 影打-逆断（露西亚·黎明）技能组，雷属性机体。 */
public class YingdaSkillSet implements CharacterSkillSet {

    // 红球「红莲-乱舞」：5 段，1/2/3 消 = 40%/80%/144%，3 消转为雷属性
    static final float[] RED_RATIO = {0.40f, 0.80f, 1.44f};
    static final int RED_HITS = 5;
    static final int RED_SUPER_ARMOR_TICKS = 20;
    // 黄球「红莲-雷震」：1/2/3 消 = 100%/200%/300% 物伤，雷域每秒 20%/30%/40% 雷伤
    static final float[] YELLOW_RATIO = {1.00f, 2.00f, 3.00f};
    static final float[] FIELD_RATIO = {0.20f, 0.30f, 0.40f};
    static final int[] FIELD_DURATION = {60, 80, 100};
    // 蓝球「红莲-煌月斩」：突进 80%/160%/240% 雷伤 + 拉近 80%/160%/240% 物伤
    static final float[] BLUE_THRUST_RATIO = {0.80f, 1.60f, 2.40f};
    static final float[] BLUE_PULL_RATIO = {0.80f, 1.60f, 2.40f};

    // 普攻「红莲-迅刀式」：整套连段共 650% 物伤，这里折算为每次命中的追加段
    static final float NORMAL_COMBO_BONUS = 1.30f;

    static final int ENERGY_PER_HIT = 2;
    static final int ENERGY_PER_ORB = 8;

    // 必杀「红莲」：50 能量，开启红莲剑 15 秒
    static final int ULT_COST = 50;
    static final int DAWN_BLADE_DURATION = 300;
    static final float DAWN_BLADE_RATIO = 0.25f;

    // 标记：最多 3 层，攻击被标记敌人时按层数概率落雷
    static final int MARK_MAX = 3;
    static final int MARK_DURATION_TICKS = 160;
    static final float[] MARK_CHANCE = {0.25f, 0.50f, 0.75f};
    static final float THUNDER_RATIO = 0.60f;
    static final int THUNDER_CHARGE_TICKS = 100;

    // 升阶
    static final float SS_FIELD_BONUS = 0.20f;
    static final float SSS_THUNDER_BONUS = 0.20f;
    static final float SSS_PLUS_EXTRA = 0.15f;
    static final int SSS_PLUS_DURATION = 200;

    // 跃升：三个节点各 18 级，总等级 1-54（顺序解锁）
    static final int LEAP_RED = 1;
    static final int LEAP_RED_PULL = 9;
    static final int LEAP_RED_FINISHER = 18;
    static final int LEAP_THUNDER = 19;
    static final int LEAP_MARK_UP = 27;
    static final int LEAP_THUNDER_UPGRADE = 36;
    static final int LEAP_ULT_SHADOW = 37;
    static final int LEAP_ULT_REFUND = 45;
    static final int LEAP_BACKLINE = 54;
    static final float LEAP_RED_BONUS = 0.10f;
    static final float LEAP_THUNDER_BONUS = 0.10f;
    static final float LEAP_THUNDER_UPGRADE_BONUS = 0.10f;
    static final float LEAP_FINISHER_RATIO = 3.60f;
    static final float[] MARK_CHANCE_UPGRADED = {0.50f, 0.75f, 1.00f};
    static final int LEAP_REFUND_DURATION = 200;
    static final int LEAP_REFUND_INTERVAL = 20;
    static final int LEAP_REFUND_ENERGY = 2;

    // 武器技能「逆断之影」
    static final float WEAPON_LIGHTNING_STACK = 0.05f;
    static final int WEAPON_LIGHTNING_MAX = 4;
    static final int WEAPON_LIGHTNING_DURATION = 80;
    static final float WEAPON_ULT_EXTRA = 0.10f;
    static final int WEAPON_ULT_DURATION = 200;

    // 动画时序：取自黎明官方动作 clip（Attack11/21/31/51），按 1 tick = 1/20 秒换算
    static final int[] RED_SWINGS = {7, 9, 13, 15, 18};
    static final int YELLOW_SWING = 4;
    static final int YELLOW_FIELD = 11;
    static final int BLUE_HIT = 9;
    static final int BLUE_PULL = 21;
    static final int ULT_OPEN = 11;
    static final int ULT_FINISH = 47;

    static final double FIELD_RADIUS = 4.0;

    static final long DODGE_COOLDOWN = 30L;
    static final float DODGE_SPEED = 1.15f;
    static final int DODGE_IFRAMES = 12;
    static final long PERFECT_DODGE_WINDOW = 8L;
    static final int HYPER_SPACE_DURATION = 60;
    static final int PERFECT_DODGE_ORB_REWARD = 2;

    static final int COLOR_LIGHTNING = 0xA8E6FF;
    static final int COLOR_RED = 0xFF4A3A;
    static final int COLOR_YELLOW = 0xFFE14A;
    static final int COLOR_BLUE = 0x4AA8FF;
    static final int COLOR_ULT = 0xE8F4FF;

    /** 被标记目标：层数 + 到期时间（世界 gameTime）。 */
    private static final Map<UUID, MarkData> MARKS = new ConcurrentHashMap<>();
    private static final Map<UUID, ThunderField> FIELDS = new ConcurrentHashMap<>();

    @Override
    public String getModelId() {
        return "yingda";
    }

    @Override
    public int ultimateEnergyCost() {
        return ULT_COST;
    }

    @Override
    public float sssPlusNormalBonus() {
        // 黎明的 SSS+ 是「释放必杀后额外伤害 +15%」，由 dawnExtraTicks 接管
        return 0.0f;
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
        state.addEnergy(consumed * ENERGY_PER_ORB);
        // 核心被动「红莲-引雷舞」：消黄/蓝球后获得引雷蓄势
        if (color == SignalOrb.YELLOW || color == SignalOrb.BLUE) {
            state.setThunderChargeTicks(THUNDER_CHARGE_TICKS);
        }
        switch (color) {
            case RED -> castRed(player, state, tier);
            case YELLOW -> castYellow(player, state, tier);
            case BLUE -> castBlue(player, state, tier);
        }
        SkillUtils.sync(player);
    }

    /** 把一段演出延后到指定 tick 执行（伤害结算不随之延后）。 */
    private void schedule(ServerPlayer player, int delayTicks, Consumer<ServerPlayer> action) {
        SkillUtils.schedule(player, delayTicks, action);
    }

    private void castRed(ServerPlayer player, SkillState state, int tier) {
        state.setSuperArmorTicks(RED_SUPER_ARMOR_TICKS);
        Vec3 look = player.getLookAngle().normalize();
        player.setDeltaMovement(look.scale(0.75).add(0, 0.04, 0));
        player.hurtMarked = true;
        int leap = SkillUtils.leap(player);
        boolean triple = tier >= 3;
        // 跃升 1/9/18 级：三消 +10% 伤害 / 牵引敌人 / 必杀中追加终结一击
        float ratioMult = triple && leap >= LEAP_RED ? 1.0f + LEAP_RED_BONUS : 1.0f;
        boolean pull = triple && leap >= LEAP_RED_PULL;
        boolean finisher = triple && leap >= LEAP_RED_FINISHER && state.getDawnBladeTicks() > 0;
        boolean lightning = triple;

        // 动画是 2 秒的 5 段乱舞，5 次挥刀落在 0.33~0.90 秒；伤害仍即时结算，只还原演出节奏
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 0.15, player.getZ(),
                0.0f, 1.3f, 0.10f, 0xFFFFFF, 200);
        for (int i = 0; i < RED_SWINGS.length; i++) {
            final byte style = (byte) (i % 4);
            final float radius = 3.0f + (i % 2) * 0.35f;
            final float thick = 0.65f + (i % 2) * 0.15f;
            schedule(player, RED_SWINGS[i], p -> {
                Vec3 eye = p.getEyePosition();
                Vec3 dir = p.getLookAngle().normalize();
                SkillUtils.sendVfx(p, (byte) 1, eye.x + dir.x * 1.1,
                        eye.y + dir.y * 1.1 + 0.15, eye.z + dir.z * 1.1,
                        0.0f, radius, thick, COLOR_RED, 260, style);
            });
        }
        // 乱舞收尾：贯穿全屏的细红线
        schedule(player, RED_SWINGS[RED_SWINGS.length - 1], p -> {
            Vec3 eye = p.getEyePosition();
            Vec3 dir = p.getLookAngle().normalize();
            SkillUtils.sendVfx(p, (byte) 24,
                    eye.x + dir.x * 0.8, eye.y + dir.y * 0.8, eye.z + dir.z * 0.8,
                    0.0f, 5.4f, 0.05f, COLOR_RED, 360, (byte) 3);
        });
        schedule(player, RED_SWINGS[0], p -> p.level().playSound(null, p.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.35f));
        schedule(player, RED_SWINGS[2], p -> p.level().playSound(null, p.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.1f, 1.15f));

        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 3.5, 0.5);
        float perHit = RED_RATIO[tier - 1] * ratioMult / RED_HITS
                * SkillUpgradeHelper.orbMultiplier(player, SignalOrb.RED);
        for (LivingEntity target : targets) {
            for (int i = 0; i < RED_HITS; i++) {
                if (lightning) {
                    dealLightning(player, target, perHit, state, false);
                } else {
                    SkillCombatHandler.dealBonusPhysical(player, target, perHit);
                }
            }
            if (pull) {
                Vec3 dir = player.position().subtract(target.position()).normalize().scale(0.6).add(0, 0.2, 0);
                target.setDeltaMovement(dir);
                target.hurtMarked = true;
            }
            applyDawnBlade(player, target, state);
        }
        if (finisher) {
            for (LivingEntity target : targets) {
                dealLightning(player, target, LEAP_FINISHER_RATIO, state, false);
            }
            int iaidoTick = RED_SWINGS[RED_SWINGS.length - 1];
            // 居合斩：前冲 + 贯穿红线 + 脚下白环
            schedule(player, iaidoTick, p -> {
                Vec3 dir = p.getLookAngle().normalize();
                p.setDeltaMovement(dir.scale(2.4).add(0, 0.12, 0));
                p.hurtMarked = true;
                Vec3 eye = p.getEyePosition();
                SkillUtils.sendVfx(p, (byte) 24,
                        eye.x + dir.x * 0.8, eye.y + dir.y * 0.8, eye.z + dir.z * 0.8,
                        0.0f, 7.0f, 0.06f, COLOR_RED, 520, (byte) 4);
                SkillUtils.sendVfx(p, (byte) 2, p.getX(), p.getY() + 0.1, p.getZ(),
                        0.0f, 2.6f, 0.09f, 0xFFFFFF, 460);
                p.level().playSound(null, p.blockPosition(), SoundEvents.TRIDENT_THUNDER.value(),
                        SoundSource.PLAYERS, 0.9f, 1.2f);
            });
            // 居合收尾：敌人身上逐段炸开三连斩
            for (LivingEntity target : targets) {
                for (int i = 0; i < 3; i++) {
                    final int index = i;
                    schedule(player, iaidoTick + 4 + i * 3, p -> {
                        if (!target.isAlive()) {
                            return;
                        }
                        Vec3 c = target.position().add(0, target.getBbHeight() * 0.55, 0);
                        SkillUtils.sendVfx(p, (byte) 1, c.x, c.y, c.z,
                                p.getYRot() + 60.0f + index * 45.0f, -25.0f + index * 20.0f,
                                1.6f, 0.42f, COLOR_RED, 280, (byte) (index % 4));
                    });
                }
            }
        }
    }

    private void castYellow(ServerPlayer player, SkillState state, int tier) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 center = player.position().add(look.scale(2.0));
        // 官方动作 1.6 秒：0.2 秒挥刀 → 0.55 秒雷柱落地；伤害与雷域仍即时生效
        schedule(player, YELLOW_SWING, p -> {
            SkillUtils.sendVfx(p, (byte) 22, p.getX(), p.getY() + 0.06, p.getZ(),
                    0.0f, 3.0f, 1.0f, COLOR_YELLOW, 460);
            p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS, 1.0f, 1.1f);
        });
        schedule(player, YELLOW_FIELD, p -> {
            // 两道方向相反的轮盘转击
            SkillUtils.sendVfx(p, (byte) 25, center.x, center.y + 0.05, center.z,
                    0.0f, 3.0f, 0.3f, 0xFF6A2A, 560, (byte) 0);
            SkillUtils.sendVfx(p, (byte) 25, center.x, center.y + 0.05, center.z,
                    0.0f, 2.2f, 0.22f, 0xFFC83C, 520, (byte) 1);
            if (SkillUtils.leap(p) >= LEAP_THUNDER) {
                // 跃升：轮盘展开为范围领域
                SkillUtils.sendVfx(p, (byte) 26, center.x, center.y + 0.05, center.z,
                        0.0f, 4.4f, 0.22f, 0xFFD24A, 1200);
            } else {
                SkillUtils.sendVfx(p, (byte) 2, center.x, center.y + 0.1, center.z,
                        0.0f, 2.4f, 0.12f, 0xFFFFFF, 340);
            }
            p.level().playSound(null, BlockPos.containing(center), SoundEvents.TRIDENT_THUNDER.value(),
                    SoundSource.PLAYERS, 0.9f, 1.4f);
        });
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, 3.0, 0.4);
        for (LivingEntity target : targets) {
            SkillCombatHandler.dealBonusPhysical(player, target,
                    YELLOW_RATIO[tier - 1] * SkillUpgradeHelper.orbMultiplier(player, SignalOrb.YELLOW));
            addMark(target, 1);
            applyDawnBlade(player, target, state);
        }
        ThunderField field = new ThunderField(center.x, center.y, center.z, FIELD_RADIUS,
                FIELD_DURATION[tier - 1], FIELD_RATIO[tier - 1]);
        FIELDS.put(player.getUUID(), field);
    }

    private void castBlue(ServerPlayer player, SkillState state, int tier) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 eye = player.getEyePosition();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        SkillUtils.sendVfx(player, (byte) 23,
                eye.x + right.x * 0.4, eye.y - 0.3, eye.z + right.z * 0.4,
                0.0f, 0.75f, 0.1f, COLOR_RED, 480);

        // 三根矛以 -22°/0°/+22° 扇形掷出，BLUE_HIT 时命中，BLUE_PULL 时收回
        final float[] fan = {-0.38f, 0.0f, 0.38f};
        final float range = 7.0f;
        final int steps = 4;
        for (float angle : fan) {
            Vec3 dir = look.yRot(angle).normalize();
            float yaw = (float) Math.toDegrees(Math.atan2(-dir.x, dir.z));
            float pitch = (float) -Math.toDegrees(Math.asin(dir.y));
            for (int i = 0; i <= steps; i++) {
                final float dist = range * i / steps;
                final int delay = Math.max(1, BLUE_HIT * i / steps);
                schedule(player, delay, p -> sendSpear(p, dir, dist, yaw, pitch));
            }
            int backSpan = BLUE_PULL - BLUE_HIT - 4;
            for (int i = 0; i <= steps; i++) {
                final float dist = range * (1.0f - i / (float) steps);
                final int delay = BLUE_HIT + 4 + backSpan * i / steps;
                schedule(player, delay, p -> sendSpear(p, dir, dist, yaw, pitch));
            }
        }
        schedule(player, BLUE_HIT, p -> p.level().playSound(null, p.blockPosition(),
                SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 1.3f));

        // 去程命中
        List<LivingEntity> targets = SkillUtils.sectorTargets(player, range + 1.5, 0.62);
        for (LivingEntity target : targets) {
            dealLightning(player, target, BLUE_THRUST_RATIO[tier - 1], state, false);
            addMark(target, 1);
            applyDawnBlade(player, target, state);
        }

        // 回程命中：第二段伤害并把敌人拉向自己
        schedule(player, BLUE_PULL, p -> {
            List<LivingEntity> recall = SkillUtils.sectorTargets(p, range + 1.5, 0.62);
            for (LivingEntity target : recall) {
                if (!target.isAlive()) {
                    continue;
                }
                SkillCombatHandler.dealBonusPhysical(p, target,
                        BLUE_PULL_RATIO[tier - 1] * SkillUpgradeHelper.orbMultiplier(p, SignalOrb.BLUE));
                Vec3 pull = p.position().subtract(target.position()).normalize().scale(0.9).add(0, 0.25, 0);
                target.setDeltaMovement(pull);
                target.hurtMarked = true;
            }
            p.level().playSound(null, p.blockPosition(), SoundEvents.TRIDENT_THROW.value(),
                    SoundSource.PLAYERS, 0.9f, 1.6f);
        });
    }

    /** 单根投掷矛的演出：在飞行路径的某一格位置生成一截矛影。 */
    private void sendSpear(ServerPlayer player, Vec3 dir, float dist, float yaw, float pitch) {
        Vec3 eye = player.getEyePosition();
        SkillUtils.sendVfx(player, (byte) 28,
                eye.x + dir.x * dist, eye.y + dir.y * dist, eye.z + dir.z * dist,
                yaw, pitch, 1.3f, 0.12f, COLOR_RED, 240, (byte) 0);
    }

    @Override
    public void onUltimateKey(ServerPlayer player, SkillState state) {
        if (state.getEnergy() < ULT_COST) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.icy_punishing.ult_not_ready"), true);
            return;
        }
        state.setEnergy(state.getEnergy() - ULT_COST);
        // 红莲剑 15 秒：普攻与技能附带 25% 雷伤
        state.setDawnBladeTicks(DAWN_BLADE_DURATION);
        int leap = SkillUtils.leap(player);
        if (leap >= LEAP_ULT_SHADOW) {
            // 跃升节点三：必杀后额外伤害 +10%，持续 10 秒
            state.setUltShadowTicks(WEAPON_ULT_DURATION);
        }
        if (leap >= LEAP_ULT_REFUND) {
            // 跃升 45 级：必杀后 10 秒内自动回复能量
            state.setUltRefundTicks(LEAP_REFUND_DURATION);
        }
        if (SkillUtils.ascension(player) >= 3) {
            state.setDawnExtraTicks(SSS_PLUS_DURATION);
        }
        // 官方必杀动作 4.1 秒：0.55 秒起手斩 → 2.35 秒终结爆发；Buff 立即生效，此处只补演出节奏
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 0.4, player.getZ(),
                0.0f, 3.6f, 0.12f, 0xFFFFFF, 420);
        // 必杀强化：身后四把残光太刀持续到红莲剑结束（跟随角色）
        SkillUtils.sendVfxFollow(player, player.getId(), (byte) 27, 1.2f, 0.11f, COLOR_RED,
                DAWN_BLADE_DURATION * 50, (byte) 0);
        schedule(player, ULT_OPEN, p -> {
            Vec3 eye = p.getEyePosition();
            Vec3 dir = p.getLookAngle().normalize();
            SkillUtils.sendVfx(p, (byte) 1, eye.x + dir.x * 1.4, eye.y + 0.2, eye.z + dir.z * 1.4,
                    0.0f, 4.4f, 1.1f, COLOR_RED, 400);
            p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS, 1.2f, 0.9f);
        });
        schedule(player, ULT_FINISH, p -> {
            Vec3 eye = p.getEyePosition();
            Vec3 dir = p.getLookAngle().normalize();
            Vec3 hit = eye.add(dir.scale(1.6));
            SkillUtils.sendVfx(p, (byte) 4, hit.x, hit.y, hit.z,
                    0.0f, 5.4f, 1.5f, COLOR_RED, 620);
            SkillUtils.sendVfx(p, (byte) 3, hit.x, hit.y, hit.z,
                    0.0f, 4.6f, 1.4f, COLOR_LIGHTNING, 560);
            SkillUtils.sendVfx(p, (byte) 8, p.getX(), p.getY() + 0.3, p.getZ(),
                    0.0f, 5.0f, 1.0f, COLOR_LIGHTNING, 520);
            p.level().playSound(null, p.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER,
                    SoundSource.PLAYERS, 1.0f, 1.0f);
            p.level().playSound(null, p.blockPosition(), SoundEvents.END_PORTAL_SPAWN,
                    SoundSource.PLAYERS, 0.5f, 1.6f);
        });
        SkillUtils.sync(player);
    }

    @Override
    public void onMeleeHit(ServerPlayer player, LivingEntity target, float dealtDamage, SkillState state) {
        long gameTime = player.level().getGameTime();
        state.setLastCombatGameTime(gameTime);

        if (state.isCombatMode()) {
            state.addEnergy(ENERGY_PER_HIT);
            state.addOrb(SignalOrb.random(player.getRandom()));
        }

        // 普攻「红莲-迅刀式」额外连段
        SkillCombatHandler.dealBonusPhysical(player, target, NORMAL_COMBO_BONUS);

        // 消红/蓝球后的引雷蓄势：下一次普攻必定落雷（优先，避免与标记引雷同时触发）
        if (state.getThunderChargeTicks() > 0) {
            thunderStrike(player, target, state);
            state.setThunderChargeTicks(0);
        } else {
            // 核心被动：被标记目标按层数概率引雷（跃升 27 级后概率提升至 50%/75%/100%）
            int marks = markStacks(target, gameTime);
            float[] chance = SkillUtils.leap(player) >= LEAP_MARK_UP ? MARK_CHANCE_UPGRADED : MARK_CHANCE;
            if (marks > 0 && player.getRandom().nextFloat() < chance[marks - 1]) {
                thunderStrike(player, target, state);
            }
        }

        applyDawnBlade(player, target, state);
        SkillUtils.sync(player);
    }

    /** 必杀「红莲剑」：普攻与技能附带 25% 雷属性伤害。 */
    private void applyDawnBlade(ServerPlayer player, LivingEntity target, SkillState state) {
        if (state.getDawnBladeTicks() > 0) {
            dealLightning(player, target, DAWN_BLADE_RATIO * SkillUpgradeHelper.ultMultiplier(player), state, false);
        }
    }

    /** 引雷：标记触发或蓄势触发的落雷（跃升节点二强化伤害与形态）。 */
    private void thunderStrike(ServerPlayer player, LivingEntity target, SkillState state) {
        int leap = SkillUtils.leap(player);
        float mult = 1.0f;
        if (leap >= LEAP_THUNDER) {
            mult *= 1.0f + LEAP_THUNDER_BONUS;
        }
        boolean upgraded = leap >= LEAP_THUNDER_UPGRADE;
        if (upgraded) {
            mult *= 1.0f + LEAP_THUNDER_UPGRADE_BONUS;
        }
        dealLightning(player, target, THUNDER_RATIO * mult, state, true);
        Vec3 pos = target.position();
        float radius = upgraded ? 2.2f : 1.6f;
        int color = upgraded ? 0xFFFFFF : COLOR_LIGHTNING;
        SkillUtils.sendVfx(player, (byte) 7, pos.x, pos.y, pos.z, 0.0f, radius, 0.45f,
                color, upgraded ? 380 : 320);
        player.level().playSound(null, target.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS, 0.7f, 1.4f);
    }

    /** 统一雷伤结算：叠加逆断之影层数、SS 雷域与 SSS 引雷加成。 */
    private void dealLightning(ServerPlayer player, LivingEntity target, float ratio,
                               SkillState state, boolean thunder) {
        float mult = 1.0f + WEAPON_LIGHTNING_STACK * state.getLightningStacks();
        int asc = SkillUtils.ascension(player);
        if (asc >= 1 && isInsideField(player, target)) {
            mult *= 1.0f + SS_FIELD_BONUS;
        }
        if (thunder && asc >= 2) {
            mult *= 1.0f + SSS_THUNDER_BONUS;
        }
        SkillCombatHandler.dealBonusLightning(player, target, ratio * mult);
        // 逆断之影：每次雷伤 +5%，持续 4 秒，最多 4 层
        state.setLightningStacks(Math.min(WEAPON_LIGHTNING_MAX, state.getLightningStacks() + 1));
        state.setLightningTicks(WEAPON_LIGHTNING_DURATION);
    }

    @Override
    public float modifyNormalAttackDamage(ServerPlayer player, SkillState state, float damage) {
        return damage * extraMultiplier(state);
    }

    @Override
    public float modifySkillDamage(ServerPlayer player, SkillState state, float damage) {
        return damage * extraMultiplier(state);
    }

    private float extraMultiplier(SkillState state) {
        float mult = 1.0f;
        if (state.getDawnExtraTicks() > 0) {
            mult *= 1.0f + SSS_PLUS_EXTRA;
        }
        if (state.getUltShadowTicks() > 0) {
            mult *= 1.0f + WEAPON_ULT_EXTRA;
        }
        return mult;
    }

    @Override
    public void onPlayerTick(ServerPlayer player, SkillState state) {
        boolean dirty = false;
        int leap = SkillUtils.leap(player);
        // 跃升 54 级：非战斗状态（后台）不消耗红莲剑时间
        boolean backline = leap >= LEAP_BACKLINE && !state.isCombatMode();
        if (state.getDawnBladeTicks() > 0 && !backline) {
            state.setDawnBladeTicks(state.getDawnBladeTicks() - 1);
            if (state.getDawnBladeTicks() == 0) {
                dirty = true;
            }
        }
        if (state.getLightningTicks() > 0) {
            state.setLightningTicks(state.getLightningTicks() - 1);
            if (state.getLightningTicks() == 0) {
                state.setLightningStacks(0);
                dirty = true;
            }
        }
        if (state.getUltShadowTicks() > 0) {
            state.setUltShadowTicks(state.getUltShadowTicks() - 1);
            if (state.getUltShadowTicks() == 0) {
                dirty = true;
            }
        }
        if (state.getDawnExtraTicks() > 0) {
            state.setDawnExtraTicks(state.getDawnExtraTicks() - 1);
            if (state.getDawnExtraTicks() == 0) {
                dirty = true;
            }
        }
        if (state.getThunderChargeTicks() > 0) {
            state.setThunderChargeTicks(state.getThunderChargeTicks() - 1);
        }
        if (state.getUltRefundTicks() > 0) {
            state.setUltRefundTicks(state.getUltRefundTicks() - 1);
            if (state.getUltRefundTicks() % LEAP_REFUND_INTERVAL == 0) {
                state.addEnergy(LEAP_REFUND_ENERGY);
                dirty = true;
            }
        }
        if (state.getDodgeCooldownTicks() > 0) {
            state.setDodgeCooldownTicks(state.getDodgeCooldownTicks() - 1);
        }
        if (state.getSuperArmorTicks() > 0) {
            state.setSuperArmorTicks(state.getSuperArmorTicks() - 1);
        }

        if (tickField(player, state)) {
            dirty = true;
        }

        if (dirty) {
            SkillUtils.sync(player);
        } else if (player.level().getGameTime() % 5 == 0
                && (state.getDawnBladeTicks() > 0 || state.getLightningTicks() > 0
                    || state.getUltShadowTicks() > 0 || state.getDawnExtraTicks() > 0
                    || state.getDodgeCooldownTicks() > 0)) {
            SkillUtils.sync(player);
        }
    }

    /** 雷域每秒结算一次，返回是否发生需要同步的变化。 */
    private boolean tickField(ServerPlayer player, SkillState state) {
        ThunderField field = FIELDS.get(player.getUUID());
        if (field == null) {
            return false;
        }
        field.ticksLeft--;
        field.pulseTicks--;
        if (field.ticksLeft <= 0) {
            FIELDS.remove(player.getUUID());
            return true;
        }
        if (field.pulseTicks <= 0) {
            field.pulseTicks = 20;
            pulseField(player, field, state);
        }
        return false;
    }

    private void pulseField(ServerPlayer player, ThunderField field, SkillState state) {
        AABB box = new AABB(field.x - field.radius, field.y - 1.5, field.z - field.radius,
                field.x + field.radius, field.y + 2.5, field.z + field.radius);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, box);
        boolean any = false;
        for (LivingEntity target : targets) {
            if (target == player || !target.isAlive()) {
                continue;
            }
            if (target.position().distanceToSqr(field.x, field.y, field.z) > field.radius * field.radius) {
                continue;
            }
            dealLightning(player, target, field.ratio, state, false);
            addMark(target, 1);
            any = true;
        }
        if (any) {
            SkillUtils.sendVfx(player, (byte) 2, field.x, field.y + 0.1, field.z,
                    0.0f, (float) field.radius, 0.35f, COLOR_YELLOW, 300);
        }
    }

    private boolean isInsideField(ServerPlayer player, LivingEntity target) {
        ThunderField field = FIELDS.get(player.getUUID());
        if (field == null) {
            return false;
        }
        return target.position().distanceToSqr(field.x, field.y, field.z) <= field.radius * field.radius;
    }

    private void addMark(LivingEntity target, int stacks) {
        long now = target.level().getGameTime();
        MarkData old = MARKS.get(target.getUUID());
        int base = old != null && old.expireTime > now ? old.stacks : 0;
        int value = Math.min(MARK_MAX, base + stacks);
        MARKS.put(target.getUUID(), new MarkData(value, now + MARK_DURATION_TICKS));
    }

    private int markStacks(LivingEntity target, long now) {
        MarkData data = MARKS.get(target.getUUID());
        if (data == null) {
            return 0;
        }
        if (data.expireTime <= now) {
            MARKS.remove(target.getUUID());
            return 0;
        }
        return Math.min(MARK_MAX, data.stacks);
    }

    @Override
    public void onDodgeKey(ServerPlayer player, SkillState state) {
        long now = player.level().getGameTime();
        if (now - state.getLastDodgeGameTime() < DODGE_COOLDOWN) {
            return;
        }
        state.setLastDodgeGameTime(now);
        state.setDodgeCooldownTicks((int) DODGE_COOLDOWN);
        com.icylib.dodge.DodgeSystem.activate(player, PERFECT_DODGE_WINDOW, (p, src) -> {
            p.addEffect(new MobEffectInstance(
                    com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE,
                    HYPER_SPACE_DURATION, 0, false, false, true));
            for (int i = 0; i < PERFECT_DODGE_ORB_REWARD; i++) {
                state.addOrb(SignalOrb.random(p.getRandom()));
            }
            p.level().playSound(null, p.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                    SoundSource.PLAYERS, 1.0f, 1.8f);
            SkillUtils.sendVfx(p, (byte) 2, p.getX(), p.getY() + 1.0, p.getZ(),
                    0.0f, 3.0f, 1.2f, COLOR_LIGHTNING, 420);
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
        player.setDeltaMovement(x / len * DODGE_SPEED, Math.max(player.getDeltaMovement().y, 0.15),
                z / len * DODGE_SPEED);
        player.hurtMarked = true;
        player.invulnerableTime = DODGE_IFRAMES;
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.8f, 1.6f);
        SkillUtils.sendVfx(player, (byte) 2, player.getX(), player.getY() + 1.0, player.getZ(),
                0.0f, 1.8f, 0.3f, COLOR_LIGHTNING, 200);
    }

    @Override
    public void onUnequip(ServerPlayer player, SkillState state) {
        FIELDS.remove(player.getUUID());
        SkillUtils.clearScheduled(player);
        state.setLightningStacks(0);
        state.setLightningTicks(0);
        state.setDawnBladeTicks(0);
        state.setUltShadowTicks(0);
        state.setDawnExtraTicks(0);
        state.setThunderChargeTicks(0);
        state.setUltRefundTicks(0);
        SkillUtils.sync(player);
    }

    private record MarkData(int stacks, long expireTime) {
    }

    private static final class ThunderField {
        final double x;
        final double y;
        final double z;
        final double radius;
        final float ratio;
        int ticksLeft;
        int pulseTicks = 20;

        ThunderField(double x, double y, double z, double radius, int ticksLeft, float ratio) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
            this.ticksLeft = ticksLeft;
            this.ratio = ratio;
        }
    }
}
