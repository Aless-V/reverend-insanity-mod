package com.reverendinsanity.client.gui;

// Wheel Menu Action Definition
public enum RadialMenuAction {
    APERTURE("Aperture Management", 0xFF4488FF),
    IMMORTAL_APERTURE("Immortal Aperture Management", 0xFFFFAA00),
    CODEX("Gu Codex", 0xFF66DD66),
    DEDUCTION("Killer Move Deduction", 0xFFDD66DD),
    SECLUSION("Closed-Door Cultivation", 0xFF88CCEE);

    public final String displayName;
    public final int color;

    RadialMenuAction(String name, int color) {
        this.displayName = name;
        this.color = color;
    }

    public static RadialMenuAction fromIndex(int idx) {
        RadialMenuAction[] vals = values();
        if (idx >= 0 && idx < vals.length) return vals[idx];
        return null;
    }
}
