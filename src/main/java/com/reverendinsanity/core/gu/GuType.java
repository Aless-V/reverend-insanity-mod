package com.reverendinsanity.core.gu;

import com.reverendinsanity.core.cultivation.Rank;
import com.reverendinsanity.core.path.DaoPath;
import net.minecraft.resources.ResourceLocation;

// 蛊虫类型定义（不可变模板）
public record GuType(
    ResourceLocation id,
    String displayName,
    int rank,
    DaoPath path,
    GuCategory category,
    float essenceCost,
    float feedInterval,
    String feedItem
) {
    public boolean isImmortal() {
        return rank >= 6;
    }

    public boolean canBeUsedBy(Rank guMasterRank) {
        return guMasterRank.getLevel() >= this.rank;
    }

    public enum GuCategory {
        ATTACK("Attack"),
        DEFENSE("Defense"),
        MOVEMENT("Movement"),
        DETECTION("Detection"),
        SUPPORT("Support"),
        HEALING("Healing"),
        ENSLAVE("Enslave"),
        SPECIAL("Special");

        private final String displayName;
        GuCategory(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
