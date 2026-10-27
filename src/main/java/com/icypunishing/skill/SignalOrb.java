package com.icypunishing.skill;

import net.minecraft.util.RandomSource;

public enum SignalOrb {
    RED(0, 0xE74856),
    YELLOW(1, 0xF0C060),
    BLUE(2, 0x3E8FFF);

    public final int id;
    public final int color;

    SignalOrb(int id, int color) {
        this.id = id;
        this.color = color;
    }

    public static SignalOrb byId(int id) {
        return switch (id) {
            case 0 -> RED;
            case 1 -> YELLOW;
            default -> BLUE;
        };
    }

    public static SignalOrb random(RandomSource rng) {
        return byId(rng.nextInt(3));
    }
}
