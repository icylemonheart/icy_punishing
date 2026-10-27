package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.item.ObjKatanaItem;
import com.icypunishing.network.ServerboundKatanaSwingPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 手持太刀时拦截原版左键攻击（准星单点射线），改由服务端按刀身扫掠结算。
 */
@EventBusSubscriber(modid = IcyPunishing.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KatanaClientAttackHandler {

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }
        if (player.isUsingItem() || player.isHandsBusy()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ObjKatanaItem)) {
            return;
        }

        // 取消原版攻击（事件取消后手上仍会播放原版挥手动画）
        event.setCanceled(true);
        player.resetAttackStrengthTicker();
        PacketDistributor.sendToServer(new ServerboundKatanaSwingPayload());
    }
}
