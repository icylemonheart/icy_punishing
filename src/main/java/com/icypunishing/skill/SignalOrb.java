package com.icypunishing.skill;

import net.minecraft.util.RandomSource;

public enum SignalOrb {
    RED(0, 0xE74856),
    YELLOW(1, 0xF0C060),
    BLUE(2, 0x3E8FFF),
    /** 剑气球：深红之渊剑气状态下的特殊球。 */
    SWORD(3, 0xFF3A55);

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
            case 2 -> BLUE;
            case 3 -> SWORD;
            default -> RED;
        };
    }

    public static SignalOrb random(RandomSource rng) {
        return byId(rng.nextInt(3));
    }
}
