package com.icypunishing.registry;

import com.icypunishing.IcyPunishing;
import com.icypunishing.core.AwarenessItem;
import com.icypunishing.core.AwarenessSet;
import com.icypunishing.core.AwarenessStats;
import com.icypunishing.core.MaterialItem;
import com.icypunishing.core.ProximityEffect;
import com.icypunishing.core.SetEffect;
import com.icypunishing.core.UpgradeKeyItem;
import com.icypunishing.item.GeoKatanaItem;
import com.icypunishing.item.ObjKatanaItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BowItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, IcyPunishing.MODID);

    public static final Map<ResourceLocation, AwarenessSet> AWARENESS_SETS = new HashMap<>();

    public static final AwarenessSet MERNIE_SET = create2StarSet("mernie", new AwarenessStats(52, 82, 2, 3, 4, 7, 7, 12));
    public static final AwarenessSet PAVTY_SET = create3StarSet("pavty", new AwarenessStats(76, 375, 3, 16, 6, 31, 11, 56));
    public static final AwarenessSet PAMELIE_SET = create3StarSet("pamelie", new AwarenessStats(74, 368, 3, 16, 6, 32, 11, 55));
    public static final AwarenessSet CURIE_COHET_SET = create4StarSet("curie_cohet", new AwarenessStats(102, 1059, 4, 49, 9, 97, 15, 158));
    public static final AwarenessSet CUNNINGHAM_SET = create4StarSet("cunningham", new AwarenessStats(107, 1102, 4, 47, 9, 94, 16, 165));
    public static final AwarenessSet ALICE_SET = create4StarSet("alice", new AwarenessStats(104, 1081, 4, 48, 9, 96, 15, 162));
    public static final AwarenessSet EDISON_SET = create4StarSet("edison", new AwarenessStats(102, 1059, 4, 48, 9, 97, 15, 162));
    public static final AwarenessSet ARCHIMEDES_SET = create4StarSet("archimedes", new AwarenessStats(102, 1059, 4, 49, 9, 97, 15, 158));
    public static final AwarenessSet NIMITZ_SET = create4StarSet("nimitiz", new AwarenessStats(104, 1081, 4, 48, 9, 97, 15, 158));
    
    public static final AwarenessSet AIFU_SET = create5StarSet("aifu", new AwarenessStats(132, 1184, 6, 53, 12, 109, 20, 181));
    public static final AwarenessSet AIKE_SET = create5StarSet("aike", new AwarenessStats(134, 1208, 5, 51, 12, 109, 20, 181));
    public static final AwarenessSet VOLTAIRE_SET = create5StarSet("voltaire", new AwarenessStats(137, 1232, 5, 51, 12, 107, 20, 181));
    public static final AwarenessSet GORODA_SET = create5StarSet("goroda", new AwarenessStats(137, 1232, 6, 53, 12, 107, 19, 177));
    
    public static final AwarenessSet RICHELIEU_SET = create5StarSet("richelieu", new AwarenessStats(134, 1208, 6, 53, 12, 107, 20, 181));
    public static final AwarenessSet SAMANDA_SET = create5StarSet("samanda", new AwarenessStats(134, 1208, 5, 51, 12, 107, 20, 185));
    public static final AwarenessSet MOZART_SET = create5StarSet("mozart", new AwarenessStats(134, 1208, 6, 53, 12, 109, 19, 177));
    public static final AwarenessSet BOUDICA_SET = create5StarSet("boudica", new AwarenessStats(134, 1208, 6, 53, 12, 109, 19, 177));

    public static final AwarenessSet EINSTEINNA_SET = create6StarSet("einsteinna", new AwarenessStats(152, 1521, 6, 66, 13, 132, 22, 219));
    public static final AwarenessSet KANTLINA_SET = create6StarSet("kantlina", new AwarenessStats(146, 1461, 6, 67, 13, 132, 22, 223));
    public static final AwarenessSet SHAKESPEARE_SET = create6StarSet("shakespeare", new AwarenessStats(149, 1491, 6, 64, 13, 134, 22, 223));
    public static final AwarenessSet HEISENBERG_SET = create6StarSet("heisenberg", new AwarenessStats(149, 1491, 6, 66, 13, 134, 22, 219));
    public static final AwarenessSet DARWIN_SET = create6StarSet("darwin", new AwarenessStats(149, 1491, 6, 67, 13, 129, 21, 218));

    public static final AwarenessSet HANNA_SET = create6StarSet("hanna", new AwarenessStats(149, 1491, 6, 66, 13, 134, 22, 219));
    public static final AwarenessSet DA_VINCI_SET = create6StarSet("da_vinci", new AwarenessStats(149, 1491, 6, 67, 13, 132, 22, 223));
    public static final AwarenessSet CATHERINE_SET = create6StarSet("catherine", new AwarenessStats(146, 1461, 6, 67, 13, 132, 22, 223));
    public static final AwarenessSet FERIET_II_SET = create6StarSet("feriet_ii", new AwarenessStats(149, 1491, 6, 66, 13, 134, 22, 219));
    public static final AwarenessSet GUINEY_SET = create6StarSet("guiney", new AwarenessStats(149, 1491, 6, 66, 13, 134, 22, 219));

    public static final AwarenessSet FRIEDRICH_SET = create6StarSet("friedrich", new AwarenessStats(145, 1446, 6, 64, 13, 136, 22, 223));
    public static final AwarenessSet PATTON_SET = create6StarSet("patton", new AwarenessStats(146, 1453, 6, 67, 13, 132, 22, 223));
    public static final AwarenessSet BASILONE_SET = create6StarSet("basilone", new AwarenessStats(146, 1453, 6, 64, 13, 134, 22, 223));

    public static final DeferredHolder<Item, Item> MERNIE_AWARENESS_1 = ITEMS.register("mernie_awareness_1",
            () -> new AwarenessItem(MERNIE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MERNIE_AWARENESS_2 = ITEMS.register("mernie_awareness_2",
            () -> new AwarenessItem(MERNIE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MERNIE_AWARENESS_3 = ITEMS.register("mernie_awareness_3",
            () -> new AwarenessItem(MERNIE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MERNIE_AWARENESS_4 = ITEMS.register("mernie_awareness_4",
            () -> new AwarenessItem(MERNIE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MERNIE_AWARENESS_5 = ITEMS.register("mernie_awareness_5",
            () -> new AwarenessItem(MERNIE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MERNIE_AWARENESS_6 = ITEMS.register("mernie_awareness_6",
            () -> new AwarenessItem(MERNIE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> PAVTY_AWARENESS_1 = ITEMS.register("pavty_awareness_1",
            () -> new AwarenessItem(PAVTY_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAVTY_AWARENESS_2 = ITEMS.register("pavty_awareness_2",
            () -> new AwarenessItem(PAVTY_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAVTY_AWARENESS_3 = ITEMS.register("pavty_awareness_3",
            () -> new AwarenessItem(PAVTY_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAVTY_AWARENESS_4 = ITEMS.register("pavty_awareness_4",
            () -> new AwarenessItem(PAVTY_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAVTY_AWARENESS_5 = ITEMS.register("pavty_awareness_5",
            () -> new AwarenessItem(PAVTY_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAVTY_AWARENESS_6 = ITEMS.register("pavty_awareness_6",
            () -> new AwarenessItem(PAVTY_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> PAMELIE_AWARENESS_1 = ITEMS.register("pamelie_awareness_1",
            () -> new AwarenessItem(PAMELIE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAMELIE_AWARENESS_2 = ITEMS.register("pamelie_awareness_2",
            () -> new AwarenessItem(PAMELIE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAMELIE_AWARENESS_3 = ITEMS.register("pamelie_awareness_3",
            () -> new AwarenessItem(PAMELIE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAMELIE_AWARENESS_4 = ITEMS.register("pamelie_awareness_4",
            () -> new AwarenessItem(PAMELIE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAMELIE_AWARENESS_5 = ITEMS.register("pamelie_awareness_5",
            () -> new AwarenessItem(PAMELIE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PAMELIE_AWARENESS_6 = ITEMS.register("pamelie_awareness_6",
            () -> new AwarenessItem(PAMELIE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> CURIE_COHET_AWARENESS_1 = ITEMS.register("curie_cohet_awareness_1",
            () -> new AwarenessItem(CURIE_COHET_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CURIE_COHET_AWARENESS_2 = ITEMS.register("curie_cohet_awareness_2",
            () -> new AwarenessItem(CURIE_COHET_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CURIE_COHET_AWARENESS_3 = ITEMS.register("curie_cohet_awareness_3",
            () -> new AwarenessItem(CURIE_COHET_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CURIE_COHET_AWARENESS_4 = ITEMS.register("curie_cohet_awareness_4",
            () -> new AwarenessItem(CURIE_COHET_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CURIE_COHET_AWARENESS_5 = ITEMS.register("curie_cohet_awareness_5",
            () -> new AwarenessItem(CURIE_COHET_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CURIE_COHET_AWARENESS_6 = ITEMS.register("curie_cohet_awareness_6",
            () -> new AwarenessItem(CURIE_COHET_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> CUNNINGHAM_AWARENESS_1 = ITEMS.register("cunningham_awareness_1",
            () -> new AwarenessItem(CUNNINGHAM_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CUNNINGHAM_AWARENESS_2 = ITEMS.register("cunningham_awareness_2",
            () -> new AwarenessItem(CUNNINGHAM_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CUNNINGHAM_AWARENESS_3 = ITEMS.register("cunningham_awareness_3",
            () -> new AwarenessItem(CUNNINGHAM_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CUNNINGHAM_AWARENESS_4 = ITEMS.register("cunningham_awareness_4",
            () -> new AwarenessItem(CUNNINGHAM_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CUNNINGHAM_AWARENESS_5 = ITEMS.register("cunningham_awareness_5",
            () -> new AwarenessItem(CUNNINGHAM_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CUNNINGHAM_AWARENESS_6 = ITEMS.register("cunningham_awareness_6",
            () -> new AwarenessItem(CUNNINGHAM_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> ALICE_AWARENESS_1 = ITEMS.register("alice_awareness_1",
            () -> new AwarenessItem(ALICE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ALICE_AWARENESS_2 = ITEMS.register("alice_awareness_2",
            () -> new AwarenessItem(ALICE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ALICE_AWARENESS_3 = ITEMS.register("alice_awareness_3",
            () -> new AwarenessItem(ALICE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ALICE_AWARENESS_4 = ITEMS.register("alice_awareness_4",
            () -> new AwarenessItem(ALICE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ALICE_AWARENESS_5 = ITEMS.register("alice_awareness_5",
            () -> new AwarenessItem(ALICE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ALICE_AWARENESS_6 = ITEMS.register("alice_awareness_6",
            () -> new AwarenessItem(ALICE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> EDISON_AWARENESS_1 = ITEMS.register("edison_awareness_1",
            () -> new AwarenessItem(EDISON_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EDISON_AWARENESS_2 = ITEMS.register("edison_awareness_2",
            () -> new AwarenessItem(EDISON_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EDISON_AWARENESS_3 = ITEMS.register("edison_awareness_3",
            () -> new AwarenessItem(EDISON_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EDISON_AWARENESS_4 = ITEMS.register("edison_awareness_4",
            () -> new AwarenessItem(EDISON_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EDISON_AWARENESS_5 = ITEMS.register("edison_awareness_5",
            () -> new AwarenessItem(EDISON_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EDISON_AWARENESS_6 = ITEMS.register("edison_awareness_6",
            () -> new AwarenessItem(EDISON_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> ARCHIMEDES_AWARENESS_1 = ITEMS.register("archimedes_awareness_1",
            () -> new AwarenessItem(ARCHIMEDES_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARCHIMEDES_AWARENESS_2 = ITEMS.register("archimedes_awareness_2",
            () -> new AwarenessItem(ARCHIMEDES_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARCHIMEDES_AWARENESS_3 = ITEMS.register("archimedes_awareness_3",
            () -> new AwarenessItem(ARCHIMEDES_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARCHIMEDES_AWARENESS_4 = ITEMS.register("archimedes_awareness_4",
            () -> new AwarenessItem(ARCHIMEDES_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARCHIMEDES_AWARENESS_5 = ITEMS.register("archimedes_awareness_5",
            () -> new AwarenessItem(ARCHIMEDES_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARCHIMEDES_AWARENESS_6 = ITEMS.register("archimedes_awareness_6",
            () -> new AwarenessItem(ARCHIMEDES_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> NIMITZ_AWARENESS_1 = ITEMS.register("nimitiz_awareness_1",
            () -> new AwarenessItem(NIMITZ_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> NIMITZ_AWARENESS_2 = ITEMS.register("nimitiz_awareness_2",
            () -> new AwarenessItem(NIMITZ_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> NIMITZ_AWARENESS_3 = ITEMS.register("nimitiz_awareness_3",
            () -> new AwarenessItem(NIMITZ_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> NIMITZ_AWARENESS_4 = ITEMS.register("nimitiz_awareness_4",
            () -> new AwarenessItem(NIMITZ_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> NIMITZ_AWARENESS_5 = ITEMS.register("nimitiz_awareness_5",
            () -> new AwarenessItem(NIMITZ_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> NIMITZ_AWARENESS_6 = ITEMS.register("nimitiz_awareness_6",
            () -> new AwarenessItem(NIMITZ_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> AIFU_AWARENESS_1 = ITEMS.register("aifu_awareness_1",
            () -> new AwarenessItem(AIFU_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIFU_AWARENESS_2 = ITEMS.register("aifu_awareness_2",
            () -> new AwarenessItem(AIFU_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIFU_AWARENESS_3 = ITEMS.register("aifu_awareness_3",
            () -> new AwarenessItem(AIFU_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIFU_AWARENESS_4 = ITEMS.register("aifu_awareness_4",
            () -> new AwarenessItem(AIFU_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIFU_AWARENESS_5 = ITEMS.register("aifu_awareness_5",
            () -> new AwarenessItem(AIFU_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIFU_AWARENESS_6 = ITEMS.register("aifu_awareness_6",
            () -> new AwarenessItem(AIFU_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> AIKE_AWARENESS_1 = ITEMS.register("aike_awareness_1",
            () -> new AwarenessItem(AIKE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIKE_AWARENESS_2 = ITEMS.register("aike_awareness_2",
            () -> new AwarenessItem(AIKE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIKE_AWARENESS_3 = ITEMS.register("aike_awareness_3",
            () -> new AwarenessItem(AIKE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIKE_AWARENESS_4 = ITEMS.register("aike_awareness_4",
            () -> new AwarenessItem(AIKE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIKE_AWARENESS_5 = ITEMS.register("aike_awareness_5",
            () -> new AwarenessItem(AIKE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> AIKE_AWARENESS_6 = ITEMS.register("aike_awareness_6",
            () -> new AwarenessItem(AIKE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> VOLTAIRE_AWARENESS_1 = ITEMS.register("voltaire_awareness_1",
            () -> new AwarenessItem(VOLTAIRE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> VOLTAIRE_AWARENESS_2 = ITEMS.register("voltaire_awareness_2",
            () -> new AwarenessItem(VOLTAIRE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> VOLTAIRE_AWARENESS_3 = ITEMS.register("voltaire_awareness_3",
            () -> new AwarenessItem(VOLTAIRE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> VOLTAIRE_AWARENESS_4 = ITEMS.register("voltaire_awareness_4",
            () -> new AwarenessItem(VOLTAIRE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> VOLTAIRE_AWARENESS_5 = ITEMS.register("voltaire_awareness_5",
            () -> new AwarenessItem(VOLTAIRE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> VOLTAIRE_AWARENESS_6 = ITEMS.register("voltaire_awareness_6",
            () -> new AwarenessItem(VOLTAIRE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> GORODA_AWARENESS_1 = ITEMS.register("goroda_awareness_1",
            () -> new AwarenessItem(GORODA_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GORODA_AWARENESS_2 = ITEMS.register("goroda_awareness_2",
            () -> new AwarenessItem(GORODA_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GORODA_AWARENESS_3 = ITEMS.register("goroda_awareness_3",
            () -> new AwarenessItem(GORODA_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GORODA_AWARENESS_4 = ITEMS.register("goroda_awareness_4",
            () -> new AwarenessItem(GORODA_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GORODA_AWARENESS_5 = ITEMS.register("goroda_awareness_5",
            () -> new AwarenessItem(GORODA_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GORODA_AWARENESS_6 = ITEMS.register("goroda_awareness_6",
            () -> new AwarenessItem(GORODA_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> RICHELIEU_AWARENESS_1 = ITEMS.register("richelieu_awareness_1",
            () -> new AwarenessItem(RICHELIEU_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> RICHELIEU_AWARENESS_2 = ITEMS.register("richelieu_awareness_2",
            () -> new AwarenessItem(RICHELIEU_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> RICHELIEU_AWARENESS_3 = ITEMS.register("richelieu_awareness_3",
            () -> new AwarenessItem(RICHELIEU_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> RICHELIEU_AWARENESS_4 = ITEMS.register("richelieu_awareness_4",
            () -> new AwarenessItem(RICHELIEU_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> RICHELIEU_AWARENESS_5 = ITEMS.register("richelieu_awareness_5",
            () -> new AwarenessItem(RICHELIEU_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> RICHELIEU_AWARENESS_6 = ITEMS.register("richelieu_awareness_6",
            () -> new AwarenessItem(RICHELIEU_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> SAMANDA_AWARENESS_1 = ITEMS.register("samanda_awareness_1",
            () -> new AwarenessItem(SAMANDA_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SAMANDA_AWARENESS_2 = ITEMS.register("samanda_awareness_2",
            () -> new AwarenessItem(SAMANDA_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SAMANDA_AWARENESS_3 = ITEMS.register("samanda_awareness_3",
            () -> new AwarenessItem(SAMANDA_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SAMANDA_AWARENESS_4 = ITEMS.register("samanda_awareness_4",
            () -> new AwarenessItem(SAMANDA_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SAMANDA_AWARENESS_5 = ITEMS.register("samanda_awareness_5",
            () -> new AwarenessItem(SAMANDA_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SAMANDA_AWARENESS_6 = ITEMS.register("samanda_awareness_6",
            () -> new AwarenessItem(SAMANDA_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> MOZART_AWARENESS_1 = ITEMS.register("mozart_awareness_1",
            () -> new AwarenessItem(MOZART_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MOZART_AWARENESS_2 = ITEMS.register("mozart_awareness_2",
            () -> new AwarenessItem(MOZART_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MOZART_AWARENESS_3 = ITEMS.register("mozart_awareness_3",
            () -> new AwarenessItem(MOZART_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MOZART_AWARENESS_4 = ITEMS.register("mozart_awareness_4",
            () -> new AwarenessItem(MOZART_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MOZART_AWARENESS_5 = ITEMS.register("mozart_awareness_5",
            () -> new AwarenessItem(MOZART_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MOZART_AWARENESS_6 = ITEMS.register("mozart_awareness_6",
            () -> new AwarenessItem(MOZART_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> BOUDICA_AWARENESS_1 = ITEMS.register("boudica_awareness_1",
            () -> new AwarenessItem(BOUDICA_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BOUDICA_AWARENESS_2 = ITEMS.register("boudica_awareness_2",
            () -> new AwarenessItem(BOUDICA_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BOUDICA_AWARENESS_3 = ITEMS.register("boudica_awareness_3",
            () -> new AwarenessItem(BOUDICA_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BOUDICA_AWARENESS_4 = ITEMS.register("boudica_awareness_4",
            () -> new AwarenessItem(BOUDICA_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BOUDICA_AWARENESS_5 = ITEMS.register("boudica_awareness_5",
            () -> new AwarenessItem(BOUDICA_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BOUDICA_AWARENESS_6 = ITEMS.register("boudica_awareness_6",
            () -> new AwarenessItem(BOUDICA_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> EINSTEINNA_AWARENESS_1 = ITEMS.register("einsteinna_awareness_1",
            () -> new AwarenessItem(EINSTEINNA_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EINSTEINNA_AWARENESS_2 = ITEMS.register("einsteinna_awareness_2",
            () -> new AwarenessItem(EINSTEINNA_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EINSTEINNA_AWARENESS_3 = ITEMS.register("einsteinna_awareness_3",
            () -> new AwarenessItem(EINSTEINNA_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EINSTEINNA_AWARENESS_4 = ITEMS.register("einsteinna_awareness_4",
            () -> new AwarenessItem(EINSTEINNA_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EINSTEINNA_AWARENESS_5 = ITEMS.register("einsteinna_awareness_5",
            () -> new AwarenessItem(EINSTEINNA_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EINSTEINNA_AWARENESS_6 = ITEMS.register("einsteinna_awareness_6",
            () -> new AwarenessItem(EINSTEINNA_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> KANTLINA_AWARENESS_1 = ITEMS.register("kantlina_awareness_1",
            () -> new AwarenessItem(KANTLINA_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> KANTLINA_AWARENESS_2 = ITEMS.register("kantlina_awareness_2",
            () -> new AwarenessItem(KANTLINA_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> KANTLINA_AWARENESS_3 = ITEMS.register("kantlina_awareness_3",
            () -> new AwarenessItem(KANTLINA_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> KANTLINA_AWARENESS_4 = ITEMS.register("kantlina_awareness_4",
            () -> new AwarenessItem(KANTLINA_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> KANTLINA_AWARENESS_5 = ITEMS.register("kantlina_awareness_5",
            () -> new AwarenessItem(KANTLINA_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> KANTLINA_AWARENESS_6 = ITEMS.register("kantlina_awareness_6",
            () -> new AwarenessItem(KANTLINA_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> SHAKESPEARE_AWARENESS_1 = ITEMS.register("shakespeare_awareness_1",
            () -> new AwarenessItem(SHAKESPEARE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SHAKESPEARE_AWARENESS_2 = ITEMS.register("shakespeare_awareness_2",
            () -> new AwarenessItem(SHAKESPEARE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SHAKESPEARE_AWARENESS_3 = ITEMS.register("shakespeare_awareness_3",
            () -> new AwarenessItem(SHAKESPEARE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SHAKESPEARE_AWARENESS_4 = ITEMS.register("shakespeare_awareness_4",
            () -> new AwarenessItem(SHAKESPEARE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SHAKESPEARE_AWARENESS_5 = ITEMS.register("shakespeare_awareness_5",
            () -> new AwarenessItem(SHAKESPEARE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SHAKESPEARE_AWARENESS_6 = ITEMS.register("shakespeare_awareness_6",
            () -> new AwarenessItem(SHAKESPEARE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> HEISENBERG_AWARENESS_1 = ITEMS.register("heisenberg_awareness_1",
            () -> new AwarenessItem(HEISENBERG_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HEISENBERG_AWARENESS_2 = ITEMS.register("heisenberg_awareness_2",
            () -> new AwarenessItem(HEISENBERG_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HEISENBERG_AWARENESS_3 = ITEMS.register("heisenberg_awareness_3",
            () -> new AwarenessItem(HEISENBERG_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HEISENBERG_AWARENESS_4 = ITEMS.register("heisenberg_awareness_4",
            () -> new AwarenessItem(HEISENBERG_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HEISENBERG_AWARENESS_5 = ITEMS.register("heisenberg_awareness_5",
            () -> new AwarenessItem(HEISENBERG_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HEISENBERG_AWARENESS_6 = ITEMS.register("heisenberg_awareness_6",
            () -> new AwarenessItem(HEISENBERG_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> DARWIN_AWARENESS_1 = ITEMS.register("darwin_awareness_1",
            () -> new AwarenessItem(DARWIN_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DARWIN_AWARENESS_2 = ITEMS.register("darwin_awareness_2",
            () -> new AwarenessItem(DARWIN_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DARWIN_AWARENESS_3 = ITEMS.register("darwin_awareness_3",
            () -> new AwarenessItem(DARWIN_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DARWIN_AWARENESS_4 = ITEMS.register("darwin_awareness_4",
            () -> new AwarenessItem(DARWIN_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DARWIN_AWARENESS_5 = ITEMS.register("darwin_awareness_5",
            () -> new AwarenessItem(DARWIN_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DARWIN_AWARENESS_6 = ITEMS.register("darwin_awareness_6",
            () -> new AwarenessItem(DARWIN_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> HANNA_AWARENESS_1 = ITEMS.register("hanna_awareness_1",
            () -> new AwarenessItem(HANNA_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HANNA_AWARENESS_2 = ITEMS.register("hanna_awareness_2",
            () -> new AwarenessItem(HANNA_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HANNA_AWARENESS_3 = ITEMS.register("hanna_awareness_3",
            () -> new AwarenessItem(HANNA_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HANNA_AWARENESS_4 = ITEMS.register("hanna_awareness_4",
            () -> new AwarenessItem(HANNA_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HANNA_AWARENESS_5 = ITEMS.register("hanna_awareness_5",
            () -> new AwarenessItem(HANNA_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HANNA_AWARENESS_6 = ITEMS.register("hanna_awareness_6",
            () -> new AwarenessItem(HANNA_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> DA_VINCI_AWARENESS_1 = ITEMS.register("da_vinci_awareness_1",
            () -> new AwarenessItem(DA_VINCI_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DA_VINCI_AWARENESS_2 = ITEMS.register("da_vinci_awareness_2",
            () -> new AwarenessItem(DA_VINCI_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DA_VINCI_AWARENESS_3 = ITEMS.register("da_vinci_awareness_3",
            () -> new AwarenessItem(DA_VINCI_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DA_VINCI_AWARENESS_4 = ITEMS.register("da_vinci_awareness_4",
            () -> new AwarenessItem(DA_VINCI_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DA_VINCI_AWARENESS_5 = ITEMS.register("da_vinci_awareness_5",
            () -> new AwarenessItem(DA_VINCI_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> DA_VINCI_AWARENESS_6 = ITEMS.register("da_vinci_awareness_6",
            () -> new AwarenessItem(DA_VINCI_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> CATHERINE_AWARENESS_1 = ITEMS.register("catherine_awareness_1",
            () -> new AwarenessItem(CATHERINE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CATHERINE_AWARENESS_2 = ITEMS.register("catherine_awareness_2",
            () -> new AwarenessItem(CATHERINE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CATHERINE_AWARENESS_3 = ITEMS.register("catherine_awareness_3",
            () -> new AwarenessItem(CATHERINE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CATHERINE_AWARENESS_4 = ITEMS.register("catherine_awareness_4",
            () -> new AwarenessItem(CATHERINE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CATHERINE_AWARENESS_5 = ITEMS.register("catherine_awareness_5",
            () -> new AwarenessItem(CATHERINE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CATHERINE_AWARENESS_6 = ITEMS.register("catherine_awareness_6",
            () -> new AwarenessItem(CATHERINE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> FERIET_II_AWARENESS_1 = ITEMS.register("feriet_ii_awareness_1",
            () -> new AwarenessItem(FERIET_II_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FERIET_II_AWARENESS_2 = ITEMS.register("feriet_ii_awareness_2",
            () -> new AwarenessItem(FERIET_II_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FERIET_II_AWARENESS_3 = ITEMS.register("feriet_ii_awareness_3",
            () -> new AwarenessItem(FERIET_II_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FERIET_II_AWARENESS_4 = ITEMS.register("feriet_ii_awareness_4",
            () -> new AwarenessItem(FERIET_II_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FERIET_II_AWARENESS_5 = ITEMS.register("feriet_ii_awareness_5",
            () -> new AwarenessItem(FERIET_II_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FERIET_II_AWARENESS_6 = ITEMS.register("feriet_ii_awareness_6",
            () -> new AwarenessItem(FERIET_II_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> GUINEY_AWARENESS_1 = ITEMS.register("guiney_awareness_1",
            () -> new AwarenessItem(GUINEY_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GUINEY_AWARENESS_2 = ITEMS.register("guiney_awareness_2",
            () -> new AwarenessItem(GUINEY_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GUINEY_AWARENESS_3 = ITEMS.register("guiney_awareness_3",
            () -> new AwarenessItem(GUINEY_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GUINEY_AWARENESS_4 = ITEMS.register("guiney_awareness_4",
            () -> new AwarenessItem(GUINEY_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GUINEY_AWARENESS_5 = ITEMS.register("guiney_awareness_5",
            () -> new AwarenessItem(GUINEY_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GUINEY_AWARENESS_6 = ITEMS.register("guiney_awareness_6",
            () -> new AwarenessItem(GUINEY_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> FRIEDRICH_AWARENESS_1 = ITEMS.register("friedrich_awareness_1",
            () -> new AwarenessItem(FRIEDRICH_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FRIEDRICH_AWARENESS_2 = ITEMS.register("friedrich_awareness_2",
            () -> new AwarenessItem(FRIEDRICH_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FRIEDRICH_AWARENESS_3 = ITEMS.register("friedrich_awareness_3",
            () -> new AwarenessItem(FRIEDRICH_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FRIEDRICH_AWARENESS_4 = ITEMS.register("friedrich_awareness_4",
            () -> new AwarenessItem(FRIEDRICH_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FRIEDRICH_AWARENESS_5 = ITEMS.register("friedrich_awareness_5",
            () -> new AwarenessItem(FRIEDRICH_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FRIEDRICH_AWARENESS_6 = ITEMS.register("friedrich_awareness_6",
            () -> new AwarenessItem(FRIEDRICH_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> PATTON_AWARENESS_1 = ITEMS.register("patton_awareness_1",
            () -> new AwarenessItem(PATTON_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PATTON_AWARENESS_2 = ITEMS.register("patton_awareness_2",
            () -> new AwarenessItem(PATTON_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PATTON_AWARENESS_3 = ITEMS.register("patton_awareness_3",
            () -> new AwarenessItem(PATTON_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PATTON_AWARENESS_4 = ITEMS.register("patton_awareness_4",
            () -> new AwarenessItem(PATTON_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PATTON_AWARENESS_5 = ITEMS.register("patton_awareness_5",
            () -> new AwarenessItem(PATTON_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> PATTON_AWARENESS_6 = ITEMS.register("patton_awareness_6",
            () -> new AwarenessItem(PATTON_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> BASILONE_AWARENESS_1 = ITEMS.register("basilone_awareness_1",
            () -> new AwarenessItem(BASILONE_SET, 0, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASILONE_AWARENESS_2 = ITEMS.register("basilone_awareness_2",
            () -> new AwarenessItem(BASILONE_SET, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASILONE_AWARENESS_3 = ITEMS.register("basilone_awareness_3",
            () -> new AwarenessItem(BASILONE_SET, 2, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASILONE_AWARENESS_4 = ITEMS.register("basilone_awareness_4",
            () -> new AwarenessItem(BASILONE_SET, 3, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASILONE_AWARENESS_5 = ITEMS.register("basilone_awareness_5",
            () -> new AwarenessItem(BASILONE_SET, 4, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BASILONE_AWARENESS_6 = ITEMS.register("basilone_awareness_6",
            () -> new AwarenessItem(BASILONE_SET, 5, new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> HUMANITY_STANDS_HERE = ITEMS.register("humanity_stands_here",
            () -> new UpgradeKeyItem(new Item.Properties().stacksTo(1)));

    // 制式刀（太刀类）—— 静态 3D 模型
    public static final DeferredHolder<Item, Item> TACHI = ITEMS.register("tachi",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.4F)), "tachi"));

    // 护身刀——短刀，攻速快伤害低
    public static final DeferredHolder<Item, Item> HUGOTOU = ITEMS.register("hugotou",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 5, -2.0F)), "hugotou"));

    // 无铭——标准太刀
    public static final DeferredHolder<Item, Item> MUMEI = ITEMS.register("mumei",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 7, -2.4F)), "mumei"));

    // 九字之定——重型太刀，带机械装置
    public static final DeferredHolder<Item, Item> KYUUJI = ITEMS.register("kyuuji",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.6F)), "kyuuji"));

    // 红莲狂士——太刀，带能量核心装置
    public static final DeferredHolder<Item, Item> HONGLIAN = ITEMS.register("honglian",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 9, -2.6F)), "honglian"));

    // 逆元碎片-红莲——红莲狂士专属进阶材料
    public static final DeferredHolder<Item, Item> INVERSE_FRAGMENT_HONGLIAN = ITEMS.register("inverse_fragment_honglian",
            () -> new MaterialItem(new Item.Properties().stacksTo(64), List.of(
                    Component.translatable("tooltip.icy_punishing.inverse_fragment_honglian.desc")
                            .withStyle(ChatFormatting.GRAY),
                    Component.empty(),
                    Component.translatable("tooltip.icy_punishing.inverse_fragment_honglian.lore")
                            .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC))));

    // 红樱——太刀，全红刀身
    public static final DeferredHolder<Item, Item> HONGYING = ITEMS.register("hongying",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.4F)), "hongying"));

    // 天狼星——太刀，米白刀身
    public static final DeferredHolder<Item, Item> TIANLANG = ITEMS.register("tianlang",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.5F)), "tianlang"));

    // 影打-逆断——太刀，带能量线与爪式装置
    public static final DeferredHolder<Item, Item> YINGDA = ITEMS.register("yingda",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 9, -2.6F)), "yingda"));

    // 朱桦——四阶段
    public static final DeferredHolder<Item, Item> ZHUHUA_C1 = ITEMS.register("zhuhua_c1",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.5F)), "zhuhua_c1"));
    public static final DeferredHolder<Item, Item> ZHUHUA_C2 = ITEMS.register("zhuhua_c2",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.4F)), "zhuhua_c2"));
    public static final DeferredHolder<Item, Item> ZHUHUA_C3 = ITEMS.register("zhuhua_c3",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 9, -2.4F)), "zhuhua_c3"));
    public static final DeferredHolder<Item, Item> ZHUHUA_C4 = ITEMS.register("zhuhua_c4",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 9, -2.3F)), "zhuhua_c4"));

    // 沙利叶——四阶段
    public static final DeferredHolder<Item, Item> SARARIEL_C1 = ITEMS.register("sarariel_c1",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.5F)), "sarariel_c1"));
    public static final DeferredHolder<Item, Item> SARARIEL_C2 = ITEMS.register("sarariel_c2",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 8, -2.4F)), "sarariel_c2"));
    public static final DeferredHolder<Item, Item> SARARIEL_C3 = ITEMS.register("sarariel_c3",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 9, -2.4F)), "sarariel_c3"));
    public static final DeferredHolder<Item, Item> SARARIEL_C4 = ITEMS.register("sarariel_c4",
            () -> new ObjKatanaItem(new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 9, -2.3F)), "sarariel_c4"));

    // 太刀涂层——纯外观
    public static final DeferredHolder<Item, Item> FENYING = ITEMS.register("fenying",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LUPERCALIA = ITEMS.register("lupercalia",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CANGUANG = ITEMS.register("canguang",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GULINFENGREN = ITEMS.register("gulinfengren",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MUSEYIDIAN = ITEMS.register("museyidian",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FLORANUM = ITEMS.register("floranum",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // 弓类
    public static final DeferredHolder<Item, Item> LEIMIER = ITEMS.register("leimier",
            () -> new BowItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LAITENING = ITEMS.register("laitening",
            () -> new BowItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASITELIYA = ITEMS.register("asiteliya",
            () -> new BowItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LIESUN = ITEMS.register("liesun",
            () -> new BowItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HUALUN = ITEMS.register("hualun",
            () -> new BowItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LIANXI = ITEMS.register("lianxi",
            () -> new BowItem(new Item.Properties().stacksTo(1)));

    // 弓涂层——纯外观
    public static final DeferredHolder<Item, Item> ZIYUAN = ITEMS.register("ziyuan",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> CHUNBAISHENGZAN = ITEMS.register("chunbaishengzan",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> YINYEJINGHONG = ITEMS.register("yinyejinghong",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> XURI = ITEMS.register("xuri",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> XUNYEZHIHU = ITEMS.register("xunyezhihu",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> XIAYE = ITEMS.register("xiaye",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> REXIAHONGXIAN = ITEMS.register("rexiahongxian",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> NUT = ITEMS.register("nut",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.nut.desc", "tooltip.icy_punishing.nut.lore", "tooltip.icy_punishing.nut.obtain")));

    public static final DeferredHolder<Item, Item> BREAKTHROUGH_ALLOY_LOW = ITEMS.register("breakthrough_alloy_low",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.breakthrough_alloy_low.desc", "tooltip.icy_punishing.breakthrough_alloy_low.lore", "tooltip.icy_punishing.breakthrough_alloy_low.obtain")));

    public static final DeferredHolder<Item, Item> BREAKTHROUGH_ALLOY_HIGH = ITEMS.register("breakthrough_alloy_high",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.breakthrough_alloy_high.desc", "tooltip.icy_punishing.breakthrough_alloy_high.lore", "tooltip.icy_punishing.breakthrough_alloy_high.obtain")));

    public static final DeferredHolder<Item, Item> AWARENESS_CIRCUIT_LOW = ITEMS.register("awareness_circuit_low",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.awareness_circuit_low.desc", "tooltip.icy_punishing.awareness_circuit_low.lore", "tooltip.icy_punishing.awareness_circuit_low.obtain")));

    public static final DeferredHolder<Item, Item> AWARENESS_CIRCUIT_HIGH = ITEMS.register("awareness_circuit_high",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.awareness_circuit_high.desc", "tooltip.icy_punishing.awareness_circuit_high.lore", "tooltip.icy_punishing.awareness_circuit_high.obtain")));

    // 意识强化素材 II/III/IV（tooltip 一致）
    public static final DeferredHolder<Item, Item> AWARENESS_MATERIAL_2 = ITEMS.register("awareness_material_2",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.awareness_material.desc", "tooltip.icy_punishing.awareness_material.lore", "tooltip.icy_punishing.awareness_material.obtain")));
    public static final DeferredHolder<Item, Item> AWARENESS_MATERIAL_3 = ITEMS.register("awareness_material_3",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.awareness_material.desc", "tooltip.icy_punishing.awareness_material.lore", "tooltip.icy_punishing.awareness_material.obtain")));
    public static final DeferredHolder<Item, Item> AWARENESS_MATERIAL_4 = ITEMS.register("awareness_material_4",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.awareness_material.desc", "tooltip.icy_punishing.awareness_material.lore", "tooltip.icy_punishing.awareness_material.obtain")));

    // 武器强化素材 II/III/IV（tooltip 一致）
    public static final DeferredHolder<Item, Item> WEAPON_MATERIAL_2 = ITEMS.register("weapon_material_2",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.weapon_material.desc", "tooltip.icy_punishing.weapon_material.lore", "tooltip.icy_punishing.weapon_material.obtain")));
    public static final DeferredHolder<Item, Item> WEAPON_MATERIAL_3 = ITEMS.register("weapon_material_3",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.weapon_material.desc", "tooltip.icy_punishing.weapon_material.lore", "tooltip.icy_punishing.weapon_material.obtain")));
    public static final DeferredHolder<Item, Item> WEAPON_MATERIAL_4 = ITEMS.register("weapon_material_4",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.weapon_material.desc", "tooltip.icy_punishing.weapon_material.lore", "tooltip.icy_punishing.weapon_material.obtain")));

    // 武器突破核心·初/高
    public static final DeferredHolder<Item, Item> WEAPON_BREAKTHROUGH_CORE_LOW = ITEMS.register("weapon_breakthrough_core_low",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.weapon_breakthrough_core_low.desc", "tooltip.icy_punishing.weapon_breakthrough_core_low.lore", "tooltip.icy_punishing.weapon_breakthrough_core_low.obtain")));
    public static final DeferredHolder<Item, Item> WEAPON_BREAKTHROUGH_CORE_HIGH = ITEMS.register("weapon_breakthrough_core_high",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.weapon_breakthrough_core_high.desc", "tooltip.icy_punishing.weapon_breakthrough_core_high.lore", "tooltip.icy_punishing.weapon_breakthrough_core_high.obtain")));

    // 碎片算力
    public static final DeferredHolder<Item, Item> COMPUTE_FRAGMENT = ITEMS.register("compute_fragment",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.compute_fragment.desc", "tooltip.icy_punishing.compute_fragment.lore", "tooltip.icy_punishing.compute_fragment.obtain")));

    // 经验仓·小/中/大/特大（背景故事与获取方法一致，仅描述中经验量不同）
    public static final DeferredHolder<Item, Item> EXP_CONTAINER_SMALL = ITEMS.register("exp_container_small",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.exp_container_small.desc", "tooltip.icy_punishing.exp_container.lore", "tooltip.icy_punishing.exp_container.obtain")));
    public static final DeferredHolder<Item, Item> EXP_CONTAINER_MEDIUM = ITEMS.register("exp_container_medium",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.exp_container_medium.desc", "tooltip.icy_punishing.exp_container.lore", "tooltip.icy_punishing.exp_container.obtain")));
    public static final DeferredHolder<Item, Item> EXP_CONTAINER_LARGE = ITEMS.register("exp_container_large",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.exp_container_large.desc", "tooltip.icy_punishing.exp_container.lore", "tooltip.icy_punishing.exp_container.obtain")));
    public static final DeferredHolder<Item, Item> EXP_CONTAINER_XLARGE = ITEMS.register("exp_container_xlarge",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltip("tooltip.icy_punishing.exp_container_xlarge.desc", "tooltip.icy_punishing.exp_container.lore", "tooltip.icy_punishing.exp_container.obtain")));

    // 螺母包·小/中/大/特大/超大（tooltip 一致，不含"使用后可获得10000螺母"）
    public static final DeferredHolder<Item, Item> NUT_PACK_SMALL = ITEMS.register("nut_pack_small",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltipNoDesc("tooltip.icy_punishing.nut_pack.lore", "tooltip.icy_punishing.nut_pack.obtain")));
    public static final DeferredHolder<Item, Item> NUT_PACK_MEDIUM = ITEMS.register("nut_pack_medium",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltipNoDesc("tooltip.icy_punishing.nut_pack.lore", "tooltip.icy_punishing.nut_pack.obtain")));
    public static final DeferredHolder<Item, Item> NUT_PACK_LARGE = ITEMS.register("nut_pack_large",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltipNoDesc("tooltip.icy_punishing.nut_pack.lore", "tooltip.icy_punishing.nut_pack.obtain")));
    public static final DeferredHolder<Item, Item> NUT_PACK_XLARGE = ITEMS.register("nut_pack_xlarge",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltipNoDesc("tooltip.icy_punishing.nut_pack.lore", "tooltip.icy_punishing.nut_pack.obtain")));
    public static final DeferredHolder<Item, Item> NUT_PACK_XXLARGE = ITEMS.register("nut_pack_xxlarge",
            () -> new MaterialItem(new Item.Properties().stacksTo(64),
                    materialTooltipNoDesc("tooltip.icy_punishing.nut_pack.lore", "tooltip.icy_punishing.nut_pack.obtain")));

    private static AwarenessSet create2StarSet(String name, AwarenessStats stats) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, name);
        AwarenessSet set = new AwarenessSet(id);
        set.setPortraitLocation(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + ".png"));
        set.setStats(stats);
        set.setRank(2);
        AWARENESS_SETS.put(id, set);
        return set;
    }

    private static AwarenessSet create3StarSet(String name, AwarenessStats stats) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, name);
        AwarenessSet set = new AwarenessSet(id);
        set.setPortraitLocation(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + ".png"));
        set.setStats(stats);
        set.setRank(3);
        AWARENESS_SETS.put(id, set);
        return set;
    }

    private static AwarenessSet create4StarSet(String name, AwarenessStats stats) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, name);
        AwarenessSet set = new AwarenessSet(id);
        set.setPortraitLocation(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + ".png"));
        set.setStats(stats);
        set.setRank(4);
        AWARENESS_SETS.put(id, set);
        return set;
    }

    private static AwarenessSet create5StarSet(String name, AwarenessStats stats) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, name);
        AwarenessSet set = new AwarenessSet(id);
        set.setPortraitLocations(
                ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + "_slot1.png"),
                ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + "_slot2.png"),
                ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + "_slot3.png")
        );
        set.setStats(stats);
        set.setRank(5);
        AWARENESS_SETS.put(id, set);
        return set;
    }

    private static AwarenessSet create6StarSet(String name, AwarenessStats stats) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, name);
        AwarenessSet set = new AwarenessSet(id);
        set.setPortraitLocation(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + ".png"));
        set.setPortraitLocations(
                ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + "_slot1.png"),
                ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + "_slot2.png"),
                ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/awareness/awareness_" + name + "_slot3.png")
        );
        set.setStats(stats);
        set.setRank(6);
        AWARENESS_SETS.put(id, set);
        return set;
    }

    /**
     * 构建材料物品 tooltip：描述行、空行、背景故事行、空行、获取方法行。
     */
    private static List<Component> materialTooltip(String descKey, String loreKey, String obtainKey) {
        return List.of(
                Component.translatable(descKey).withStyle(ChatFormatting.GRAY),
                Component.empty(),
                Component.translatable(loreKey).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                Component.empty(),
                Component.translatable(obtainKey).withStyle(ChatFormatting.DARK_GRAY)
        );
    }

    /**
     * 构建不含描述行的材料 tooltip（用于螺母包等去掉首行描述的物品）。
     */
    private static List<Component> materialTooltipNoDesc(String loreKey, String obtainKey) {
        return List.of(
                Component.translatable(loreKey).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                Component.empty(),
                Component.translatable(obtainKey).withStyle(ChatFormatting.DARK_GRAY)
        );
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static final ResourceLocation MERNIE_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mernie_2pc");
    private static final ResourceLocation MERNIE_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mernie_4pc");
    private static final ResourceLocation PAVTY_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pavty_2pc");
    private static final ResourceLocation PAVTY_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pavty_4pc");
    private static final ResourceLocation PAMELIE_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pamelie_2pc");
    private static final ResourceLocation PAMELIE_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pamelie_4pc");
    private static final ResourceLocation CURIE_COHET_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "curie_cohet_2pc");
    private static final ResourceLocation CUNNINGHAM_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "cunningham_2pc");
    private static final ResourceLocation CUNNINGHAM_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "cunningham_4pc");
    private static final ResourceLocation ALICE_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "alice_2pc");
    private static final ResourceLocation ALICE_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "alice_4pc");
    private static final ResourceLocation EDISON_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "edison_2pc");
    private static final ResourceLocation EDISON_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "edison_4pc");
    private static final ResourceLocation ARCHIMEDES_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "archimedes_2pc");
    private static final ResourceLocation ARCHIMEDES_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "archimedes_4pc");
    private static final ResourceLocation NIMITZ_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "nimitiz_2pc");
    private static final ResourceLocation NIMITZ_6PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "nimitiz_6pc");

    private static final ResourceLocation AIFU_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "aifu_2pc");
    private static final ResourceLocation AIKE_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "aike_2pc");
    private static final ResourceLocation VOLTAIRE_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "voltaire_2pc");
    private static final ResourceLocation GORODA_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "goroda_2pc");

    private static final ResourceLocation RICHELIEU_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "richelieu_2pc");
    private static final ResourceLocation SAMANDA_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "samanda_2pc");
    private static final ResourceLocation MOZART_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mozart_2pc");
    private static final ResourceLocation BOUDICA_2PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "boudica_2pc");
    private static final ResourceLocation BOUDICA_4PC_ID = ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "boudica_4pc");

    static {
        MERNIE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mernie_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {
                AttributeModifier modifier = new AttributeModifier(MERNIE_2PC_ID,
                        10.0, AttributeModifier.Operation.ADD_VALUE);
                addModifierIfAbsent(entity.getAttribute(Attributes.ATTACK_DAMAGE), modifier);
            }

            @Override
            public void remove(LivingEntity entity) {
                removeModifierIfPresent(entity.getAttribute(Attributes.ATTACK_DAMAGE), MERNIE_2PC_ID);
            }
        });

        MERNIE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mernie_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {
                AttributeModifier modifier = new AttributeModifier(MERNIE_4PC_ID,
                        50.0, AttributeModifier.Operation.ADD_VALUE);
                addModifierIfAbsent(entity.getAttribute(Attributes.MAX_HEALTH), modifier);
            }

            @Override
            public void remove(LivingEntity entity) {
                removeModifierIfPresent(entity.getAttribute(Attributes.MAX_HEALTH), MERNIE_4PC_ID);
            }
        });

        PAVTY_SET.addEffect(new ProximityEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pavty_2pc"), 2, 2.0, true, true, 3.0));
        PAVTY_SET.addEffect(new ProximityEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pavty_4pc"), 4, 2.0, true, false, 3.0));

        PAMELIE_SET.addEffect(new ProximityEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pamelie_2pc"), 2, 3.0, false, true, 3.0));
        PAMELIE_SET.addEffect(new ProximityEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "pamelie_4pc"), 4, 3.0, false, false, 3.0));

        CURIE_COHET_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "curie_cohet_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}
        });

        CUNNINGHAM_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "cunningham_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public float onAttackFortitude(boolean hasFortitude) {
                return hasFortitude ? 1.05f : 1.0f;
            }
        });

        CUNNINGHAM_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "cunningham_4pc"), 4) {
            private int cooldown = 0;
            private boolean hasShield = false;

            @Override
            public void tick(LivingEntity entity, int equippedCount) {
                if (cooldown > 0) cooldown--;
                if (hasShield && cooldown == 0) hasShield = false;
            }

            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public void onHit(LivingEntity entity) {
                if (cooldown == 0) {
                    hasShield = true;
                    cooldown = 5 * 20;
                }
            }

            public float reduceDamage(float damage) {
                if (hasShield) {
                    hasShield = false;
                    return damage * 0.7f;
                }
                return damage;
            }
        });

        ALICE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "alice_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public float onHeal(float healAmount) {
                return healAmount * 1.15f;
            }
        });

        ALICE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "alice_4pc"), 4) {
            private int buffDuration = 0;

            @Override
            public void tick(LivingEntity entity, int equippedCount) {
                if (buffDuration > 0) buffDuration--;
            }

            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public void onHeal() {
                buffDuration = 4 * 20;
            }

            public float getDamageBonus() {
                return buffDuration > 0 ? 1.04f : 1.0f;
            }
        });

        EDISON_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "edison_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public float getCritDamageBonus() {
                return 1.05f;
            }
        });

        EDISON_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "edison_4pc"), 4) {
            private int buffDuration = 0;

            @Override
            public void tick(LivingEntity entity, int equippedCount) {
                if (buffDuration > 0) buffDuration--;
            }

            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public void onCrit() {
                buffDuration = 5 * 20;
            }

            public float getDamageBonus() {
                return buffDuration > 0 ? 1.05f : 1.0f;
            }
        });

        ARCHIMEDES_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "archimedes_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {
                AttributeModifier modifier = new AttributeModifier(ARCHIMEDES_2PC_ID,
                        0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                addModifierIfAbsent(entity.getAttribute(Attributes.ATTACK_DAMAGE), modifier);
            }

            @Override
            public void remove(LivingEntity entity) {
                removeModifierIfPresent(entity.getAttribute(Attributes.ATTACK_DAMAGE), ARCHIMEDES_2PC_ID);
            }
        });

        ARCHIMEDES_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "archimedes_4pc"), 4) {
            private int tickCounter = 0;
            private boolean empowered = false;

            @Override
            public void tick(LivingEntity entity, int equippedCount) {
                tickCounter++;
                if (tickCounter >= 10 * 20) {
                    empowered = true;
                }
            }

            @Override
            public void apply(LivingEntity entity, int equippedCount) {
                tickCounter = 0;
            }

            @Override
            public void remove(LivingEntity entity) {
                empowered = false;
            }

            public float getDamageBonus() {
                if (empowered) {
                    empowered = false;
                    tickCounter = 0;
                    return 1.1f;
                }
                return 1.0f;
            }
        });

        NIMITZ_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "nimitiz_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {
                AttributeModifier modifier = new AttributeModifier(NIMITZ_2PC_ID,
                        0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                addModifierIfAbsent(entity.getAttribute(Attributes.ATTACK_DAMAGE), modifier);
            }

            @Override
            public void remove(LivingEntity entity) {
                removeModifierIfPresent(entity.getAttribute(Attributes.ATTACK_DAMAGE), NIMITZ_2PC_ID);
            }
        });

        NIMITZ_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "nimitiz_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            public float getDamageBonus() {
                return 1.05f;
            }
        });

        NIMITZ_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "nimitiz_6pc"), 6) {
            @Override
            public void apply(LivingEntity entity, int equippedCount) {}

            @Override
            public void remove(LivingEntity entity) {}

            @Override
            public float getMinionDamageBonus() {
                return 1.1f;
            }
        });

        AIFU_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "aifu_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        AIFU_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "aifu_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        AIKE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "aike_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        AIKE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "aike_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        VOLTAIRE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "voltaire_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        VOLTAIRE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "voltaire_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        GORODA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "goroda_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        GORODA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "goroda_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        RICHELIEU_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "richelieu_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        RICHELIEU_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "richelieu_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        SAMANDA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "samanda_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        SAMANDA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "samanda_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        MOZART_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mozart_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        MOZART_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "mozart_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        BOUDICA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "boudica_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {
                AttributeModifier modifier = new AttributeModifier(BOUDICA_2PC_ID,
                        0.07, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                addModifierIfAbsent(entity.getAttribute(Attributes.ATTACK_DAMAGE), modifier);
            }
            @Override
            public void remove(LivingEntity entity) {
                removeModifierIfPresent(entity.getAttribute(Attributes.ATTACK_DAMAGE), BOUDICA_2PC_ID);
            }
        });

        BOUDICA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "boudica_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        BOUDICA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "boudica_6pc"), 6) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        EINSTEINNA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "einsteinna_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        EINSTEINNA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "einsteinna_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        KANTLINA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "kantlina_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {
                AttributeModifier modifier = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "kantlina_2pc"),
                        0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                addModifierIfAbsent(entity.getAttribute(Attributes.ATTACK_DAMAGE), modifier);
            }
            @Override
            public void remove(LivingEntity entity) {
                removeModifierIfPresent(entity.getAttribute(Attributes.ATTACK_DAMAGE), ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "kantlina_2pc"));
            }
        });

        KANTLINA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "kantlina_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        SHAKESPEARE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "shakespeare_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        SHAKESPEARE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "shakespeare_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        HEISENBERG_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "heisenberg_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        HEISENBERG_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "heisenberg_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        DARWIN_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "darwin_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        DARWIN_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "darwin_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        HANNA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "hanna_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        HANNA_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "hanna_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        DA_VINCI_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "da_vinci_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        DA_VINCI_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "da_vinci_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        CATHERINE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "catherine_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        CATHERINE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "catherine_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        FERIET_II_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "feriet_ii_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        FERIET_II_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "feriet_ii_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        GUINEY_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "guiney_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        GUINEY_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "guiney_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        FRIEDRICH_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "friedrich_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        FRIEDRICH_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "friedrich_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        PATTON_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "patton_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        PATTON_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "patton_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        BASILONE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "basilone_2pc"), 2) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });

        BASILONE_SET.addEffect(new SetEffect(ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "basilone_4pc"), 4) {
            @Override
            public void apply(LivingEntity entity, int count) {}
            @Override
            public void tick(LivingEntity entity, int count) {}
            @Override
            public void remove(LivingEntity entity) {}
        });
    }
}