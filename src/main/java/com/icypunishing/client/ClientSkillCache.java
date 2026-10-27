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
}
