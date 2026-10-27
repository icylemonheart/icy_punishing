package com.icypunishing.event;

import com.icypunishing.item.ObjKatanaItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 太刀左键攻击判定：不再走准星单点射线，而是按挥刀轨迹扫过的刀身区域命中。
 * 一刀对每个被刀身扫到的生物结算一次伤害（服务端权威）。
 */
public final class KatanaSweepHandler {

    /** 刀身长度（格）：4 格 OBJ 模型经手持缩放后的视觉长度 */
    private static final double BLADE_LENGTH = 3.0D;
    /** 刀身碰撞半径（格），容许的接触厚度 */
    private static final double BLADE_RADIUS = 0.35D;
    /** 手部锚点相对眼睛的前移 / 侧移 / 下移 */
    private static final double HAND_FORWARD = 0.35D;
    private static final double HAND_SIDE = 0.35D;
    private static final double HAND_DROP = 0.15D;
    /** 挥刀弧线：相对视线从斜上劈到斜下 */
    private static final double ARC_START_DEG = 65.0D;
    private static final double ARC_END_DEG = -55.0D;
    private static final int ARC_STEPS = 9;
    /** 横向偏移，让刀身覆盖身前一条横向带 */
    private static final double[] LATERAL_DEG = {-15.0D, 0.0D, 15.0D};

    private KatanaSweepHandler() {
    }

    public static void onSwing(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ObjKatanaItem)) {
            return;
        }
        if (!player.isAlive() || player.isSpectator() || player.isUsingItem()) {
            return;
        }

        float scale = player.getAttackStrengthScale(0.5F);
        player.resetAttackStrengthTicker();

        List<LivingEntity> targets = collectTargets(player);
        if (targets.isEmpty()) {
            return;
        }

        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * (0.2F + scale * scale * 0.8F);
        float yaw = player.getYRot() * (float) (Math.PI / 180.0);
        boolean hitAny = false;
        for (LivingEntity target : targets) {
            if (target.hurt(player.damageSources().playerAttack(player), damage)) {
                hitAny = true;
                target.knockback(0.2D, Mth.sin(yaw), -Mth.cos(yaw));
                player.setLastHurtMob(target);
            }
        }
        if (!hitAny) {
            return;
        }

        if (targets.size() > 1) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.sweepAttack();
        } else {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        player.causeFoodExhaustion(0.1F);
    }

    private static List<LivingEntity> collectTargets(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 right = look.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (right.lengthSqr() < 1.0E-6D) {
            right = new Vec3(1.0D, 0.0D, 0.0D);
        } else {
            right = right.normalize();
        }
        Vec3 up = right.cross(look).normalize();
        Vec3 hand = eye.add(look.scale(HAND_FORWARD))
                .add(right.scale(HAND_SIDE))
                .subtract(up.scale(HAND_DROP));

        double reach = HAND_FORWARD + BLADE_LENGTH + BLADE_RADIUS;
        AABB broad = new AABB(hand, hand).inflate(reach + 1.0D);
        List<LivingEntity> candidates = player.level().getEntitiesOfClass(LivingEntity.class, broad,
                entity -> entity != player
                        && entity.isAlive()
                        && !entity.isSpectator()
                        && entity.isAttackable()
                        && !player.isAlliedTo(entity)
                        && (!(entity instanceof ArmorStand armorStand) || !armorStand.isMarker()));

        List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity entity : candidates) {
            if (touchesBladeArc(entity.getBoundingBox().inflate(BLADE_RADIUS), hand, look, up)) {
                result.add(entity);
            }
        }
        return result;
    }

    private static boolean touchesBladeArc(AABB box, Vec3 hand, Vec3 look, Vec3 up) {
        if (box.contains(hand)) {
            return true;
        }
        for (int i = 0; i < ARC_STEPS; i++) {
            double angle = Math.toRadians(ARC_START_DEG + (ARC_END_DEG - ARC_START_DEG) * i / (ARC_STEPS - 1));
            Vec3 baseDir = look.scale(Math.cos(angle)).add(up.scale(Math.sin(angle)));
            for (double lateral : LATERAL_DEG) {
                Vec3 tip = hand.add(rotateY(baseDir, lateral).scale(BLADE_LENGTH));
                if (box.clip(hand, tip).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 绕世界 Y 轴旋转，用于让刀身覆盖横向范围 */
    private static Vec3 rotateY(Vec3 vec, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vec3(vec.x * cos + vec.z * sin, vec.y, -vec.x * sin + vec.z * cos).normalize();
    }
}
