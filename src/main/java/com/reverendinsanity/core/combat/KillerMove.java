package com.reverendinsanity.core.combat;

import com.reverendinsanity.core.cultivation.Rank;
import com.reverendinsanity.core.path.DaoPath;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

// 杀招定义：核心蛊 + 辅助蛊组合产生的战斗招式
public record KillerMove(
    ResourceLocation id,
    String displayName,
    DaoPath primaryPath,
    int minRank,
    ResourceLocation coreGu,
    List<ResourceLocation> supportGu,
    float essenceCost,
    float thoughtsCost,
    float power,
    int cooldownTicks,
    MoveType moveType
) {
    public boolean canUse(Rank userRank) {
        return userRank.getLevel() >= minRank;
    }

    public List<ResourceLocation> getAllRequiredGu() {
        List<ResourceLocation> all = new ArrayList<>();
        all.add(coreGu);
        all.addAll(supportGu);
        return all;
    }

    public int getGuCount() {
        return getAllRequiredGu().size();
    }

    public enum MoveType {
        ATTACK("Attack"),
        DEFENSE("Defense"),
        MOVEMENT("Movement"),
        CONTROL("Control"),
        HEAL("Heal"),
        BUFF("Buff"),
        DEBUFF("Debuff"),
        ULTIMATE("Ultimate");

        private final String displayName;
        MoveType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
