package com.icypunishing.recipe;

import com.icypunishing.core.AwarenessItem;
import com.icypunishing.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 回收意识：工作台只放一枚意识，按星级产出强化素材（2/3/4 星→II/III/IV，5 星以上→2 个 IV）。
 */
public class AwarenessRecycleRecipe extends CustomRecipe {

    public AwarenessRecycleRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findAwareness(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack awareness = findAwareness(input);
        if (awareness == null) {
            return ItemStack.EMPTY;
        }
        int rank = ((AwarenessItem) awareness.getItem()).getRank();
        return switch (rank) {
            case 2 -> new ItemStack(ModItems.AWARENESS_MATERIAL_2.get(), 1);
            case 3 -> new ItemStack(ModItems.AWARENESS_MATERIAL_3.get(), 1);
            case 4 -> new ItemStack(ModItems.AWARENESS_MATERIAL_4.get(), 1);
            default -> rank >= 5 ? new ItemStack(ModItems.AWARENESS_MATERIAL_4.get(), 2) : ItemStack.EMPTY;
        };
    }

    /** 网格内必须恰好只有一枚意识。 */
    private static ItemStack findAwareness(CraftingInput input) {
        ItemStack found = null;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!(stack.getItem() instanceof AwarenessItem) || found != null) {
                return null;
            }
            found = stack;
        }
        return found;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.AWARENESS_RECYCLE.get();
    }
}
