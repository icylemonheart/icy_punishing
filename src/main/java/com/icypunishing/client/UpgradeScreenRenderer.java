package com.icypunishing.client;

import com.icylib.client.upgrade.IUpgradeScreenRenderer;
import com.icylib.menu.UpgradeMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UpgradeScreenRenderer implements IUpgradeScreenRenderer {
    private ResourceLocation[] animationFrames;
    private int currentFrame = 0;
    private int frameCount = 0;
    private boolean hasAnimation = false;

    @Override
    public void init(AbstractContainerScreen<UpgradeMenu> screen) {
        frameCount = loadAnimationFrames();
        hasAnimation = frameCount > 0;
    }

    private int loadAnimationFrames() {
        int count = 0;
        ResourceLocation testFrame = ResourceLocation.fromNamespaceAndPath("icy_punishing", "textures/gui/upgrade_frame_000.png");

        if (Minecraft.getInstance().getResourceManager().getResource(testFrame).isPresent()) {
            count = 1;
            while (count < 1024) {
                ResourceLocation frame = ResourceLocation.fromNamespaceAndPath("icy_punishing",
                    String.format("textures/gui/upgrade_frame_%03d.png", count));
                if (Minecraft.getInstance().getResourceManager().getResource(frame).isEmpty()) {
                    break;
                }
                count++;
            }
        }

        if (count > 0) {
            animationFrames = new ResourceLocation[count];
            for (int i = 0; i < count; i++) {
                animationFrames[i] = ResourceLocation.fromNamespaceAndPath("icy_punishing",
                    String.format("textures/gui/upgrade_frame_%03d.png", i));
            }
        }

        return count;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick) {
        if (hasAnimation && animationFrames != null && animationFrames.length > 0) {
            guiGraphics.blit(animationFrames[currentFrame], x, y, 0, 0, width, height, width, height);
        } else {
            guiGraphics.fill(x, y, x + width, y + height, 0x80000000);
        }

        guiGraphics.fill(x + 54, y + 80, x + 122, y + 134, 0x80000000);
    }

    @Override
    public void renderAdditional(GuiGraphics guiGraphics, AbstractContainerScreen<UpgradeMenu> screen, float partialTick, int mouseX, int mouseY) {
        if (hasAnimation) {
            currentFrame = (currentFrame + 1) % frameCount;
        }
    }
}
