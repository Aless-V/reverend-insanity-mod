package com.reverendinsanity.core.cultivation;

// 蛊师资质，决定真元占空窍比例和可达到的最高境界
public enum Aptitude {
    NONE(0, 0f, 0, "No Talent"),
    D(1, 0.25f, 2, "D Grade"),
    C(2, 0.45f, 3, "C Grade"),
    B(3, 0.65f, 4, "B Grade"),
    A(4, 0.85f, 5, "A Grade"),
    EXTREME(5, 0.98f, 5, "Ten Absolute Physique");

    private final int tier;
    private final float essenceRatio;
    private final int maxRank;
    private final String displayName;

    Aptitude(int tier, float essenceRatio, int maxRank, String displayName) {
        this.tier = tier;
        this.essenceRatio = essenceRatio;
        this.maxRank = maxRank;
        this.displayName = displayName;
    }

    public int getTier() { return tier; }
    public float getEssenceRatio() { return essenceRatio; }
    public int getMaxRank() { return maxRank; }
    public String getDisplayName() { return displayName; }

    public boolean canAdvanceTo(Rank rank) {
        return rank.getLevel() <= maxRank;
    }
}
