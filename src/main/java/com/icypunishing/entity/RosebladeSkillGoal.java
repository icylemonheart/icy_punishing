package com.icypunishing.entity;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

/**
 * 蔷薇之剑技能体系：烧夷战术、黑暗侵蚀、黑暗剑风、烈焰三段式、地火、影袭、恶魔之吻、一闪。
 * 每个技能带触发距离/阶段/冷却判定，命中判定、粒子特效与音效在固定时间轴上触发。
 */
public class RosebladeSkillGoal extends Goal {
    private static final DustParticleOptions ROSE_DUST =
            new DustParticleOptions(new Vector3f(0.85F, 0.1F, 0.25F), 1.2F);

    enum Skill {
        /** 黑暗剑风：远程黑暗剑气波（P1+） */
        SWORDWIND("swordwind", 18, 100, 4.0, 18.0, 1),
        /** 一闪：快速突刺（P1+） */
        FLASH("flash", 11, 140, 3.0, 10.0, 1),
        /** 烈焰三段式：三段火焰斩击（P2+） */
        FLAME3("flame3", 34, 200, 0.0, 5.0, 2),
        /** 地火：敌人脚下火阵爆炸（P2+） */
        EARTHFIRE("earthfire", 22, 240, 4.0, 16.0, 2),
        /** 恶魔之吻：玫瑰能量阵爆炸（P2+） */
        DEVILSKISS("devilskiss", 24, 280, 4.0, 16.0, 2),
        /** 烧夷战术：战场持续灼烧光环（P2+） */
        BURNAURA("burnaura", 20, 400, 0.0, 7.0, 2),
        /** 影袭：多段影子突刺（P3） */
        SHADOWSTRIKE("shadowstrike", 32, 180, 0.0, 8.0, 3),
        /** 黑暗侵蚀：后撤隐身突进击飞（P3） */
        DARKEROSION("darkerosion", 26, 220, 4.0, 14.0, 3);

        final String anim;
        final int duration;
        final int cooldown;
        final double minRange;
        final double maxRange;
        final int minPhase;

        Skill(String anim, int duration, int cooldown, double minRange, double maxRange, int minPhase) {
            this.anim = anim;
            this.duration = duration;
            this.cooldown = cooldown;
            this.minRange = minRange;
            this.maxRange = maxRange;
            this.minPhase = minPhase;
        }
    }

    private final RosebladeEntity mob;
    private final EnumMap<Skill, Long> nextUseGameTime = new EnumMap<>(Skill.class);
    private final List<Vec3> marks = new ArrayList<>();

    private LivingEntity target;
    private Skill active;
    private int tick;

    public RosebladeSkillGoal(RosebladeEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        if (t == null || !t.isAlive()) {
            return false;
        }
        double dist = mob.distanceTo(t);
        long now = mob.level().getGameTime();
        int phase = mob.getPhase();
        List<Skill> available = new ArrayList<>();
        for (Skill skill : Skill.values()) {
            if (phase < skill.minPhase || dist < skill.minRange || dist > skill.maxRange) {
                continue;
            }
            if (now < nextUseGameTime.getOrDefault(skill, 0L)) {
                continue;
            }
            available.add(skill);
        }
        if (available.isEmpty()) {
            return false;
        }
        this.pendingSkill = available.get(mob.getRandom().nextInt(available.size()));
        return true;
    }

    private Skill pendingSkill;

    @Override
    public boolean canContinueToUse() {
        return active != null && target != null && target.isAlive() && tick < active.duration;
    }

    @Override
    public void start() {
        active = pendingSkill;
        pendingSkill = null;
        target = mob.getTarget();
        tick = 0;
        marks.clear();
        nextUseGameTime.put(active, mob.level().getGameTime() + active.cooldown);
        mob.getNavigation().stop();
        mob.setActionBusy(true);
        mob.triggerAnim("action", active.anim);
    }

    @Override
    public void stop() {
        active = null;
        target = null;
        marks.clear();
        mob.setActionBusy(false);
    }

    @Override
    public void tick() {
        mob.getNavigation().stop();
        if (target != null) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        tick++;
        switch (active) {
            case SWORDWIND -> tickSwordwind();
            case FLASH -> tickFlash();
            case FLAME3 -> tickFlame3();
            case EARTHFIRE -> tickEarthfire();
            case DEVILSKISS -> tickDevilsKiss();
            case BURNAURA -> tickBurnAura();
            case SHADOWSTRIKE -> tickShadowStrike();
            case DARKEROSION -> tickDarkErosion();
        }
    }

