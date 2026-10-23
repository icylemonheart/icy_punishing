package com.icypunishing.core;

import com.icypunishing.registry.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeaponUpgradeData {
    public record Entry(int rank, double baseAttack, double maxAttack, int baseCrit, int maxCrit, int levelCap) {}

    // 数值取自 WIKI（属性 初始/MAX，等级上限）
    // 朱桦/沙利叶仅形态一拥有属性，其余形态机制待实现
    private static final Map<String, Entry> WEAPONS = Map.ofEntries(
            Map.entry("tachi", new Entry(2, 32, 37, 14, 19, 10)),
            Map.entry("hugotou", new Entry(3, 35, 104, 15, 55, 25)),
            Map.entry("mumei", new Entry(4, 40, 224, 18, 121, 35)),
            Map.entry("kyuuji", new Entry(5, 45, 354, 20, 186, 45)),
            Map.entry("honglian", new Entry(6, 49, 428, 23, 234, 45)),
            Map.entry("hongying", new Entry(6, 51, 447, 23, 239, 45)),
            Map.entry("tianlang", new Entry(6, 52, 458, 21, 221, 45)),
            Map.entry("yingda", new Entry(6, 48, 445, 23, 226, 45)),
            Map.entry("zhuhua_c1", new Entry(6, 53, 463, 23, 234, 45)),
            Map.entry("sarariel_c1", new Entry(6, 50, 441, 24, 241, 45))
    );

    public static boolean hasUpgradeData(String modelId) {
        return WEAPONS.containsKey(modelId);
    }

    public static int getRank(String modelId) {
        Entry entry = WEAPONS.get(modelId);
        return entry != null ? entry.rank() : 6;
    }

    public static double getBaseAttack(String modelId) {
        Entry entry = WEAPONS.get(modelId);
        return entry != null ? entry.baseAttack() : 0;
    }

    public static double getMaxAttack(String modelId) {
        Entry entry = WEAPONS.get(modelId);
        return entry != null ? entry.maxAttack() : 0;
    }

    public static int getBaseCrit(String modelId) {
        Entry entry = WEAPONS.get(modelId);
        return entry != null ? entry.baseCrit() : 0;
    }

    public static int getMaxCrit(String modelId) {
        Entry entry = WEAPONS.get(modelId);
        return entry != null ? entry.maxCrit() : 0;
    }

    public static int getLevelCap(String modelId) {
        Entry entry = WEAPONS.get(modelId);
        return entry != null ? entry.levelCap() : 0;
    }

    // 朱桦/沙利叶的四形态共用同一份 tooltip 文本
    public static String getLoreId(String modelId) {
        if (modelId.startsWith("zhuhua")) {
            return "zhuhua";
        }
        if (modelId.startsWith("sarariel")) {
            return "sarariel";
        }
        return modelId;
    }

    public static List<ItemStack> getUpgradeMaterials(int rank) {
        return switch (rank) {
            case 2 -> List.of(new ItemStack(ModItems.WEAPON_MATERIAL_2.get(), 1));
            case 3 -> List.of(new ItemStack(ModItems.WEAPON_MATERIAL_2.get(), 2));
            case 4 -> List.of(new ItemStack(ModItems.WEAPON_MATERIAL_3.get(), 2));
            case 5 -> List.of(new ItemStack(ModItems.WEAPON_MATERIAL_3.get(), 3));
            case 6 -> List.of(new ItemStack(ModItems.WEAPON_MATERIAL_4.get(), 2));
            default -> List.of();
        };
    }

    // 突破材料取自 WIKI（螺母按 1:1000 折算）
    public static List<ItemStack> getBreakthroughMaterials(int rank, int stage) {
        List<ItemStack> materials = new ArrayList<>();

        if (rank == 3) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.NUT.get(), 4));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.NUT.get(), 8));
            }
        } else if (rank == 4) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.NUT.get(), 6));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.NUT.get(), 12));
            } else if (stage == 3) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 2));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 2));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.NUT.get(), 18));
            } else if (stage == 4) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 4));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 4));
                materials.add(new ItemStack(ModItems.NUT.get(), 24));
            }
        } else if (rank == 5) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.NUT.get(), 8));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.NUT.get(), 16));
            } else if (stage == 3) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 4));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 4));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.NUT.get(), 24));
            } else if (stage == 4) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 8));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 8));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.NUT.get(), 32));
            }
        } else if (rank == 6) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 8));
                materials.add(new ItemStack(ModItems.NUT.get(), 10));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 8));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 8));
                materials.add(new ItemStack(ModItems.NUT.get(), 20));
            } else if (stage == 3) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 6));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 6));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.NUT.get(), 30));
            } else if (stage == 4) {
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 10));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get(), 10));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get(), 6));
                materials.add(new ItemStack(ModItems.NUT.get(), 40));
            }
        }

        return materials;
    }
}
