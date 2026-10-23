package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.core.AwarenessItem;
import com.icylib.client.IPortraitProvider;
import com.icylib.client.TooltipPortraitRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = IcyPunishing.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AwarenessTooltipRenderer {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TooltipPortraitRenderer.registerPortraitProvider(stack -> {
                if (stack.getItem() instanceof AwarenessItem) {
                    return (IPortraitProvider) stack.getItem();
                }
                return null;
            });
        });
    }
}
