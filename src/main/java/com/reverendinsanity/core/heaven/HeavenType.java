package com.reverendinsanity.core.heaven;

// 太古九天：九个远古世界的残片
public enum HeavenType {
    WHITE("White Heaven", 0xFFFFFFFF, "purification"),
    RED("Red Heaven", 0xFFFF4444, "destruction"),
    ORANGE("Orange Heaven", 0xFFFF8800, "illumination"),
    YELLOW("Yellow Heaven", 0xFFFFDD00, "trade"),
    GREEN("Green Heaven", 0xFF44BB44, "growth"),
    CYAN("Cyan Heaven", 0xFF00CCCC, "bamboo"),
    BLUE("Blue Heaven", 0xFF4488FF, "starlight"),
    PURPLE("Purple Heaven", 0xFFAA44FF, "mystery"),
    BLACK("Black Heaven", 0xFF222222, "void");

    private final String displayName;
    private final int color;
    private final String aspect;

    HeavenType(String displayName, int color, String aspect) {
        this.displayName = displayName;
        this.color = color;
        this.aspect = aspect;
    }

    public String getDisplayName() { return displayName; }
    public int getColor() { return color; }
    public String getAspect() { return aspect; }
}
