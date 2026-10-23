package com.icypunishing.registry;

import com.icypunishing.IcyPunishing;
import com.icypunishing.entity.RepairMechanicEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, IcyPunishing.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RepairMechanicEntity>> REPAIR_MECHANIC =
            ENTITIES.register("repair_mechanic", () -> EntityType.Builder
                    .of(RepairMechanicEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .eyeHeight(0.9F)
                    .clientTrackingRange(10)
                    .build("repair_mechanic"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
        eventBus.addListener(ModEntities::onAttributeCreation);
    }

    private static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(REPAIR_MECHANIC.get(), RepairMechanicEntity.createAttributes().build());
    }
}
