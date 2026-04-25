package com.reverendinsanity.command;

import com.reverendinsanity.core.clone.CloneManager;
import com.reverendinsanity.core.combat.CombatState;
import com.reverendinsanity.core.combat.KillerMove;
import com.reverendinsanity.core.combat.KillerMoveRegistry;
import com.reverendinsanity.core.combat.TrapManager;
import com.reverendinsanity.core.combat.ability.GuAbility;
import com.reverendinsanity.core.combat.ability.GuAbilityRegistry;
import com.reverendinsanity.core.combat.killermove.KillerMoveExecutor;
import com.reverendinsanity.core.cultivation.*;
import com.reverendinsanity.core.gu.GuInstance;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.core.gu.GuType;
import com.reverendinsanity.core.heavenwill.HeavenWillManager;
import com.reverendinsanity.core.oath.PoisonOathManager;
import com.reverendinsanity.core.transformation.TransformationManager;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// Automated Walkthrough Test: Step-by-step execution + confirmation pause + final report.
public class WalkthroughTestRunner {

    private static final Map<UUID, TestSession> sessions = new ConcurrentHashMap<>();

    public static int start(ServerPlayer player) {
        if (sessions.containsKey(player.getUUID())) {
            send(player, "A test is already in progress. Enter /gu test stop to terminate the current test.", ChatFormatting.RED);
            return 0;
        }
        TestSession session = new TestSession(player);
        sessions.put(player.getUUID(), session);
        send(player, "");
        send(player, "╔══════════════════════════════════════╗", ChatFormatting.GOLD);
        send(player, "║   Gu Real · Full Process Walkthrough Test            ║", ChatFormatting.GOLD);
        send(player, "╚══════════════════════════════════════╝", ChatFormatting.GOLD);
        send(player, "");
        send(player, "The test will execute each system feature step-by-step.", ChatFormatting.GRAY);
        send(player, "Please confirm the behavior after each step:", ChatFormatting.GRAY);
        send(player, "  /gu test ok    - Behavior is normal", ChatFormatting.GREEN);
        send(player, "  /gu test fail  - Behavior is abnormal", ChatFormatting.RED);
        send(player, "  /gu test skip  - Skip this step", ChatFormatting.YELLOW);
        send(player, "  /gu test stop  - Stop the test", ChatFormatting.GRAY);
        send(player, "");
        send(player, "Total of " + session.steps.size() + " test steps. Starting...", ChatFormatting.AQUA);
        send(player, "");
        session.executeCurrentStep();
        return 1;
    }

    public static int respond(ServerPlayer player, String response) {
        TestSession session = sessions.get(player.getUUID());
        if (session == null) {
            send(player, "No test in progress. Use /gu test to start.", ChatFormatting.RED);
            return 0;
        }

        switch (response.toLowerCase()) {
            case "ok" -> {
                session.recordResult(TestResult.PASS);
                send(player, "  ✓ Recorded: Passed", ChatFormatting.GREEN);
            }
            case "fail" -> {
                session.recordResult(TestResult.FAIL);
                send(player, "  ✗ Recorded: Failed", ChatFormatting.RED);
            }
            case "skip" -> {
                session.recordResult(TestResult.SKIP);
                send(player, "  - Recorded: Skipped", ChatFormatting.YELLOW);
            }
            case "stop" -> {
                send(player, "Test terminated.", ChatFormatting.RED);
                showReport(player, session);
                sessions.remove(player.getUUID());
                return 1;
            }
            default -> {
                send(player, "Invalid response. Options: ok / fail / skip / stop", ChatFormatting.RED);
                return 0;
            }
        }

        session.currentStep++;
        if (session.currentStep >= session.steps.size()) {
            send(player, "");
            send(player, "All test steps completed!", ChatFormatting.GOLD);
            showReport(player, session);
            sessions.remove(player.getUUID());
        } else {
            send(player, "");
            session.executeCurrentStep();
        }
        return 1;
    }

    public static boolean hasActiveSession(ServerPlayer player) {
        return sessions.containsKey(player.getUUID());
    }

