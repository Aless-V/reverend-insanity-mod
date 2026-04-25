package com.reverendinsanity.core.faction;

// 势力阵营
public enum Faction {
    RIGHTEOUS("Righteous", 0xFF44AAFF),
    DEMONIC("Demonic", 0xFFCC2222),
    INDEPENDENT("Independant", 0xFFCCCC44);

    private final String displayName;
    private final int color;

    Faction(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public int getColor() { return color; }
}
