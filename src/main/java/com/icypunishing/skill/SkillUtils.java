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
        CharacterSkillSet set = SkillSetRegistry.find(player);
        int ultCost = set != null ? set.ultimateEnergyCost() : SkillState.MAX_ENERGY;
        PacketDistributor.sendToPlayer(player, new ClientboundSkillStatePayload(
                ids, state.getEnergy(), state.getBurstTicks(), state.getFireBuffTicks(),
                holding, state.isCombatMode(), state.getRageStacks(), state.getRageTicks(),
                state.getDodgeCooldownTicks(), state.getDawnBladeTicks(),
                state.getLightningStacks(), state.getLightningTicks(),
                state.getUltShadowTicks(), state.getDawnExtraTicks(), ultCost,
                state.getSwordAuraTicks(), state.getSwordStacks(), state.getEnhanceTicks(),
                state.getEnhanceTier(), state.getCrimsonTicks(), state.getOrbBuffStacks(),
                state.getUltSwordTicks()));
    }

    public static int ascension(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ObjKatanaItem katana) {
            return katana.getAscension(stack);
        }
        return 0;
    }

    public static int leap(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ObjKatanaItem katana) {
            return katana.getLeap(stack);
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

    /** 按 tick 排定的演出（仅视觉与音效，不延迟伤害结算）。 */
    private static final java.util.Map<java.util.UUID, java.util.Queue<Scheduled>> SCHEDULED =
            new java.util.concurrent.ConcurrentHashMap<>();

    private record Scheduled(long due, java.util.function.Consumer<ServerPlayer> action) {
    }

    public static void schedule(ServerPlayer player, int delayTicks,
                                java.util.function.Consumer<ServerPlayer> action) {
        long due = player.level().getGameTime() + delayTicks;
        SCHEDULED.computeIfAbsent(player.getUUID(), k -> new java.util.concurrent.ConcurrentLinkedQueue<>())
                .add(new Scheduled(due, action));
    }

    public static void tickScheduled(ServerPlayer player) {
        java.util.Queue<Scheduled> queue = SCHEDULED.get(player.getUUID());
        if (queue == null) {
            return;
        }
        long now = player.level().getGameTime();
        java.util.List<Scheduled> pending = new ArrayList<>();
        Scheduled action;
        while ((action = queue.poll()) != null) {
            if (action.due() <= now) {
                if (!player.isRemoved() && player.isAlive()) {
                    action.action().accept(player);
                }
            } else {
                pending.add(action);
            }
        }
        pending.forEach(queue::add);
        if (queue.isEmpty()) {
            SCHEDULED.remove(player.getUUID());
        }
    }

    public static void clearScheduled(ServerPlayer player) {
        SCHEDULED.remove(player.getUUID());
    }

    /** 广播太刀大招开始，客户端据此驱动玩家模型动画。 */
    public static void sendUltStart(ServerPlayer player, int durationTicks) {
        com.icypunishing.network.ClientboundUltStartPayload payload =
                new com.icypunishing.network.ClientboundUltStartPayload(player.getUUID(), durationTicks);
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other.distanceToSqr(player) < 4096.0) {
                PacketDistributor.sendToPlayer(other, payload);
            }
        }
    }

    /** 广播刀光状态切换（0 = 白转红，1 = 全部清除）。 */
    public static void sendUltTrail(ServerPlayer player, byte action) {
        com.icypunishing.network.ClientboundUltTrailPayload payload =
                new com.icypunishing.network.ClientboundUltTrailPayload(action);
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other.distanceToSqr(player) < 4096.0) {
                PacketDistributor.sendToPlayer(other, payload);
            }
        }
    }

    public static void sendVfx(ServerPlayer player, byte shape, double x, double y, double z,
                               float pitchOffset, float radius, float thickness, int color, int lifeMillis) {
        sendVfx(player, shape, x, y, z, pitchOffset, radius, thickness, color, lifeMillis, (byte) 0);
    }

    /** 允许显式指定朝向（线/环等需要任意方向时使用） */
    public static void sendVfx(ServerPlayer player, byte shape, double x, double y, double z,
                               float yaw, float pitch, float radius, float thickness, int color, int lifeMillis,
                               byte style) {
        com.icypunishing.network.ClientboundSkillVfxPayload payload =
                new com.icypunishing.network.ClientboundSkillVfxPayload(shape, style,
                        (float) x, (float) y, (float) z,
                        yaw, pitch, radius, thickness, color, lifeMillis, 0);
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other.distanceToSqr(x, y, z) < 4096.0) {
                PacketDistributor.sendToPlayer(other, payload);
            }
        }
    }

    public static void sendVfx(ServerPlayer player, byte shape, double x, double y, double z,
                               float pitchOffset, float radius, float thickness, int color, int lifeMillis,
                               byte style) {
        com.icypunishing.network.ClientboundSkillVfxPayload payload =
                new com.icypunishing.network.ClientboundSkillVfxPayload(shape, style,
                        (float) x, (float) y, (float) z,
                        player.getYRot(), player.getXRot() + pitchOffset, radius, thickness, color, lifeMillis, 0);
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other.distanceToSqr(x, y, z) < 4096.0) {
                PacketDistributor.sendToPlayer(other, payload);
            }
        }
    }

    /** 跟随实体的特效：客户端每帧按该实体位置与朝向重建，用于必杀刀阵等长时间演出。 */
    public static void sendVfxFollow(ServerPlayer player, int followId, byte shape,
                                     float radius, float thickness, int color, int lifeMillis, byte style) {
        com.icypunishing.network.ClientboundSkillVfxPayload payload =
                new com.icypunishing.network.ClientboundSkillVfxPayload(shape, style,
                        (float) player.getX(), (float) player.getY(), (float) player.getZ(),
                        player.getYRot(), player.getXRot(), radius, thickness, color, lifeMillis, followId);
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other.distanceToSqr(player) < 4096.0) {
                PacketDistributor.sendToPlayer(other, payload);
            }
        }
    }

}
