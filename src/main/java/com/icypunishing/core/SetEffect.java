package com.icypunishing.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public abstract class SetEffect {
    protected final ResourceLocation id;
    protected final int requiredCount;

    public SetEffect(ResourceLocation id, int requiredCount) {
        this.id = id;
        this.requiredCount = requiredCount;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public abstract void apply(LivingEntity entity, int equippedCount);

    public void tick(LivingEntity entity, int equippedCount) {}

    public void remove(LivingEntity entity) {}

    public float getMinionDamageBonus() {
        return 1.0f;
    }

    protected void addModifierIfAbsent(AttributeInstance instance, AttributeModifier modifier) {
        if (!instance.hasModifier(modifier.id())) {
            instance.addTransientModifier(modifier);
        }
    }

    protected void removeModifierIfPresent(AttributeInstance instance, ResourceLocation id) {
        if (instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }
}