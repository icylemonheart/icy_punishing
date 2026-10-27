package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSkillVfxPayload(byte shape, byte style, float x, float y, float z,
                                         float yaw, float pitch,
                                         float radius, float thickness,
                                         int color, int lifeMillis)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundSkillVfxPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "skill_vfx"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundSkillVfxPayload> STREAM_CODEC =
            StreamCodec.ofMember(ClientboundSkillVfxPayload::write, ClientboundSkillVfxPayload::read);

    private static ClientboundSkillVfxPayload read(FriendlyByteBuf buf) {
        return new ClientboundSkillVfxPayload(buf.readByte(), buf.readByte(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(),
                buf.readInt(), buf.readVarInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeByte(shape);
        buf.writeByte(style);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeFloat(radius);
        buf.writeFloat(thickness);
        buf.writeInt(color);
        buf.writeVarInt(lifeMillis);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundSkillVfxPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> com.icypunishing.client.SkillVfxClient.add(payload));
    }
}
