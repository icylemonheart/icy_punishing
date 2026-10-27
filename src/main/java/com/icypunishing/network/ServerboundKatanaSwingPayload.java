package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import com.icypunishing.event.KatanaSweepHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端左键点击太刀时发送：请求服务端按刀身扫掠区域结算伤害。
 */
public record ServerboundKatanaSwingPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundKatanaSwingPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "katana_swing"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundKatanaSwingPayload> STREAM_CODEC =
            StreamCodec.unit(new ServerboundKatanaSwingPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundKatanaSwingPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                KatanaSweepHandler.onSwing(sp);
            }
        });
    }
}
