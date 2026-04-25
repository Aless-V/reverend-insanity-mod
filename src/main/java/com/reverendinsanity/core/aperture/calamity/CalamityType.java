package com.reverendinsanity.core.aperture.calamity;

import com.reverendinsanity.core.aperture.BlessedLandGrade;

// Tribulation Types: Heavenly Tribulation (rare, powerful) and Earthly Tribulation (common, weaker).
public enum CalamityType {
    EARTH_CRACK("Earth Split", Category.EARTH_DISASTER, 10, 5.0f),
    BEAST_TIDE("Beast Tide", Category.EARTH_DISASTER, 15, 8.0f),
    FIRE_SPREAD("Fire Disaster", Category.EARTH_DISASTER, 8, 3.0f),
    VOID_EROSION("Void Erosion", Category.EARTH_DISASTER, 20, 12.0f),
    THUNDER_TRIBULATION("Lightning Tribulation", Category.HEAVENLY_TRIBULATION, 30, 20.0f),
    SILVER_SERPENT("Silver-Horned Green-Scaled Python", Category.HEAVENLY_TRIBULATION, 50, 35.0f),
    CHAOS_STORM("Chaos Storm", Category.HEAVENLY_TRIBULATION, 40, 25.0f);

    private final String displayName;
    private final Category category;
    private final int baseDuration;
    private final float baseDamage;

    CalamityType(String displayName, Category category, int baseDuration, float baseDamage) {
        this.displayName = displayName;
        this.category = category;
        this.baseDuration = baseDuration;
        this.baseDamage = baseDamage;
    }

    public boolean isHeavenlyTribulation() {
        return category == Category.HEAVENLY_TRIBULATION;
    }

    public float getScaledDamage(BlessedLandGrade grade) {
        return baseDamage * (1 + grade.ordinal() * 0.5f);
    }

    public int getScaledDuration(BlessedLandGrade grade) {
        return baseDuration * 20;
    }

    public String getDisplayName() { return displayName; }
    public Category getCategory() { return category; }
    public int getBaseDuration() { return baseDuration; }
    public float getBaseDamage() { return baseDamage; }

    public enum Category {
        EARTH_DISASTER("Earthly Tribulation"),
        HEAVENLY_TRIBULATION("Heavenly Tribulation");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
}
