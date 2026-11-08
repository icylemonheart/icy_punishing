package com.icypunishing.recipe;

import com.icypunishing.IcyPunishing;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, IcyPunishing.MODID);

    /** 意识回收：单枚意识 → 对应星级的强化素材。 */
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AwarenessRecycleRecipe>> AWARENESS_RECYCLE =
            SERIALIZERS.register("awareness_recycle",
                    () -> new SimpleCraftingRecipeSerializer<>(AwarenessRecycleRecipe::new));
}
