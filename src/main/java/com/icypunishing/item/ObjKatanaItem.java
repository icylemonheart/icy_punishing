package com.icypunishing.item;

import com.icylib.init.ModAttributes;
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
    private static final String TAG_ASCENSION = "ascension";
    private static final ResourceLocation UPGRADE_ATTACK_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "weapon_upgrade_attack");
    private static final ResourceLocation UPGRADE_CRIT_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "weapon_upgrade_crit");
    private static final ResourceLocation MUMEI_CRIT_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "mumei_crit_damage");
    /** 无铭：拔刀无铭，额外 0.15 会心伤害加成。 */
    private static final double MUMEI_CRIT_DAMAGE_BONUS = 0.15;

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

    public int getAscension(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getInt(TAG_ASCENSION);
    }

    public void setAscension(ItemStack stack, int level) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(TAG_ASCENSION, level);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != net.minecraft.world.InteractionHand.MAIN_HAND) {
            return super.use(level, player, hand);
        }
        ItemStack offhand = player.getOffhandItem();
        int asc = getAscension(stack);
        boolean low = offhand.is(com.icypunishing.registry.ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get());
        boolean high = offhand.is(com.icypunishing.registry.ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get());
        // 逆元碎片-红莲：红莲狂士专属进阶材料（替代突破核心，旧路径已删除）
        boolean fragment = "honglian".equals(modelId)
                && offhand.is(com.icypunishing.registry.ModItems.INVERSE_FRAGMENT_HONGLIAN.get());
        boolean valid = "honglian".equals(modelId)
                ? (fragment && asc < 3)
                : (low && asc == 0) || (high && (asc == 1 || asc == 2));
        if (!valid) {
            if (low || high || fragment) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.icy_punishing.ascension_invalid"), true);
                }
                return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
            return super.use(level, player, hand);
        }
        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) {
                offhand.shrink(1);
            }
            setAscension(stack, asc + 1);
            level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.2f);
            player.displayClientMessage(Component.translatable("message.icy_punishing.ascension_success",
                    Component.translatable("ascension.icy_punishing." + (asc + 1))), true);
        }
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
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
        String modelId = katana.getModelId();
        if (!WeaponUpgradeData.hasUpgradeData(modelId)) {
            return;
        }
        double attack = katana.getAttack(stack);
        double baseAttack = WeaponUpgradeData.getBaseAttack(modelId);
        double maxAttack = WeaponUpgradeData.getMaxAttack(modelId);
        double progress = maxAttack > baseAttack
                ? Math.min(1.0, Math.max(0.0, (attack - baseAttack) / (maxAttack - baseAttack)))
                : 0.0;
        int baseCrit = WeaponUpgradeData.getBaseCrit(modelId);
        int maxCrit = WeaponUpgradeData.getMaxCrit(modelId);
        double crit = baseCrit + (maxCrit - baseCrit) * progress;

        // 清除武器默认攻击伤害修饰符，以 WIKI 攻击数值为准
        event.removeAllModifiersFor(Attributes.ATTACK_DAMAGE);
        event.addModifier(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(UPGRADE_ATTACK_ID, attack, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HAND);
        // 会心（暴击率）
        event.addModifier(
                ModAttributes.CRITICAL_STRIKE_CHANCE,
                new AttributeModifier(UPGRADE_CRIT_ID, crit, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HAND);

        // 无铭「拔刀无铭」：额外 0.15 会心伤害加成
        if ("mumei".equals(modelId)) {
            event.addModifier(
                    ModAttributes.CRITICAL_STRIKE_MULTIPLIER_ADD,
                    new AttributeModifier(MUMEI_CRIT_DAMAGE_ID, MUMEI_CRIT_DAMAGE_BONUS,
                            AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.HAND);
        }
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

        int ascension = getAscension(stack);
        net.minecraft.ChatFormatting ascColor = switch (ascension) {
            case 1 -> net.minecraft.ChatFormatting.BLUE;
            case 2 -> net.minecraft.ChatFormatting.GOLD;
            case 3 -> net.minecraft.ChatFormatting.RED;
            default -> net.minecraft.ChatFormatting.WHITE;
        };
        tooltipComponents.add(Component.literal("阶级: ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.translatable("ascension.icy_punishing." + ascension).withStyle(ascColor)));

        if (com.icypunishing.skill.SkillSetRegistry.hasSkillSet(modelId)) {
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.weapon.combat_toggle")
                    .withStyle(net.minecraft.ChatFormatting.YELLOW));
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.weapon.combat_orb")
                    .withStyle(net.minecraft.ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.weapon.combat_ult")
                    .withStyle(net.minecraft.ChatFormatting.GRAY));
        }

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