    // ---------- 黑暗剑风：直线黑暗剑气 ----------

    private void tickSwordwind() {
        if (tick == 6) {
            playSound(SoundEvents.WITHER_SHOOT, 1.0F, 0.7F);
            Vec3 flat = flatLook();
            Vec3 origin = new Vec3(mob.getX(), mob.getY() + 1.0D, mob.getZ());
            if (mob.level() instanceof ServerLevel server) {
                for (int i = 1; i <= 26; i++) {
                    Vec3 p = origin.add(flat.scale(i * 0.5D));
                    server.sendParticles(ParticleTypes.SQUID_INK, p.x, p.y, p.z, 3, 0.12D, 0.12D, 0.12D, 0.0D);
                }
            }
            for (LivingEntity e : entitiesNear(16.0D)) {
                Vec3 rel = e.position().subtract(mob.position());
                double along = rel.x * flat.x + rel.z * flat.z;
                if (along < 0.5D || along > 14.0D) {
                    continue;
                }
                double perp = Math.abs(rel.x * flat.z - rel.z * flat.x);
                if (perp <= 1.3D) {
                    hurt(e, 8.0F);
                    knock(e, flat.scale(0.5D).add(0, 0.2D, 0));
                }
            }
        }
    }

    // ---------- 一闪：快速突刺 ----------

