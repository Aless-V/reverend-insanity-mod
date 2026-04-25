package com.reverendinsanity.core.path;

// 流派境界
public enum PathRealm {
    ORDINARY("Ordinary", 0),
    MASTER("Master", 1),
    GRANDMASTER("Grandmaster", 2),
    GREAT_GRANDMASTER("Great Grandmaster", 3),
    QUASI_SUPREME("Quasi Supreme Grandmaster", 4),
    SUPREME("Supreme Grandmaster", 5),
    DAO_LORD("Dao Lord", 6);

    private final String displayName;
    private final int tier;

    PathRealm(String displayName, int tier) {
        this.displayName = displayName;
        this.tier = tier;
    }

    public String getDisplayName() { return displayName; }
    public int getTier() { return tier; }

    public PathRealm next() {
        int nextOrd = this.ordinal() + 1;
        if (nextOrd >= values().length) return null;
        return values()[nextOrd];
    }
}
