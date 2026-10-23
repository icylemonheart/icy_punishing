package com.icypunishing.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RepairMechanicAttackGoal extends Goal {
    private static final int DAMAGE_TICK = 10;
    private static final int TOTAL_TICKS = 20;
    private static final double ATTACK_RANGE = 2.5D;

    private final RepairMechanicEntity mob;
    private LivingEntity target;
    private int tick;
    private boolean damaged;

    public RepairMechanicAttackGoal(RepairMechanicEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        return t != null && t.isAlive() && mob.distanceTo(t) <= ATTACK_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && tick < TOTAL_TICKS;
    }

    @Override
    public void start() {
        target = mob.getTarget();
        tick = 0;
        damaged = false;
        mob.getNavigation().stop();
        mob.triggerAnim("attack", "swing");
    }

    @Override
    public void tick() {
        mob.getNavigation().stop();
        if (target != null) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        tick++;
        if (!damaged && tick >= DAMAGE_TICK) {
            damaged = true;
            swing();
        }
    }

    @Override
    public void stop() {
        target = null;
    }

    private void swing() {
        Vec3 look = mob.getLookAngle();
        double lookLen = Math.max(1.0E-4D, Math.sqrt(look.x * look.x + look.z * look.z));
        double lx = look.x / lookLen;
        double lz = look.z / lookLen;
        AABB box = mob.getBoundingBox().inflate(ATTACK_RANGE + 0.5D, 0.5D, ATTACK_RANGE + 0.5D);
        for (LivingEntity entity : mob.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && e != mob)) {
            double dx = entity.getX() - mob.getX();
            double dz = entity.getZ() - mob.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > ATTACK_RANGE + 0.5D) {
                continue;
            }
            double dot = dist < 1.0E-4D ? 1.0D : (dx * lx + dz * lz) / dist;
            if (dist <= 0.9D || dot > 0.5D) {
                mob.doHurtTarget(entity);
            }
        }
    }
}
