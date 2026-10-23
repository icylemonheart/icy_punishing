package com.icypunishing.client;

import com.icylib.client.upgrade.UpgradeScreenRendererManager;
import com.icypunishing.client.entity.RepairMechanicModel;
import com.icypunishing.client.obj.ObjKatanaRenderer;
import com.icypunishing.registry.ModEntities;
import com.icypunishing.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@EventBusSubscriber(modid = "icy_punishing", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class IcyPunishingClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        BakedModelFactory.register("icy_punishing", new IcyPunishingModelFactory());
        LOGGER.info("[ICY] IcyPunishingModelFactory registered (static init) for namespace icy_punishing");
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            UpgradeScreenRendererManager.setRenderer(new UpgradeScreenRenderer());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.REPAIR_MECHANIC.get(),
                context -> new GeoEntityRenderer<>(context, new RepairMechanicModel()));
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
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tianlang_blade.png"));
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
