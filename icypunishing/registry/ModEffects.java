package com.icypunishing.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final RegistryObject<MobEffect> FORTITUDE = com.icylib.init.ModEffects.FORTITUDE;
    public static final RegistryObject<MobEffect> HYPER_CALCULATION_SPACE = com.icylib.init.ModEffects.HYPER_CALCULATION_SPACE;
    public static final RegistryObject<MobEffect> CATHERINE_SHIELD = com.icylib.init.ModEffects.CATHERINE_SHIELD;

    public static void register(IEventBus eventBus) {
    }
}