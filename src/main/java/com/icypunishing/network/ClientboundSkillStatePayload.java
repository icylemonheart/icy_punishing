package com.icypunishing.network;

import com.icypunishing.IcyPunishing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** S2C：同步玩家技能状态（信号球、能量与各 Buff 计时）到客户端缓存。 */
public record ClientboundSkillStatePayload(byte[] orbIds, int energy, int burstTicks,
                                           int fireBuffTicks, boolean holdingSkillWeapon,
                                           boolean combatMode, int rageStacks, int rageTicks,
                                           int dodgeCooldownTicks, int dawnBladeTicks,
                                           int lightningStacks, int lightningTicks,
                                           int ultShadowTicks, int dawnExtraTicks,
                                           int ultEnergyCost, int swordAuraTicks,
                                           int swordStacks, int enhanceTicks, int enhanceTier,
                                           int crimsonTicks, int orbBuffStacks, int ultSwordTicks)
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
        int dawnBlade = buf.readVarInt();
        int lightningStacks = buf.readVarInt();
        int lightningTicks = buf.readVarInt();
        int ultShadow = buf.readVarInt();
        int dawnExtra = buf.readVarInt();
        int ultCost = buf.readVarInt();
        int swordAura = buf.readVarInt();
        int swordStacks = buf.readVarInt();
        int enhanceTicks = buf.readVarInt();
        int enhanceTier = buf.readVarInt();
        int crimsonTicks = buf.readVarInt();
        int orbBuffStacks = buf.readVarInt();
        int ultSword = buf.readVarInt();
        return new ClientboundSkillStatePayload(orbIds, energy, burst, fire, holding, combat,
                rageStacks, rageTicks, dodgeCd, dawnBlade, lightningStacks, lightningTicks,
                ultShadow, dawnExtra, ultCost, swordAura, swordStacks, enhanceTicks, enhanceTier,
                crimsonTicks, orbBuffStacks, ultSword);
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
        buf.writeVarInt(dawnBladeTicks);
        buf.writeVarInt(lightningStacks);
        buf.writeVarInt(lightningTicks);
        buf.writeVarInt(ultShadowTicks);
        buf.writeVarInt(dawnExtraTicks);
        buf.writeVarInt(ultEnergyCost);
        buf.writeVarInt(swordAuraTicks);
        buf.writeVarInt(swordStacks);
        buf.writeVarInt(enhanceTicks);
        buf.writeVarInt(enhanceTier);
        buf.writeVarInt(crimsonTicks);
        buf.writeVarInt(orbBuffStacks);
        buf.writeVarInt(ultSwordTicks);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundSkillStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> com.icypunishing.client.ClientSkillCache.update(payload));
    }
}