    public static void onPlayerLogout(ServerPlayer player) {
        sessions.remove(player.getUUID());
    }

    private static void showReport(ServerPlayer player, TestSession session) {
        send(player, "");
        send(player, "╔══════════════════════════════════════╗", ChatFormatting.GOLD);
        send(player, "║         Test Report                   ║", ChatFormatting.GOLD);
        send(player, "╚══════════════════════════════════════╝", ChatFormatting.GOLD);

        int pass = 0, fail = 0, skip = 0, untested = 0;
        List<String> failedItems = new ArrayList<>();

        for (int i = 0; i < session.steps.size(); i++) {
            TestStep step = session.steps.get(i);
            TestResult result = session.results.getOrDefault(i, TestResult.UNTESTED);
            switch (result) {
                case PASS -> pass++;
                case FAIL -> { fail++; failedItems.add(step.name); }
                case SKIP -> skip++;
                case UNTESTED -> untested++;
            }
        }

        int total = session.steps.size();
        send(player, String.format("  Total: %d steps", total), ChatFormatting.GRAY);
        send(player, String.format("  Passed: %d", pass), ChatFormatting.GREEN);
        send(player, String.format("  Failed: %d", fail), fail > 0 ? ChatFormatting.RED : ChatFormatting.GREEN);
        send(player, String.format("  Skipped: %d", skip), ChatFormatting.YELLOW);
        send(player, String.format("  Untested: %d", untested), ChatFormatting.GRAY);
        send(player, "");

        if (!failedItems.isEmpty()) {
            send(player, "Failed Items:", ChatFormatting.RED);
            for (String item : failedItems) {
                send(player, "  ✗ " + item, ChatFormatting.RED);
            }
        }

        float passRate = total > 0 ? (float) pass / (total - skip) * 100 : 0;
        ChatFormatting rateColor = passRate >= 90 ? ChatFormatting.GREEN : passRate >= 70 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        send(player, String.format("Pass Rate: %.0f%%", passRate), rateColor);
    }

    enum TestResult { PASS, FAIL, SKIP, UNTESTED }

    static class TestStep {
        final String name;
        final String category;
        final String[] expectedFeedback;
        final Consumer<ServerPlayer> action;

        TestStep(String category, String name, String[] expected, Consumer<ServerPlayer> action) {
            this.category = category;
            this.name = name;
            this.expectedFeedback = expected;
            this.action = action;
        }
    }

    static class TestSession {
        final ServerPlayer player;
        final List<TestStep> steps;
        final Map<Integer, TestResult> results = new HashMap<>();
        int currentStep = 0;

        TestSession(ServerPlayer player) {
            this.player = player;
            this.steps = buildSteps();
        }

        void executeCurrentStep() {
            TestStep step = steps.get(currentStep);
            send(player, String.format("[Step %d/%d] [%s] %s",
                currentStep + 1, steps.size(), step.category, step.name), ChatFormatting.AQUA);
            send(player, "  Executing...", ChatFormatting.GRAY);

            try {
                step.action.accept(player);
                send(player, "  ✓ Execution completed", ChatFormatting.GREEN);
            } catch (Exception e) {
                send(player, "  ✗ Execution error: " + e.getMessage(), ChatFormatting.RED);
            }

            send(player, "  Expected Feedback:", ChatFormatting.YELLOW);
            for (String fb : step.expectedFeedback) {
                send(player, "    → " + fb, ChatFormatting.YELLOW);
            }
            send(player, "  Enter /gu test ok|fail|skip to confirm", ChatFormatting.GRAY);
        }

        void recordResult(TestResult result) {
            results.put(currentStep, result);
        }

