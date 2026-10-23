package com.icypunishing.client.entity;

import com.icypunishing.entity.RepairMechanicEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RepairMechanicModel extends GeoModel<RepairMechanicEntity> {
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "geo/entity/repair_mechanic.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "textures/entity/repair_mechanic.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "animations/entity/repair_mechanic.animation.json");

    @Override
    public ResourceLocation getModelResource(RepairMechanicEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RepairMechanicEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RepairMechanicEntity animatable) {
        return ANIMATION;
    }
}
