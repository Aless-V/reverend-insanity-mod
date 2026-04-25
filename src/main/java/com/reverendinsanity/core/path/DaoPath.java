package com.reverendinsanity.core.path;

// 蛊修流派（大道），原著48种流派
public enum DaoPath {
    STRENGTH("Strength Path", Category.COMBAT),
    BLOOD("Blood Path", Category.COMBAT),
    SWORD("Sword Path", Category.COMBAT),
    BLADE("Blade Path", Category.COMBAT),
    KILL("Kill Path", Category.COMBAT),
    SOLDIER("Soldier Path", Category.COMBAT),

    SOUL("Soul Path", Category.SPIRITUAL),
    DREAM("Dream Path", Category.SPIRITUAL),
    WISDOM("Wisdom Path", Category.SPIRITUAL),
    CHARM("Charm Path", Category.SPIRITUAL),
    ILLUSION("Illusion Path", Category.SPIRITUAL),

    FIRE("Fire Path", Category.ELEMENTAL),
    WATER("Water Path", Category.ELEMENTAL),
    EARTH("Earth Path", Category.ELEMENTAL),
    METAL("Metal Path", Category.ELEMENTAL),
    WOOD("Wood Path", Category.ELEMENTAL),
    WIND("Wind Path", Category.ELEMENTAL),
    LIGHTNING("Lightning Path", Category.ELEMENTAL),
    ICE("Ice Path", Category.ELEMENTAL),
    LIGHT("Light Path", Category.ELEMENTAL),
    DARK("Dark Path", Category.ELEMENTAL),
    SHADOW("Shadow Path", Category.ELEMENTAL),
    CLOUD("Cloud Path", Category.ELEMENTAL),

    SPACE("Space Path", Category.RULE),
    TIME("Time Path", Category.RULE),
    RULE("Rule Path", Category.RULE),
    LUCK("Luck Path", Category.RULE),
    HEAVEN("Heaven Path", Category.RULE),
    HUMAN("Human Path", Category.RULE),
    STAR("Star Path", Category.RULE),

    REFINEMENT("Refinement Path", Category.SUPPORT),
    FORMATION("Formation Path", Category.SUPPORT),
    PILL("Pill Path", Category.SUPPORT),
    ENSLAVE("Enslave Path", Category.SUPPORT),
    FOOD("Food Path", Category.SUPPORT),
    PAINT("Paint Path", Category.SUPPORT),
    STEAL("Steal Path", Category.SUPPORT),
    BONE("Bone Path", Category.SUPPORT),
    SOUND("Sound Path", Category.SUPPORT),
    INFORMATION("Information Path", Category.SUPPORT),

    POISON("Poison Path", Category.SPECIAL),
    TRANSFORMATION("Transformation Path", Category.SPECIAL),
    YIN_YANG("Yin Yang Path", Category.SPECIAL),
    FLIGHT("Flight Path", Category.SPECIAL),
    MOON("Moon Path", Category.SPECIAL),
    QI("Qi Path", Category.SPECIAL),
    VOID("Void Path", Category.SPECIAL),
    RESTRICTION("Restriction Path", Category.SPECIAL);

    private final String displayName;
    private final Category category;

    DaoPath(String displayName, Category category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() { return displayName; }
    public Category getCategory() { return category; }

    public int getColor() {
        return switch (this) {
            case STRENGTH -> 0xCC4400;
            case BLOOD -> 0xAA0000;
            case SWORD -> 0xCCCCFF;
            case BLADE -> 0x888888;
            case KILL -> 0x660000;
            case SOLDIER -> 0x886633;
            case SOUL -> 0x8866FF;
            case DREAM -> 0xCC88FF;
            case WISDOM -> 0x4488CC;
            case CHARM -> 0xFF66AA;
            case ILLUSION -> 0xAA66CC;
            case FIRE -> 0xFF4400;
            case WATER -> 0x2288FF;
            case EARTH -> 0x886622;
            case METAL -> 0xFFDD00;
            case WOOD -> 0x22AA22;
            case WIND -> 0x88FFCC;
            case LIGHTNING -> 0xFFFF00;
            case ICE -> 0x88DDFF;
            case LIGHT -> 0xFFFFCC;
            case DARK -> 0x330066;
            case SHADOW -> 0x333355;
            case CLOUD -> 0xCCCCDD;
            case SPACE -> 0x2222CC;
            case TIME -> 0xCCAA44;
            case RULE -> 0xFFCC00;
            case LUCK -> 0x00CC44;
            case HEAVEN -> 0xDDDDFF;
            case HUMAN -> 0xFFAA88;
            case STAR -> 0xFFFFAA;
            case REFINEMENT -> 0xFF6600;
            case FORMATION -> 0x4466AA;
            case PILL -> 0x44CC88;
            case ENSLAVE -> 0x664488;
            case FOOD -> 0xCC8844;
            case PAINT -> 0xFF44FF;
            case STEAL -> 0x444444;
            case BONE -> 0xDDDDCC;
            case SOUND -> 0x66CCCC;
            case INFORMATION -> 0x44AACC;
            case POISON -> 0x44AA00;
            case TRANSFORMATION -> 0xCC44CC;
            case YIN_YANG -> 0xAAAAAA;
            case FLIGHT -> 0xAADDFF;
            case MOON -> 0xCCDDFF;
            case QI -> 0x88CCAA;
            case VOID -> 0x220044;
            case RESTRICTION -> 0xAA4444;
        };
    }

    public enum Category {
        COMBAT("Combat"),
        SPIRITUAL("Spiritual"),
        ELEMENTAL("Elemental"),
        RULE("Rule"),
        SUPPORT("Support"),
        SPECIAL("Special");

        private final String displayName;
        Category(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
