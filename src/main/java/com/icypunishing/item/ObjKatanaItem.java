package com.icypunishing.item;

import com.icylib.upgrade.AwarenessUpgradeManager;
import com.icylib.upgrade.AwarenessUpgradeStats;
import com.icylib.upgrade.IUpgradeableItem;
import com.icylib.upgrade.Upgrade;
import com.icypunishing.core.WeaponUpgradeData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class ObjKatanaItem extends SwordItem implements IUpgradeableItem {

    private static final String TAG_UPGRADE_STATS = "weapon_upgrade_stats";
    private static final String TAG_UPGRADE = "weapon_upgrade";
    private static final ResourceLocation UPGRADE_ATTACK_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "weapon_upgrade_attack");

    private final String modelId;

    static {
        NeoForge.EVENT_BUS.register(ObjKatanaItem.class);
    }

    public ObjKatanaItem(Properties properties, String modelId) {
        super(Tiers.IRON, properties);
        this.modelId = modelId;
    }

    public String getModelId() {
        return modelId;
    }

    @Override
    public int getRank() {
        return WeaponUpgradeData.getRank(modelId);
    }

    public double getAttack(ItemStack stack) {
        return getUpgradeStats(stack).map(stats -> {
            var attr = stats.statMap.get("attack");
            return attr != null ? attr.value : WeaponUpgradeData.getBaseAttack(modelId);
        }).orElse(WeaponUpgradeData.getBaseAttack(modelId));
    }

    @Override
    public Optional<AwarenessUpgradeStats> getUpgradeStats(ItemStack stack) {
        if (!WeaponUpgradeData.hasUpgradeData(modelId)) {
            return Optional.empty();
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_UPGRADE_STATS)) {
            AwarenessUpgradeStats stats = new AwarenessUpgradeStats(getRank());
            stats.addAttribute("attack", WeaponUpgradeData.getBaseAttack(modelId));
            setUpgradeStats(stack, stats);
            return Optional.of(stats);
        }
        AwarenessUpgradeStats stats = AwarenessUpgradeStats.fromTag(tag.getCompound(TAG_UPGRADE_STATS));
        if (stats.rank <= 0) {
            stats.rank = getRank();
        }
        return Optional.of(stats);
    }

    @Override
    public void setUpgradeStats(ItemStack stack, AwarenessUpgradeStats stats) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.put(TAG_UPGRADE_STATS, stats.toTag());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public Optional<Upgrade> getUpgrade(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_UPGRADE)) {
            return Optional.empty();
        }
        return Optional.of(Upgrade.fromTag(tag.getCompound(TAG_UPGRADE)));
    }

    @Override
    public void setUpgrade(ItemStack stack, Upgrade upgrade) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.put(TAG_UPGRADE, upgrade.toTag());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public boolean canUpgrade(ItemStack stack) {
        return getUpgradeStats(stack).map(AwarenessUpgradeManager::canUpgrade).orElse(false);
    }

    @Override
    public boolean canBreakthrough(ItemStack stack) {
        return getUpgradeStats(stack).map(AwarenessUpgradeManager::canBreakthrough).orElse(false);
    }

    @Override
    public boolean upgradeWithMaterials() {
        return true;
    }

    @Override
    public List<ItemStack> getUpgradeMaterials(ItemStack stack) {
        return getUpgradeStats(stack)
                .filter(AwarenessUpgradeManager::canUpgrade)
                .map(stats -> WeaponUpgradeData.getUpgradeMaterials(getRank()))
                .orElse(List.of());
    }

    @Override
    public List<ItemStack> getBreakthroughMaterials(ItemStack stack, int stage) {
        if (!WeaponUpgradeData.hasUpgradeData(modelId)) {
            return List.of();
        }
        return WeaponUpgradeData.getBreakthroughMaterials(getRank(), stage);
    }

    @Override
    public boolean tryUpgrade(ItemStack stack, Player player) {
        return getUpgradeStats(stack).map(stats -> {
            if (!AwarenessUpgradeManager.canUpgrade(stats)) {
                return false;
            }
            stats.level++;
            stats.exp = 0;
            refreshAttack(stats);
            setUpgradeStats(stack, stats);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean tryBreakthrough(ItemStack stack, Player player, List<ItemStack> materials) {
        return getUpgradeStats(stack).map(stats -> {
            if (!AwarenessUpgradeManager.canBreakthrough(stats)) {
                return false;
            }
            stats.stage++;
            stats.level = 0;
            stats.exp = 0;
            refreshAttack(stats);
            setUpgradeStats(stack, stats);
            return true;
        }).orElse(false);
    }

    private void refreshAttack(AwarenessUpgradeStats stats) {
        int gained = stats.level;
        for (int s = 1; s < stats.stage; s++) {
            gained += AwarenessUpgradeManager.getMaxLevel(s, stats.rank);
        }
        int total = 0;
        int maxStage = AwarenessUpgradeManager.getMaxStage(stats.rank);
        for (int s = 1; s <= maxStage; s++) {
            total += AwarenessUpgradeManager.getMaxLevel(s, stats.rank);
        }
        double base = WeaponUpgradeData.getBaseAttack(modelId);
        double max = WeaponUpgradeData.getMaxAttack(modelId);
        double attack = total > 0 ? base + (max - base) * gained / total : base;
        var attr = stats.statMap.get("attack");
        if (attr != null) {
            attr.value = attack;
        }
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ObjKatanaItem katana)) {
            return;
        }
        if (!WeaponUpgradeData.hasUpgradeData(katana.getModelId())) {
            return;
        }
        double bonus = katana.getAttack(stack) - WeaponUpgradeData.getBaseAttack(katana.getModelId());
        if (bonus <= 0) {
            return;
        }
        event.addModifier(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(UPGRADE_ATTACK_ID, bonus, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public int getExpForConversion(ItemStack stack) {
        return AwarenessUpgradeManager.getExpForConversion(getRank(), getUpgradeStats(stack).orElse(null));
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiFunction<AwarenessUpgradeStats, Integer, Double>[] getAttributeGrowthFunctions() {
        return new BiFunction[0];
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
        int rank = getRank();
        boolean hasData = WeaponUpgradeData.hasUpgradeData(modelId);
        String loreId = WeaponUpgradeData.getLoreId(modelId);
        String lorePrefix = "tooltip.icy_punishing.weapon." + loreId;

        String stars = "⭐".repeat(rank);
        net.minecraft.ChatFormatting rankColor = switch (rank) {
            case 2 -> net.minecraft.ChatFormatting.GREEN;
            case 3 -> net.minecraft.ChatFormatting.BLUE;
            case 4 -> net.minecraft.ChatFormatting.DARK_PURPLE;
            case 5 -> net.minecraft.ChatFormatting.GOLD;
            case 6 -> net.minecraft.ChatFormatting.RED;
            default -> net.minecraft.ChatFormatting.WHITE;
        };
        tooltipComponents.add(Component.literal(stars).withStyle(rankColor));

        var typeLine = Component.literal("武器类型: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal("太刀").withStyle(net.minecraft.ChatFormatting.WHITE));
        if (hasData) {
            typeLine.append(Component.literal("   等级上限: ").withStyle(net.minecraft.ChatFormatting.GRAY))
                    .append(Component.literal(Integer.toString(WeaponUpgradeData.getLevelCap(modelId))).withStyle(net.minecraft.ChatFormatting.WHITE));
        }
        tooltipComponents.add(typeLine);

        String constructKey = lorePrefix + ".construct";
        Component construct = Component.translatable(constructKey);
        if (!construct.getString().equals(constructKey)) {
            tooltipComponents.add(Component.literal("专属机体: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.translatable(constructKey).withStyle(net.minecraft.ChatFormatting.AQUA)));
        }

        if (hasData) {
            double baseAttack = WeaponUpgradeData.getBaseAttack(modelId);
            double maxAttack = WeaponUpgradeData.getMaxAttack(modelId);
            double attack = getAttack(stack);
            double progress = maxAttack > baseAttack
                    ? Math.min(1.0, Math.max(0.0, (attack - baseAttack) / (maxAttack - baseAttack)))
                    : 0.0;

            Component attackText = Component.literal("攻击: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Long.toString(Math.round(attack))).withStyle(net.minecraft.ChatFormatting.RED))
                    .append(Component.literal(" (" + Math.round(baseAttack) + "-" + Math.round(maxAttack) + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(attackText);

            int baseCrit = WeaponUpgradeData.getBaseCrit(modelId);
            int maxCrit = WeaponUpgradeData.getMaxCrit(modelId);
            int crit = (int) Math.round(baseCrit + (maxCrit - baseCrit) * progress);
            Component critText = Component.literal("会心: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Integer.toString(crit)).withStyle(net.minecraft.ChatFormatting.GOLD))
                    .append(Component.literal(" (" + baseCrit + "-" + maxCrit + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(critText);

            int maxStage = AwarenessUpgradeManager.getMaxStage(rank);
            getUpgradeStats(stack).ifPresent(stats -> {
                int maxLevelForStage = AwarenessUpgradeManager.getMaxLevel(stats.stage, rank);

                Component stageText = Component.literal("阶段: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(Integer.toString(stats.stage)).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE))
                        .append(Component.literal("/" + maxStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(stageText);

                Component levelText = Component.literal("等级: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(Integer.toString(stats.level)).withStyle(net.minecraft.ChatFormatting.GREEN))
                        .append(Component.literal("/" + maxLevelForStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(levelText);
            });
        }

        String skillNameKey = lorePrefix + ".skill_name";
        if (!Component.translatable(skillNameKey).getString().equals(skillNameKey)) {
            tooltipComponents.add(Component.literal("技能: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.translatable(skillNameKey).withStyle(net.minecraft.ChatFormatting.GOLD)));
            String skillDescKey = lorePrefix + ".skill_desc";
            Component skillDesc = Component.translatable(skillDescKey);
            if (!skillDesc.getString().equals(skillDescKey)) {
                tooltipComponents.add(skillDesc.copy().withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        }

        String dataKey = lorePrefix + ".data";
        Component data = Component.translatable(dataKey);
        if (!data.getString().equals(dataKey)) {
            tooltipComponents.add(data.copy().withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
        }

        String storyKey = lorePrefix + ".story";
        Component story = Component.translatable(storyKey);
        if (!story.getString().equals(storyKey)) {
            tooltipComponents.add(story.copy().withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));
        }
    }
}
