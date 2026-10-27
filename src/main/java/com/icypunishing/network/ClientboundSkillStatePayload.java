package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSkillStatePayload(byte[] orbIds, int energy, int burstTicks,
                                           int fireBuffTicks, boolean holdingSkillWeapon,
                                           boolean combatMode, int rageStacks, int rageTicks,
                                           int dodgeCooldownTicks)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundSkillStatePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "skill_state_sync"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundSkillStatePayload> STREAM_CODEC =
            StreamCodec.ofMember(ClientboundSkillStatePayload::write, ClientboundSkillStatePayload::read);

    private static ClientboundSkillStatePayload read(FriendlyByteBuf buf) {
        byte[] orbIds = buf.readByteArray();
        int energy = buf.readVarInt();
        int burst = buf.readVarInt();
        int fire = buf.readVarInt();
        boolean holding = buf.readBoolean();
        boolean combat = buf.readBoolean();
        int rageStacks = buf.readVarInt();
        int rageTicks = buf.readVarInt();
        int dodgeCd = buf.readVarInt();
        return new ClientboundSkillStatePayload(orbIds, energy, burst, fire, holding, combat,
                rageStacks, rageTicks, dodgeCd);
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeByteArray(orbIds);
        buf.writeVarInt(energy);
        buf.writeVarInt(burstTicks);
        buf.writeVarInt(fireBuffTicks);
        buf.writeBoolean(holdingSkillWeapon);
        buf.writeBoolean(combatMode);
        buf.writeVarInt(rageStacks);
        buf.writeVarInt(rageTicks);
        buf.writeVarInt(dodgeCooldownTicks);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundSkillStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> com.icypunishing.client.ClientSkillCache.update(payload));
    }
}
