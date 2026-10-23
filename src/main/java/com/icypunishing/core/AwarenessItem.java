package com.icypunishing.core;

import com.icylib.client.IPortraitProvider;
import com.icylib.upgrade.*;
import com.icylib.init.ModAttributes;
import com.icypunishing.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;

public class AwarenessItem extends Item implements IPortraitProvider, IUpgradeableItem, ICurioItem {
    private final AwarenessSet set;
    private final int slotIndex;
    private static final Random RANDOM = new Random();

    private static final String TAG_HEALTH = "awareness_health";
    private static final String TAG_ATTACK = "awareness_attack";
    private static final String TAG_DEFENSE = "awareness_defense";
    private static final String TAG_CRITICAL_STRIKE = "awareness_critical_strike";
    private static final String TAG_UPGRADE_STATS = "upgrade_stats";
    private static final String TAG_UPGRADE = "upgrade";

    private final BiFunction<AwarenessUpgradeStats, Integer, Double> healthGrowth;
    private final BiFunction<AwarenessUpgradeStats, Integer, Double> attackGrowth;
    private final BiFunction<AwarenessUpgradeStats, Integer, Double> defenseGrowth;
    private final BiFunction<AwarenessUpgradeStats, Integer, Double> criticalStrikeGrowth;

    public AwarenessItem(AwarenessSet set, int slotIndex, Properties properties) {
        super(properties);
        this.set = set;
        this.slotIndex = slotIndex;

        AwarenessStats stats = set.getStats();
        int rank = set.getRank();
        int maxStage = AwarenessUpgradeManager.getMaxStage(rank);
        final int totalLevels;
        {
            int temp = 0;
            for (int stage = 1; stage <= maxStage; stage++) {
                temp += AwarenessUpgradeManager.getMaxLevel(stage, rank);
            }
            totalLevels = temp;
        }

        final int healthRange = stats != null ? stats.getMaxHealth() - stats.getMinHealth() : 0;
        final int attackRange = stats != null ? stats.getMaxAttack() - stats.getMinAttack() : 0;
        final int defenseRange = stats != null ? stats.getMaxDefense() - stats.getMinDefense() : 0;
        final int criticalRange = stats != null ? stats.getMaxCriticalStrike() - stats.getMinCriticalStrike() : 0;

        this.healthGrowth = (s, l) -> totalLevels > 0 ? (double) healthRange / totalLevels : 0;
        this.attackGrowth = (s, l) -> totalLevels > 0 ? (double) attackRange / totalLevels : 0;
        this.defenseGrowth = (s, l) -> totalLevels > 0 ? (double) defenseRange / totalLevels : 0;
        this.criticalStrikeGrowth = (s, l) -> totalLevels > 0 ? (double) criticalRange / totalLevels : 0;
    }

