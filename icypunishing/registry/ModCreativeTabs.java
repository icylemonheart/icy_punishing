package com.icypunishing.registry;

import com.icypunishing.IcyPunishing;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IcyPunishing.MODID);

    public static final RegistryObject<CreativeModeTab> AWARENESS_TAB = CREATIVE_MODE_TABS.register("awareness",
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

    public static final RegistryObject<CreativeModeTab> MATERIAL_TAB = CREATIVE_MODE_TABS.register("material",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.NUT.get()))
                    .title(Component.translatable("itemGroup.icy_punishing.material"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.NUT.get());
                        output.accept(ModItems.BREAKTHROUGH_ALLOY_LOW.get());
                        output.accept(ModItems.BREAKTHROUGH_ALLOY_HIGH.get());
                        output.accept(ModItems.AWARENESS_CIRCUIT_LOW.get());
                        output.accept(ModItems.AWARENESS_CIRCUIT_HIGH.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> ICY_PUNISHING_TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HUMANITY_STANDS_HERE.get()))
                    .title(Component.translatable("itemGroup.icy_punishing"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HUMANITY_STANDS_HERE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}