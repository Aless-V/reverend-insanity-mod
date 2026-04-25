package com.reverendinsanity.core.cultivation;

// 真元品质，1-9转各四小阶段，效率指数增长（9转达几亿量级）
public enum EssenceGrade {

    RANK1_INITIAL(Rank.RANK_1, SubRank.INITIAL, "Jade Green", 0x00CC66, 1.0f),
    RANK1_MIDDLE(Rank.RANK_1, SubRank.MIDDLE, "Verdant Green", 0x009944, 1.5f),
    RANK1_UPPER(Rank.RANK_1, SubRank.UPPER, "Deep Green", 0x006633, 2.0f),
    RANK1_PEAK(Rank.RANK_1, SubRank.PEAK, "Dark Green", 0x003311, 3.0f),

    RANK2_INITIAL(Rank.RANK_2, SubRank.INITIAL, "Light Red", 0xFF9999, 5.0f),
    RANK2_MIDDLE(Rank.RANK_2, SubRank.MIDDLE, "Crimson", 0xFF4444, 7.0f),
    RANK2_UPPER(Rank.RANK_2, SubRank.UPPER, "Deep Red", 0xCC0000, 10.0f),
    RANK2_PEAK(Rank.RANK_2, SubRank.PEAK, "Dark Red", 0x880000, 14.0f),

    RANK3_INITIAL(Rank.RANK_3, SubRank.INITIAL, "Pale Silver", 0xDDDDDD, 30.0f),
    RANK3_MIDDLE(Rank.RANK_3, SubRank.MIDDLE, "Mottled Silver", 0xCCCCCC, 40.0f),
    RANK3_UPPER(Rank.RANK_3, SubRank.UPPER, "Bright Silver", 0xBBBBBB, 55.0f),
    RANK3_PEAK(Rank.RANK_3, SubRank.PEAK, "Snow Silver", 0xAAAAAA, 75.0f),

    RANK4_INITIAL(Rank.RANK_4, SubRank.INITIAL, "Pale Gold", 0xFFEE88, 200.0f),
    RANK4_MIDDLE(Rank.RANK_4, SubRank.MIDDLE, "Bright Gold", 0xFFDD44, 280.0f),
    RANK4_UPPER(Rank.RANK_4, SubRank.UPPER, "Pure Gold", 0xFFCC00, 400.0f),
    RANK4_PEAK(Rank.RANK_4, SubRank.PEAK, "True Gold", 0xDDAA00, 550.0f),

    RANK5_INITIAL(Rank.RANK_5, SubRank.INITIAL, "Pale Purple", 0xCC99FF, 1_500.0f),
    RANK5_MIDDLE(Rank.RANK_5, SubRank.MIDDLE, "Vibrant Purple", 0xAA55FF, 2_200.0f),
    RANK5_UPPER(Rank.RANK_5, SubRank.UPPER, "Deep Purple", 0x8822DD, 3_200.0f),
    RANK5_PEAK(Rank.RANK_5, SubRank.PEAK, "Crystal Purple", 0x6600AA, 4_500.0f),

    RANK6_INITIAL(Rank.RANK_6, SubRank.INITIAL, "Immortal Cyan", 0x00FFCC, 15_000.0f),
    RANK6_MIDDLE(Rank.RANK_6, SubRank.MIDDLE, "Immortal Jade", 0x00DDAA, 25_000.0f),
    RANK6_UPPER(Rank.RANK_6, SubRank.UPPER, "Immortal Emerald", 0x00BB88, 40_000.0f),
    RANK6_PEAK(Rank.RANK_6, SubRank.PEAK, "Immortal Nephrite", 0x009966, 60_000.0f),

    RANK7_INITIAL(Rank.RANK_7, SubRank.INITIAL, "Semi-Sage White", 0xEEFFFF, 200_000.0f),
    RANK7_MIDDLE(Rank.RANK_7, SubRank.MIDDLE, "Sage Silver-White", 0xDDEEFF, 350_000.0f),
    RANK7_UPPER(Rank.RANK_7, SubRank.UPPER, "Sage Gold-White", 0xCCDDFF, 600_000.0f),
    RANK7_PEAK(Rank.RANK_7, SubRank.PEAK, "Sage Radiant White", 0xBBCCFF, 1_000_000.0f),

    RANK8_INITIAL(Rank.RANK_8, SubRank.INITIAL, "Heavenly Mystic Black", 0x110022, 5_000_000.0f),
    RANK8_MIDDLE(Rank.RANK_8, SubRank.MIDDLE, "Heavenly Abbysal Black", 0x220033, 10_000_000.0f),
    RANK8_UPPER(Rank.RANK_8, SubRank.UPPER, "Heavenly Deep Black", 0x330044, 18_000_000.0f),
    RANK8_PEAK(Rank.RANK_8, SubRank.PEAK, "Heavenly Supreme Black", 0x440055, 30_000_000.0f),

    RANK9_INITIAL(Rank.RANK_9, SubRank.INITIAL, "Primordial Chaos Grey", 0x888899, 200_000_000.0f),
    RANK9_MIDDLE(Rank.RANK_9, SubRank.MIDDLE, "Primordial Chaos Silver", 0xAABBCC, 350_000_000.0f),
    RANK9_UPPER(Rank.RANK_9, SubRank.UPPER, "Primordial Chaos Gold", 0xCCDDEE, 550_000_000.0f),
    RANK9_PEAK(Rank.RANK_9, SubRank.PEAK, "Primordial Chaos White", 0xEEEEFF, 800_000_000.0f);

    private final Rank rank;
    private final SubRank subRank;
    private final String displayName;
    private final int color;
    private final float efficiency;

    EssenceGrade(Rank rank, SubRank subRank, String displayName, int color, float efficiency) {
        this.rank = rank;
        this.subRank = subRank;
        this.displayName = displayName;
        this.color = color;
        this.efficiency = efficiency;
    }

    public Rank getRank() { return rank; }
    public SubRank getSubRank() { return subRank; }
    public String getDisplayName() { return displayName; }
    public int getColor() { return color; }
    public float getEfficiency() { return efficiency; }

    public static EssenceGrade of(Rank rank, SubRank subRank) {
        for (EssenceGrade g : values()) {
            if (g.rank == rank && g.subRank == subRank) return g;
        }
        return null;
    }
}
