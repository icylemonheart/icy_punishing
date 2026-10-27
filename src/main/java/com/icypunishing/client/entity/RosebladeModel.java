package com.icypunishing.client.entity;

import com.icypunishing.entity.RosebladeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RosebladeModel extends GeoModel<RosebladeEntity> {
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "geo/entity/roseblade.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "textures/entity/roseblade.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "animations/entity/roseblade.animation.json");

    @Override
    public ResourceLocation getModelResource(RosebladeEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RosebladeEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RosebladeEntity animatable) {
        return ANIMATION;
    }
}
