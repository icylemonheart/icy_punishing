package com.icypunishing.client;

import com.icylib.client.upgrade.UpgradeScreenRendererManager;
import com.icypunishing.client.entity.TianlangBladeRenderer;
import com.icypunishing.client.obj.ObjKatanaRenderer;
import com.icypunishing.registry.ModEntities;
import com.icypunishing.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import software.bernie.geckolib.loading.object.BakedModelFactory;

@EventBusSubscriber(modid = "icy_punishing", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class IcyPunishingClient {

    static {
        // 注册支持 poly_mesh 的模型工厂，否则只有 poly_mesh 的模型不会产生任何几何体
        BakedModelFactory.register("icy_punishing", new IcyPunishingModelFactory());
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            UpgradeScreenRendererManager.setRenderer(new UpgradeScreenRenderer());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TIANLANG_BLADE.get(), TianlangBladeRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/hongying_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/hongying_blade.png"));
                }
                return renderer;
            }
        }, ModItems.HONGYING.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/honglian_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/honglian_blade.png"));
                }
                return renderer;
            }
        }, ModItems.HONGLIAN.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/kyuuji_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/kyuuji_blade.png"));
                }
                return renderer;
            }
        }, ModItems.KYUUJI.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tachi_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tachi_blade.png"));
                }
                return renderer;
            }
        }, ModItems.TACHI.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/hugotou_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/hugotou_blade.png"));
                }
                return renderer;
            }
        }, ModItems.HUGOTOU.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/mumei_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/mumei_blade.png"));
                }
                return renderer;
            }
        }, ModItems.MUMEI.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tianlang_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tianlang_blade.png")) {
                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                                                 MultiBufferSource buffer, int packedLight, int packedOverlay) {
                            // 天狼星置于副手时由 TianlangBladeEntity 悬浮渲染，手部不再显示本体
                            if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                                    || displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                                return;
                            }
                            super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
                        }
                    };
                }
                return renderer;
            }
        }, ModItems.TIANLANG.get());

        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/yingda_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/yingda_blade.png"));
                }
                return renderer;
            }
        }, ModItems.YINGDA.get());

        registerKatana(event, ModItems.ZHUHUA_C1, "zhuhua_c1");
        registerKatana(event, ModItems.ZHUHUA_C2, "zhuhua_c2");
        registerKatana(event, ModItems.ZHUHUA_C3, "zhuhua_c3");
        registerKatana(event, ModItems.ZHUHUA_C4, "zhuhua_c4");
        registerKatana(event, ModItems.SARARIEL_C1, "sarariel_c1");
        registerKatana(event, ModItems.SARARIEL_C2, "sarariel_c2");
        registerKatana(event, ModItems.SARARIEL_C3, "sarariel_c3");
        registerKatana(event, ModItems.SARARIEL_C4, "sarariel_c4");

        registerKatana(event, ModItems.FENYING, "fenying");
        registerKatana(event, ModItems.LUPERCALIA, "lupercalia");
        registerKatana(event, ModItems.CANGUANG, "canguang");
        registerKatana(event, ModItems.GULINFENGREN, "gulinfengren");
        registerKatana(event, ModItems.MUSEYIDIAN, "museyidian");
        registerKatana(event, ModItems.FLORANUM, "floranum");

        registerKatana(event, ModItems.LEIMIER, "leimier", true);
        registerKatana(event, ModItems.LAITENING, "laitening", true);
        registerKatana(event, ModItems.ASITELIYA, "asiteliya", true);
        registerKatana(event, ModItems.LIESUN, "liesun", true);
        registerKatana(event, ModItems.HUALUN, "hualun", true);
        registerKatana(event, ModItems.LIANXI, "lianxi", true);

        registerKatana(event, ModItems.ZIYUAN, "ziyuan", true);
        registerKatana(event, ModItems.CHUNBAISHENGZAN, "chunbaishengzan", true);
        registerKatana(event, ModItems.YINYEJINGHONG, "yinyejinghong", true);
        registerKatana(event, ModItems.XURI, "xuri", true);
        registerKatana(event, ModItems.XUNYEZHIHU, "xunyezhihu", true);
        registerKatana(event, ModItems.XIAYE, "xiaye", true);
        registerKatana(event, ModItems.REXIAHONGXIAN, "rexiahongxian", true);
    }

    private static void registerKatana(RegisterClientExtensionsEvent event,
                                       DeferredHolder<Item, Item> item, String name) {
        registerKatana(event, item, name, false);
    }

    private static void registerKatana(RegisterClientExtensionsEvent event,
                                       DeferredHolder<Item, Item> item, String name, boolean holdCenter) {
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ObjKatanaRenderer(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/" + name + "_blade.obj"),
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/" + name + "_blade.png"),
                            holdCenter);
                }
                return renderer;
            }
        }, item.get());
    }
}
