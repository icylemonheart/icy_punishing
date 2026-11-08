package com.icypunishing.event;

import com.icypunishing.entity.TianlangBladeEntity;
import com.icypunishing.item.ObjKatanaItem;
import com.icypunishing.registry.ModEntities;
import com.icypunishing.skill.SkillUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 无专属角色武器的武器技能：九字之定落雷、天狼星副手悬浮攻击。
 * 无铭的会心伤害加成由 ObjKatanaItem 的属性修饰符实现。
 */
public class WeaponSkillHandler {

    private static final String KYUUJI = "kyuuji";
    private static final String TIANLANG = "tianlang";

    private static final float KYUUJI_CHANCE = 0.20f;
    private static final float KYUUJI_LIGHTNING_RATIO = 0.50f;
    private static final int KYUUJI_COOLDOWN_TICKS = 60;

    private static final double TIANLANG_RANGE = 12.0;
    private static final int TIANLANG_INTERVAL_TICKS = 20;
    private static final float TIANLANG_RATIO = 1.00f;

    /** 玩家当前普攻目标，用于天狼星优先攻击 */
    private final Map<UUID, UUID> attackTargets = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lightningCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> tianlangCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, TianlangBladeEntity> blades = new ConcurrentHashMap<>();

    /** 记录普攻目标，并处理九字之定的落雷 */
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity target) || target == player) {
            return;
        }
        if (SkillCombatHandler.isBonus(player)) {
            return;
        }
        attackTargets.put(player.getUUID(), target.getUUID());

        ItemStack mainHand = player.getMainHandItem();
        if (!isWeapon(mainHand, KYUUJI) || !target.isAlive()) {
            return;
        }
        long now = player.level().getGameTime();
        Long cooldownEnd = lightningCooldowns.get(player.getUUID());
        if (cooldownEnd != null && now < cooldownEnd) {
            return;
        }
        if (player.getRandom().nextFloat() >= KYUUJI_CHANCE) {
            return;
        }
        lightningCooldowns.put(player.getUUID(), now + KYUUJI_COOLDOWN_TICKS);
        strikeLightning(player, target);
    }

    /** 九字之定落雷：仅视觉，不点燃、不破坏方块，伤害单独结算 */
    private void strikeLightning(ServerPlayer player, LivingEntity target) {
        ServerLevel level = player.serverLevel();
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.setVisualOnly(true);
            bolt.moveTo(target.getX(), target.getY(), target.getZ());
            level.addFreshEntity(bolt);
        }
        level.playSound(null, target.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS, 0.8f, 1.4f);
        // 竖直雷柱 + 地面冲击波特效
        SkillUtils.sendVfx(player, (byte) 7, target.getX(), target.getY(), target.getZ(),
                0.0f, 3.0f, 0.9f, 0xFFE98A, 420);
        SkillUtils.sendVfx(player, (byte) 8, target.getX(), target.getY() + 0.1, target.getZ(),
                0.0f, 2.6f, 0.5f, 0xFFE98A, 320);
        boolean guard = SkillCombatHandler.beginBonus(player);
        try {
            target.hurt(player.damageSources().playerAttack(player),
                    SkillUtils.attackOf(player) * KYUUJI_LIGHTNING_RATIO);
        } finally {
            if (guard) {
                SkillCombatHandler.endBonus(player);
            }
        }
    }

    /** 天狼星：副手持有时自动攻击 */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!isWeapon(player.getOffhandItem(), TIANLANG)) {
            removeBlade(player);
            tianlangCooldowns.remove(player.getUUID());
            return;
        }
        ensureBlade(player);
        long now = player.level().getGameTime();
        Long cooldownEnd = tianlangCooldowns.get(player.getUUID());
        if (cooldownEnd != null && now < cooldownEnd) {
            return;
        }
        LivingEntity target = findTarget(player);
        if (target == null) {
            return;
        }
        tianlangCooldowns.put(player.getUUID(), now + TIANLANG_INTERVAL_TICKS);
        sendBladeSlash(player, target);
        boolean guard = SkillCombatHandler.beginBonus(player);
        try {
            target.hurt(player.damageSources().playerAttack(player),
                    SkillUtils.attackOf(player) * TIANLANG_RATIO);
        } finally {
            if (guard) {
                SkillCombatHandler.endBonus(player);
            }
        }
    }

    /** 确保天狼星悬浮体存在 */
    private void ensureBlade(ServerPlayer player) {
        TianlangBladeEntity blade = blades.get(player.getUUID());
        if (blade != null && blade.isAlive() && blade.level() == player.level()) {
            return;
        }
        TianlangBladeEntity spawned = new TianlangBladeEntity(ModEntities.TIANLANG_BLADE.get(), player.serverLevel());
        spawned.setOwnerId(player.getUUID());
        spawned.setOwnerEntity(player.getId());
        spawned.setDisplayStack(player.getOffhandItem().copy());
        spawned.moveTo(player.position());
        player.serverLevel().addFreshEntity(spawned);
        blades.put(player.getUUID(), spawned);
    }

    private void removeBlade(ServerPlayer player) {
        TianlangBladeEntity blade = blades.remove(player.getUUID());
        if (blade != null) {
            blade.discard();
        }
    }

    private LivingEntity findTarget(ServerPlayer player) {
        // 优先玩家当前目标；否则取最近的敌对生物
        UUID lastTarget = attackTargets.get(player.getUUID());
        if (lastTarget != null) {
            var entity = player.serverLevel().getEntity(lastTarget);
            if (entity instanceof LivingEntity living && living.isAlive() && living != player
                    && player.distanceToSqr(living) <= TIANLANG_RANGE * TIANLANG_RANGE) {
                return living;
            }
        }
        AABB box = player.getBoundingBox().inflate(TIANLANG_RANGE);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (entity == player || !entity.isAlive() || !(entity instanceof Enemy)) {
                continue;
            }
            double distance = player.distanceToSqr(entity);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }
        return nearest;
    }

    private void sendBladeSlash(ServerPlayer player, LivingEntity target) {
        SkillUtils.sendVfx(player, (byte) 1, target.getX(), target.getY(0.6),
                target.getZ(), 0.0f, 2.2f, 0.9f, 0xFFF2A8, 200);
        SkillUtils.sendVfx(player, (byte) 5, target.getX(), target.getY(0.6),
                target.getZ(), 0.0f, 1.2f, 0.35f, 0xFFD24A, 220);
        player.level().playSound(null, target.blockPosition(), SoundEvents.TRIDENT_THROW.value(),
                SoundSource.PLAYERS, 0.6f, 1.6f);
    }

    private static boolean isWeapon(ItemStack stack, String modelId) {
        return stack.getItem() instanceof ObjKatanaItem katana && modelId.equals(katana.getModelId());
    }

    /** 下线时清理缓存 */
    @SubscribeEvent
    public void onPlayerLoggedOut(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        attackTargets.remove(id);
        lightningCooldowns.remove(id);
        tianlangCooldowns.remove(id);
        TianlangBladeEntity blade = blades.remove(id);
        if (blade != null) {
            blade.discard();
        }
    }
}
