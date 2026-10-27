package com.icypunishing.skill;

import com.icypunishing.item.ObjKatanaItem;
import com.icypunishing.network.ClientboundSkillStatePayload;
import com.icypunishing.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class SkillUtils {

    private SkillUtils() {
    }

    public static SkillState state(ServerPlayer player) {
        return player.getData(ModAttachments.SKILL_STATE);
    }

    public static void sync(ServerPlayer player) {
        sendState(player, SkillSetRegistry.find(player) != null);
    }

    public static void syncHolding(ServerPlayer player, boolean holding) {
        sendState(player, holding);
    }

    private static void sendState(ServerPlayer player, boolean holding) {
        SkillState state = state(player);
        List<SignalOrb> orbs = state.getOrbs();
        byte[] ids = new byte[orbs.size()];
        for (int i = 0; i < orbs.size(); i++) {
            ids[i] = (byte) orbs.get(i).id;
        }
        PacketDistributor.sendToPlayer(player, new ClientboundSkillStatePayload(
                ids, state.getEnergy(), state.getBurstTicks(), state.getFireBuffTicks(),
                holding, state.isCombatMode(), state.getRageStacks(), state.getRageTicks(),
                state.getDodgeCooldownTicks()));
    }

    public static int ascension(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ObjKatanaItem katana) {
            return katana.getAscension(stack);
        }
        return 0;
    }

    public static List<LivingEntity> sectorTargets(ServerPlayer player, double range, double minDot) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        AABB box = player.getBoundingBox().inflate(range);
        List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (entity == player || !entity.isAlive()) {
                continue;
            }
            Vec3 to = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(eye);
            double dist = to.length();
            if (dist > range || dist < 1.0E-4) {
                continue;
            }
            Vec3 dir = to.normalize();
            if (dir.dot(look) >= minDot) {
                result.add(entity);
            }
        }
        return result;
    }

    public static float attackOf(ServerPlayer player) {
        return (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public static void sendVfx(ServerPlayer player, byte shape, double x, double y, double z,
                               float pitchOffset, float radius, float thickness, int color, int lifeMillis) {
        sendVfx(player, shape, x, y, z, pitchOffset, radius, thickness, color, lifeMillis, (byte) 0);
    }

    public static void sendVfx(ServerPlayer player, byte shape, double x, double y, double z,
                               float pitchOffset, float radius, float thickness, int color, int lifeMillis,
                               byte style) {
        com.icypunishing.network.ClientboundSkillVfxPayload payload =
                new com.icypunishing.network.ClientboundSkillVfxPayload(shape, style,
                        (float) x, (float) y, (float) z,
                        player.getYRot(), player.getXRot() + pitchOffset, radius, thickness, color, lifeMillis);
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other.distanceToSqr(x, y, z) < 4096.0) {
                PacketDistributor.sendToPlayer(other, payload);
            }
        }
        spawnParticles(player, shape, x, y, z, radius, color);
    }

    private static void spawnParticles(ServerPlayer player, byte shape, double x, double y, double z,
                                       float radius, int color) {
        net.minecraft.server.level.ServerLevel level = player.serverLevel();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        net.minecraft.core.particles.DustParticleOptions dust =
                new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(r, g, b), 2.2f);
        net.minecraft.core.particles.DustParticleOptions bright =
                new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(1.0f, 1.0f, 1.0f), 1.1f);
        double spread = Math.max(0.8, radius * 0.45);
        level.sendParticles(dust, x, y, z, 26, spread, spread * 0.7, spread, 0.12);
        level.sendParticles(bright, x, y, z, 12, spread * 0.6, spread * 0.5, spread * 0.6, 0.08);
        switch (shape) {
            case 0 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, x, y, z, 2, 0.3, 0.3, 0.3, 0.0);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, x, y, z, 18, spread, spread * 0.6, spread, 0.3);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA, x, y, z, 10, spread * 0.5, 0.2, spread * 0.5, 0.0);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, x, y, z, 20, spread * 0.7, 0.25, spread * 0.7, 0.06);
            }
            case 1 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.4, 0.4, 0.4, 0.0);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, x, y, z, 22, spread, spread * 0.8, spread, 0.35);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT, x, y, z, 16, spread * 0.8, spread * 0.6, spread * 0.8, 0.2);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 8, spread * 0.6, spread * 0.6, spread * 0.6, 0.08);
            }
            case 2 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, x, y + 0.6, z, 30, spread, 0.4, spread, 0.18);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE, x, y + 0.3, z, 20, spread, 0.3, spread, 0.06);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, x, y, z, 2, 0.5, 0.2, 0.5, 0.0);
            }
            case 3 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 40, spread, spread, spread, 0.22);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, x, y, z, 40, spread, spread, spread, 0.5);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, x, y, z, 24, spread, 0.5, spread, 0.06);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0.0);
            }
            case 4 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, x, y, z, 2, 0.35, 0.35, 0.35, 0.0);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, x, y, z, 26, spread, spread * 0.8, spread, 0.42);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, x, y, z, 26, spread * 0.9, 0.4, spread * 0.9, 0.02);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 12, spread * 0.7, 0.3, spread * 0.7, 0.02);
            }
            case 5 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 26, spread * 0.9, spread, spread * 0.9, 0.06);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, x, y, z, 30, spread * 0.8, spread * 0.8, spread * 0.8, 0.5);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.WITCH, x, y, z, 18, spread * 0.8, spread * 0.6, spread * 0.8, 0.02);
            }
            case 6 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 26, spread * 0.7, spread * 0.9, spread * 0.7, 0.04);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL, x, y, z, 18, spread * 0.6, spread * 0.8, spread * 0.6, 0.12);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 14, spread * 0.5, spread * 0.9, spread * 0.5, 0.05);
            }
            case 7 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, x, y + 0.5, z, 34, 0.6, 2.2, 0.6, 0.35);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y + 0.5, z, 20, 0.4, 2.0, 0.4, 0.05);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.FIREWORK, x, y + 1.0, z, 14, 0.5, 1.6, 0.5, 0.18);
            }
            case 8 -> {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, x, y + 0.2, z, 26, spread, 0.2, spread, 0.12);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, x, y, z, 4, spread * 0.6, 0.1, spread * 0.6, 0.0);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, x, y, z, 18, spread, 0.2, spread, 0.2);
            }
            default -> {
            }
        }
    }
}
