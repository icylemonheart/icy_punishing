package com.icypunishing.event;

import com.icypunishing.registry.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "icy_punishing")
public class HyperCalculationSpaceHandler {
    private static final double RANGE = 16.0D;
    
    private static final Map<UUID, Integer> playerTickTimers = new HashMap<>();

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (!(entity instanceof Player) && !entity.level().isClientSide) {
            Player nearbyPlayer = getNearbyPlayerWithEffect(entity);
            if (nearbyPlayer != null) {
                UUID playerId = nearbyPlayer.getUUID();
                int timer = playerTickTimers.getOrDefault(playerId, 0);
                
                if (timer % 10 != 0) {
                    event.setCanceled(true);
                }
                
                playerTickTimers.put(playerId, timer + 1);
            }
        }
        
        if (entity instanceof Player player) {
            if (player.hasEffect(ModEffects.HYPER_CALCULATION_SPACE.get())) {
                playerTickTimers.put(player.getUUID(), playerTickTimers.getOrDefault(player.getUUID(), 0) + 1);
            } else {
                playerTickTimers.remove(player.getUUID());
            }
        }
    }

    private static Player getNearbyPlayerWithEffect(LivingEntity entity) {
        for (Player player : entity.level().players()) {
            if (player.hasEffect(ModEffects.HYPER_CALCULATION_SPACE.get())) {
                double distance = player.distanceTo(entity);
                if (distance <= RANGE) {
                    return player;
                }
            }
        }
        return null;
    }
}