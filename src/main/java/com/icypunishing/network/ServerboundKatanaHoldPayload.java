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

/** C2S：左键长按/松开，用于逆闪切、纳刀斩与剑气爆发。 */
public record ServerboundKatanaHoldPayload(boolean pressed) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundKatanaHoldPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "katana_hold"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundKatanaHoldPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, ServerboundKatanaHoldPayload::pressed,
                    ServerboundKatanaHoldPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundKatanaHoldPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                SkillCombatHandler.onAttackHold(sp, payload.pressed());
            }
        });
    }
}
