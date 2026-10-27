package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.network.ServerboundSkillKeyPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = IcyPunishing.MODID, value = Dist.CLIENT)
public final class SkillKeyInputHandler {

    private SkillKeyInputHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            drain();
            return;
        }
        if (!ClientSkillCache.holdingSkillWeapon) {
            drain();
            return;
        }
        while (SkillKeyMappings.COMBAT.consumeClick()) {
            PacketDistributor.sendToServer(new ServerboundSkillKeyPayload((byte) 0));
        }
        while (SkillKeyMappings.ULT.consumeClick()) {
            PacketDistributor.sendToServer(new ServerboundSkillKeyPayload((byte) 1));
        }
        if (ClientSkillCache.combatMode) {
            KeyMapping[] slots = minecraft.options.keyHotbarSlots;
            for (int i = 0; i < slots.length && i < 8; i++) {
                while (slots[i].consumeClick()) {
                    PacketDistributor.sendToServer(new ServerboundSkillKeyPayload((byte) (i + 2)));
                }
            }
            for (int i = 8; i < slots.length; i++) {
                while (slots[i].consumeClick()) {
                }
            }
            // 战斗态接管 Shift：触发闪避并禁止潜行
            while (minecraft.options.keyShift.consumeClick()) {
                PacketDistributor.sendToServer(new ServerboundSkillKeyPayload((byte) 10));
            }
            if (minecraft.player != null) {
                minecraft.player.setShiftKeyDown(false);
            }
        }
    }

    private static void drain() {
        while (SkillKeyMappings.COMBAT.consumeClick()) {
        }
        while (SkillKeyMappings.ULT.consumeClick()) {
        }
    }
}
