package com.icypunishing.core;

public class AwarenessStats {
    private final int minHealth;
    private final int maxHealth;
    private final int minCriticalStrike;
    private final int maxCriticalStrike;
    private final int minAttack;
    private final int maxAttack;
    private final int minDefense;
    private final int maxDefense;

    public AwarenessStats(int minHealth, int maxHealth,
                         int minCriticalStrike, int maxCriticalStrike,
                         int minAttack, int maxAttack, int minDefense, int maxDefense) {
        this.minHealth = minHealth;
        this.maxHealth = maxHealth;
        this.minCriticalStrike = minCriticalStrike;
        this.maxCriticalStrike = maxCriticalStrike;
        this.minAttack = minAttack;
        this.maxAttack = maxAttack;
        this.minDefense = minDefense;
        this.maxDefense = maxDefense;
    }

    public int getMinHealth() { return minHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getMinCriticalStrike() { return minCriticalStrike; }
    public int getMaxCriticalStrike() { return maxCriticalStrike; }
    public int getMinAttack() { return minAttack; }
    public int getMaxAttack() { return maxAttack; }
    public int getMinDefense() { return minDefense; }
    public int getMaxDefense() { return maxDefense; }
}
