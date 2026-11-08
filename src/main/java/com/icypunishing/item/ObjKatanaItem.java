package com.icypunishing.item;

import com.icylib.init.ModAttributes;
import com.icylib.upgrade.AwarenessUpgradeManager;
import com.icylib.upgrade.AwarenessUpgradeStats;
import com.icylib.upgrade.ISkillWeapon;
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

public class ObjKatanaItem extends SwordItem implements IUpgradeableItem, ISkillWeapon {

    private static final String TAG_UPGRADE_STATS = "weapon_upgrade_stats";
    private static final String TAG_UPGRADE = "weapon_upgrade";
    private static final String TAG_ASCENSION = "ascension";
    private static final String TAG_LEAP = "weapon_leap";
    private static final String TAG_SKILL_LEVELS = "skill_levels";
    /** 技能名：0红 1黄 2蓝 3必杀 4被动 5-7跃升 */
    private static final String[] SKILL_NAME_KEYS = {
            "skill.icy_punishing.red", "skill.icy_punishing.yellow", "skill.icy_punishing.blue",
            "skill.icy_punishing.ult", "skill.icy_punishing.passive",
            "skill.icy_punishing.leap1", "skill.icy_punishing.leap2", "skill.icy_punishing.leap3"};
    /** 跃升上限：3 节点 × 18 级 */
    public static final int MAX_LEAP = 54;
    private static final ResourceLocation UPGRADE_ATTACK_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "weapon_upgrade_attack");
    private static final ResourceLocation UPGRADE_CRIT_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "weapon_upgrade_crit");
    private static final ResourceLocation MUMEI_CRIT_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "mumei_crit_damage");
    /** 无铭：额外 0.15 会心伤害 */
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

    public int getLeap(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getInt(TAG_LEAP);
    }

    public void setLeap(ItemStack stack, int level) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(TAG_LEAP, level);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    // ISkillWeapon：技能等级与阶级

    @Override
    public String skillName(int index) {
        return tr(SKILL_NAME_KEYS[Math.max(0, Math.min(ISkillWeapon.SKILL_COUNT - 1, index))]);
    }

    private static String tr(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }

    @Override
    public boolean supportsSkillUpgrade(ItemStack stack) {
        // 仅红莲、影打、红樱有技能培养界面
        return "honglian".equals(modelId) || "yingda".equals(modelId) || "hongying".equals(modelId);
    }

    @Override
    public boolean skillUpgradable(int index) {
        // 被动仅作介绍，不参与技能点升级
        return index != ISkillWeapon.SKILL_PASSIVE;
    }

    @Override
    public String skillDesc(int index, int level) {
        return switch (modelId) {
            case "honglian" -> honglianSkillDesc(index, level);
            case "yingda" -> yingdaSkillDesc(index, level);
            case "hongying" -> hongyingSkillDesc(index, level);
            default -> tr("skill_desc.icy_punishing.placeholder", skillName(index));
        };
    }

    /** 红莲技能文本：18 级为表值，1 级半值，线性增长 */
    private String honglianSkillDesc(int index, int level) {
        int lv = Math.max(1, Math.min(ISkillWeapon.MAX_SKILL_LEVEL, level));
        float s = 0.5f + 0.5f * (lv - 1) / (ISkillWeapon.MAX_SKILL_LEVEL - 1);
        return switch (index) {
            case ISkillWeapon.SKILL_RED -> tr("skill_desc.icy_punishing.honglian.red",
                    pct(266.72f * s), pct(533.32f * s), pct(800f * s));
            case ISkillWeapon.SKILL_YELLOW -> tr("skill_desc.icy_punishing.honglian.yellow",
                    pct(150f * s), pct(300f * s), pct(450f * s));
            case ISkillWeapon.SKILL_BLUE -> tr("skill_desc.icy_punishing.honglian.blue",
                    pct(100f * s), pct(200f * s), pct(300f * s),
                    pct(10f * s), pct(15f * s), pct(20f * s));
            case ISkillWeapon.SKILL_ULT -> tr("skill_desc.icy_punishing.honglian.ult",
                    pct(140f * s), pct(1000f * s));
            case ISkillWeapon.SKILL_PASSIVE -> tr("skill_desc.icy_punishing.honglian.passive");
            default -> tr("skill_desc.icy_punishing.honglian.leap",
                    index - ISkillWeapon.SKILL_LEAP_1 + 1);
        };
    }

    /** 影打-逆断技能文本 */
    private String yingdaSkillDesc(int index, int level) {
        float s = skillScale(level);
        return switch (index) {
            case ISkillWeapon.SKILL_RED -> tr("skill_desc.icy_punishing.yingda.red",
                    pct(40f * s), pct(80f * s), pct(144f * s));
            case ISkillWeapon.SKILL_YELLOW -> tr("skill_desc.icy_punishing.yingda.yellow",
                    pct(100f * s), pct(200f * s), pct(300f * s),
                    pct(20f * s), pct(30f * s), pct(40f * s));
            case ISkillWeapon.SKILL_BLUE -> tr("skill_desc.icy_punishing.yingda.blue",
                    pct(80f * s), pct(160f * s), pct(240f * s),
                    pct(80f * s), pct(160f * s), pct(240f * s));
            case ISkillWeapon.SKILL_ULT -> tr("skill_desc.icy_punishing.yingda.ult", pct(25f * s));
            case ISkillWeapon.SKILL_PASSIVE -> tr("skill_desc.icy_punishing.yingda.passive", pct(60f * s));
            case ISkillWeapon.SKILL_LEAP_1 -> tr("skill_desc.icy_punishing.yingda.leap1");
            case ISkillWeapon.SKILL_LEAP_2 -> tr("skill_desc.icy_punishing.yingda.leap2");
            default -> tr("skill_desc.icy_punishing.yingda.leap3");
        };
    }

    /** 红樱技能文本 */
    private String hongyingSkillDesc(int index, int level) {
        float s = skillScale(level);
        return switch (index) {
            case ISkillWeapon.SKILL_RED -> tr("skill_desc.icy_punishing.hongying.red",
                    pct(25f * s), pct(50f * s), pct(75f * s), pct(75f * s));
            case ISkillWeapon.SKILL_YELLOW -> tr("skill_desc.icy_punishing.hongying.yellow",
                    pct(250f * s), pct(500f * s), pct(750f * s));
            case ISkillWeapon.SKILL_BLUE -> tr("skill_desc.icy_punishing.hongying.blue",
                    pct(20f * s), pct(30f * s), pct(50f * s),
                    pct(4f * s), pct(8f * s), pct(12f * s),
                    pct(8f * s), pct(16f * s), pct(24f * s));
            case ISkillWeapon.SKILL_ULT -> tr("skill_desc.icy_punishing.hongying.ult", pct(2600f * s));
            case ISkillWeapon.SKILL_PASSIVE -> tr("skill_desc.icy_punishing.hongying.passive", pct(340f * s));
            case ISkillWeapon.SKILL_LEAP_1 -> tr("skill_desc.icy_punishing.hongying.leap1");
            case ISkillWeapon.SKILL_LEAP_2 -> tr("skill_desc.icy_punishing.hongying.leap2");
            default -> tr("skill_desc.icy_punishing.hongying.leap3");
        };
    }

    /** 技能缩放：1 级 50%，18 级 100%，线性 */
    private static float skillScale(int level) {
        int lv = Math.max(1, Math.min(ISkillWeapon.MAX_SKILL_LEVEL, level));
        return 0.5f + 0.5f * (lv - 1) / (ISkillWeapon.MAX_SKILL_LEVEL - 1);
    }

    private static String pct(float value) {
        if (Math.abs(value - Math.round(value)) < 0.005f) {
            return Math.round(value) + "%";
        }
        return String.format(java.util.Locale.ROOT, "%.2f%%", value);
    }

    @Override
    public String ascensionEffect(int stage) {
        if (stage < 1 || stage > 3) {
            return null;
        }
        if ("honglian".equals(modelId) || "yingda".equals(modelId) || "hongying".equals(modelId)) {
            return tr("asc_desc.icy_punishing." + modelId + "." + stage);
        }
        return null;
    }

    @Override
    public int getSkillLevel(ItemStack stack, int index) {
        if (index < 0 || index >= ISkillWeapon.SKILL_COUNT) {
            return 1;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int[] levels = tag.getIntArray(TAG_SKILL_LEVELS);
        // 等级 1~18；旧存档缺省按 1 级
        int level = levels.length > index ? levels[index] : 1;
        return Math.max(1, Math.min(ISkillWeapon.MAX_SKILL_LEVEL, level));
    }

    @Override
    public void setSkillLevel(ItemStack stack, int index, int level) {
        if (index < 0 || index >= ISkillWeapon.SKILL_COUNT) {
            return;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int[] levels = tag.getIntArray(TAG_SKILL_LEVELS);
        if (levels.length != ISkillWeapon.SKILL_COUNT) {
            // 旧存档 7 项，迁移时保留原等级
            int[] migrated = new int[ISkillWeapon.SKILL_COUNT];
            java.util.Arrays.fill(migrated, 1);
            System.arraycopy(levels, 0, migrated, 0, Math.min(levels.length, ISkillWeapon.SKILL_COUNT));
            levels = migrated;
        }
        levels[index] = Math.max(1, Math.min(ISkillWeapon.MAX_SKILL_LEVEL, level));
        // 空槽补 1 级，避免留下 0
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] < 1) {
                levels[i] = 1;
            }
        }
        tag.putIntArray(TAG_SKILL_LEVELS, levels);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public List<ItemStack> getAscensionMaterials(ItemStack stack) {
        int asc = getAscension(stack);
        if (asc >= maxAscension()) {
            return List.of();
        }
        // 专属武器：对应逆元碎片 ×1
        if ("honglian".equals(modelId)) {
            return List.of(new ItemStack(com.icypunishing.registry.ModItems.INVERSE_FRAGMENT_HONGLIAN.get()));
        }
        if ("yingda".equals(modelId)) {
            return List.of(new ItemStack(com.icypunishing.registry.ModItems.INVERSE_FRAGMENT_DAWN.get()));
        }
        if ("hongying".equals(modelId)) {
            return List.of(new ItemStack(com.icypunishing.registry.ModItems.INVERSE_FRAGMENT_HONGYING.get()));
        }
        // 其他武器：核心·初（0→1）/ 核心·高（1→3）
        return List.of(new ItemStack(asc == 0
                ? com.icypunishing.registry.ModItems.WEAPON_BREAKTHROUGH_CORE_LOW.get()
                : com.icypunishing.registry.ModItems.WEAPON_BREAKTHROUGH_CORE_HIGH.get()));
    }

    @Override
    public void ascend(ItemStack stack) {
        int asc = getAscension(stack);
        if (asc < maxAscension()) {
            setAscension(stack, asc + 1);
        }
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != net.minecraft.world.InteractionHand.MAIN_HAND) {
            return super.use(level, player, hand);
        }
        ItemStack offhand = player.getOffhandItem();
        int leap = getLeap(stack);
        boolean yingda = "yingda".equals(modelId);
        boolean hongying = "hongying".equals(modelId);
        // 跃升晶片：提升跃升等级
        boolean leapChip = (yingda && offhand.is(com.icypunishing.registry.ModItems.ASCENSION_CHIP_DAWN.get()))
                || (hongying && offhand.is(com.icypunishing.registry.ModItems.ASCENSION_CHIP_HONGYING.get()));
        if (leapChip) {
            if (leap >= MAX_LEAP) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.icy_punishing.leap_invalid"), true);
                }
                return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    offhand.shrink(1);
                }
                setLeap(stack, leap + 1);
                level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                        net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.4f);
                player.displayClientMessage(Component.translatable("message.icy_punishing.leap_success",
                        leap + 1), true);
            }
            return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
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

        // 以 WIKI 攻击值为准，先清掉默认攻击修饰符
        event.removeAllModifiersFor(Attributes.ATTACK_DAMAGE);
        event.addModifier(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(UPGRADE_ATTACK_ID, attack, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HAND);
        event.addModifier(
                ModAttributes.CRITICAL_STRIKE_CHANCE,
                new AttributeModifier(UPGRADE_CRIT_ID, crit, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HAND);

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
        tooltipComponents.add(Component.translatable("tooltip.icy_punishing.label.ascension").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.translatable("ascension.icy_punishing." + ascension).withStyle(ascColor)));

        int leap = getLeap(stack);
        if (leap > 0) {
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.label.leap").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(leap + "/" + MAX_LEAP).withStyle(net.minecraft.ChatFormatting.AQUA)));
        }

        if (com.icypunishing.skill.SkillSetRegistry.hasSkillSet(modelId)) {
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.weapon.combat_toggle")
                    .withStyle(net.minecraft.ChatFormatting.YELLOW));
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.weapon.combat_orb")
                    .withStyle(net.minecraft.ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.weapon.combat_ult")
                    .withStyle(net.minecraft.ChatFormatting.GRAY));
        }

        var typeLine = Component.translatable("tooltip.icy_punishing.label.weapon_type").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.translatable("tooltip.icy_punishing.label.weapon_type.tachi").withStyle(net.minecraft.ChatFormatting.WHITE));
        if (hasData) {
            typeLine.append(Component.translatable("tooltip.icy_punishing.label.level_cap").withStyle(net.minecraft.ChatFormatting.GRAY))
                    .append(Component.literal(Integer.toString(WeaponUpgradeData.getLevelCap(modelId))).withStyle(net.minecraft.ChatFormatting.WHITE));
        }
        tooltipComponents.add(typeLine);

        String constructKey = lorePrefix + ".construct";
        Component construct = Component.translatable(constructKey);
        if (!construct.getString().equals(constructKey)) {
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.label.construct").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.translatable(constructKey).withStyle(net.minecraft.ChatFormatting.AQUA)));
        }

        if (hasData) {
            double baseAttack = WeaponUpgradeData.getBaseAttack(modelId);
            double maxAttack = WeaponUpgradeData.getMaxAttack(modelId);
            double attack = getAttack(stack);
            double progress = maxAttack > baseAttack
                    ? Math.min(1.0, Math.max(0.0, (attack - baseAttack) / (maxAttack - baseAttack)))
                    : 0.0;

            Component attackText = Component.translatable("tooltip.icy_punishing.label.attack").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Long.toString(Math.round(attack))).withStyle(net.minecraft.ChatFormatting.RED))
                    .append(Component.literal(" (" + Math.round(baseAttack) + "-" + Math.round(maxAttack) + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(attackText);

            int baseCrit = WeaponUpgradeData.getBaseCrit(modelId);
            int maxCrit = WeaponUpgradeData.getMaxCrit(modelId);
            int crit = (int) Math.round(baseCrit + (maxCrit - baseCrit) * progress);
            Component critText = Component.translatable("tooltip.icy_punishing.label.crit").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(Integer.toString(crit)).withStyle(net.minecraft.ChatFormatting.GOLD))
                    .append(Component.literal(" (" + baseCrit + "-" + maxCrit + ")").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
            tooltipComponents.add(critText);

            int maxStage = AwarenessUpgradeManager.getMaxStage(rank);
            getUpgradeStats(stack).ifPresent(stats -> {
                int maxLevelForStage = AwarenessUpgradeManager.getMaxLevel(stats.stage, rank);

                Component stageText = Component.translatable("tooltip.icy_punishing.label.stage").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(Integer.toString(stats.stage)).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE))
                        .append(Component.literal("/" + maxStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(stageText);

                Component levelText = Component.translatable("tooltip.icy_punishing.label.level").withStyle(net.minecraft.ChatFormatting.GRAY)
                        .append(Component.literal(Integer.toString(stats.level)).withStyle(net.minecraft.ChatFormatting.GREEN))
                        .append(Component.literal("/" + maxLevelForStage).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                tooltipComponents.add(levelText);
            });
        }

        String skillNameKey = lorePrefix + ".skill_name";
        if (!Component.translatable(skillNameKey).getString().equals(skillNameKey)) {
            tooltipComponents.add(Component.translatable("tooltip.icy_punishing.label.skill").withStyle(net.minecraft.ChatFormatting.GRAY)
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
