package com.icypunishing.event;

import com.icylib.damage.DamageTypeHelper;
import com.icylib.damage.IcyDamageTypes;
import com.icypunishing.skill.CharacterSkillSet;
import com.icypunishing.skill.SignalOrb;
import com.icypunishing.skill.SkillSetRegistry;
import com.icypunishing.skill.SkillState;
import com.icypunishing.skill.SkillUpgradeHelper;
import com.icypunishing.skill.SkillUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkillCombatHandler {

    private static final Set<UUID> BONUS_PROCESSING = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, CharacterSkillSet> LAST_SET = new ConcurrentHashMap<>();
    private static final ResourceLocation CAPTAIN_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "honglian_captain");
    private static final float CLASS_EXTRA_BONUS = 1.20f;
    private static final long COMBAT_TOGGLE_COOLDOWN = 40L;

    public static void onKey(ServerPlayer player, byte key) {
        CharacterSkillSet set = SkillSetRegistry.find(player);
        if (set == null) {
            return;
        }
        SkillState state = SkillUtils.state(player);
        // 大招锁定期间屏蔽技能输入
        if (state.getUltLockTicks() > 0) {
            return;
        }
        switch (key) {
            case 0 -> {
                long now = player.level().getGameTime();
                if (now < state.getCombatCooldownUntil()) {
                    return;
                }
                boolean entering = !state.isCombatMode();
                state.setCombatMode(entering);
                state.setCombatCooldownUntil(now + COMBAT_TOGGLE_COOLDOWN);
                if (entering) {
                    // 终阶解放：首次进入战斗获得三个信号球
                    for (int i = 0; i < 3; i++) {
                        state.addOrb(SignalOrb.random(player.getRandom()));
                    }
                }
                SkillUtils.sync(player);
            }
            case 1 -> {
                if (state.isCombatMode()) {
                    set.onUltimateKey(player, state);
                }
            }
            case 10 -> {
                if (state.isCombatMode()) {
                    set.onDodgeKey(player, state);
                }
            }
            default -> {
                if (key >= 2 && key <= 9 && state.isCombatMode()) {
                    set.onOrbConsumeKey(player, key - 2, state);
                }
            }
        }
    }

    /** 长按/松开左键，交给技能组处理 */
    public static void onAttackHold(ServerPlayer player, boolean pressed) {
        CharacterSkillSet set = SkillSetRegistry.find(player);
        if (set == null) {
            return;
        }
        SkillState state = SkillUtils.state(player);
        if (!state.isCombatMode() && pressed) {
            return;
        }
        set.onAttackHold(player, state, pressed);
        SkillUtils.sync(player);
    }

    @SubscribeEvent
    public void onMeleeDamage(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (BONUS_PROCESSING.contains(player.getUUID())) {
            return;
        }
        if (!source.is(DamageTypes.PLAYER_ATTACK)) {
            return;
        }
        CharacterSkillSet set = SkillSetRegistry.find(player);
        if (set == null) {
            return;
        }
        LivingEntity target = event.getEntity();
        set.onMeleeHit(player, target, event.getNewDamage(), SkillUtils.state(player));
    }

    /** 霸体：深红之渊闪身/突进期间免疫击退。 */
    @SubscribeEvent
    public void onKnockback(net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (SkillUtils.state(player).getSuperArmorTicks() > 0) {
            event.setCanceled(true);
        }
    }

    /** 大招锁定期间禁止左键攻击与挖掘 */
    @SubscribeEvent
    public void onAttackEntity(net.neoforged.neoforge.event.entity.player.AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && SkillUtils.state(player).getUltLockTicks() > 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player
                && SkillUtils.state(player).getUltLockTicks() > 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (BONUS_PROCESSING.contains(player.getUUID())) {
            return;
        }
        if (!source.is(DamageTypes.PLAYER_ATTACK)) {
            return;
        }
        CharacterSkillSet set = SkillSetRegistry.find(player);
        if (set == null) {
            return;
        }
        SkillState state = SkillUtils.state(player);
        var container = event.getContainer();
        float dmg = container.getNewDamage();
        if (SkillUtils.ascension(player) >= 3) {
            dmg *= (1.0f + set.sssPlusNormalBonus());
        }
        container.setNewDamage(set.modifyNormalAttackDamage(player, state, dmg));
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        CharacterSkillSet set = SkillSetRegistry.find(player);
        boolean holding = set != null;

        // ConcurrentHashMap 不允许 null value：未持有武器时用 remove 清理旧条目
        CharacterSkillSet prev = set != null
                ? LAST_SET.put(player.getUUID(), set)
                : LAST_SET.remove(player.getUUID());
        boolean wasHolding = prev != null;
        if (holding != wasHolding) {
            if (!holding && prev != null) {
                prev.onUnequip(player, SkillUtils.state(player));
            }
            SkillState state = SkillUtils.state(player);
            if (!holding && state.isCombatMode()) {
                state.setCombatMode(false);
            }
            SkillUtils.sync(player);
        }

        SkillUtils.tickScheduled(player);

        // 太刀大招锁定：固定坐标，屏蔽位移
        SkillState lockState = SkillUtils.state(player);
        if (lockState.getUltLockTicks() > 0) {
            lockState.tickUltLock();
            player.teleportTo(lockState.getUltLockX(), lockState.getUltLockY(), lockState.getUltLockZ());
            player.setDeltaMovement(0.0, 0.0, 0.0);
            player.setSprinting(false);
            player.resetAttackStrengthTicker();
            player.hurtMarked = true;
        }

        if (set != null) {
            set.onPlayerTick(player, SkillUtils.state(player));
        }

        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null) {
            boolean has = instance.getModifier(CAPTAIN_ID) != null;
            if (holding && !has) {
                instance.addTransientModifier(new AttributeModifier(CAPTAIN_ID, 0.10,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            } else if (!holding && has) {
                instance.removeModifier(CAPTAIN_ID);
            }
        }
    }

    /** 标记该玩家正在造成非普攻伤害，防止再次触发普攻机制 */
    public static boolean beginBonus(ServerPlayer player) {
        return BONUS_PROCESSING.add(player.getUUID());
    }

    public static void endBonus(ServerPlayer player) {
        BONUS_PROCESSING.remove(player.getUUID());
    }

    public static boolean isBonus(ServerPlayer player) {
        return BONUS_PROCESSING.contains(player.getUUID());
    }

    public static void dealBonusPhysical(ServerPlayer player, LivingEntity target, float ratio) {
        if (!BONUS_PROCESSING.add(player.getUUID())) {
            return;
        }
        try {
            float dmg = SkillUtils.attackOf(player) * ratio * CLASS_EXTRA_BONUS;
            CharacterSkillSet set = SkillSetRegistry.find(player);
            if (set != null) {
                dmg = set.modifySkillDamage(player, SkillUtils.state(player), dmg);
            }
            dmg *= SkillUpgradeHelper.leapMultiplier(player);
            target.hurt(player.damageSources().playerAttack(player), dmg);
        } finally {
            BONUS_PROCESSING.remove(player.getUUID());
        }
    }

    public static void dealBonusFire(ServerPlayer player, LivingEntity target, float ratio) {
        if (!BONUS_PROCESSING.add(player.getUUID())) {
            return;
        }
        try {
            float dmg = SkillUtils.attackOf(player) * ratio * CLASS_EXTRA_BONUS;
            CharacterSkillSet set = SkillSetRegistry.find(player);
            if (set != null) {
                dmg = set.modifySkillDamage(player, SkillUtils.state(player), dmg);
            }
            dmg *= SkillUpgradeHelper.leapMultiplier(player);
            DamageSource fire = DamageTypeHelper.createDamageSource(player, IcyDamageTypes.FIRE);
            target.hurt(fire != null ? fire : player.damageSources().onFire(), dmg);
        } finally {
            BONUS_PROCESSING.remove(player.getUUID());
        }
    }

    public static void dealBonusLightning(ServerPlayer player, LivingEntity target, float ratio) {
        if (!BONUS_PROCESSING.add(player.getUUID())) {
            return;
        }
        try {
            float dmg = SkillUtils.attackOf(player) * ratio * CLASS_EXTRA_BONUS;
            CharacterSkillSet set = SkillSetRegistry.find(player);
            if (set != null) {
                dmg = set.modifySkillDamage(player, SkillUtils.state(player), dmg);
            }
            dmg *= SkillUpgradeHelper.leapMultiplier(player);
            DamageSource lightning = DamageTypeHelper.createDamageSource(player, IcyDamageTypes.LIGHT);
            target.hurt(lightning != null ? lightning : player.damageSources().lightningBolt(), dmg);
        } finally {
            BONUS_PROCESSING.remove(player.getUUID());
        }
    }
}
