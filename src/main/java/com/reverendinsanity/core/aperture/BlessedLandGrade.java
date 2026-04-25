package com.reverendinsanity.core.aperture;

import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.Aptitude;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.path.DaoPath;

// Blessed Land Rank: Faithful to the original novel
// Small Blessed Land: Up to 3 million mu (1:5 scale), Medium: 4-6 million mu (1:16), Large: 7-9 million mu (1:33), Special: 10+ million mu (1:38)
// size = island diameter (blocks), featureRadius = central feature placement radius
public enum BlessedLandGrade {
    LOWER("Lower Blessed Land", 3000, 2, 0.5f, 5, 1000, 128),
    MIDDLE("Middle Blessed Land", 6000, 4, 1.0f, 16, 2500, 200),
    UPPER("Upper Blessed Land", 10000, 8, 1.5f, 33, 5000, 300),
    SUPREME("Supreme Blessed Land", 16000, 15, 2.0f, 38, 10000, 400);

    private final String displayName;
    private final int size;
    private final int essencePerDay;
    private final float resourceDensity;
    private final int timeFlowRate;
    private final float qiCapacity;
    private final int featureRadius;

    BlessedLandGrade(String displayName, int size, int essencePerDay, float resourceDensity, int timeFlowRate, float qiCapacity, int featureRadius) {
        this.displayName = displayName;
        this.size = size;
        this.essencePerDay = essencePerDay;
        this.resourceDensity = resourceDensity;
        this.timeFlowRate = timeFlowRate;
        this.qiCapacity = qiCapacity;
        this.featureRadius = featureRadius;
    }

    public static BlessedLandGrade determine(Aperture aperture, GuMasterData data) {
        Aptitude apt = aperture.getAptitude();
        int totalMarks = 0;
        for (DaoPath path : DaoPath.values()) {
            totalMarks += data.getDaoMarks(path);
        }
        if ((apt == Aptitude.EXTREME || apt == Aptitude.A) && totalMarks > 5000) {
            return SUPREME;
        }
        if (apt == Aptitude.A || apt == Aptitude.EXTREME || totalMarks > 3000) {
            return UPPER;
        }
        if (apt.getTier() >= Aptitude.B.getTier() || totalMarks > 1000) {
            return MIDDLE;
        }
        return LOWER;
    }

    public String getDisplayName() { return displayName; }
    public int getSize() { return size; }
    public int getRadius() { return size / 2; }
    public int getEssencePerDay() { return essencePerDay; }
    public float getResourceDensity() { return resourceDensity; }
    public int getTimeFlowRate() { return timeFlowRate; }
    public float getQiCapacity() { return qiCapacity; }
    public int getFeatureRadius() { return featureRadius; }
}
