package com.icypunishing.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ProximityEffect extends SetEffect {
    private final double range;
    private final boolean isMelee;
    private final boolean isDamageBoost;
    private final double percentage;
    private static final Map<UUID, Set<ProximityEffect>> ACTIVE_EFFECTS = new HashMap<>();
    private static boolean registered = false;

    public ProximityEffect(ResourceLocation id, int requiredCount, double range, boolean isMelee, boolean isDamageBoost, double percentage) {
        super(id, requiredCount);
        this.range = range;
        this.isMelee = isMelee;
        this.isDamageBoost = isDamageBoost;
        this.percentage = percentage;
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(new ProximityEventListener());
            registered = true;
        }
    }

    @Override
    public void apply(LivingEntity entity, int equippedCount) {
        if (equippedCount >= getRequiredCount()) {
            ACTIVE_EFFECTS.computeIfAbsent(entity.getUUID(), k -> new HashSet<>()).add(this);
        }
    }

    @Override
    public void remove(LivingEntity entity) {
        ACTIVE_EFFECTS.computeIfPresent(entity.getUUID(), (k, effects) -> {
            effects.remove(this);
            return effects.isEmpty() ? null : effects;
        });
    }

    public double getRange() {
        return range;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public boolean isDamageBoost() {
        return isDamageBoost;
    }

    public double getPercentage() {
        return percentage;
    }

    private static class ProximityEventListener {
        @SubscribeEvent
        public void onDamage(LivingDamageEvent event) {
            LivingEntity target = event.getEntity();
            LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;
            
            if (attacker != null) {
                Set<ProximityEffect> attackerEffects = ACTIVE_EFFECTS.get(attacker.getUUID());
                if (attackerEffects != null) {
                    for (ProximityEffect attackerEffect : attackerEffects) {
                        if (attackerEffect.isDamageBoost()) {
                            double distance = attacker.distanceTo(target);
                            boolean inRange = attackerEffect.isMelee() ? distance <= attackerEffect.getRange() : distance > attackerEffect.getRange();
                            if (inRange) {
                                float damage = event.getAmount();
                                event.setAmount((float) (damage * (1 + attackerEffect.getPercentage() / 100)));
                            }
                        }
                    }
                }
                
                Set<ProximityEffect> targetEffects = ACTIVE_EFFECTS.get(target.getUUID());
                if (targetEffects != null) {
                    for (ProximityEffect targetEffect : targetEffects) {
                        if (!targetEffect.isDamageBoost()) {
                            double distance = target.distanceTo(attacker);
                            boolean inRange = targetEffect.isMelee() ? distance <= targetEffect.getRange() : distance > targetEffect.getRange();
                            if (inRange) {
                                float damage = event.getAmount();
                                event.setAmount((float) (damage * (1 - targetEffect.getPercentage() / 100)));
                            }
                        }
                    }
                }
            }
        }
    }
}