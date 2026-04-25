package com.reverendinsanity.core.tutorial;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// 新手引导系统：首次进入触发分步教程
public class TutorialManager {

    private static final Map<UUID, Integer> playerStep = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> cooldownTicks = new ConcurrentHashMap<>();
    private static final int STEP_DELAY = 200;

    private static final String[][] TUTORIAL_STEPS = {
        {
            "Welcome to the world of Reverend Insanity!",
            "You are a fledgling Gu Master. You must open your aperture, refine Gu, and cultivate Killer Moves.",
            "Hold [~] to open the radial menu — your main interaction interface."
        },
        {
            "[Step 1: Open Aperture]",
            "Find and obtain the 'Hope Gu' (Hope Gu). Right-click to open your aperture.",
            "Once opened, you will receive an aptitude grade and Primeval Essence, officially becoming a Gu Master."
        },
        {
            "[Step 2: Obtain Gu Insects]",
            "Gu insects are the source of your power. Ways to obtain them:",
            "  1. Defeat monsters — they may drop Gu insects",
            "  2. Capture wild Gu (naturally spawning Wild Gu entities)",
            "  3. Trade with Gu merchants",
            "  4. Refine higher-rank Gu in the Gu Refining Furnace"
        },
        {
            "[Step 3: Activate Skills]",
            "Once a Gu insect is placed in your aperture, its corresponding skill is automatically unlocked.",
            "Press R / F / V / C to activate skills in slots 1-4.",
            "Each Gu insect has a different Path attribute, affecting combat effectiveness."
        },
        {
            "[Step 4: Killer Moves]",
            "Killer Moves are powerful combination techniques using multiple Gu insects together.",
            "Press [~] to open the menu → Aperture Management → Equip Killer Moves (max 2).",
            "Press Z / X to unleash Killer Moves. Their power far exceeds single Gu skills."
        },
        {
            "[Step 5: Rank Breakthrough]",
            "Cultivate to accumulate Primeval Essence and Thoughts. Break through minor sub-ranks through meditation.",
            "Major rank breakthroughs require 'Breakthrough Stones'. Higher ranks also trigger tribulations.",
            "The higher your rank, the higher-rank Gu insects you can use."
        },
        {
            "[Step 6: Advanced Systems]",
            "Deduction (J key): Research new Killer Move combinations.",
            "Avatar / Seclusion / Poison Oath / Transformation: Activate via the radial menu [~].",
            "Immortal Aperture (H key): Open your personal space at Rank 4+.",
            "Gu Insect Codex (K key): Record discovered Gu insects."
        },
        {
            "[Step 7: Diagnostic System]",
            "Admins can use /gu diagnose to check all system statuses.",
            "/gu diagnose <module> checks a single module.",
            "This is a core tool for development and testing."
        },
        {
            "Tutorial complete! May you become a great Gu Immortal!",
            "Friendly reminder: The Heaven's Will system is watching your actions. Be careful.",
            "Use /gu tutorial to view this tutorial again at any time."
        }
    };

    public static void onPlayerJoin(ServerPlayer player) {
        if (!playerStep.containsKey(player.getUUID())) {
            playerStep.put(player.getUUID(), 0);
            cooldownTicks.put(player.getUUID(), 100);
        }
    }

    public static void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (!playerStep.containsKey(uuid)) return;

        int step = playerStep.get(uuid);
        if (step >= TUTORIAL_STEPS.length) return;

        int cd = cooldownTicks.getOrDefault(uuid, 0);
        if (cd > 0) {
            cooldownTicks.put(uuid, cd - 1);
            return;
        }

        showStep(player, step);
        playerStep.put(uuid, step + 1);
        cooldownTicks.put(uuid, STEP_DELAY);
    }

    public static void showAllSteps(ServerPlayer player) {
        for (int i = 0; i < TUTORIAL_STEPS.length; i++) {
            showStep(player, i);
        }
        playerStep.put(player.getUUID(), TUTORIAL_STEPS.length);
    }

    public static void resetTutorial(ServerPlayer player) {
        playerStep.put(player.getUUID(), 0);
        cooldownTicks.put(player.getUUID(), 40);
        player.sendSystemMessage(
            Component.literal("Tutorial has been reset. The guidance will now restart.").withStyle(ChatFormatting.GREEN));
    }

    private static void showStep(ServerPlayer player, int step) {
        if (step < 0 || step >= TUTORIAL_STEPS.length) return;
        String[] lines = TUTORIAL_STEPS[step];

        player.sendSystemMessage(Component.literal(""));
        ChatFormatting titleColor = step == 0 || step == TUTORIAL_STEPS.length - 1
            ? ChatFormatting.GOLD : ChatFormatting.AQUA;

        for (int i = 0; i < lines.length; i++) {
            ChatFormatting color = i == 0 ? titleColor : ChatFormatting.GRAY;
            player.sendSystemMessage(Component.literal(lines[i]).withStyle(color));
        }
    }

    public static void onPlayerLogout(ServerPlayer player) {
        int step = playerStep.getOrDefault(player.getUUID(), 0);
        if (step >= TUTORIAL_STEPS.length) {
            playerStep.remove(player.getUUID());
            cooldownTicks.remove(player.getUUID());
        }
    }

    public static boolean isComplete(ServerPlayer player) {
        int step = playerStep.getOrDefault(player.getUUID(), 0);
        return step >= TUTORIAL_STEPS.length;
    }
}
