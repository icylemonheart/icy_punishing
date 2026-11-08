package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.skill.SignalOrb;
import com.icypunishing.skill.SkillState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.List;

@EventBusSubscriber(modid = IcyPunishing.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SignalOrbHUD {

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "signal_orb_hud");

    private static final ResourceLocation ICON_RED = skillIcon("honglian/red");
    private static final ResourceLocation ICON_YELLOW = skillIcon("honglian/yellow");
    private static final ResourceLocation ICON_BLUE = skillIcon("honglian/blue");
    private static final ResourceLocation ICON_ULT = skillIcon("honglian/ult");
    private static final ResourceLocation ICON_DAWN_RED = skillIcon("yingda/red");
    private static final ResourceLocation ICON_DAWN_YELLOW = skillIcon("yingda/yellow");
    private static final ResourceLocation ICON_DAWN_BLUE = skillIcon("yingda/blue");
    private static final ResourceLocation ICON_DAWN_ULT = skillIcon("yingda/ult");
    private static final ResourceLocation ICON_ABYSS_RED = skillIcon("hongying/red");
    private static final ResourceLocation ICON_ABYSS_YELLOW = skillIcon("hongying/yellow");
    private static final ResourceLocation ICON_ABYSS_BLUE = skillIcon("hongying/blue");
    private static final ResourceLocation ICON_ABYSS_SWORD = skillIcon("hongying/sword");
    private static final ResourceLocation ICON_ABYSS_ULT = skillIcon("hongying/ult");
    private static boolean dawnIcons;
    private static boolean abyssIcons;

    private static int clientBurstTicks;
    private static int clientFireTicks;
    private static int clientRageStacks;
    private static int clientRageTicks;
    private static int clientDodgeCd;
    private static int clientDawnBladeTicks;
    private static int clientLightningStacks;
    private static int clientLightningTicks;
    private static int clientUltShadowTicks;
    private static int clientDawnExtraTicks;
    private static int clientSwordAuraTicks;
    private static int clientSwordStacks;
    private static int clientEnhanceTicks;
    private static int clientCrimsonTicks;
    private static int clientOrbBuffStacks;
    private static int clientUltSwordTicks;
    private static int clientUltCost = SkillState.MAX_ENERGY;

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_ID, SignalOrbHUD::render);
    }

    private static ResourceLocation skillIcon(String name) {
        return ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, name);
    }

    private static void blitIcon(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int size) {
        graphics.blitSprite(sprite, x, y, size, size);
    }

    private static ResourceLocation iconFor(SignalOrb orb) {
        return switch (orb) {
            case RED -> abyssIcons ? ICON_ABYSS_RED : dawnIcons ? ICON_DAWN_RED : ICON_RED;
            case YELLOW -> abyssIcons ? ICON_ABYSS_YELLOW : dawnIcons ? ICON_DAWN_YELLOW : ICON_YELLOW;
            case BLUE -> abyssIcons ? ICON_ABYSS_BLUE : dawnIcons ? ICON_DAWN_BLUE : ICON_BLUE;
            case SWORD -> ICON_ABYSS_SWORD;
        };
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!ClientSkillCache.holdingSkillWeapon || !ClientSkillCache.combatMode) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        List<SignalOrb> orbs = ClientSkillCache.orbsSnapshot();
        int energy = ClientSkillCache.energy();
        clientBurstTicks = ClientSkillCache.burstTicks();
        clientFireTicks = ClientSkillCache.fireBuffTicks();
        clientRageStacks = ClientSkillCache.rageStacks();
        clientRageTicks = ClientSkillCache.rageTicks();
        clientDodgeCd = ClientSkillCache.dodgeCooldownTicks();
        clientDawnBladeTicks = ClientSkillCache.dawnBladeTicks();
        clientLightningStacks = ClientSkillCache.lightningStacks();
        clientLightningTicks = ClientSkillCache.lightningTicks();
        clientUltShadowTicks = ClientSkillCache.ultShadowTicks();
        clientDawnExtraTicks = ClientSkillCache.dawnExtraTicks();
        clientSwordAuraTicks = ClientSkillCache.swordAuraTicks();
        clientSwordStacks = ClientSkillCache.swordStacks();
        clientEnhanceTicks = ClientSkillCache.enhanceTicks();
        clientCrimsonTicks = ClientSkillCache.crimsonTicks();
        clientOrbBuffStacks = ClientSkillCache.orbBuffStacks();
        clientUltSwordTicks = ClientSkillCache.ultSwordTicks();
        clientUltCost = ClientSkillCache.ultEnergyCost();
        String modelId = minecraft.player.getMainHandItem().getItem()
                instanceof com.icypunishing.item.ObjKatanaItem katana ? katana.getModelId() : "";
        dawnIcons = "yingda".equals(modelId);
        abyssIcons = "hongying".equals(modelId);

        int width = graphics.guiWidth();
        Font font = minecraft.font;

        int slotSize = 18;
        int gap = 2;
        int storedSize = 10;
        int storedGap = 2;

        int mainWidth = SkillState.HOTBAR_SLOTS * slotSize + (SkillState.HOTBAR_SLOTS - 1) * gap;
        int storedWidth = SkillState.STORED_SLOTS * storedSize + (SkillState.STORED_SLOTS - 1) * storedGap;
        int x0 = (width - mainWidth) / 2;
        int y0 = graphics.guiHeight() - 95;

        RenderSystem.enableBlend();

        Component combatLabel = Component.translatable("hud.icy_punishing.combat");
        graphics.drawString(font, combatLabel, x0, y0 - font.lineHeight - 2, 0xFFFF7A2F, false);

        int storedX = (width - storedWidth) / 2;
        int storedY = y0;
        Component storedLabel = Component.translatable("hud.icy_punishing.stored");
        graphics.drawString(font, storedLabel, storedX - font.width(storedLabel) - 4,
                storedY + (storedSize - font.lineHeight) / 2 + 1, 0xFF9AA4B2, false);
        for (int i = 0; i < SkillState.STORED_SLOTS; i++) {
            int sx = storedX + i * (storedSize + storedGap);
            int index = SkillState.HOTBAR_SLOTS + i;
            graphics.fill(sx, storedY, sx + storedSize, storedY + storedSize, 0x40000000);
            if (index < orbs.size()) {
                blitIcon(graphics, iconFor(orbs.get(index)), sx, storedY, storedSize);
            }
        }

        int mainY = storedY + storedSize + 3;
        for (int i = 0; i < SkillState.HOTBAR_SLOTS; i++) {
            int x = x0 + i * (slotSize + gap);
            graphics.fill(x, mainY, x + slotSize, mainY + slotSize, 0x50000000);
            if (i < orbs.size()) {
                blitIcon(graphics, iconFor(orbs.get(i)), x, mainY, slotSize);
            }
            Component index = Component.literal(Integer.toString(i + 1));
            int numX = x + (slotSize - font.width(index)) / 2;
            graphics.drawString(font, index, numX, mainY + slotSize + 1,
                    i < orbs.size() ? 0xFFFFFFFF : 0xFF808080, false);
        }

        int barY = mainY + slotSize + font.lineHeight + 4;
        int barHeight = 5;
        boolean ready = energy >= clientUltCost;
        int iconSize = 14;
        int iconX = x0 - iconSize - 4;
        int iconY = barY + (barHeight - iconSize) / 2;
        blitIcon(graphics, abyssIcons ? ICON_ABYSS_ULT : dawnIcons ? ICON_DAWN_ULT : ICON_ULT,
                iconX, iconY, iconSize);
        if (ready) {
            graphics.renderOutline(iconX - 1, iconY - 1, iconSize + 2, iconSize + 2, 0xFFFFD700);
        }

        graphics.fill(x0, barY, x0 + mainWidth, barY + barHeight, 0x80000000);
        float ratio = energy / (float) Math.max(1, clientUltCost);
        if (ratio > 1.0f) {
            ratio = 1.0f;
        }
        int fill = (int) (mainWidth * ratio);
        graphics.fill(x0, barY, x0 + fill, barY + barHeight, ready ? 0xFFFFD700 : 0xFFFF7A2F);
        if (ready) {
            Component text = Component.translatable("hud.icy_punishing.ult_ready");
            graphics.drawString(font, text, x0 + mainWidth + 6, barY - 1, 0xFF55FFFF, false);
        }

        int infoY = barY + barHeight + 4;
        if (clientBurstTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.burst",
                    String.format("%.1f", clientBurstTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFFF5555, false);
            infoY += font.lineHeight + 2;
        }
        if (clientFireTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.fire",
                    String.format("%.1f", clientFireTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFFFAA00, false);
            infoY += font.lineHeight + 2;
        }
        if (clientRageTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.rage",
                    clientRageStacks, 5, String.format("%.1f", clientRageTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFFFC24A, false);
            infoY += font.lineHeight + 2;
        }
        if (clientDodgeCd > 0) {
            Component text = Component.translatable("hud.icy_punishing.dodge_cd",
                    String.format("%.1f", clientDodgeCd / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFF4AD8FF, false);
            infoY += font.lineHeight + 2;
        }
        if (clientDawnBladeTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.dawn_blade",
                    String.format("%.1f", clientDawnBladeTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFA8E6FF, false);
            infoY += font.lineHeight + 2;
        }
        if (clientLightningTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.lightning_buff",
                    clientLightningStacks, 4, String.format("%.1f", clientLightningTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFF7AC8FF, false);
            infoY += font.lineHeight + 2;
        }
        if (clientUltShadowTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.ult_extra",
                    String.format("%.1f", clientUltShadowTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFD9F4FF, false);
            infoY += font.lineHeight + 2;
        }
        if (clientDawnExtraTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.dawn_extra",
                    String.format("%.1f", clientDawnExtraTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFF9FEFFF, false);
            infoY += font.lineHeight + 2;
        }
        if (clientSwordAuraTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.sword_aura",
                    String.format("%.1f", clientSwordAuraTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFFF5C6E, false);
            infoY += font.lineHeight + 2;
        }
        if (clientSwordStacks > 0) {
            Component text = Component.translatable("hud.icy_punishing.sword_stacks",
                    clientSwordStacks, 2);
            graphics.drawString(font, text, x0, infoY, 0xFFFF9AA8, false);
            infoY += font.lineHeight + 2;
        }
        if (clientEnhanceTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.enhance",
                    String.format("%.1f", clientEnhanceTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFF6EC8FF, false);
            infoY += font.lineHeight + 2;
        }
        if (clientCrimsonTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.crimson",
                    String.format("%.1f", clientCrimsonTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFFF7A7A, false);
            infoY += font.lineHeight + 2;
        }
        if (clientOrbBuffStacks > 0) {
            Component text = Component.translatable("hud.icy_punishing.orb_buff",
                    clientOrbBuffStacks, 10);
            graphics.drawString(font, text, x0, infoY, 0xFFFFB3C1, false);
            infoY += font.lineHeight + 2;
        }
        if (clientUltSwordTicks > 0) {
            Component text = Component.translatable("hud.icy_punishing.ult_sword",
                    String.format("%.1f", clientUltSwordTicks / 20.0f));
            graphics.drawString(font, text, x0, infoY, 0xFFFFD1D9, false);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
    }
}