    private void tickFlash() {
        if (tick == 5) {
            dashTowardTarget(1.5D);
            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.4F);
        }
        if (tick == 7) {
            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.5F);
            Vec3 flat = flatLook();
            if (mob.level() instanceof ServerLevel server) {
                Vec3 hand = new Vec3(mob.getX(), mob.getY() + 1.2D, mob.getZ()).add(flat.scale(1.5D));
                server.sendParticles(ParticleTypes.CRIT, hand.x, hand.y, hand.z, 12, 0.3D, 0.3D, 0.3D, 0.1D);
            }
            for (LivingEntity e : entitiesNear(5.0D)) {
                Vec3 rel = e.position().subtract(mob.position());
                double along = rel.x * flat.x + rel.z * flat.z;
                if (along < -0.3D || along > 3.5D) {
                    continue;
                }
                double perp = Math.abs(rel.x * flat.z - rel.z * flat.x);
                if (perp <= 0.9D) {
                    hurt(e, 10.0F);
                    knock(e, flat.scale(0.4D));
                }
            }
        }
    }

    // ---------- 烈焰三段式 ----------

    private void tickFlame3() {
        if (tick == 6 || tick == 14) {
            flameSlash(6.0F, 3.5D);
        }
        if (tick == 24) {
            flameSlash(8.0F, 4.0D);
        }
    }

    private void flameSlash(float damage, double radius) {
        playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.1F);
        Vec3 flat = flatLook();
        if (mob.level() instanceof ServerLevel server) {
            Vec3 c = mob.position().add(flat.scale(radius * 0.6D)).add(0, 1.0D, 0);
            server.sendParticles(ParticleTypes.FLAME, c.x, c.y, c.z, 25, 0.6D, 0.4D, 0.6D, 0.02D);
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, c.x, c.y, c.z, 4, 0.4D, 0.3D, 0.4D, 0.0D);
        }
        for (LivingEntity e : frontalSector(radius, 0.35D)) {
            hurt(e, damage);
            e.igniteForSeconds(3.0F);
            knock(e, flat.scale(0.35D));
        }
    }

    // ---------- 地火：多点火阵延迟爆炸 ----------

    private void tickEarthfire() {
        if (tick == 7) {
            playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.8F);
            marks.clear();
            marks.add(target.position());
            for (int i = 0; i < 2; i++) {
                marks.add(target.position().add(
                        (mob.getRandom().nextDouble() - 0.5D) * 5.0D, 0.0D,
                        (mob.getRandom().nextDouble() - 0.5D) * 5.0D));
            }
        }
        if (tick >= 8 && tick <= 15 && mob.level() instanceof ServerLevel server) {
            // 预警火圈
            for (Vec3 mark : marks) {
                for (int i = 0; i < 8; i++) {
                    double a = i * Math.PI * 2.0D / 8.0D;
                    server.sendParticles(ParticleTypes.FLAME,
                            mark.x + Math.cos(a) * 2.0D, mark.y + 0.2D, mark.z + Math.sin(a) * 2.0D,
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (tick == 16) {
            for (Vec3 mark : marks) {
                explodeAt(mark, 2.5D, 10.0F, ParticleTypes.FLAME);
            }
            marks.clear();
        }
    }

    // ---------- 恶魔之吻：玫瑰能量阵 ----------

    private void tickDevilsKiss() {
        if (tick == 8) {
            playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
            marks.clear();
            marks.add(target.position());
        }
        if (tick >= 9 && tick <= 17 && mob.level() instanceof ServerLevel server) {
            // 玫瑰色能量环预警
            for (Vec3 mark : marks) {
                for (int i = 0; i < 12; i++) {
                    double a = i * Math.PI * 2.0D / 12.0D + tick * 0.15D;
                    server.sendParticles(ROSE_DUST,
                            mark.x + Math.cos(a) * 3.0D, mark.y + 0.3D, mark.z + Math.sin(a) * 3.0D,
                            1, 0.0D, 0.05D, 0.0D, 0.0D);
                }
            }
        }
        if (tick == 18) {
            playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.4F);
            for (Vec3 mark : marks) {
                if (mob.level() instanceof ServerLevel server) {
                    server.sendParticles(ROSE_DUST, mark.x, mark.y + 1.0D, mark.z, 50, 1.5D, 1.0D, 1.5D, 0.05D);
                    server.sendParticles(ParticleTypes.EXPLOSION, mark.x, mark.y + 0.5D, mark.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                for (LivingEntity e : entitiesInRadius(mark, 3.0D)) {
                    hurt(e, 12.0F);
                    e.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 12, 0));
                    knock(e, new Vec3(0, 0.55D, 0));
                }
            }
            marks.clear();
        }
    }

    // ---------- 烧夷战术：灼烧光环 ----------

    private void tickBurnAura() {
        if (tick == 6) {
            playSound(SoundEvents.BLAZE_AMBIENT, 1.5F, 0.9F);
            mob.igniteBurnAura(160);
            if (mob.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.FLAME, mob.getX(), mob.getY() + 1.0D, mob.getZ(),
                        40, 1.0D, 0.6D, 1.0D, 0.05D);
                server.sendParticles(ParticleTypes.LAVA, mob.getX(), mob.getY() + 0.5D, mob.getZ(),
                        10, 0.8D, 0.3D, 0.8D, 0.0D);
            }
        }
    }

    // ---------- 影袭：多段影子突刺 ----------

    private void tickShadowStrike() {
        if (tick == 4) {
            dashTowardTarget(0.9D);
        }
        if (tick == 4 || tick == 7 || tick == 10 || tick == 13 || tick == 16) {
            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.9F, 1.4F + tick * 0.02F);
            shadowHit(3.0F);
        }
        if (tick == 26) {
            // 终结一击：击退
            playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
            Vec3 flat = flatLook();
            for (LivingEntity e : frontalSector(3.2D, 0.3D)) {
                hurt(e, 6.0F);
                knock(e, flat.scale(0.8D).add(0, 0.45D, 0));
            }
        }
    }

    private void shadowHit(float damage) {
        if (mob.level() instanceof ServerLevel server) {
            Vec3 c = target.position().add(0, 1.0D, 0);
            server.sendParticles(ParticleTypes.SQUID_INK, c.x, c.y, c.z, 6, 0.3D, 0.4D, 0.3D, 0.02D);
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, c.x, c.y, c.z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
        }
        for (LivingEntity e : frontalSector(3.0D, 0.2D)) {
            hurt(e, damage);
        }
    }

    // ---------- 黑暗侵蚀：隐身突进击飞 ----------

    private void tickDarkErosion() {
        if (tick == 2) {
            // 后撤隐身
            mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 26, 0, false, false, false));
            Vec3 away = mob.position().subtract(target.position());
            Vec3 flat = new Vec3(away.x, 0, away.z).normalize();
            mob.setDeltaMovement(flat.scale(0.6D).add(0, 0.3D, 0));
            mob.hurtMarked = true;
            playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.8F);
            inkBurst(30);
        }
        if (tick == 11 && target != null) {
            // 现身于目标面前
            Vec3 to = target.position().subtract(mob.position());
            Vec3 flat = new Vec3(to.x, 0, to.z).normalize();
            Vec3 pos = target.position().subtract(flat.scale(1.4D));
            float yaw = (float) (Math.atan2(-flat.x, -flat.z) * 180.0D / Math.PI);
            mob.moveTo(pos.x, pos.y, pos.z, yaw, 0.0F);
            mob.getNavigation().stop();
            playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.2F);
            inkBurst(30);
        }
        if (tick == 13) {
            playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.9F);
            Vec3 flat = flatLook();
            for (LivingEntity e : frontalSector(3.2D, 0.3D)) {
                hurt(e, 12.0F);
                knock(e, flat.scale(0.3D).add(0, 0.65D, 0));
            }
            if (mob.level() instanceof ServerLevel server) {
                Vec3 c = mob.position().add(flat.scale(1.5D)).add(0, 1.2D, 0);
                server.sendParticles(ParticleTypes.CRIT, c.x, c.y, c.z, 15, 0.3D, 0.4D, 0.3D, 0.1D);
            }
        }
    }

    // ---------- 通用工具 ----------

    private Vec3 flatLook() {
        Vec3 look = mob.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        double len = flat.length();
        return len < 1.0E-4D ? new Vec3(0, 0, -1) : flat.scale(1.0D / len);
    }

    private List<LivingEntity> entitiesNear(double radius) {
        AABB box = mob.getBoundingBox().inflate(radius, 2.0D, radius);
        return mob.level().getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != mob);
    }

    private List<LivingEntity> entitiesInRadius(Vec3 center, double radius) {
        AABB box = AABB.ofSize(center, radius * 2.0D, 3.0D, radius * 2.0D);
        return mob.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && e != mob && e.position().distanceTo(center) <= radius);
    }

    /** 前方扇形：半径 radius，dot 阈值 minDot */
    private List<LivingEntity> frontalSector(double radius, double minDot) {
        Vec3 flat = flatLook();
        List<LivingEntity> result = new ArrayList<>();
        for (LivingEntity e : entitiesNear(radius)) {
            Vec3 rel = e.position().subtract(mob.position());
            double dist = Math.sqrt(rel.x * rel.x + rel.z * rel.z);
            if (dist > radius) {
                continue;
            }
            double dot = dist < 1.0E-4D ? 1.0D : (rel.x * flat.x + rel.z * flat.z) / dist;
            if (dist <= 0.9D || dot > minDot) {
                result.add(e);
            }
        }
        return result;
    }

    private void hurt(LivingEntity e, float damage) {
        e.hurt(mob.damageSources().mobAttack(mob), damage);
    }

    private void knock(LivingEntity e, Vec3 velocity) {
        e.setDeltaMovement(e.getDeltaMovement().add(velocity));
        e.hurtMarked = true;
    }

    private void dashTowardTarget(double speed) {
        if (target == null) {
            return;
        }
        Vec3 to = target.position().subtract(mob.position());
        Vec3 flat = new Vec3(to.x, 0, to.z);
        if (flat.length() > 1.0E-4D) {
            mob.setDeltaMovement(flat.normalize().scale(speed).add(0, 0.1D, 0));
            mob.hurtMarked = true;
        }
    }

    private void explodeAt(Vec3 center, double radius, float damage, net.minecraft.core.particles.ParticleOptions flame) {
        playSoundAt(center, SoundEvents.GENERIC_EXPLODE.value(), 0.8F, 1.2F);
        if (mob.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.5D, center.z, 2, 0.3D, 0.2D, 0.3D, 0.0D);
            server.sendParticles(flame, center.x, center.y + 0.8D, center.z, 30, 0.8D, 0.6D, 0.8D, 0.05D);
            server.sendParticles(ParticleTypes.LAVA, center.x, center.y + 0.5D, center.z, 8, 0.5D, 0.3D, 0.5D, 0.0D);
        }
        for (LivingEntity e : entitiesInRadius(center, radius)) {
            hurt(e, damage);
            e.igniteForSeconds(3.0F);
            knock(e, new Vec3(0, 0.35D, 0));
        }
    }

    private void inkBurst(int count) {
        if (mob.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SQUID_INK, mob.getX(), mob.getY() + 1.0D, mob.getZ(),
                    count, 0.4D, 0.8D, 0.4D, 0.05D);
        }
    }

    private void playSound(net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        mob.level().playSound(null, mob.blockPosition(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    private void playSoundAt(Vec3 pos, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        mob.level().playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.HOSTILE, volume, pitch);
    }
}
