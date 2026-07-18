package com.icypunishing.client;

import com.icylib.client.upgrade.UpgradeScreenRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = "icy_punishing", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class IcyPunishingClient {
    public IcyPunishingClient() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        UpgradeScreenRendererManager.setRenderer(new UpgradeScreenRenderer());
    }
}
