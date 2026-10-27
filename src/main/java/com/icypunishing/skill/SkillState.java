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
    private int dodgeCooldownTicks;

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

    /** 闪避剩余冷却（tick）。 */
    public int getDodgeCooldownTicks() {
        return dodgeCooldownTicks;
    }

    public void setDodgeCooldownTicks(int dodgeCooldownTicks) {
        this.dodgeCooldownTicks = dodgeCooldownTicks;
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
        tag.putInt("dodgeCd", dodgeCooldownTicks);
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
        state.dodgeCooldownTicks = tag.getInt("dodgeCd");
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
