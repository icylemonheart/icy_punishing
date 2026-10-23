package com.icypunishing.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 带自定义 tooltip 的材料物品。
 * 构造时传入 tooltip 文本行列表，会在 appendHoverText 中追加到物品提示中。
 */
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
