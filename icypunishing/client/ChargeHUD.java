package com.icypunishing.client;

import com.icypunishing.event.AwarenessEffectHandler;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "icy_punishing", value = Dist.CLIENT)
public class ChargeHUD {
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        
        Integer ferietCharges = AwarenessEffectHandler.ferietChargeCount.get(player);
        Integer guineyCharges = AwarenessEffectHandler.guineyChargeCount.get(player);
        
        int yOffset = 0;
        
        if (ferietCharges != null && ferietCharges > 0) {
            renderChargeBar(event, "Feriet II", ferietCharges, 5, yOffset);
            yOffset += 20;
        }
        
        if (guineyCharges != null && guineyCharges > 0) {
            renderChargeBar(event, "Guiney", guineyCharges, 5, yOffset);
        }
    }
    
    private static void renderChargeBar(RenderGuiEvent.Pre event, String name, int charges, int maxCharges, int yOffset) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Window window = minecraft.getWindow();
        Font font = minecraft.font;
        
        int screenWidth = window.getGuiScaledWidth();
        
        String text = name + ": " + charges + "/" + maxCharges;
        int textWidth = font.width(text);
        
        int x = screenWidth - textWidth - 10;
        int y = 50 + yOffset;
        
        RenderSystem.enableBlend();
        guiGraphics.fill(x - 2, y - 2, x + textWidth + 2, y + font.lineHeight + 2, 0x80000000);
        guiGraphics.drawString(font, text, x, y, 0xFFD700, false);
        
        int barWidth = 60;
        int barHeight = 4;
        int barX = screenWidth - barWidth - 10;
        int barY = y + font.lineHeight + 4;
        
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x80000000);
        
        float percent = (float) charges / maxCharges;
        guiGraphics.fill(barX, barY, barX + (int)(barWidth * percent), barY + barHeight, 0xFFD700);
        
        RenderSystem.disableBlend();
    }
}