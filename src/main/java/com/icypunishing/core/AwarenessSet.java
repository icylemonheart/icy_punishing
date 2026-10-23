package com.icypunishing.core;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.ArrayList;
import java.util.List;

public class AwarenessSet {
    private final ResourceLocation id;
    private final AwarenessItem[] items = new AwarenessItem[6];
    private final List<SetEffect> effects = new ArrayList<>();
    private ResourceLocation portraitLocation;
    private ResourceLocation[] portraitLocations = new ResourceLocation[6];
    private boolean hasMultiplePortraits = false;
    private AwarenessStats stats;
    private int rank = 4;

    public AwarenessSet(ResourceLocation id) {
        this.id = id;
    }

    public void setRank(int rank) { this.rank = rank; }
    public int getRank() { return rank; }
    public void setPortraitLocation(ResourceLocation location) { this.portraitLocation = location; }
    public ResourceLocation getPortraitLocation() { return portraitLocation; }

    public void setPortraitLocations(ResourceLocation slot1, ResourceLocation slot2, ResourceLocation slot3) {
        this.portraitLocations[0] = slot1;
        this.portraitLocations[1] = slot2;
        this.portraitLocations[2] = slot3;
        this.hasMultiplePortraits = true;
    }

    public void setPortraitLocations(ResourceLocation slot1, ResourceLocation slot2, ResourceLocation slot3,
                                    ResourceLocation slot4, ResourceLocation slot5, ResourceLocation slot6) {
        this.portraitLocations[0] = slot1;
        this.portraitLocations[1] = slot2;
        this.portraitLocations[2] = slot3;
        this.portraitLocations[3] = slot4;
        this.portraitLocations[4] = slot5;
        this.portraitLocations[5] = slot6;
        this.hasMultiplePortraits = true;
    }

    public ResourceLocation getPortraitLocation(int slotIndex) {
        if (hasMultiplePortraits) {
            int typeIndex = slotIndex % 3;
            return portraitLocations[typeIndex];
        }
        return portraitLocation;
    }

    public boolean hasMultiplePortraits() { return hasMultiplePortraits; }
    public void setStats(AwarenessStats stats) { this.stats = stats; }
    public AwarenessStats getStats() { return stats; }
    public void addItem(int slot, AwarenessItem item) { items[slot] = item; }
    public void addEffect(SetEffect effect) { effects.add(effect); }
    public ResourceLocation getId() { return id; }
    public AwarenessItem[] getItems() { return items; }
    public List<SetEffect> getEffects() { return effects; }

    public int countEquipped(LivingEntity entity) {
        int count = 0;
        var opt = CuriosApi.getCuriosInventory(entity);
        if (opt.isPresent()) {
            List<SlotResult> list = opt.get().findCurios(stack -> stack.getItem() instanceof AwarenessItem);
            for (SlotResult result : list) {
                if (result.stack().getItem() instanceof AwarenessItem awarenessItem) {
                    if (awarenessItem.getSet() == this) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void applyEffects(LivingEntity entity) {
        int count = countEquipped(entity);
        for (SetEffect effect : effects) {
            if (count >= effect.getRequiredCount()) {
                effect.apply(entity, count);
            }
        }
    }

    public void tickEffects(LivingEntity entity) {
        int count = countEquipped(entity);
        for (SetEffect effect : effects) {
            if (count >= effect.getRequiredCount()) {
                effect.tick(entity, count);
            }
        }
    }

    public List<MutableComponent> getSetDescription(ItemStack stack) {
        List<MutableComponent> descriptions = new ArrayList<>();
        descriptions.add(Component.translatable("set." + id.getPath()).withStyle(net.minecraft.ChatFormatting.YELLOW));

        for (SetEffect effect : effects) {
            descriptions.add(Component.translatable("set.effect." + effect.getId().getPath() + ".desc")
                    .withStyle(net.minecraft.ChatFormatting.GRAY));
        }

        Component story = Component.translatable("set.story." + id.getPath());
        if (!story.getString().equals("set.story." + id.getPath())) {
            descriptions.add(Component.translatable("set.story." + id.getPath()).withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));
        }

        return descriptions;
    }
}