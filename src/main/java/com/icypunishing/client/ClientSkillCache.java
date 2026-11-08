package com.icypunishing.client;

import com.icypunishing.network.ClientboundSkillStatePayload;
import com.icypunishing.skill.SignalOrb;

import java.util.ArrayList;
import java.util.List;

public final class ClientSkillCache {

    public static volatile boolean holdingSkillWeapon;
    public static volatile boolean combatMode;

    private static final List<SignalOrb> orbs = new ArrayList<>();
    private static volatile int energy;
    private static volatile int burstTicks;
    private static volatile int fireBuffTicks;
    private static volatile int rageStacks;
    private static volatile int rageTicks;
    private static volatile int dodgeCooldownTicks;
    private static volatile int dawnBladeTicks;
    private static volatile int lightningStacks;
    private static volatile int lightningTicks;
    private static volatile int ultShadowTicks;
    private static volatile int dawnExtraTicks;
    private static volatile int ultEnergyCost;
    private static volatile int swordAuraTicks;
    private static volatile int swordStacks;
    private static volatile int enhanceTicks;
    private static volatile int enhanceTier;
    private static volatile int crimsonTicks;
    private static volatile int orbBuffStacks;
    private static volatile int ultSwordTicks;

    private ClientSkillCache() {
    }

    public static void update(ClientboundSkillStatePayload payload) {
        synchronized (orbs) {
            orbs.clear();
            for (byte id : payload.orbIds()) {
                orbs.add(SignalOrb.byId(id));
            }
        }
        energy = payload.energy();
        burstTicks = payload.burstTicks();
        fireBuffTicks = payload.fireBuffTicks();
        holdingSkillWeapon = payload.holdingSkillWeapon();
        combatMode = payload.combatMode();
        rageStacks = payload.rageStacks();
        rageTicks = payload.rageTicks();
        dodgeCooldownTicks = payload.dodgeCooldownTicks();
        dawnBladeTicks = payload.dawnBladeTicks();
        lightningStacks = payload.lightningStacks();
        lightningTicks = payload.lightningTicks();
        ultShadowTicks = payload.ultShadowTicks();
        dawnExtraTicks = payload.dawnExtraTicks();
        ultEnergyCost = payload.ultEnergyCost();
        swordAuraTicks = payload.swordAuraTicks();
        swordStacks = payload.swordStacks();
        enhanceTicks = payload.enhanceTicks();
        enhanceTier = payload.enhanceTier();
        crimsonTicks = payload.crimsonTicks();
        orbBuffStacks = payload.orbBuffStacks();
        ultSwordTicks = payload.ultSwordTicks();
    }

    public static List<SignalOrb> orbsSnapshot() {
        synchronized (orbs) {
            return List.copyOf(orbs);
        }
    }

    public static int energy() {
        return energy;
    }

    public static int burstTicks() {
        return burstTicks;
    }

    public static int fireBuffTicks() {
        return fireBuffTicks;
    }

    public static int rageStacks() {
        return rageStacks;
    }

    public static int rageTicks() {
        return rageTicks;
    }

    public static int dodgeCooldownTicks() {
        return dodgeCooldownTicks;
    }

    public static int dawnBladeTicks() {
        return dawnBladeTicks;
    }

    public static int lightningStacks() {
        return lightningStacks;
    }

    public static int lightningTicks() {
        return lightningTicks;
    }

    public static int ultShadowTicks() {
        return ultShadowTicks;
    }

    public static int dawnExtraTicks() {
        return dawnExtraTicks;
    }

    /** 当前武器的必杀能量需求（默认满能量 100）。 */
    public static int ultEnergyCost() {
        return ultEnergyCost > 0 ? ultEnergyCost : com.icypunishing.skill.SkillState.MAX_ENERGY;
    }

    public static int swordAuraTicks() {
        return swordAuraTicks;
    }

    public static int swordStacks() {
        return swordStacks;
    }

    public static int enhanceTicks() {
        return enhanceTicks;
    }

    public static int enhanceTier() {
        return enhanceTier;
    }

    public static int crimsonTicks() {
        return crimsonTicks;
    }

    public static int orbBuffStacks() {
        return orbBuffStacks;
    }

    public static int ultSwordTicks() {
        return ultSwordTicks;
    }
}
