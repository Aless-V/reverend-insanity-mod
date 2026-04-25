package com.reverendinsanity.entity;

import com.reverendinsanity.core.path.DaoPath;

// 十大九转尊者类型枚举（按原著实力排名设定属性，统一使用9转数值体系）
public enum VenerableType {
    WU_JI("Wuji Demon Venerable", "Limitless", 0xFF990099,
            24_000_000_000.0, 3_200_000_000.0, 1_600_000_000.0, 0.36,
            1.00, DaoPath.RULE, DaoPath.RESTRICTION),
    YOU_HUN("Youhun Demon Venerable", "Spectral Soul", 0xFF330066,
            22_800_000_000.0, 3_040_000_000.0, 912_000_000.0, 0.37,
            0.95, DaoPath.SOUL, DaoPath.SHADOW),
    DAO_TIAN("Daotian Demon Venerable", "Thieving Heaven", 0xFF444444,
            16_800_000_000.0, 2_816_000_000.0, 1_408_000_000.0, 0.42,
            0.88, DaoPath.STEAL, DaoPath.SPACE),
    KUANG_MAN("Kuangman Demon Venerable", "Reckless Savage", 0xFFCC0000,
            26_240_000_000.0, 3_328_000_000.0, 960_000_000.0, 0.40,
            0.82, DaoPath.STRENGTH, DaoPath.TRANSFORMATION),
    JU_YANG("Juyang Immortal Venerable", "Giant Sun", 0xFFFF8800,
            18_000_000_000.0, 2_400_000_000.0, 1_500_000_000.0, 0.35,
            0.75, DaoPath.LUCK, DaoPath.BLOOD),
    XING_XIU("Xingxiu Immortal Venerable", "Star Constellation", 0xFF6699FF,
            15_600_000_000.0, 2_400_000_000.0, 1_200_000_000.0, 0.36,
            0.75, DaoPath.WISDOM, DaoPath.STAR),
    LE_TU("Letu Immortal Venerable", "Paradise Earth", 0xFFC8A86E,
            21_600_000_000.0, 1_792_000_000.0, 2_240_000_000.0, 0.33,
            0.70, DaoPath.EARTH, DaoPath.HEAVEN),
    YUAN_SHI("Yuanshi Immortal Venerable", "Primordial Origin", 0xFFFFD700,
            17_280_000_000.0, 1_920_000_000.0, 1_440_000_000.0, 0.34,
            0.60, DaoPath.QI, DaoPath.YIN_YANG),
    HONG_LIAN("Honglian Demon Venerable", "Red Lotus", 0xFFFF0033,
            12_480_000_000.0, 1_664_000_000.0, 1_248_000_000.0, 0.38,
            0.52, DaoPath.TIME, DaoPath.SPACE),
    YUAN_LIAN("Yuanlian Immortal Venerable", "Genesis Lotus", 0xFF33CC33,
            20_800_000_000.0, 1_664_000_000.0, 1_040_000_000.0, 0.32,
            0.65, DaoPath.WOOD, DaoPath.PAINT);

    public final String displayNameCN;
    public final String displayNameEN;
    public final int color;
    public final double maxHealth;
    public final double attackDamage;
    public final double armor;
    public final double moveSpeed;
    public final double powerCoefficient;
    private final DaoPath primaryPath;
    private final DaoPath secondaryPath;

    VenerableType(String cn, String en, int color, double hp, double dmg, double armor, double speed,
                  double powerCoefficient, DaoPath primary, DaoPath secondary) {
        this.displayNameCN = cn;
        this.displayNameEN = en;
        this.color = color;
        this.maxHealth = hp;
        this.attackDamage = dmg;
        this.armor = armor;
        this.moveSpeed = speed;
        this.powerCoefficient = powerCoefficient;
        this.primaryPath = primary;
        this.secondaryPath = secondary;
    }

    public DaoPath getPrimaryPath() { return primaryPath; }
    public DaoPath getSecondaryPath() { return secondaryPath; }
    public double getPowerCoefficient() { return powerCoefficient; }

    public boolean isDemon() {
        return this == WU_JI || this == KUANG_MAN || this == DAO_TIAN || this == YOU_HUN || this == HONG_LIAN;
    }

    public static VenerableType fromName(String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            return WU_JI;
        }
    }
}
