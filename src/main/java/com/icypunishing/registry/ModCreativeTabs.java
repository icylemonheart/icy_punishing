package com.icypunishing.registry;

import com.icypunishing.IcyPunishing;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IcyPunishing.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AWARENESS_TAB = CREATIVE_MODE_TABS.register("awareness",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.MERNIE_AWARENESS_1.get()))
                    .title(Component.translatable("itemGroup.icy_punishing.awareness"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.MERNIE_AWARENESS_1.get());
                        output.accept(ModItems.MERNIE_AWARENESS_2.get());
                        output.accept(ModItems.MERNIE_AWARENESS_3.get());
                        output.accept(ModItems.MERNIE_AWARENESS_4.get());
                        output.accept(ModItems.MERNIE_AWARENESS_5.get());
                        output.accept(ModItems.MERNIE_AWARENESS_6.get());
                        output.accept(ModItems.PAVTY_AWARENESS_1.get());
                        output.accept(ModItems.PAVTY_AWARENESS_2.get());
                        output.accept(ModItems.PAVTY_AWARENESS_3.get());
                        output.accept(ModItems.PAVTY_AWARENESS_4.get());
                        output.accept(ModItems.PAVTY_AWARENESS_5.get());
                        output.accept(ModItems.PAVTY_AWARENESS_6.get());
                        output.accept(ModItems.PAMELIE_AWARENESS_1.get());
                        output.accept(ModItems.PAMELIE_AWARENESS_2.get());
                        output.accept(ModItems.PAMELIE_AWARENESS_3.get());
                        output.accept(ModItems.PAMELIE_AWARENESS_4.get());
                        output.accept(ModItems.PAMELIE_AWARENESS_5.get());
                        output.accept(ModItems.PAMELIE_AWARENESS_6.get());
                        output.accept(ModItems.CURIE_COHET_AWARENESS_1.get());
                        output.accept(ModItems.CURIE_COHET_AWARENESS_2.get());
                        output.accept(ModItems.CURIE_COHET_AWARENESS_3.get());
                        output.accept(ModItems.CURIE_COHET_AWARENESS_4.get());
                        output.accept(ModItems.CURIE_COHET_AWARENESS_5.get());
                        output.accept(ModItems.CURIE_COHET_AWARENESS_6.get());
                        output.accept(ModItems.CUNNINGHAM_AWARENESS_1.get());
                        output.accept(ModItems.CUNNINGHAM_AWARENESS_2.get());
                        output.accept(ModItems.CUNNINGHAM_AWARENESS_3.get());
                        output.accept(ModItems.CUNNINGHAM_AWARENESS_4.get());
                        output.accept(ModItems.CUNNINGHAM_AWARENESS_5.get());
                        output.accept(ModItems.CUNNINGHAM_AWARENESS_6.get());
                        output.accept(ModItems.ALICE_AWARENESS_1.get());
                        output.accept(ModItems.ALICE_AWARENESS_2.get());
                        output.accept(ModItems.ALICE_AWARENESS_3.get());
                        output.accept(ModItems.ALICE_AWARENESS_4.get());
                        output.accept(ModItems.ALICE_AWARENESS_5.get());
                        output.accept(ModItems.ALICE_AWARENESS_6.get());
                        output.accept(ModItems.EDISON_AWARENESS_1.get());
                        output.accept(ModItems.EDISON_AWARENESS_2.get());
                        output.accept(ModItems.EDISON_AWARENESS_3.get());
                        output.accept(ModItems.EDISON_AWARENESS_4.get());
                        output.accept(ModItems.EDISON_AWARENESS_5.get());
                        output.accept(ModItems.EDISON_AWARENESS_6.get());
                        output.accept(ModItems.ARCHIMEDES_AWARENESS_1.get());
                        output.accept(ModItems.ARCHIMEDES_AWARENESS_2.get());
                        output.accept(ModItems.ARCHIMEDES_AWARENESS_3.get());
                        output.accept(ModItems.ARCHIMEDES_AWARENESS_4.get());
                        output.accept(ModItems.ARCHIMEDES_AWARENESS_5.get());
                        output.accept(ModItems.ARCHIMEDES_AWARENESS_6.get());
                        output.accept(ModItems.NIMITZ_AWARENESS_1.get());
                        output.accept(ModItems.NIMITZ_AWARENESS_2.get());
                        output.accept(ModItems.NIMITZ_AWARENESS_3.get());
                        output.accept(ModItems.NIMITZ_AWARENESS_4.get());
                        output.accept(ModItems.NIMITZ_AWARENESS_5.get());
                        output.accept(ModItems.NIMITZ_AWARENESS_6.get());
                        output.accept(ModItems.AIFU_AWARENESS_1.get());
                        output.accept(ModItems.AIFU_AWARENESS_2.get());
                        output.accept(ModItems.AIFU_AWARENESS_3.get());
                        output.accept(ModItems.AIFU_AWARENESS_4.get());
                        output.accept(ModItems.AIFU_AWARENESS_5.get());
                        output.accept(ModItems.AIFU_AWARENESS_6.get());
                        output.accept(ModItems.AIKE_AWARENESS_1.get());
                        output.accept(ModItems.AIKE_AWARENESS_2.get());
                        output.accept(ModItems.AIKE_AWARENESS_3.get());
                        output.accept(ModItems.AIKE_AWARENESS_4.get());
                        output.accept(ModItems.AIKE_AWARENESS_5.get());
                        output.accept(ModItems.AIKE_AWARENESS_6.get());
                        output.accept(ModItems.VOLTAIRE_AWARENESS_1.get());
                        output.accept(ModItems.VOLTAIRE_AWARENESS_2.get());
                        output.accept(ModItems.VOLTAIRE_AWARENESS_3.get());
                        output.accept(ModItems.VOLTAIRE_AWARENESS_4.get());
                        output.accept(ModItems.VOLTAIRE_AWARENESS_5.get());
                        output.accept(ModItems.VOLTAIRE_AWARENESS_6.get());
                        output.accept(ModItems.GORODA_AWARENESS_1.get());
                        output.accept(ModItems.GORODA_AWARENESS_2.get());
                        output.accept(ModItems.GORODA_AWARENESS_3.get());
                        output.accept(ModItems.GORODA_AWARENESS_4.get());
                        output.accept(ModItems.GORODA_AWARENESS_5.get());
                        output.accept(ModItems.GORODA_AWARENESS_6.get());
                        output.accept(ModItems.RICHELIEU_AWARENESS_1.get());
                        output.accept(ModItems.RICHELIEU_AWARENESS_2.get());
                        output.accept(ModItems.RICHELIEU_AWARENESS_3.get());
                        output.accept(ModItems.RICHELIEU_AWARENESS_4.get());
                        output.accept(ModItems.RICHELIEU_AWARENESS_5.get());
                        output.accept(ModItems.RICHELIEU_AWARENESS_6.get());
                        output.accept(ModItems.SAMANDA_AWARENESS_1.get());
                        output.accept(ModItems.SAMANDA_AWARENESS_2.get());
                        output.accept(ModItems.SAMANDA_AWARENESS_3.get());
                        output.accept(ModItems.SAMANDA_AWARENESS_4.get());
                        output.accept(ModItems.SAMANDA_AWARENESS_5.get());
                        output.accept(ModItems.SAMANDA_AWARENESS_6.get());
                        output.accept(ModItems.MOZART_AWARENESS_1.get());
                        output.accept(ModItems.MOZART_AWARENESS_2.get());
                        output.accept(ModItems.MOZART_AWARENESS_3.get());
                        output.accept(ModItems.MOZART_AWARENESS_4.get());
                        output.accept(ModItems.MOZART_AWARENESS_5.get());
                        output.accept(ModItems.MOZART_AWARENESS_6.get());
                        output.accept(ModItems.BOUDICA_AWARENESS_1.get());
                        output.accept(ModItems.BOUDICA_AWARENESS_2.get());
                        output.accept(ModItems.BOUDICA_AWARENESS_3.get());
                        output.accept(ModItems.BOUDICA_AWARENESS_4.get());
                        output.accept(ModItems.BOUDICA_AWARENESS_5.get());
                        output.accept(ModItems.BOUDICA_AWARENESS_6.get());
                        output.accept(ModItems.EINSTEINNA_AWARENESS_1.get());
                        output.accept(ModItems.EINSTEINNA_AWARENESS_2.get());
                        output.accept(ModItems.EINSTEINNA_AWARENESS_3.get());
                        output.accept(ModItems.EINSTEINNA_AWARENESS_4.get());
                        output.accept(ModItems.EINSTEINNA_AWARENESS_5.get());
                        output.accept(ModItems.EINSTEINNA_AWARENESS_6.get());
                        output.accept(ModItems.KANTLINA_AWARENESS_1.get());
                        output.accept(ModItems.KANTLINA_AWARENESS_2.get());
                        output.accept(ModItems.KANTLINA_AWARENESS_3.get());
                        output.accept(ModItems.KANTLINA_AWARENESS_4.get());
                        output.accept(ModItems.KANTLINA_AWARENESS_5.get());
                        output.accept(ModItems.KANTLINA_AWARENESS_6.get());
                        output.accept(ModItems.SHAKESPEARE_AWARENESS_1.get());
                        output.accept(ModItems.SHAKESPEARE_AWARENESS_2.get());
                        output.accept(ModItems.SHAKESPEARE_AWARENESS_3.get());
                        output.accept(ModItems.SHAKESPEARE_AWARENESS_4.get());
                        output.accept(ModItems.SHAKESPEARE_AWARENESS_5.get());
                        output.accept(ModItems.SHAKESPEARE_AWARENESS_6.get());
                        output.accept(ModItems.HEISENBERG_AWARENESS_1.get());
                        output.accept(ModItems.HEISENBERG_AWARENESS_2.get());
                        output.accept(ModItems.HEISENBERG_AWARENESS_3.get());
                        output.accept(ModItems.HEISENBERG_AWARENESS_4.get());
                        output.accept(ModItems.HEISENBERG_AWARENESS_5.get());
                        output.accept(ModItems.HEISENBERG_AWARENESS_6.get());
                        output.accept(ModItems.DARWIN_AWARENESS_1.get());
                        output.accept(ModItems.DARWIN_AWARENESS_2.get());
                        output.accept(ModItems.DARWIN_AWARENESS_3.get());
                        output.accept(ModItems.DARWIN_AWARENESS_4.get());
                        output.accept(ModItems.DARWIN_AWARENESS_5.get());
                        output.accept(ModItems.DARWIN_AWARENESS_6.get());
                        output.accept(ModItems.HANNA_AWARENESS_1.get());
                        output.accept(ModItems.HANNA_AWARENESS_2.get());
                        output.accept(ModItems.HANNA_AWARENESS_3.get());
                        output.accept(ModItems.HANNA_AWARENESS_4.get());
                        output.accept(ModItems.HANNA_AWARENESS_5.get());
                        output.accept(ModItems.HANNA_AWARENESS_6.get());
                        output.accept(ModItems.DA_VINCI_AWARENESS_1.get());
                        output.accept(ModItems.DA_VINCI_AWARENESS_2.get());
                        output.accept(ModItems.DA_VINCI_AWARENESS_3.get());
                        output.accept(ModItems.DA_VINCI_AWARENESS_4.get());
                        output.accept(ModItems.DA_VINCI_AWARENESS_5.get());
                        output.accept(ModItems.DA_VINCI_AWARENESS_6.get());
                        output.accept(ModItems.CATHERINE_AWARENESS_1.get());
                        output.accept(ModItems.CATHERINE_AWARENESS_2.get());
                        output.accept(ModItems.CATHERINE_AWARENESS_3.get());
                        output.accept(ModItems.CATHERINE_AWARENESS_4.get());
                        output.accept(ModItems.CATHERINE_AWARENESS_5.get());
                        output.accept(ModItems.CATHERINE_AWARENESS_6.get());
                        output.accept(ModItems.FERIET_II_AWARENESS_1.get());
                        output.accept(ModItems.FERIET_II_AWARENESS_2.get());
                        output.accept(ModItems.FERIET_II_AWARENESS_3.get());
                        output.accept(ModItems.FERIET_II_AWARENESS_4.get());
                        output.accept(ModItems.FERIET_II_AWARENESS_5.get());
                        output.accept(ModItems.FERIET_II_AWARENESS_6.get());
                        output.accept(ModItems.GUINEY_AWARENESS_1.get());
                        output.accept(ModItems.GUINEY_AWARENESS_2.get());
                        output.accept(ModItems.GUINEY_AWARENESS_3.get());
                        output.accept(ModItems.GUINEY_AWARENESS_4.get());
                        output.accept(ModItems.GUINEY_AWARENESS_5.get());
                        output.accept(ModItems.GUINEY_AWARENESS_6.get());
                        output.accept(ModItems.FRIEDRICH_AWARENESS_1.get());
                        output.accept(ModItems.FRIEDRICH_AWARENESS_2.get());
                        output.accept(ModItems.FRIEDRICH_AWARENESS_3.get());
                        output.accept(ModItems.FRIEDRICH_AWARENESS_4.get());
                        output.accept(ModItems.FRIEDRICH_AWARENESS_5.get());
                        output.accept(ModItems.FRIEDRICH_AWARENESS_6.get());
                        output.accept(ModItems.PATTON_AWARENESS_1.get());
                        output.accept(ModItems.PATTON_AWARENESS_2.get());
                        output.accept(ModItems.PATTON_AWARENESS_3.get());
                        output.accept(ModItems.PATTON_AWARENESS_4.get());
                        output.accept(ModItems.PATTON_AWARENESS_5.get());
                        output.accept(ModItems.PATTON_AWARENESS_6.get());
                        output.accept(ModItems.BASILONE_AWARENESS_1.get());
                        output.accept(ModItems.BASILONE_AWARENESS_2.get());
                        output.accept(ModItems.BASILONE_AWARENESS_3.get());
                        output.accept(ModItems.BASILONE_AWARENESS_4.get());
                        output.accept(ModItems.BASILONE_AWARENESS_5.get());
                        output.accept(ModItems.BASILONE_AWARENESS_6.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIAL_TAB = CREATIVE_MODE_TABS.register("material",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.NUT.get()))
                    .title(Component.translatable("itemGroup.icy_punishing.material"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.NUT.get());
                        output.accept(ModItems.NUT_PACK_SMALL.get());
                        output.accept(ModItems.NUT_PACK_MEDIUM.get());
                        output.accept(ModItems.NUT_PACK_LARGE.get());
                        output.accept(ModItems.NUT_PACK_XLARGE.get());
                        output.accept(ModItems.NUT_PACK_XXLARGE.get());
                        output.accept(ModItems.COMPUTE_FRAGMENT.get());

                        output.accept(ModItems.BREAKTHROUGH_ALLOY_LOW.get());
                        output.accept(ModItems.BREAKTHROUGH_ALLOY_HIGH.get());
                        output.accept(ModItems.AWARENESS_CIRCUIT_LOW.get());
                        output.accept(ModItems.AWARENESS_CIRCUIT_HIGH.get());

                        output.accept(ModItems.AWARENESS_MATERIAL_2.get());
                        output.accept(ModItems.AWARENESS_MATERIAL_3.get());
                        output.accept(ModItems.AWARENESS_MATERIAL_4.get());

                        output.accept(ModItems.WEAPON_MATERIAL_2.get());
                        output.accept(ModItems.WEAPON_MATERIAL_3.get());
                        output.accept(ModItems.WEAPON_MATERIAL_4.get());

                        output.accept(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get());
                        output.accept(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get());

                        output.accept(ModItems.EXP_CONTAINER_SMALL.get());
                        output.accept(ModItems.EXP_CONTAINER_MEDIUM.get());
                        output.accept(ModItems.EXP_CONTAINER_LARGE.get());
                        output.accept(ModItems.EXP_CONTAINER_XLARGE.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ICY_PUNISHING_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HUMANITY_STANDS_HERE.get()))
                    .title(Component.translatable("itemGroup.icy_punishing"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HUMANITY_STANDS_HERE.get());
                        output.accept(ModItems.TACHI.get());
                        output.accept(ModItems.HUGOTOU.get());
                        output.accept(ModItems.MUMEI.get());
                        output.accept(ModItems.KYUUJI.get());
                        output.accept(ModItems.HONGLIAN.get());
                        output.accept(ModItems.HONGYING.get());
                        output.accept(ModItems.TIANLANG.get());
                        output.accept(ModItems.YINGDA.get());
                        output.accept(ModItems.ZHUHUA_C1.get());
                        output.accept(ModItems.ZHUHUA_C2.get());
                        output.accept(ModItems.ZHUHUA_C3.get());
                        output.accept(ModItems.ZHUHUA_C4.get());
                        output.accept(ModItems.SARARIEL_C1.get());
                        output.accept(ModItems.SARARIEL_C2.get());
                        output.accept(ModItems.SARARIEL_C3.get());
                        output.accept(ModItems.SARARIEL_C4.get());
                        output.accept(ModItems.FENYING.get());
                        output.accept(ModItems.LUPERCALIA.get());
                        output.accept(ModItems.CANGUANG.get());
                        output.accept(ModItems.GULINFENGREN.get());
                        output.accept(ModItems.MUSEYIDIAN.get());
                        output.accept(ModItems.FLORANUM.get());
                        output.accept(ModItems.LEIMIER.get());
                        output.accept(ModItems.LAITENING.get());
                        output.accept(ModItems.ASITELIYA.get());
                        output.accept(ModItems.LIESUN.get());
                        output.accept(ModItems.HUALUN.get());
                        output.accept(ModItems.LIANXI.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATURE_TAB = CREATIVE_MODE_TABS.register("creature",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.REPAIR_MECHANIC_SPAWN_EGG.get()))
                    .title(Component.translatable("itemGroup.icy_punishing.creature"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.REPAIR_MECHANIC_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}