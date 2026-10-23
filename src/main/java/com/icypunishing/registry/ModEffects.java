package com.icypunishing.registry;

import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
    public static final DeferredHolder<MobEffect, MobEffect> FORTITUDE = com.icylib.init.ModEffects.FORTITUDE;
    public static final DeferredHolder<MobEffect, MobEffect> HYPER_CALCULATION_SPACE = com.icylib.init.ModEffects.HYPER_CALCULATION_SPACE;
    public static final DeferredHolder<MobEffect, MobEffect> CATHERINE_SHIELD = com.icylib.init.ModEffects.CATHERINE_SHIELD;

    public static void register(IEventBus eventBus) {
    }
}