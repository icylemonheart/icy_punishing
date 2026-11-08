package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/** S2C：通知客户端某玩家开始播放太刀大招模型动画，持续 durationTicks。 */
public record ClientboundUltStartPayload(UUID playerId, int durationTicks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundUltStartPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "ult_start"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundUltStartPayload> STREAM_CODEC =
            StreamCodec.ofMember(ClientboundUltStartPayload::write, ClientboundUltStartPayload::read);

    private static ClientboundUltStartPayload read(FriendlyByteBuf buf) {
        return new ClientboundUltStartPayload(buf.readUUID(), buf.readVarInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeVarInt(durationTicks);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundUltStartPayload payload, IPayloadContext context) {
        context.enqueueWork(() ->
                com.icypunishing.client.ult.UltPlayerAnimation.start(payload.playerId(), payload.durationTicks()));
    }
}
