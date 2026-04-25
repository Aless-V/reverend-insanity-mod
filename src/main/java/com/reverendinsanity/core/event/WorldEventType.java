package com.reverendinsanity.core.event;

// 天地异象类型枚举
public enum WorldEventType {

    ESSENCE_STORM(2400, false,
        "The world's vital essence surges, primeval essence recovery accelerated!",
        "The world's vital essence surges, primeval essence recovery accelerated!"),

    DAO_MARK_SURGE(3600, false,
        "Dao comprehension floods your mind, cultivation is twice as effective!",
        "Dao comprehension floods your mind, cultivation is twice as effective!"),

    RARE_GU_EMERGENCE(6000, false,
        "Heaven and earth shift, rare Gu worms emerge!",
        "Heaven and earth shift, rare Gu worms emerge!"),

    THOUGHTS_CLARITY(2400, false,
        "Your consciousness reaches unprecedented clarity, thoughts overflow!",
        "Your consciousness reaches unprecedented clarity, thoughts overflow!"),

    HEAVEN_WRATH(1200, true,
        "Heaven's wrath descends, cultivation is hindered!",
        "Heaven's wrath descends, cultivation is hindered!"),

    BEAST_TIDE(6000, true,
        "Beast tide surges, wild beasts pour in!",
        "Beast tide surges, wild beasts pour in!");

    private final int duration;
    private final boolean negative;
    private final String zhMessage;
    private final String enMessage;

    WorldEventType(int duration, boolean negative, String zhMessage, String enMessage) {
        this.duration = duration;
        this.negative = negative;
        this.zhMessage = zhMessage;
        this.enMessage = enMessage;
    }

    public int getDuration() { return duration; }
    public boolean isNegative() { return negative; }
    public String getZhMessage() { return zhMessage; }
    public String getEnMessage() { return enMessage; }
}
