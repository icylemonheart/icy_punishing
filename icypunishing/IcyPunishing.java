package com.icypunishing;

import com.icypunishing.config.IcyPunishingConfig;
import com.icypunishing.event.AwarenessDropHandler;
import com.icypunishing.event.AwarenessEffectHandler;
import com.icypunishing.registry.ModAwarenessSlots;
import com.icypunishing.registry.ModCreativeTabs;
import com.icypunishing.registry.ModEffects;
import com.icypunishing.registry.ModEntities;
import com.icypunishing.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(IcyPunishing.MODID)
public class IcyPunishing {
    public static final String MODID = "icy_punishing";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IcyPunishing() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModAwarenessSlots.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new AwarenessEffectHandler());
        MinecraftForge.EVENT_BUS.register(new AwarenessDropHandler());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IcyPunishingConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Icy Punishing loaded successfully!");
        LOGGER.info("Awareness slots registered: 6");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Icy Punishing server starting!");
    }
}