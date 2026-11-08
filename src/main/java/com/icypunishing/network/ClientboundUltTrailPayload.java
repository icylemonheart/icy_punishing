package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** S2C：大招刀光控制，action=0 残留刀光转红，action=1 清除全部刀光。 */
public record ClientboundUltTrailPayload(byte action) implements CustomPacketPayload {

    public static final byte ACTION_RECOLOR = 0;
    public static final byte ACTION_CLEAR = 1;

    public static final CustomPacketPayload.Type<ClientboundUltTrailPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "ult_trail"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundUltTrailPayload> STREAM_CODEC =
            StreamCodec.ofMember(ClientboundUltTrailPayload::write, ClientboundUltTrailPayload::read);

    private static ClientboundUltTrailPayload read(FriendlyByteBuf buf) {
        return new ClientboundUltTrailPayload(buf.readByte());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeByte(action);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundUltTrailPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.action() == ACTION_RECOLOR) {
                com.icypunishing.client.SkillVfxClient.recolorUltTrails(0xFF3B4D);
            } else {
                com.icypunishing.client.SkillVfxClient.clearUltTrails();
            }
        });
    }
}
