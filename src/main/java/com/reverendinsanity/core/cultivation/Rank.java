package com.reverendinsanity.core.cultivation;

// 蛊师境界，一到九转，对应凡(1-5)和仙(6-9)
public enum Rank {
    RANK_1(1, false, "Rank 1", 100),
    RANK_2(2, false, "Rank 2", 300),
    RANK_3(3, false, "Rank 3", 800),
    RANK_4(4, false, "Rank 4", 2000),
    RANK_5(5, false, "Rank 5", 5000),
    RANK_6(6, true,  "Rank 6", 15000),
    RANK_7(7, true,  "Rank 7", 50000),
    RANK_8(8, true,  "Rank 8", 200000),
    RANK_9(9, true,  "Rank 9", 1000000);

    private final int level;
    private final boolean immortal;
    private final String displayName;
    private final int basePrimevalEssence;

    Rank(int level, boolean immortal, String displayName, int basePrimevalEssence) {
        this.level = level;
        this.immortal = immortal;
        this.displayName = displayName;
        this.basePrimevalEssence = basePrimevalEssence;
    }

    public int getLevel() { return level; }
    public boolean isImmortal() { return immortal; }
    public String getDisplayName() { return displayName; }
    public int getBasePrimevalEssence() { return basePrimevalEssence; }

    public Rank next() {
        int nextOrd = this.ordinal() + 1;
        if (nextOrd >= values().length) return null;
        return values()[nextOrd];
    }

    public static Rank fromLevel(int level) {
        for (Rank r : values()) {
            if (r.level == level) return r;
        }
        return null;
    }
}
