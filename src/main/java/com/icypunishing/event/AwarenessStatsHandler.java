package com.icypunishing.event;

import com.icypunishing.core.AwarenessItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.icylib.init.ModAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

public class AwarenessStatsHandler {
    private static final ResourceLocation HEALTH_BONUS_ID = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_health_bonus");
    private static final ResourceLocation ATTACK_BONUS_ID = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_attack_bonus");
    private static final ResourceLocation DEFENSE_BONUS_ID = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_defense_bonus");

    @SubscribeEvent
    public void onEquipCurio(LivingEquipmentChangeEvent event) {
        ItemStack stack = event.getTo();
        if (stack.getItem() instanceof AwarenessItem awarenessItem) {
            if (!stack.has(DataComponents.CUSTOM_DATA)) {
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
                AttributeModifier healthModifier = new AttributeModifier(HEALTH_BONUS_ID, health, AttributeModifier.Operation.ADD_VALUE);
                addModifier(entity, Attributes.MAX_HEALTH, healthModifier);
            }
        } else {
            int attack = item.getAttack(stack);
            int defense = item.getDefense(stack);
            if (attack > 0) {
                AttributeModifier attackModifier = new AttributeModifier(ATTACK_BONUS_ID, attack, AttributeModifier.Operation.ADD_VALUE);
                addModifier(entity, Attributes.ATTACK_DAMAGE, attackModifier);
            }
            if (defense > 0) {
                AttributeModifier defenseModifier = new AttributeModifier(DEFENSE_BONUS_ID, defense, AttributeModifier.Operation.ADD_VALUE);
                addModifier(entity, ModAttributes.DEFENSE, defenseModifier);
            }
        }
    }

    public static void removeStats(LivingEntity entity, AwarenessItem item) {
        if (item.isUpperSlot()) {
            removeModifier(entity, Attributes.MAX_HEALTH, HEALTH_BONUS_ID);
        } else {
            removeModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_BONUS_ID);
            removeModifier(entity, ModAttributes.DEFENSE, DEFENSE_BONUS_ID);
        }
    }

    private static void addModifier(LivingEntity entity, Holder<Attribute> attribute, AttributeModifier modifier) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr != null && !attr.hasModifier(modifier.id())) {
            attr.addTransientModifier(modifier);
        }
    }

    private static void removeModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.removeModifier(id);
        }
    }
}
