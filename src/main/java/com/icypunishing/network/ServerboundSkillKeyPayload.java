package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import com.icypunishing.event.SkillCombatHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSkillKeyPayload(byte key) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSkillKeyPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "skill_key"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundSkillKeyPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BYTE, ServerboundSkillKeyPayload::key,
                    ServerboundSkillKeyPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundSkillKeyPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                SkillCombatHandler.onKey(sp, payload.key());
            }
        });
    }
}