    public AwarenessSet getSet() {
        return set;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            UpgradeUIHelper.openUpgradeMenu(player);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public boolean isUpperSlot() {
        return slotIndex < 3;
    }

    public void initRandomStats(ItemStack stack) {
        AwarenessStats stats = set.getStats();
        if (stats == null) return;

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (isUpperSlot()) {
            int health = stats.getMinHealth();
            int criticalStrike = stats.getMinCriticalStrike();
            tag.putInt(TAG_HEALTH, health);
            tag.putInt(TAG_CRITICAL_STRIKE, criticalStrike);
        } else {
            int attack = stats.getMinAttack();
            int defense = stats.getMinDefense();
            tag.putInt(TAG_ATTACK, attack);
            tag.putInt(TAG_DEFENSE, defense);
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public int getHealth(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int base = tag.getInt(TAG_HEALTH);
        if (tag.contains(TAG_UPGRADE_STATS)) {
            return getUpgradeStats(stack).map(stats -> {
                var attr = stats.statMap.get("health");
                return attr != null ? (int) Math.round(attr.value) : base;
            }).orElse(base);
        }
        return base;
    }

    public int getAttack(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int base = tag.getInt(TAG_ATTACK);
        if (tag.contains(TAG_UPGRADE_STATS)) {
            return getUpgradeStats(stack).map(stats -> {
                var attr = stats.statMap.get("attack");
                return attr != null ? (int) Math.round(attr.value) : base;
            }).orElse(base);
        }
        return base;
    }

    public int getDefense(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int base = tag.getInt(TAG_DEFENSE);
        if (tag.contains(TAG_UPGRADE_STATS)) {
            return getUpgradeStats(stack).map(stats -> {
                var attr = stats.statMap.get("defense");
                return attr != null ? (int) Math.round(attr.value) : base;
            }).orElse(base);
        }
        return base;
    }

    public int getCriticalStrike(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int base = tag.getInt(TAG_CRITICAL_STRIKE);
        if (tag.contains(TAG_UPGRADE_STATS)) {
            return getUpgradeStats(stack).map(stats -> {
                var attr = stats.statMap.get("critical_strike");
                return attr != null ? (int) Math.round(attr.value) : base;
            }).orElse(base);
        }
        return base;
    }

    // ICurioItem 接口实现 - 1.21.1 中直接在 Item 上实现，不再需要 initCapabilities
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        set.tickEffects(slotContext.entity());
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        set.applyEffects(slotContext.entity());
        applyBaseStats(slotContext.entity(), stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        for (SetEffect effect : set.getEffects()) {
            effect.remove(slotContext.entity());
        }
        removeBaseStats(slotContext.entity(), stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    private int calculateTotalLevelsToMax() {
        int rank = getRank();
        int maxStage = AwarenessUpgradeManager.getMaxStage(rank);
        int total = 0;
        for (int stage = 1; stage <= maxStage; stage++) {
            total += AwarenessUpgradeManager.getMaxLevel(stage, rank);
        }
        return total;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);

        AwarenessStats stats = set.getStats();
        if (stats == null) {
            tooltipComponents.addAll(set.getSetDescription(stack));
            return;
        }

        int rank = getRank();
        String stars = "\u2B50".repeat(rank);
        net.minecraft.ChatFormatting rankColor = switch (rank) {
            case 2 -> net.minecraft.ChatFormatting.GREEN;
            case 3 -> net.minecraft.ChatFormatting.BLUE;
            case 4 -> net.minecraft.ChatFormatting.DARK_PURPLE;
            case 5 -> net.minecraft.ChatFormatting.GOLD;
            case 6 -> net.minecraft.ChatFormatting.RED;
            default -> net.minecraft.ChatFormatting.WHITE;
        };

        int initialHealth = stats.getMinHealth();
        int initialAttack = stats.getMinAttack();
        int initialDefense = stats.getMinDefense();
        int initialCriticalStrike = stats.getMinCriticalStrike();

        int maxHealth = stats.getMaxHealth();
        int maxAttack = stats.getMaxAttack();
        int maxDefense = stats.getMaxDefense();
        int maxCriticalStrike = stats.getMaxCriticalStrike();

        if (isUpperSlot()) {
            int health = getHealth(stack);
            int criticalStrike = getCriticalStrike(stack);

            if (health > 0) {
                Component healthText = Component.literal("生命: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%d", health)).withStyle(net.minecraft.ChatFormatting.RED))
                        .append(Component.literal(" (" + String.format("%d", initialHealth) + "-" + String.format("%d", maxHealth) + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(healthText);
            }

            if (criticalStrike > 0) {
                Component criticalText = Component.literal("会心: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%d", criticalStrike)).withStyle(net.minecraft.ChatFormatting.GOLD))
                        .append(Component.literal(" (" + String.format("%d", initialCriticalStrike) + "-" + String.format("%d", maxCriticalStrike) + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(criticalText);
            }
        } else {
            int attack = getAttack(stack);
            int defense = getDefense(stack);

            if (attack > 0) {
                Component attackText = Component.literal("攻击: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%d", attack)).withStyle(net.minecraft.ChatFormatting.RED))
                        .append(Component.literal(" (" + String.format("%d", initialAttack) + "-" + String.format("%d", maxAttack) + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(attackText);
            }
            if (defense > 0) {
                Component defenseText = Component.literal("防御: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%d", defense)).withStyle(net.minecraft.ChatFormatting.BLUE))
                        .append(Component.literal(" (" + String.format("%d", initialDefense) + "-" + String.format("%d", maxDefense) + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(defenseText);
            }
        }

        getUpgradeStats(stack).ifPresentOrElse(upgradeStats -> {
            int currentStage = upgradeStats.stage;
            int currentLevel = upgradeStats.level;
            int maxLevelForStage = AwarenessUpgradeManager.getMaxLevel(currentStage, rank);
            int maxStage = AwarenessUpgradeManager.getMaxStage(rank);

            Component stageText = Component.literal("阶段: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Integer.toString(currentStage)).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE))
                    .append(Component.literal("/" + maxStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(stageText);

            Component levelText = Component.literal("等级: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Integer.toString(currentLevel)).withStyle(net.minecraft.ChatFormatting.GREEN))
                    .append(Component.literal("/" + maxLevelForStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(levelText);
        }, () -> {
            int defaultStage = 1;
            int defaultLevel = 0;
            int maxLevelForStage = AwarenessUpgradeManager.getMaxLevel(defaultStage, rank);
            int maxStage = AwarenessUpgradeManager.getMaxStage(rank);

            Component stageText = Component.literal("阶段: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Integer.toString(defaultStage)).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE))
                    .append(Component.literal("/" + maxStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(stageText);

            Component levelText = Component.literal("等级: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Integer.toString(defaultLevel)).withStyle(net.minecraft.ChatFormatting.GREEN))
                    .append(Component.literal("/" + maxLevelForStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(levelText);
        });

        List<net.minecraft.network.chat.MutableComponent> setDescription = set.getSetDescription(stack);
        if (!setDescription.isEmpty()) {
            Component firstLine = setDescription.get(0);
            Component starredLine = Component.literal(stars + " ").append(firstLine).withStyle(rankColor);
            tooltipComponents.add(starredLine);
            for (int i = 1; i < setDescription.size(); i++) {
                tooltipComponents.add(setDescription.get(i));
            }
        }
    }

    private void applyBaseStats(LivingEntity entity, ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            initRandomStats(stack);
        }

        if (isUpperSlot()) {
            int health = getHealth(stack);
            int criticalStrike = getCriticalStrike(stack);

            if (health > 0) {
                var healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    ResourceLocation healthId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_health_" + slotIndex + "_" + set.getId().getPath());
                    AttributeModifier modifier = new AttributeModifier(healthId, health, AttributeModifier.Operation.ADD_VALUE);
                    if (!healthAttr.hasModifier(modifier.id())) {
                        healthAttr.addTransientModifier(modifier);
                    }
                }
            }

            if (criticalStrike > 0) {
                var criticalAttr = entity.getAttribute(ModAttributes.CRITICAL_STRIKE_CHANCE);
                if (criticalAttr != null) {
                    ResourceLocation criticalId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_critical_" + slotIndex + "_" + set.getId().getPath());
                    AttributeModifier modifier = new AttributeModifier(criticalId, criticalStrike, AttributeModifier.Operation.ADD_VALUE);
                    if (!criticalAttr.hasModifier(modifier.id())) {
                        criticalAttr.addTransientModifier(modifier);
                    }
                }
            }
        } else {
            int attack = getAttack(stack);
            int defense = getDefense(stack);

            if (attack > 0) {
                var attackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackAttr != null) {
                    ResourceLocation attackId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_attack_" + slotIndex + "_" + set.getId().getPath());
                    AttributeModifier modifier = new AttributeModifier(attackId, attack, AttributeModifier.Operation.ADD_VALUE);
                    if (!attackAttr.hasModifier(modifier.id())) {
                        attackAttr.addTransientModifier(modifier);
                    }
                }
            }

            if (defense > 0) {
                var defenseAttr = entity.getAttribute(ModAttributes.DEFENSE);
                if (defenseAttr != null) {
                    ResourceLocation defenseId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_defense_" + slotIndex + "_" + set.getId().getPath());
                    AttributeModifier modifier = new AttributeModifier(defenseId, defense, AttributeModifier.Operation.ADD_VALUE);
                    if (!defenseAttr.hasModifier(modifier.id())) {
                        defenseAttr.addTransientModifier(modifier);
                    }
                }
            }
        }
    }

    private void removeBaseStats(LivingEntity entity, ItemStack stack) {
        if (isUpperSlot()) {
            var healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                ResourceLocation healthId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_health_" + slotIndex + "_" + set.getId().getPath());
                healthAttr.removeModifier(healthId);
            }

            var criticalAttr = entity.getAttribute(ModAttributes.CRITICAL_STRIKE_CHANCE);
            if (criticalAttr != null) {
                ResourceLocation criticalId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_critical_" + slotIndex + "_" + set.getId().getPath());
                criticalAttr.removeModifier(criticalId);
            }
        } else {
            var attackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackAttr != null) {
                ResourceLocation attackId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_attack_" + slotIndex + "_" + set.getId().getPath());
                attackAttr.removeModifier(attackId);
            }

            var defenseAttr = entity.getAttribute(ModAttributes.DEFENSE);
            if (defenseAttr != null) {
                ResourceLocation defenseId = ResourceLocation.fromNamespaceAndPath("icy_punishing", "awareness_defense_" + slotIndex + "_" + set.getId().getPath());
                defenseAttr.removeModifier(defenseId);
            }
        }
    }

    @Override
    public ResourceLocation getPortraitLocation(ItemStack stack) {
        return set.getPortraitLocation(slotIndex);
    }

    @Override
    public int getPortraitWidth(ItemStack stack) {
        return 100;
    }

    @Override
    public int getPortraitHeight(ItemStack stack) {
        return 140;
    }

    @Override
    public Optional<AwarenessUpgradeStats> getUpgradeStats(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            initRandomStats(stack);
        }

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_UPGRADE_STATS)) {
            AwarenessUpgradeStats stats = new AwarenessUpgradeStats(getRank());
            if (isUpperSlot()) {
                stats.addAttribute("health", getHealth(stack));
                stats.addAttribute("critical_strike", getCriticalStrike(stack));
            } else {
                stats.addAttribute("attack", getAttack(stack));
                stats.addAttribute("defense", getDefense(stack));
            }
            setUpgradeStats(stack, stats);
            return Optional.of(stats);
        }

        CompoundTag statsTag = tag.getCompound(TAG_UPGRADE_STATS);
        AwarenessUpgradeStats stats = AwarenessUpgradeStats.fromTag(statsTag);

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
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            return Optional.empty();
        }
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
    public int getRank() {
        return set.getRank();
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
    public boolean tryUpgrade(ItemStack stack, net.minecraft.world.entity.player.Player player) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            initRandomStats(stack);
        }

        AwarenessUpgradeStats stats = getUpgradeStats(stack).orElseGet(() -> {
            AwarenessUpgradeStats newStats = new AwarenessUpgradeStats(getRank());
            if (isUpperSlot()) {
                newStats.addAttribute("health", getHealth(stack));
                newStats.addAttribute("critical_strike", getCriticalStrike(stack));
            } else {
                newStats.addAttribute("attack", getAttack(stack));
                newStats.addAttribute("defense", getDefense(stack));
            }
            setUpgradeStats(stack, newStats);
            return newStats;
        });

        boolean success = AwarenessUpgradeManager.tryUpgrade(stats, player, getAttributeGrowthFunctions());

        if (success) {
            setUpgradeStats(stack, stats);
            syncAttributesToTag(stack, stats);
        }

        return success;
    }

    @Override
    public boolean tryBreakthrough(ItemStack stack, net.minecraft.world.entity.player.Player player, List<ItemStack> materials) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            initRandomStats(stack);
        }

        return getUpgradeStats(stack).map(stats -> {
            boolean success = AwarenessUpgradeManager.tryBreakthrough(stats, player, materials, getAttributeGrowthFunctions());
            if (success) {
                setUpgradeStats(stack, stats);
                syncAttributesToTag(stack, stats);
            }
            return success;
        }).orElse(false);
    }

    private void syncAttributesToTag(ItemStack stack, AwarenessUpgradeStats stats) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        for (AwarenessUpgradeStats.StatEntry entry : stats.attributes) {
            if (entry.type.equals("health")) {
                tag.putInt(TAG_HEALTH, (int) entry.value);
            } else if (entry.type.equals("attack")) {
                tag.putInt(TAG_ATTACK, (int) entry.value);
            } else if (entry.type.equals("defense")) {
                tag.putInt(TAG_DEFENSE, (int) entry.value);
            } else if (entry.type.equals("critical_strike")) {
                tag.putInt(TAG_CRITICAL_STRIKE, (int) entry.value);
            }
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public int getExpForConversion(ItemStack stack) {
        return AwarenessUpgradeManager.getExpForConversion(getRank(), getUpgradeStats(stack).orElse(null));
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiFunction<AwarenessUpgradeStats, Integer, Double>[] getAttributeGrowthFunctions() {
        if (isUpperSlot()) {
            return new BiFunction[]{healthGrowth, criticalStrikeGrowth};
        } else {
            return new BiFunction[]{attackGrowth, defenseGrowth};
        }
    }

    @Override
    public List<ItemStack> getBreakthroughMaterials(ItemStack stack, int stage) {
        List<ItemStack> materials = new ArrayList<>();
        int rank = set.getRank();

        if (rank == 3) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.NUT.get(), 4));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 1));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.NUT.get(), 8));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 1));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 1));
            }
        } else if (rank == 4) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.NUT.get(), 6));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 2));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.NUT.get(), 12));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 1));
            } else if (stage == 3) {
                materials.add(new ItemStack(ModItems.NUT.get(), 18));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 1));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 1));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 1));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 1));
            } else if (stage == 4) {
                materials.add(new ItemStack(ModItems.NUT.get(), 24));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 1));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 1));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 2));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 2));
            }
        } else if (rank == 5) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.NUT.get(), 8));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 3));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.NUT.get(), 16));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 3));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 3));
            } else if (stage == 3) {
                materials.add(new ItemStack(ModItems.NUT.get(), 24));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 2));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 2));
            } else if (stage == 4) {
                materials.add(new ItemStack(ModItems.NUT.get(), 32));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 2));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 4));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 4));
            }
        } else if (rank == 6) {
            if (stage == 1) {
                materials.add(new ItemStack(ModItems.NUT.get(), 10));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 4));
            } else if (stage == 2) {
                materials.add(new ItemStack(ModItems.NUT.get(), 20));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 4));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 4));
            } else if (stage == 3) {
                materials.add(new ItemStack(ModItems.NUT.get(), 30));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 3));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 3));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 3));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 3));
            } else if (stage == 4) {
                materials.add(new ItemStack(ModItems.NUT.get(), 40));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_LOW.get(), 3));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_LOW.get(), 3));
                materials.add(new ItemStack(ModItems.AWARENESS_CIRCUIT_HIGH.get(), 5));
                materials.add(new ItemStack(ModItems.BREAKTHROUGH_ALLOY_HIGH.get(), 5));
            }
        }

        return materials;
    }
}
