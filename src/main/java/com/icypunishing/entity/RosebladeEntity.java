package com.icypunishing.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 蔷薇之剑 BOSS 实体：三阶段、Boss 血条、GeckoLib 动画控制器、烧夷光环。
 */
public class RosebladeEntity extends Monster implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    private static final ResourceLocation PHASE3_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "roseblade_phase3_speed");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.icy_punishing.roseblade"),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    /** 烧夷战术光环剩余 tick */
    private int burnAuraTicks;
    /** 是否正在播放技能动作（抑制受击动画打断技能演出） */
    private boolean actionBusy;
    /** 下一次允许普通攻击的游戏时间 */
    private long nextMeleeGameTime;
    private int lastPhase = 1;

    public RosebladeEntity(EntityType<? extends RosebladeEntity> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RosebladeSkillGoal(this));
        this.goalSelector.addGoal(2, new RosebladeAttackGoal(this));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 1.0D, 2.2F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ---------- 战斗阶段 ----------

    /** 阶段：P1 > 70% HP，P2 40%~70%，P3 < 40% */
    public int getPhase() {
        float frac = this.getHealth() / this.getMaxHealth();
        if (frac > 0.7F) {
            return 1;
        }
        return frac > 0.4F ? 2 : 3;
    }

    private void onPhaseChange(int phase) {
        Level level = this.level();
        level.playSound(null, this.blockPosition(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.2F, 1.2F);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 1.0D, this.getZ(),
                    40, 0.5D, 0.8D, 0.5D, 0.05D);
            server.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1.0D, this.getZ(),
                    30, 0.5D, 0.8D, 0.5D, 0.05D);
        }
        if (phase == 3) {
            AttributeInstance inst = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (inst != null && inst.getModifier(PHASE3_SPEED_ID) == null) {
                inst.addTransientModifier(new AttributeModifier(PHASE3_SPEED_ID, 0.2D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    // ---------- 烧夷光环 ----------

    public void igniteBurnAura(int ticks) {
        this.burnAuraTicks = ticks;
    }

    private void tickBurnAura() {
        if (this.burnAuraTicks <= 0) {
            return;
        }
        this.burnAuraTicks--;
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        // 火焰环粒子
        if (this.burnAuraTicks % 5 == 0) {
            double r = 4.0D;
            for (int i = 0; i < 16; i++) {
                double a = i * Math.PI * 2.0D / 16.0D;
                server.sendParticles(ParticleTypes.FLAME,
                        this.getX() + Math.cos(a) * r, this.getY() + 0.3D, this.getZ() + Math.sin(a) * r,
                        1, 0.0D, 0.05D, 0.0D, 0.0D);
            }
        }
        // 每秒灼烧周围敌人
        if (this.burnAuraTicks % 20 == 0) {
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(4.5D), e -> e.isAlive() && e != this)) {
                entity.hurt(this.damageSources().mobAttack(this), 2.0F);
                entity.igniteForSeconds(2.0F);
            }
        }
    }

    // ---------- 状态标志 ----------

    public boolean isActionBusy() {
        return actionBusy;
    }

    public void setActionBusy(boolean actionBusy) {
        this.actionBusy = actionBusy;
    }

    public boolean isMeleeReady() {
        return this.level().getGameTime() >= this.nextMeleeGameTime;
    }

    public void setMeleeCooldown(int ticks) {
        this.nextMeleeGameTime = this.level().getGameTime() + ticks;
    }

    // ---------- 生命周期 ----------

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            int phase = this.getPhase();
            if (phase != this.lastPhase) {
                this.lastPhase = phase;
                this.onPhaseChange(phase);
            }
            this.tickBurnAura();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean ok = super.hurt(source, amount);
        if (ok && !this.level().isClientSide && this.isAlive() && amount >= 6.0F
                && !this.actionBusy && this.random.nextFloat() < 0.5F) {
            this.triggerAnim("action", "hurt");
        }
        return ok;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            this.triggerAnim("action", "death");
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // ---------- Boss 血条 ----------

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    // ---------- 音效 ----------

    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    // ---------- GeckoLib ----------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 过渡帧数调大，避免待机/行走/技能之间硬切
        controllers.add(new AnimationController<>(this, "movement", 8, state ->
                state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE)));
        AnimationController<RosebladeEntity> action =
                new AnimationController<>(this, "action", 5, state -> PlayState.STOP);
        for (String name : new String[]{"attack", "hurt", "death", "swordwind", "flash",
                "earthfire", "devilskiss", "flame3", "darkerosion", "shadowstrike", "burnaura"}) {
            action.triggerableAnim(name, RawAnimation.begin().thenPlay(name));
        }
        controllers.add(action);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
