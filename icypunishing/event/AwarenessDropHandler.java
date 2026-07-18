package com.icypunishing.event;

import com.icypunishing.config.IcyPunishingConfig;
import com.icypunishing.core.AwarenessItem;
import com.icypunishing.registry.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AwarenessDropHandler {
    
    private static final Random RANDOM = new Random();
    
    private static final List<Item> TWO_STAR_AWARENESSES = new ArrayList<>();
    private static final List<Item> THREE_STAR_AWARENESSES = new ArrayList<>();
    private static final List<Item> FOUR_STAR_AWARENESSES = new ArrayList<>();
    private static final List<Item> FIVE_STAR_AWARENESSES = new ArrayList<>();
    private static final List<Item> SIX_STAR_AWARENESSES = new ArrayList<>();
    
    private static boolean initialized = false;
    
    private static void initAwarenesses() {
        if (initialized) {
            return;
        }
        
        TWO_STAR_AWARENESSES.add(ModItems.MERNIE_AWARENESS_1.get());
        TWO_STAR_AWARENESSES.add(ModItems.MERNIE_AWARENESS_2.get());
        TWO_STAR_AWARENESSES.add(ModItems.MERNIE_AWARENESS_3.get());
        TWO_STAR_AWARENESSES.add(ModItems.MERNIE_AWARENESS_4.get());
        TWO_STAR_AWARENESSES.add(ModItems.MERNIE_AWARENESS_5.get());
        TWO_STAR_AWARENESSES.add(ModItems.MERNIE_AWARENESS_6.get());
        
        THREE_STAR_AWARENESSES.add(ModItems.PAVTY_AWARENESS_1.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAVTY_AWARENESS_2.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAVTY_AWARENESS_3.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAVTY_AWARENESS_4.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAVTY_AWARENESS_5.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAVTY_AWARENESS_6.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAMELIE_AWARENESS_1.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAMELIE_AWARENESS_2.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAMELIE_AWARENESS_3.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAMELIE_AWARENESS_4.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAMELIE_AWARENESS_5.get());
        THREE_STAR_AWARENESSES.add(ModItems.PAMELIE_AWARENESS_6.get());
        
        FOUR_STAR_AWARENESSES.add(ModItems.CURIE_COHET_AWARENESS_1.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CURIE_COHET_AWARENESS_2.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CURIE_COHET_AWARENESS_3.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CURIE_COHET_AWARENESS_4.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CURIE_COHET_AWARENESS_5.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CURIE_COHET_AWARENESS_6.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CUNNINGHAM_AWARENESS_1.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CUNNINGHAM_AWARENESS_2.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CUNNINGHAM_AWARENESS_3.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CUNNINGHAM_AWARENESS_4.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CUNNINGHAM_AWARENESS_5.get());
        FOUR_STAR_AWARENESSES.add(ModItems.CUNNINGHAM_AWARENESS_6.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ALICE_AWARENESS_1.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ALICE_AWARENESS_2.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ALICE_AWARENESS_3.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ALICE_AWARENESS_4.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ALICE_AWARENESS_5.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ALICE_AWARENESS_6.get());
        FOUR_STAR_AWARENESSES.add(ModItems.EDISON_AWARENESS_1.get());
        FOUR_STAR_AWARENESSES.add(ModItems.EDISON_AWARENESS_2.get());
        FOUR_STAR_AWARENESSES.add(ModItems.EDISON_AWARENESS_3.get());
        FOUR_STAR_AWARENESSES.add(ModItems.EDISON_AWARENESS_4.get());
        FOUR_STAR_AWARENESSES.add(ModItems.EDISON_AWARENESS_5.get());
        FOUR_STAR_AWARENESSES.add(ModItems.EDISON_AWARENESS_6.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ARCHIMEDES_AWARENESS_1.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ARCHIMEDES_AWARENESS_2.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ARCHIMEDES_AWARENESS_3.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ARCHIMEDES_AWARENESS_4.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ARCHIMEDES_AWARENESS_5.get());
        FOUR_STAR_AWARENESSES.add(ModItems.ARCHIMEDES_AWARENESS_6.get());
        FOUR_STAR_AWARENESSES.add(ModItems.NIMITZ_AWARENESS_1.get());
        FOUR_STAR_AWARENESSES.add(ModItems.NIMITZ_AWARENESS_2.get());
        FOUR_STAR_AWARENESSES.add(ModItems.NIMITZ_AWARENESS_3.get());
        FOUR_STAR_AWARENESSES.add(ModItems.NIMITZ_AWARENESS_4.get());
        FOUR_STAR_AWARENESSES.add(ModItems.NIMITZ_AWARENESS_5.get());
        FOUR_STAR_AWARENESSES.add(ModItems.NIMITZ_AWARENESS_6.get());
        
        FIVE_STAR_AWARENESSES.add(ModItems.AIFU_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIFU_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIFU_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIFU_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIFU_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIFU_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIKE_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIKE_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIKE_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIKE_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIKE_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.AIKE_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.VOLTAIRE_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.VOLTAIRE_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.VOLTAIRE_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.VOLTAIRE_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.VOLTAIRE_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.VOLTAIRE_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.GORODA_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.GORODA_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.GORODA_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.GORODA_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.GORODA_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.GORODA_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.RICHELIEU_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.RICHELIEU_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.RICHELIEU_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.RICHELIEU_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.RICHELIEU_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.RICHELIEU_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.SAMANDA_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.SAMANDA_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.SAMANDA_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.SAMANDA_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.SAMANDA_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.SAMANDA_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.MOZART_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.MOZART_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.MOZART_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.MOZART_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.MOZART_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.MOZART_AWARENESS_6.get());
        FIVE_STAR_AWARENESSES.add(ModItems.BOUDICA_AWARENESS_1.get());
        FIVE_STAR_AWARENESSES.add(ModItems.BOUDICA_AWARENESS_2.get());
        FIVE_STAR_AWARENESSES.add(ModItems.BOUDICA_AWARENESS_3.get());
        FIVE_STAR_AWARENESSES.add(ModItems.BOUDICA_AWARENESS_4.get());
        FIVE_STAR_AWARENESSES.add(ModItems.BOUDICA_AWARENESS_5.get());
        FIVE_STAR_AWARENESSES.add(ModItems.BOUDICA_AWARENESS_6.get());
        
        SIX_STAR_AWARENESSES.add(ModItems.EINSTEINNA_AWARENESS_1.get());
        SIX_STAR_AWARENESSES.add(ModItems.EINSTEINNA_AWARENESS_2.get());
        SIX_STAR_AWARENESSES.add(ModItems.EINSTEINNA_AWARENESS_3.get());
        SIX_STAR_AWARENESSES.add(ModItems.EINSTEINNA_AWARENESS_4.get());
        SIX_STAR_AWARENESSES.add(ModItems.EINSTEINNA_AWARENESS_5.get());
        SIX_STAR_AWARENESSES.add(ModItems.EINSTEINNA_AWARENESS_6.get());
        SIX_STAR_AWARENESSES.add(ModItems.KANTLINA_AWARENESS_1.get());
        SIX_STAR_AWARENESSES.add(ModItems.KANTLINA_AWARENESS_2.get());
        SIX_STAR_AWARENESSES.add(ModItems.KANTLINA_AWARENESS_3.get());
        SIX_STAR_AWARENESSES.add(ModItems.KANTLINA_AWARENESS_4.get());
        SIX_STAR_AWARENESSES.add(ModItems.KANTLINA_AWARENESS_5.get());
        SIX_STAR_AWARENESSES.add(ModItems.KANTLINA_AWARENESS_6.get());
        SIX_STAR_AWARENESSES.add(ModItems.SHAKESPEARE_AWARENESS_1.get());
        SIX_STAR_AWARENESSES.add(ModItems.SHAKESPEARE_AWARENESS_2.get());
        SIX_STAR_AWARENESSES.add(ModItems.SHAKESPEARE_AWARENESS_3.get());
        SIX_STAR_AWARENESSES.add(ModItems.SHAKESPEARE_AWARENESS_4.get());
        SIX_STAR_AWARENESSES.add(ModItems.SHAKESPEARE_AWARENESS_5.get());
        SIX_STAR_AWARENESSES.add(ModItems.SHAKESPEARE_AWARENESS_6.get());
        SIX_STAR_AWARENESSES.add(ModItems.HEISENBERG_AWARENESS_1.get());
        SIX_STAR_AWARENESSES.add(ModItems.HEISENBERG_AWARENESS_2.get());
        SIX_STAR_AWARENESSES.add(ModItems.HEISENBERG_AWARENESS_3.get());
        SIX_STAR_AWARENESSES.add(ModItems.HEISENBERG_AWARENESS_4.get());
        SIX_STAR_AWARENESSES.add(ModItems.HEISENBERG_AWARENESS_5.get());
        SIX_STAR_AWARENESSES.add(ModItems.HEISENBERG_AWARENESS_6.get());
        SIX_STAR_AWARENESSES.add(ModItems.DARWIN_AWARENESS_1.get());
        SIX_STAR_AWARENESSES.add(ModItems.DARWIN_AWARENESS_2.get());
        SIX_STAR_AWARENESSES.add(ModItems.DARWIN_AWARENESS_3.get());
        SIX_STAR_AWARENESSES.add(ModItems.DARWIN_AWARENESS_4.get());
        SIX_STAR_AWARENESSES.add(ModItems.DARWIN_AWARENESS_5.get());
        SIX_STAR_AWARENESSES.add(ModItems.DARWIN_AWARENESS_6.get());
        
        initialized = true;
    }
    
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        initAwarenesses();
        
        LivingEntity entity = event.getEntity();
        
        if (entity instanceof Player) {
            return;
        }
        
        double maxHealth = entity.getMaxHealth();
        
        if (maxHealth >= 10000) {
            if (RANDOM.nextDouble() < 0.001) {
                dropRandomAwareness(entity, SIX_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < 0.005) {
                dropRandomAwareness(entity, FIVE_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < 0.02) {
                dropRandomAwareness(entity, FOUR_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < 0.08) {
                dropRandomAwareness(entity, THREE_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < 0.2) {
                dropRandomAwareness(entity, TWO_STAR_AWARENESSES);
                return;
            }
        }
        
        if (maxHealth >= IcyPunishingConfig.FIVE_STAR_DROP_HEALTH.get()) {
            if (RANDOM.nextDouble() < IcyPunishingConfig.FIVE_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, FIVE_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < IcyPunishingConfig.FOUR_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, FOUR_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < IcyPunishingConfig.THREE_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, THREE_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < IcyPunishingConfig.TWO_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, TWO_STAR_AWARENESSES);
                return;
            }
        }
        
        if (maxHealth >= IcyPunishingConfig.FOUR_STAR_DROP_HEALTH.get()) {
            if (RANDOM.nextDouble() < IcyPunishingConfig.FOUR_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, FOUR_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < IcyPunishingConfig.THREE_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, THREE_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < IcyPunishingConfig.TWO_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, TWO_STAR_AWARENESSES);
                return;
            }
        }
        
        if (maxHealth >= IcyPunishingConfig.THREE_STAR_DROP_HEALTH.get()) {
            if (RANDOM.nextDouble() < IcyPunishingConfig.THREE_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, THREE_STAR_AWARENESSES);
                return;
            }
            if (RANDOM.nextDouble() < IcyPunishingConfig.TWO_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, TWO_STAR_AWARENESSES);
                return;
            }
        }
        
        if (maxHealth >= IcyPunishingConfig.TWO_STAR_DROP_HEALTH.get()) {
            if (RANDOM.nextDouble() < IcyPunishingConfig.TWO_STAR_DROP_CHANCE.get()) {
                dropRandomAwareness(entity, TWO_STAR_AWARENESSES);
            }
        }
    }
    
    private void dropRandomAwareness(LivingEntity entity, List<Item> awarenesses) {
        if (awarenesses.isEmpty()) {
            return;
        }
        
        Item randomItem = awarenesses.get(RANDOM.nextInt(awarenesses.size()));
        ItemStack stack = new ItemStack(randomItem);
        
        if (randomItem instanceof AwarenessItem awarenessItem) {
            awarenessItem.initRandomStats(stack);
        }
        
        entity.spawnAtLocation(stack);
    }
}