        private List<TestStep> buildSteps() {
            List<TestStep> s = new ArrayList<>();

            // === Core System ===
            s.add(new TestStep("Basic", "Reset Data", new String[]{
                "Chat bar shows reset confirmation",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                data.getAperture().reset();
                p.displayClientMessage(Component.literal("Data has been reset"), false);
            }));

            s.add(new TestStep("Opening", "Open Aperture (A-Rank Aptitude)", new String[]{
                "Chat bar shows opening confirmation",
                "Should have audio feedback",
                "HUD top-left shows cultivation status bar",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                Aperture ap = data.getAperture();
                if (!ap.isOpened()) {
                    ap.open(Aptitude.A);
                }
                data.setLifespan(LifespanManager.getMaxLifespan(1));
                p.displayClientMessage(Component.literal("Aperture opened! Aptitude A 1 transformation·Beginner").withStyle(ChatFormatting.GOLD), false);
                p.level().playSound(null, p.getX(), p.getY(), p.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
            }));

            s.add(new TestStep("Opening", "HUD Overlay Display", new String[]{
                "Top-left corner of the screen shows: Realm, Qi Bar, Thought Bar",
                "Qi and Thoughts should be slowly recovering",
            }, p -> {
                p.displayClientMessage(Component.literal("Please check the HUD overlay in the top-left corner of the screen"), false);
            }));

            // === Gu Insects System ===
            s.add(new TestStep("Gu Insects", "Give Gu Insects (Moonlight+Bear Strength+Copper Skin)", new String[]{
                "3 gu insects appear in the inventory",
                "Items have tooltip descriptions (hover to view)",
            }, p -> {
                giveGuItem(p, "moonlight_gu");
                giveGuItem(p, "bear_strength_gu");
                giveGuItem(p, "copper_skin_gu");
                p.displayClientMessage(Component.literal("3 gu insects have been given"), false);
            }));

            s.add(new TestStep("Gu Insects", "Place Gu Insects in Aperture", new String[]{
                "Gu insects disappear from inventory, enter the aperture",
                "Pressing G to open the aperture interface should show the list of gu insects",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                Aperture ap = data.getAperture();
                addGuToAperture(ap, "moonlight_gu");
                addGuToAperture(ap, "bear_strength_gu");
                addGuToAperture(ap, "copper_skin_gu");
                p.displayClientMessage(Component.literal("3 gu insects have been placed in the aperture"), false);
            }));

            s.add(new TestStep("Gu Insects", "Aperture Management Interface (G Key)", new String[]{
                "Open the Aperture Management interface (dark-themed panel)",
                "Left side shows Gu insect list (Moonlight/Bear Strength/Copper Skin)",
                "Right side shows Gu insect details and feed button",
                "Interface has no blur effect",
            }, p -> {
                p.displayClientMessage(Component.literal("Please press the G key to open the aperture management interface"), false);
            }));

            // === Skills System ===
            s.add(new TestStep("Skills", "Activate Gu Abilities (R Key)", new String[]{
                "Activate Moonlight Gu Ability",
                "Should play Moonlight Sound Effect",
                "Should display visual particles/effects",
                "HUD Qi Bar should decrease",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                Aperture ap = data.getAperture();
                CombatState cs = data.getCombatState();
                GuAbility ability = GuAbilityRegistry.get(GuRegistry.id("moonlight_gu"));
                if (ability != null && ability.canUse(p, ap, cs)) {
                    ability.execute(p, ap, cs);
                    p.displayClientMessage(Component.literal("Moonlight Gu ability activated!"), false);
                } else {
                    p.displayClientMessage(Component.literal("Manually activated Moonlight Gu ability"), false);
                    if (ability != null) {
                        ap.setCurrentEssence(ap.getMaxEssence());
                        ability.execute(p, ap, cs);
                    }
                }
            }));

            s.add(new TestStep("Skills", "Activate Bear Strength Gu Ability (F Key)", new String[]{
                "Activate Bear Strength Gu Ability",
                "Should play Bear Strength Sound Effect",
                "Character attack power should increase",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                Aperture ap = data.getAperture();
                CombatState cs = data.getCombatState();
                ap.setCurrentEssence(ap.getMaxEssence());
                GuAbility ability = GuAbilityRegistry.get(GuRegistry.id("bear_strength_gu"));
                if (ability != null) {
                    ability.execute(p, ap, cs);
                    p.displayClientMessage(Component.literal("Bear Strength Gu ability activated!"), false);
                }
            }));

            // === Killer Moves System ===
            s.add(new TestStep("Killer Moves", "Equip Killer Move", new String[]{
                "Chat message shows successful equip",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                CombatState cs = data.getCombatState();
                KillerMove firstMove = null;
                for (KillerMove m : KillerMoveRegistry.getAll()) {
                    if (m.minRank() <= 1) { firstMove = m; break; }
                }
                if (firstMove != null) {
                    cs.equipMove(firstMove.id());
                    p.displayClientMessage(Component.literal("Killer Move equipped: " + firstMove.displayName()).withStyle(ChatFormatting.LIGHT_PURPLE), false);
                } else {
                    p.displayClientMessage(Component.literal("No 1-star Killer Move found"), false);
                }
            }));

            s.add(new TestStep("Killer Moves", "Execute Killer Move (Z Key)", new String[]{
                "Killer Move executed, should have significant sound effect",
                "Should have noticeable visual effects (particles/light effects)",
                "Essence consumed in large amounts",
                "HUD may display buff icons",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                Aperture ap = data.getAperture();
                CombatState cs = data.getCombatState();
                ap.setCurrentEssence(ap.getMaxEssence());
                List<ResourceLocation> equipped = cs.getEquippedMoves();
                if (!equipped.isEmpty()) {
                    KillerMove move = KillerMoveRegistry.get(equipped.get(0));
                    if (move != null) {
                        KillerMoveExecutor.execute(p, ap, cs, move);
                        p.displayClientMessage(Component.literal("Killer Move executed: " + move.displayName() + "!").withStyle(ChatFormatting.LIGHT_PURPLE), false);
                    }
                }
            }));

            // === Wheel Menu ===
            s.add(new TestStep("Interface", "Wheel Menu (~ Key)", new String[]{
                "Circular wheel menu appears (12 sectors)",
                "Mouse hover highlights action name",
                "Background is semi-transparent and not blurred",
                "Releasing key or clicking selects action",
            }, p -> {
                p.displayClientMessage(Component.literal("Please hold the ~ key to open the wheel menu"), false);
            }));

            // === Illusionary Clone ===
            s.add(new TestStep("Clone", "Activate Illusionary Clone", new String[]{
                "Chat message shows successful clone activation",
                "Should have purple particles surrounding the character",
                "Movement speed should increase significantly",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                data.getAperture().setCurrentEssence(data.getAperture().getMaxEssence());
                if (CloneManager.tryActivate(p)) {
                    p.displayClientMessage(Component.literal("Illusionary Clone — Activated!").withStyle(ChatFormatting.LIGHT_PURPLE), false);
                } else {
                    p.displayClientMessage(Component.literal("Failed to activate illusionary clone (might be on cooldown)"), false);
                }
            }));

            // === Seclusion ===
            s.add(new TestStep("Seclusion", "Enter Seclusion State", new String[]{
                "Chat message shows seclusion entry",
                "Purple particles appear around the character",
                "Essence begins to recover at an accelerated rate",
                "Movement will interrupt seclusion",
            }, p -> {
                if (SeclusionManager.enterSeclusion(p)) {
                    p.displayClientMessage(Component.literal("Entering seclusion... Calm your mind").withStyle(ChatFormatting.AQUA), false);
                } else {
                    p.displayClientMessage(Component.literal("Failed to enter seclusion"), false);
                }
            }));

            // === Life-Death Gate ===
            s.add(new TestStep("Life-Death Gate", "Open Life-Death Gate (50% chance)", new String[]{
                "Life Gate: Full recovery + Attack/Speed buff",
                "Death Gate: Lose 40% HP + Debuff + Lifespan consumption",
                "Should have noticeable sound effect feedback",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                data.getAperture().setCurrentEssence(data.getAperture().getMaxEssence());
                if (com.reverendinsanity.core.combat.LifeDeathGateManager.openGate(p)) {
                    // openGate Internal handling message
                } else {
                    p.displayClientMessage(Component.literal("Life-Death Gate is on cooldown"), false);
                }
            }));

            // === Trap ===
            s.add(new TestStep("Trap", "Place Hidden Trap", new String[]{
                "Chat message shows successful trap placement",
                "Ground position should have subtle particle indicator",
                "Enemy proximity will trigger AoE damage",
            }, p -> {
                if (TrapManager.placeTrap(p)) {
                    p.displayClientMessage(Component.literal("Trap placed successfully!").withStyle(ChatFormatting.GOLD), false);
                } else {
                    p.displayClientMessage(Component.literal("Failed to place trap"), false);
                }
            }));

            // === Poison Oath ===
            s.add(new TestStep("Poison Oath", "Make a Killing Vow", new String[]{
                "Chat message shows poison oath information",
                "Gain temporary attack/speed buff",
                "Must kill target within time limit, otherwise face penalties",
            }, p -> {
                if (!PoisonOathManager.hasActiveOath(p)) {
                    if (PoisonOathManager.makeOath(p, PoisonOathManager.OathType.KILL_VOW)) {
                        p.displayClientMessage(Component.literal("Killing Vow made!").withStyle(ChatFormatting.DARK_RED), false);
                    }
                } else {
                    p.displayClientMessage(Component.literal("Active poison oath already exists"), false);
                }
            }));

            // === Transformation ===
            s.add(new TestStep("Transformation", "Transform: Wolf Form", new String[]{
                "Character movement speed significantly increases",
                "Should have gray particles surrounding the character",
                "Chat message shows transformation information",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                data.getAperture().setCurrentEssence(data.getAperture().getMaxEssence());
                if (TransformationManager.isTransformed(p)) {
                    TransformationManager.cancelTransform(p);
                }
                if (TransformationManager.tryTransform(p, TransformationManager.TransformForm.WOLF)) {
                    p.displayClientMessage(Component.literal("Transformed into Wolf Form!").withStyle(ChatFormatting.GRAY), false);
                }
            }));

            s.add(new TestStep("Transformation", "Cancel Transformation", new String[]{
                "Speed returns to normal",
                "Particle effects disappear",
            }, p -> {
                if (TransformationManager.isTransformed(p)) {
                    TransformationManager.cancelTransform(p);
                    p.displayClientMessage(Component.literal("Transformation cancelled!"), false);
                }
            }));

            // === Heaven's Will System ===
            s.add(new TestStep("Heaven's Will", "Heaven's Will Attention Test", new String[]{
                "Chat bar shows current Heaven's Will attention value",
                "Attention should increase from previous skill/Killer Move operations",
            }, p -> {
                float attention = HeavenWillManager.getAttention(p);
                p.displayClientMessage(Component.literal(
                    "Heaven's Will Attention: " + String.format("%.1f", attention) + "/100").withStyle(
                    attention < 25 ? ChatFormatting.GREEN : attention < 50 ? ChatFormatting.YELLOW : ChatFormatting.RED), false);
            }));

            // === Lifespan ===
            s.add(new TestStep("Lifespan", "Lifespan System Status", new String[]{
                "Display current lifespan/maximum",
                "Lifespan should consume slowly (every 200 ticks-1)",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                int ls = data.getLifespan();
                int max = LifespanManager.getMaxLifespan(data.getAperture().getRank().getLevel());
                p.displayClientMessage(Component.literal(
                    "Lifespan: " + ls + "/" + max + " (" + (max > 0 ? (ls * 100 / max) : 0) + "%)").withStyle(ChatFormatting.AQUA), false);
            }));

            // === Bloodline ===
            s.add(new TestStep("Bloodline", "Bloodline Status", new String[]{
                "Display assigned bloodline type",
                "Bloodline provides permanent attribute bonuses",
            }, p -> {
                BloodlineManager.Bloodline bl = BloodlineManager.getBloodline(p);
                if (bl != null && bl != BloodlineManager.Bloodline.NONE) {
                    p.displayClientMessage(Component.literal("Bloodline: " + bl.displayName).withStyle(ChatFormatting.GOLD), false);
                } else {
                    BloodlineManager.assignBloodline(p);
                    bl = BloodlineManager.getBloodline(p);
                    p.displayClientMessage(Component.literal("Bloodline assigned: " + (bl != null ? bl.displayName : "None")).withStyle(ChatFormatting.GOLD), false);
                }
            }));

            // === Luck ===
            s.add(new TestStep("Luck", "Luck System Status", new String[]{
                "Display luck value and status description",
                "Luck affects drop rates and gu cultivation success",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                float luck = data.getLuck();
                String desc = luck > 1.0f ? "Good Luck" : luck >= 1.0f ? "Normal" : "Bad Luck";
                p.displayClientMessage(Component.literal(
                    "Luck: " + String.format("%.2f", luck) + " (" + desc + ")").withStyle(ChatFormatting.GOLD), false);
            }));

            // === Gu Insect Codex ===
            s.add(new TestStep("Interface", "Gu Insect Codex (K Key)", new String[]{
                "Open the codex interface",
                "Display the list of discovered gu insects",
                "No blur effect on the interface",
            }, p -> {
                p.displayClientMessage(Component.literal("Press K key to open the Gu Insect Codex"), false);
            }));

            // === Simulation Interface ===
            s.add(new TestStep("Interface", "Simulation Interface (J Key)", new String[]{
                "Open the simulation interface",
                "Display gu insect selection/road selection area",
                "Display success rate estimation",
            }, p -> {
                p.displayClientMessage(Component.literal("Press J key to open the Simulation Interface"), false);
            }));

            // === Realm Breakthrough ===
            s.add(new TestStep("Breakthrough", "Minor Realm Breakthrough (Crouching and Meditating)", new String[]{
                "Crouch for 5 seconds to trigger realm breakthrough",
                "Should have sound effects and particle effects",
                "Realm should upgrade from the initial stage",
            }, p -> {
                GuMasterData data = p.getData(ModAttachments.GU_MASTER_DATA.get());
                Aperture ap = data.getAperture();
                ap.setCurrentEssence(ap.getMaxEssence());
                ap.regenerateThoughts(ap.getMaxThoughts());
                p.displayClientMessage(Component.literal("真元/念头已充满。请蹲下5秒触发冥想突破。"), false);
            }));

            // === Registry Validation ===
            s.add(new TestStep("Registry", "Data Integrity Validation", new String[]{
                "Gu Insects >= 148 / Abilities >= 146 / Killer Moves >= 61",
                "48 Roads Fully Covered",
                "All Numbers Should Be Green (Met)",
            }, p -> {
                int gu = GuRegistry.getAll().size();
                int ab = GuAbilityRegistry.getAll().size();
                int mv = KillerMoveRegistry.getAll().size();
                ChatFormatting gc = gu >= 148 ? ChatFormatting.GREEN : ChatFormatting.RED;
                ChatFormatting ac = ab >= 146 ? ChatFormatting.GREEN : ChatFormatting.RED;
                ChatFormatting mc = mv >= 61 ? ChatFormatting.GREEN : ChatFormatting.RED;
                p.sendSystemMessage(Component.literal("  Gu Insects: " + gu).withStyle(gc));
                p.sendSystemMessage(Component.literal("  Abilities: " + ab).withStyle(ac));
                p.sendSystemMessage(Component.literal("  Killer Moves: " + mv).withStyle(mc));
            }));

            return s;
        }
    }

    private static void giveGuItem(ServerPlayer player, String guId) {
        ResourceLocation id = GuRegistry.id(guId);
        net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(id);
        if (item != net.minecraft.world.item.Items.AIR) {
            player.getInventory().add(new net.minecraft.world.item.ItemStack(item));
        }
    }

    private static void addGuToAperture(Aperture ap, String guId) {
        ResourceLocation id = GuRegistry.id(guId);
        if (GuRegistry.get(id) != null) {
            GuInstance gu = new GuInstance(id);
            ap.addGu(gu);
        }
    }

    private static void send(ServerPlayer player, String msg, ChatFormatting... formats) {
        MutableComponent comp = Component.literal(msg);
        for (ChatFormatting f : formats) comp = comp.withStyle(f);
        player.sendSystemMessage(comp);
    }
}
