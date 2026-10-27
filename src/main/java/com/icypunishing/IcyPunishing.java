package com.icypunishing;

import com.icylib.quality.ItemQualityRegistry;
import com.icypunishing.config.IcyPunishingConfig;
import com.icypunishing.core.AwarenessItem;
import com.icypunishing.event.AwarenessDropHandler;
import com.icypunishing.event.AwarenessEffectHandler;
import com.icypunishing.event.AwarenessStatsHandler;
import com.icypunishing.event.HyperCalculationSpaceHandler;
import com.icypunishing.event.SkillCombatHandler;
import com.icypunishing.registry.ModAttachments;
import com.icypunishing.registry.ModAwarenessSlots;
import com.icypunishing.skill.SkillSetRegistry;
import com.icypunishing.registry.ModCreativeTabs;
import com.icypunishing.registry.ModEffects;
import com.icypunishing.registry.ModEntities;
import com.icypunishing.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

@Mod(IcyPunishing.MODID)
public class IcyPunishing {
    public static final String MODID = "icy_punishing";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IcyPunishing(IEventBus bus, ModContainer modContainer) {
        ModItems.register(bus);
        ModCreativeTabs.register(bus);
        ModAwarenessSlots.register(bus);
        ModEffects.register(bus);
        ModEntities.register(bus);
        ModAttachments.register(bus);

        bus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new SkillCombatHandler());
        NeoForge.EVENT_BUS.register(new AwarenessEffectHandler());
        NeoForge.EVENT_BUS.register(new AwarenessDropHandler());
        NeoForge.EVENT_BUS.register(new AwarenessStatsHandler());
        NeoForge.EVENT_BUS.register(new HyperCalculationSpaceHandler());
        NeoForge.EVENT_BUS.register(new com.icypunishing.event.WeaponSkillHandler());

        modContainer.registerConfig(ModConfig.Type.COMMON, IcyPunishingConfig.SPEC, "icy_punishing-common.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModItems.ITEMS.getEntries().forEach(holder -> {
                Item item = holder.get();
                if (item instanceof AwarenessItem awarenessItem) {
                    ItemQualityRegistry.register(item, awarenessItem.getRank());
                }
            });
            registerQualities();
            SkillSetRegistry.bootstrap();
            LOGGER.info("Icy Punishing v1.0.1 (NeoForge 1.21.1) loaded successfully!");
            LOGGER.info("Awareness slots registered: 6");
        });
    }

    private void registerQualities() {
        ItemQualityRegistry.register(ModItems.TACHI.get(), 2);
        ItemQualityRegistry.register(ModItems.HUGOTOU.get(), 3);
        ItemQualityRegistry.register(ModItems.MUMEI.get(), 4);
        ItemQualityRegistry.register(ModItems.KYUUJI.get(), 5);

        ItemQualityRegistry.register(ModItems.HONGLIAN.get(), 6);
        ItemQualityRegistry.register(ModItems.HONGYING.get(), 6);
        ItemQualityRegistry.register(ModItems.TIANLANG.get(), 6);
        ItemQualityRegistry.register(ModItems.YINGDA.get(), 6);
        ItemQualityRegistry.register(ModItems.ZHUHUA_C1.get(), 6);
        ItemQualityRegistry.register(ModItems.ZHUHUA_C2.get(), 6);
        ItemQualityRegistry.register(ModItems.ZHUHUA_C3.get(), 6);
        ItemQualityRegistry.register(ModItems.ZHUHUA_C4.get(), 6);
        ItemQualityRegistry.register(ModItems.SARARIEL_C1.get(), 6);
        ItemQualityRegistry.register(ModItems.SARARIEL_C2.get(), 6);
        ItemQualityRegistry.register(ModItems.SARARIEL_C3.get(), 6);
        ItemQualityRegistry.register(ModItems.SARARIEL_C4.get(), 6);

        ItemQualityRegistry.register(ModItems.LEIMIER.get(), 6);
        ItemQualityRegistry.register(ModItems.LAITENING.get(), 6);
        ItemQualityRegistry.register(ModItems.ASITELIYA.get(), 6);
        ItemQualityRegistry.register(ModItems.LIESUN.get(), 5);
        ItemQualityRegistry.register(ModItems.HUALUN.get(), 4);
        ItemQualityRegistry.register(ModItems.LIANXI.get(), 3);

        ItemQualityRegistry.register(ModItems.NUT.get(), 1);
        ItemQualityRegistry.register(ModItems.NUT_PACK_SMALL.get(), 2);
        ItemQualityRegistry.register(ModItems.NUT_PACK_MEDIUM.get(), 3);
        ItemQualityRegistry.register(ModItems.NUT_PACK_LARGE.get(), 3);
        ItemQualityRegistry.register(ModItems.NUT_PACK_XLARGE.get(), 4);
        ItemQualityRegistry.register(ModItems.NUT_PACK_XXLARGE.get(), 4);

        ItemQualityRegistry.register(ModItems.COMPUTE_FRAGMENT.get(), 3);

        ItemQualityRegistry.register(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 3);
        ItemQualityRegistry.register(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 4);
        ItemQualityRegistry.register(ModItems.AWARENESS_CIRCUIT_LOW.get(), 3);
        ItemQualityRegistry.register(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 4);
        ItemQualityRegistry.register(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 3);
        ItemQualityRegistry.register(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 4);

        ItemQualityRegistry.register(ModItems.AWARENESS_MATERIAL_2.get(), 2);
        ItemQualityRegistry.register(ModItems.AWARENESS_MATERIAL_3.get(), 3);
        ItemQualityRegistry.register(ModItems.AWARENESS_MATERIAL_4.get(), 4);
        ItemQualityRegistry.register(ModItems.WEAPON_MATERIAL_2.get(), 2);
        ItemQualityRegistry.register(ModItems.WEAPON_MATERIAL_3.get(), 3);
        ItemQualityRegistry.register(ModItems.WEAPON_MATERIAL_4.get(), 4);

        ItemQualityRegistry.register(ModItems.EXP_CONTAINER_SMALL.get(), 2);
        ItemQualityRegistry.register(ModItems.EXP_CONTAINER_MEDIUM.get(), 3);
        ItemQualityRegistry.register(ModItems.EXP_CONTAINER_LARGE.get(), 4);
        ItemQualityRegistry.register(ModItems.EXP_CONTAINER_XLARGE.get(), 4);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Icy Punishing server starting!");
    }
}