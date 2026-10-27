package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = IcyPunishing.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ServerboundSkillKeyPayload.TYPE, ServerboundSkillKeyPayload.STREAM_CODEC,
                ServerboundSkillKeyPayload::handle);
        registrar.playToServer(ServerboundKatanaSwingPayload.TYPE, ServerboundKatanaSwingPayload.STREAM_CODEC,
                ServerboundKatanaSwingPayload::handle);
        registrar.playToClient(ClientboundSkillStatePayload.TYPE, ClientboundSkillStatePayload.STREAM_CODEC,
                ClientboundSkillStatePayload::handle);
        registrar.playToClient(ClientboundSkillVfxPayload.TYPE, ClientboundSkillVfxPayload.STREAM_CODEC,
                ClientboundSkillVfxPayload::handle);
    }
}
