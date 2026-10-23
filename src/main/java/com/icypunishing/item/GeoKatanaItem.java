package com.icypunishing.item;

import com.icypunishing.IcyPunishing;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

// 太刀类武器基类——静态 3D 模型，原版挥手
public class GeoKatanaItem extends SwordItem implements GeoItem {
    private final ResourceLocation model;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeoKatanaItem(Properties properties, String modelPath) {
        super(Tiers.IRON, properties.attributes(SwordItem.createAttributes(Tiers.IRON, 7, -2.4F)));
        this.model = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, modelPath);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<GeoKatanaItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(GeoKatanaItem.this.model));
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
