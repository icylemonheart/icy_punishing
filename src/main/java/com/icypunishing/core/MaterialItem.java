package com.icypunishing.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** 材料物品：构造时传入的 tooltip 行会追加到物品提示。 */
public class MaterialItem extends Item {
    private final List<Component> tooltipLines;

    public MaterialItem(Properties properties, List<Component> tooltipLines) {
        super(properties);
        this.tooltipLines = tooltipLines;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.addAll(tooltipLines);
    }
}
