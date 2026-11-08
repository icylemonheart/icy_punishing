package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.item.ObjKatanaItem;
import com.icypunishing.network.ServerboundKatanaHoldPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 手持太刀时检测左键长按：按下与松开各发一次，由服务端按持续时间结算蓄力技能。
 */
@EventBusSubscriber(modid = IcyPunishing.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KatanaHoldHandler {

    private static boolean wasDown;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        boolean down = player != null && mc.level != null && mc.screen == null
                && mc.options.keyAttack.isDown()
                && player.getMainHandItem().getItem() instanceof ObjKatanaItem
                && ClientSkillCache.combatMode;
        if (down == wasDown) {
            return;
        }
        wasDown = down;
        PacketDistributor.sendToServer(new ServerboundKatanaHoldPayload(down));
    }
}
