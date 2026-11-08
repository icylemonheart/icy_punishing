package com.icypunishing.skill;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class SkillState {
    public static final int HOTBAR_SLOTS = 8;
    public static final int STORED_SLOTS = 8;
    public static final int MAX_ORBS = HOTBAR_SLOTS + STORED_SLOTS;
    public static final int MAX_ENERGY = 100;

    private final List<SignalOrb> orbs = new ArrayList<>(MAX_ORBS);
    private int energy;
    private boolean combatMode;
    private int burstTicks;
    private int fireBuffTicks;
    private float fireRatio;
    /** 未触发过的计时哨兵值：不可用 Long.MIN_VALUE（参与减法会溢出成负数）。 */
    public static final long NO_TIME = -100_000L;

    private long lastTripleGameTime = NO_TIME;
    private long lastCombatGameTime = NO_TIME;
    private long combatCooldownUntil = NO_TIME;
    private long lastDodgeGameTime = NO_TIME;
    private int rageStacks;
    private int rageTicks;
    /** 已发放过技能点的经验等级里程碑（每 10 级一个）。 */
    private int skillMilestone;
    private int dodgeCooldownTicks;
    /** 黎明：必杀「红莲剑」剩余时间（tick）。 */
    private int dawnBladeTicks;
    /** 黎明：武器技能「逆断之影」雷伤层数（0-4）。 */
    private int lightningStacks;
    /** 黎明：逆断之影层数剩余时间（tick）。 */
    private int lightningTicks;
    /** 黎明：必杀触发的额外伤害加成剩余时间（tick）。 */
    private int ultShadowTicks;
    /** 黎明：SSS+ 黎明解放额外伤害剩余时间（tick）。 */
    private int dawnExtraTicks;
    /** 黎明：消红/蓝球后获得的引雷蓄势剩余时间（tick）。 */
    private int thunderChargeTicks;
    /** 黎明：跃升 9 级后必杀附带的能量回复剩余时间（tick）。 */
    private int ultRefundTicks;
    /** 深红之渊：剑气状态剩余时间（tick）。 */
    private int swordAuraTicks;
    /** 深红之渊：霸体（免疫击退）剩余时间（tick）。 */
    private int superArmorTicks;
    /** 深红之渊：心剑层数（0-2）。 */
    private int swordStacks;
    /** 深红之渊：强化状态剩余时间（tick）。 */
    private int enhanceTicks;
    /** 深红之渊：强化状态对应的消球数（1-3）。 */
    private int enhanceTier;
    /** 深红之渊：赤鞘出物理伤害加成剩余时间（tick）。 */
    private int crimsonTicks;
    /** 深红之渊：落樱散华层数（0-10）。 */
    private int orbBuffStacks;
    /** 深红之渊：落樱散华剩余时间（tick）。 */
    private int orbBuffTicks;
    /** 深红之渊：剑气状态下必杀后可释放剑气爆发的窗口（tick）。 */
    private int ultSwordTicks;
    /** 深红之渊：三消蓝球后的剑气触发窗口（tick）。 */
    private int blueTripleTicks;
    /** 深红之渊：跃升 18 级的首次出场能量是否已发放。 */
    private boolean deployEnergyGranted;
    /** 深红之渊：武器技能「进入剑气额外获得剑气球」的冷却到期时间（gameTime）。 */
    private long auraOrbReadyTime = NO_TIME;
    /** 深红之渊：太刀大招锁定剩余时间（tick），期间禁止移动、跳跃与左键攻击。 */
    private int ultLockTicks;
    /** 深红之渊：大招锁定期间保持的坐标。 */
    private double ultLockX;
    private double ultLockY;
    private double ultLockZ;

    public List<SignalOrb> getOrbs() {
        return orbs;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isCombatMode() {
        return combatMode;
    }

    public void setCombatMode(boolean combatMode) {
        this.combatMode = combatMode;
    }

    public int getBurstTicks() {
        return burstTicks;
    }

    public void setBurstTicks(int burstTicks) {
        this.burstTicks = burstTicks;
    }

    public int getFireBuffTicks() {
        return fireBuffTicks;
    }

    public void setFireBuffTicks(int fireBuffTicks) {
        this.fireBuffTicks = fireBuffTicks;
    }

    public float getFireRatio() {
        return fireRatio;
    }

    public void setFireRatio(float fireRatio) {
        this.fireRatio = fireRatio;
    }

    public long getLastTripleGameTime() {
        return lastTripleGameTime;
    }

    public void setLastTripleGameTime(long lastTripleGameTime) {
        this.lastTripleGameTime = lastTripleGameTime;
    }

    public long getLastCombatGameTime() {
        return lastCombatGameTime;
    }

    public void setLastCombatGameTime(long lastCombatGameTime) {
        this.lastCombatGameTime = lastCombatGameTime;
    }

    public long getCombatCooldownUntil() {
        return combatCooldownUntil;
    }

    public void setCombatCooldownUntil(long combatCooldownUntil) {
        this.combatCooldownUntil = combatCooldownUntil;
    }

    public long getLastDodgeGameTime() {
        return lastDodgeGameTime;
    }

    public void setLastDodgeGameTime(long lastDodgeGameTime) {
        this.lastDodgeGameTime = lastDodgeGameTime;
    }

    /** 怒火狂刃层数（0-5）。 */
    public int getRageStacks() {
        return rageStacks;
    }

    public void setRageStacks(int rageStacks) {
        this.rageStacks = rageStacks;
    }

    /** 怒火狂刃剩余持续时间（tick）。 */
    public int getRageTicks() {
        return rageTicks;
    }

    public void setRageTicks(int rageTicks) {
        this.rageTicks = rageTicks;
    }

    public int getSkillMilestone() {
        return skillMilestone;
    }

    public void setSkillMilestone(int skillMilestone) {
        this.skillMilestone = skillMilestone;
    }

    /** 闪避剩余冷却（tick）。 */
    public int getDodgeCooldownTicks() {
        return dodgeCooldownTicks;
    }

    public void setDodgeCooldownTicks(int dodgeCooldownTicks) {
        this.dodgeCooldownTicks = dodgeCooldownTicks;
    }

    public int getDawnBladeTicks() {
        return dawnBladeTicks;
    }

    public void setDawnBladeTicks(int dawnBladeTicks) {
        this.dawnBladeTicks = dawnBladeTicks;
    }

    public int getLightningStacks() {
        return lightningStacks;
    }

    public void setLightningStacks(int lightningStacks) {
        this.lightningStacks = Math.max(0, lightningStacks);
    }

    public int getLightningTicks() {
        return lightningTicks;
    }

    public void setLightningTicks(int lightningTicks) {
        this.lightningTicks = lightningTicks;
    }

    public int getUltShadowTicks() {
        return ultShadowTicks;
    }

    public void setUltShadowTicks(int ultShadowTicks) {
        this.ultShadowTicks = ultShadowTicks;
    }

    public int getDawnExtraTicks() {
        return dawnExtraTicks;
    }

    public void setDawnExtraTicks(int dawnExtraTicks) {
        this.dawnExtraTicks = dawnExtraTicks;
    }

    public int getThunderChargeTicks() {
        return thunderChargeTicks;
    }

    public void setThunderChargeTicks(int thunderChargeTicks) {
        this.thunderChargeTicks = thunderChargeTicks;
    }

    public int getUltRefundTicks() {
        return ultRefundTicks;
    }

    public void setUltRefundTicks(int ultRefundTicks) {
        this.ultRefundTicks = ultRefundTicks;
    }

    public int getSwordAuraTicks() {
        return swordAuraTicks;
    }

    public void setSwordAuraTicks(int swordAuraTicks) {
        this.swordAuraTicks = Math.max(0, swordAuraTicks);
    }

    public int getSuperArmorTicks() {
        return superArmorTicks;
    }

    public void setSuperArmorTicks(int superArmorTicks) {
        this.superArmorTicks = Math.max(0, superArmorTicks);
    }

    public int getSwordStacks() {
        return swordStacks;
    }

    public void setSwordStacks(int swordStacks) {
        this.swordStacks = Math.max(0, Math.min(2, swordStacks));
    }

    public int getEnhanceTicks() {
        return enhanceTicks;
    }

    public void setEnhanceTicks(int enhanceTicks) {
        this.enhanceTicks = Math.max(0, enhanceTicks);
    }

    public int getEnhanceTier() {
        return enhanceTier;
    }

    public void setEnhanceTier(int enhanceTier) {
        this.enhanceTier = enhanceTier;
    }

    public int getCrimsonTicks() {
        return crimsonTicks;
    }

    public void setCrimsonTicks(int crimsonTicks) {
        this.crimsonTicks = Math.max(0, crimsonTicks);
    }

    public int getOrbBuffStacks() {
        return orbBuffStacks;
    }

    public void setOrbBuffStacks(int orbBuffStacks) {
        this.orbBuffStacks = Math.max(0, Math.min(10, orbBuffStacks));
    }

    public int getOrbBuffTicks() {
        return orbBuffTicks;
    }

    public void setOrbBuffTicks(int orbBuffTicks) {
        this.orbBuffTicks = Math.max(0, orbBuffTicks);
    }

    public int getUltSwordTicks() {
        return ultSwordTicks;
    }

    public void setUltSwordTicks(int ultSwordTicks) {
        this.ultSwordTicks = Math.max(0, ultSwordTicks);
    }

    public int getBlueTripleTicks() {
        return blueTripleTicks;
    }

    public void setBlueTripleTicks(int blueTripleTicks) {
        this.blueTripleTicks = Math.max(0, blueTripleTicks);
    }

    public boolean isDeployEnergyGranted() {
        return deployEnergyGranted;
    }

    public void setDeployEnergyGranted(boolean deployEnergyGranted) {
        this.deployEnergyGranted = deployEnergyGranted;
    }

    public long getAuraOrbReadyTime() {
        return auraOrbReadyTime;
    }

    public void setAuraOrbReadyTime(long auraOrbReadyTime) {
        this.auraOrbReadyTime = auraOrbReadyTime;
    }

    public int getUltLockTicks() {
        return ultLockTicks;
    }

    /** 开始大招锁定：记录当前坐标并设置持续时间。 */
    public void startUltLock(int ticks, double x, double y, double z) {
        this.ultLockTicks = ticks;
        this.ultLockX = x;
        this.ultLockY = y;
        this.ultLockZ = z;
    }

    public void tickUltLock() {
        if (ultLockTicks > 0) {
            ultLockTicks--;
        }
    }

    /** 强制解除大招锁定。 */
    public void clearUltLock() {
        this.ultLockTicks = 0;
    }

    public double getUltLockX() {
        return ultLockX;
    }

    public double getUltLockY() {
        return ultLockY;
    }

    public double getUltLockZ() {
        return ultLockZ;
    }

    /** 把所有信号球转化为指定类型（剑气状态）。 */
    public void convertAllOrbs(SignalOrb orb) {
        orbs.replaceAll(o -> orb);
    }

    /** 按色块重新排列，便于凑成三消。 */
    public void sortGrouped() {
        orbs.sort(java.util.Comparator.comparingInt(o -> o.id));
    }

    public boolean addOrb(SignalOrb orb) {
        if (orbs.size() >= MAX_ORBS) {
            return false;
        }
        orbs.add(orb);
        return true;
    }

    public SignalOrb orbAt(int index) {
        return index >= 0 && index < orbs.size() ? orbs.get(index) : null;
    }

    public int consumeAt(int index) {
        SignalOrb color = orbAt(index);
        if (color == null) {
            return 0;
        }
        int start = index;
        while (start - 1 >= 0 && orbs.get(start - 1) == color) {
            start--;
        }
        int end = index;
        int limit = Math.min(orbs.size(), HOTBAR_SLOTS) - 1;
        while (end + 1 <= limit && orbs.get(end + 1) == color) {
            end++;
        }
        // 单次最多消除 3 个同色球；超过 3 时取包含点击位置的 3 格窗口
        if (end - start + 1 > 3) {
            int s = Math.max(start, Math.min(index - 1, end - 2));
            start = s;
            end = s + 2;
        }
        int consumed = end - start + 1;
        orbs.subList(start, end + 1).clear();
        return consumed;
    }

    /** 只消除指定位置的 1 个球（剑气球无二消/三消）。 */
    public int consumeOne(int index) {
        if (orbAt(index) == null) {
            return 0;
        }
        orbs.remove(index);
        return 1;
    }

    public void addEnergy(int v) {
        energy = Math.max(0, Math.min(MAX_ENERGY, energy + v));
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(MAX_ENERGY, energy));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (SignalOrb orb : orbs) {
            CompoundTag o = new CompoundTag();
            o.putInt("c", orb.id);
            list.add(o);
        }
        tag.put("orbs", list);
        tag.putInt("energy", energy);
        tag.putBoolean("combat", combatMode);
        tag.putInt("burst", burstTicks);
        tag.putInt("fire", fireBuffTicks);
        tag.putFloat("fireRatio", fireRatio);
        tag.putLong("lastTriple", lastTripleGameTime);
        tag.putLong("lastCombat", lastCombatGameTime);
        tag.putLong("combatCd", combatCooldownUntil);
        tag.putLong("lastDodge", lastDodgeGameTime);
        tag.putInt("rageStacks", rageStacks);
        tag.putInt("rageTicks", rageTicks);
        tag.putInt("skillMilestone", skillMilestone);
        tag.putInt("dodgeCd", dodgeCooldownTicks);
        tag.putInt("dawnBlade", dawnBladeTicks);
        tag.putInt("lightStacks", lightningStacks);
        tag.putInt("lightTicks", lightningTicks);
        tag.putInt("ultShadow", ultShadowTicks);
        tag.putInt("dawnExtra", dawnExtraTicks);
        tag.putInt("thunderCharge", thunderChargeTicks);
        tag.putInt("ultRefund", ultRefundTicks);
        tag.putInt("swordAura", swordAuraTicks);
        tag.putInt("swordStacks", swordStacks);
        tag.putInt("enhanceTicks", enhanceTicks);
        tag.putInt("enhanceTier", enhanceTier);
        tag.putInt("crimsonTicks", crimsonTicks);
        tag.putInt("orbBuffStacks", orbBuffStacks);
        tag.putInt("orbBuffTicks", orbBuffTicks);
        tag.putInt("ultSword", ultSwordTicks);
        tag.putInt("blueTriple", blueTripleTicks);
        tag.putBoolean("deployEnergy", deployEnergyGranted);
        tag.putLong("auraOrbReady", auraOrbReadyTime);
        return tag;
    }

    public static SkillState load(CompoundTag tag) {
        SkillState state = new SkillState();
        ListTag list = tag.getList("orbs", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            state.orbs.add(SignalOrb.byId(list.getCompound(i).getInt("c")));
        }
        state.energy = tag.getInt("energy");
        state.combatMode = tag.getBoolean("combat");
        state.burstTicks = tag.getInt("burst");
        state.fireBuffTicks = tag.getInt("fire");
        state.fireRatio = tag.getFloat("fireRatio");
        state.lastTripleGameTime = clampTime(readTime(tag, "lastTriple"));
        state.lastCombatGameTime = clampTime(readTime(tag, "lastCombat"));
        state.combatCooldownUntil = clampTime(readTime(tag, "combatCd"));
        state.lastDodgeGameTime = clampTime(readTime(tag, "lastDodge"));
        state.rageStacks = tag.getInt("rageStacks");
        state.rageTicks = tag.getInt("rageTicks");
        state.skillMilestone = tag.getInt("skillMilestone");
        state.dodgeCooldownTicks = tag.getInt("dodgeCd");
        state.dawnBladeTicks = tag.getInt("dawnBlade");
        state.lightningStacks = tag.getInt("lightStacks");
        state.lightningTicks = tag.getInt("lightTicks");
        state.ultShadowTicks = tag.getInt("ultShadow");
        state.dawnExtraTicks = tag.getInt("dawnExtra");
        state.thunderChargeTicks = tag.getInt("thunderCharge");
        state.ultRefundTicks = tag.getInt("ultRefund");
        state.swordAuraTicks = tag.getInt("swordAura");
        state.swordStacks = tag.getInt("swordStacks");
        state.enhanceTicks = tag.getInt("enhanceTicks");
        state.enhanceTier = tag.getInt("enhanceTier");
        state.crimsonTicks = tag.getInt("crimsonTicks");
        state.orbBuffStacks = tag.getInt("orbBuffStacks");
        state.orbBuffTicks = tag.getInt("orbBuffTicks");
        state.ultSwordTicks = tag.getInt("ultSword");
        state.blueTripleTicks = tag.getInt("blueTriple");
        state.deployEnergyGranted = tag.getBoolean("deployEnergy");
        state.auraOrbReadyTime = clampTime(readTime(tag, "auraOrbReady"));
        return state;
    }

    /** 读取计时字段，缺省视为从未触发。 */
    private static long readTime(CompoundTag tag, String key) {
        return tag.contains(key) ? tag.getLong(key) : NO_TIME;
    }

    /** 兼容旧存档里写入的 Long.MIN_VALUE，避免减法溢出。 */
    private static long clampTime(long value) {
        return value < NO_TIME ? NO_TIME : value;
    }

    public static final Codec<SkillState> CODEC = CompoundTag.CODEC.xmap(SkillState::load, SkillState::save);
}
