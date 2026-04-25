package com.reverendinsanity.core.deduction;

import com.reverendinsanity.core.combat.KillerMove;
import javax.annotation.Nullable;

// 推演结果
public record DeductionResult(
    Outcome outcome,
    @Nullable KillerMove resultMove,
    int improvementLevel,
    float experienceGained,
    String message
) {
    public enum Outcome {
        GREAT_SUCCESS("Great Success"),
        SUCCESS("Success"),
        PARTIAL("Partial Success"),
        FAILURE("Failure"),
        DISCOVERY("Discovery");

        private final String displayName;
        Outcome(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
