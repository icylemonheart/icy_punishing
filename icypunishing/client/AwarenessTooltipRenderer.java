package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.core.AwarenessItem;
import com.icylib.client.IPortraitProvider;
import com.icylib.client.TooltipPortraitRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IcyPunishing.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AwarenessTooltipRenderer {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        TooltipPortraitRenderer.registerPortraitProvider(stack -> {
            if (stack.getItem() instanceof AwarenessItem) {
                return (IPortraitProvider) stack.getItem();
            }
            return null;
        });
    }
}
