package com.icypunishing.registry;

import com.icypunishing.IcyPunishing;
import com.icypunishing.entity.TianlangBladeEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, IcyPunishing.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final DeferredHolder<EntityType<?>, EntityType<TianlangBladeEntity>> TIANLANG_BLADE =
            ENTITIES.register("tianlang_blade", () -> EntityType.Builder
                    .of(TianlangBladeEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("tianlang_blade"));
}
