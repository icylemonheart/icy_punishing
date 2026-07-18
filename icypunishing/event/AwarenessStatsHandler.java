package com.icypunishing.event;

import com.icypunishing.core.AwarenessItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.icylib.init.ModAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.UUID;

@Mod.EventBusSubscriber
public class AwarenessStatsHandler {
    private static final UUID HEALTH_BONUS_UUID = UUID.fromString("f1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID ATTACK_BONUS_UUID = UUID.fromString("b1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID DEFENSE_BONUS_UUID = UUID.fromString("c1b2c3d4-e5f6-7890-abcd-ef1234567890");

    @SubscribeEvent
    public static void onEquipCurio(LivingEquipmentChangeEvent event) {
        ItemStack stack = event.getTo();
        if (stack.getItem() instanceof AwarenessItem awarenessItem) {
            if (!stack.hasTag()) {
                awarenessItem.initRandomStats(stack);
            }
            
            applyStats(event.getEntity(), stack, awarenessItem);
        }
        
        ItemStack oldStack = event.getFrom();
        if (oldStack.getItem() instanceof AwarenessItem oldAwarenessItem) {
            removeStats(event.getEntity(), oldAwarenessItem);
        }
    }

    public static void applyStats(LivingEntity entity, ItemStack stack, AwarenessItem item) {
        if (item.isUpperSlot()) {
            int health = item.getHealth(stack);
            
            if (health > 0) {
                AttributeModifier healthModifier = new AttributeModifier(HEALTH_BONUS_UUID,
                        "Awareness Health Bonus", health, AttributeModifier.Operation.ADDITION);
                addModifier(entity, Attributes.MAX_HEALTH, healthModifier);
            }
        } else {
            int attack = item.getAttack(stack);
            int defense = item.getDefense(stack);
            
            if (attack > 0) {
                AttributeModifier attackModifier = new AttributeModifier(ATTACK_BONUS_UUID,
                        "Awareness Attack Bonus", attack, AttributeModifier.Operation.ADDITION);
                addModifier(entity, Attributes.ATTACK_DAMAGE, attackModifier);
            }
            
            if (defense > 0) {
                AttributeModifier defenseModifier = new AttributeModifier(DEFENSE_BONUS_UUID,
                        "Awareness Defense Bonus", defense, AttributeModifier.Operation.ADDITION);
                addModifier(entity, ModAttributes.DEFENSE.get(), defenseModifier);
            }
        }
    }

    public static void removeStats(LivingEntity entity, AwarenessItem item) {
        if (item.isUpperSlot()) {
            removeModifier(entity, Attributes.MAX_HEALTH, HEALTH_BONUS_UUID);
        } else {
            removeModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_BONUS_UUID);
            removeModifier(entity, ModAttributes.DEFENSE.get(), DEFENSE_BONUS_UUID);
        }
    }

    private static void addModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute, AttributeModifier modifier) {
        var attr = entity.getAttribute(attribute);
        if (attr != null && !attr.hasModifier(modifier)) {
            attr.addTransientModifier(modifier);
        }
    }

    private static void removeModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute, UUID uuid) {
        var attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.removeModifier(uuid);
        }
    }
}