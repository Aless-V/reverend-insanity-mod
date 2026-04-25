package com.reverendinsanity.command;

import com.reverendinsanity.core.aperture.ImmortalAperture;
import com.reverendinsanity.core.aperture.calamity.CalamityManager;
import com.reverendinsanity.core.aperture.calamity.Calamity;
import com.reverendinsanity.core.clone.CloneManager;
import com.reverendinsanity.core.combat.CombatState;
import com.reverendinsanity.core.combat.KillerMove;
import com.reverendinsanity.core.combat.KillerMoveRegistry;
import com.reverendinsanity.core.combat.SealManager;
import com.reverendinsanity.core.combat.TrapManager;
import com.reverendinsanity.core.combat.LifeDeathGateManager;
import com.reverendinsanity.core.combat.SelfDestructManager;
import com.reverendinsanity.core.combat.AmbushManager;
import com.reverendinsanity.core.combat.ability.GuAbility;
import com.reverendinsanity.core.combat.ability.GuAbilityRegistry;
import com.reverendinsanity.core.combat.buff.GuBuffManager;
import com.reverendinsanity.core.combat.custom.PathEffectComponent;
import com.reverendinsanity.core.combat.custom.PathReactionRegistry;
import com.reverendinsanity.core.combat.custom.PathStackingRule;
import com.reverendinsanity.core.combat.killermove.MoveEffectRegistry;
import com.reverendinsanity.core.cultivation.*;
import com.reverendinsanity.core.deduction.DeductionManager;
import com.reverendinsanity.core.dream.DreamExplorationManager;
import com.reverendinsanity.core.event.WorldEventManager;
import com.reverendinsanity.core.faction.FactionReputation;
import com.reverendinsanity.core.gu.GuInstance;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.core.gu.GuType;
import com.reverendinsanity.core.gu.RefinementRecipe;
import com.reverendinsanity.core.heavenwill.HeavenWillManager;
import com.reverendinsanity.core.oath.PoisonOathManager;
import com.reverendinsanity.core.path.DaoPath;
import com.reverendinsanity.core.transformation.TransformationManager;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

// Diagnostic Command: Tests data and status of all mod systems.
public class DiagnosticCommand {

    private static final ChatFormatting HEAD = ChatFormatting.GOLD;
    private static final ChatFormatting OK = ChatFormatting.GREEN;
    private static final ChatFormatting WARN = ChatFormatting.YELLOW;
    private static final ChatFormatting ERR = ChatFormatting.RED;
    private static final ChatFormatting INFO = ChatFormatting.GRAY;

    public static int diagnoseAll(ServerPlayer player) {
        send(player, "========== [Reverend Insanity] Full System Diagnosis ==========", HEAD);
        send(player, "");
        diagnoseRegistry(player);
        diagnoseAperture(player);
        diagnoseCombat(player);
        diagnoseHeavenWill(player);
        diagnoseLifespan(player);
        diagnoseClone(player);
        diagnoseFortune(player);
        diagnoseBloodline(player);
        diagnoseOath(player);
        diagnoseSeclusion(player);
        diagnoseDaoMarks(player);
        diagnoseImmortal(player);
        diagnoseDeduction(player);
        diagnoseWorldEvent(player);
        diagnoseDream(player);
        diagnoseTransformation(player);
        diagnoseFaction(player);
        diagnoseDamage(player);
        diagnoseDaoEngine(player);
        send(player, "");
        send(player, "========== Diagnosis Complete ==========", HEAD);
        return 1;
    }

    public static int diagnoseModule(ServerPlayer player, String module) {
        send(player, "===== [Reverend Insanity] Module Diagnosis: " + module + " =====", HEAD);
        return switch (module.toLowerCase()) {
            case "registry" -> { diagnoseRegistry(player); yield 1; }
            case "aperture" -> { diagnoseAperture(player); yield 1; }
            case "combat" -> { diagnoseCombat(player); yield 1; }
            case "heavenwill" -> { diagnoseHeavenWill(player); yield 1; }
            case "lifespan" -> { diagnoseLifespan(player); yield 1; }
            case "clone" -> { diagnoseClone(player); yield 1; }
            case "fortune" -> { diagnoseFortune(player); yield 1; }
            case "bloodline" -> { diagnoseBloodline(player); yield 1; }
            case "oath" -> { diagnoseOath(player); yield 1; }
            case "seclusion" -> { diagnoseSeclusion(player); yield 1; }
            case "daomarks" -> { diagnoseDaoMarks(player); yield 1; }
            case "immortal" -> { diagnoseImmortal(player); yield 1; }
            case "deduction" -> { diagnoseDeduction(player); yield 1; }
            case "worldevent" -> { diagnoseWorldEvent(player); yield 1; }
            case "dream" -> { diagnoseDream(player); yield 1; }
            case "transformation" -> { diagnoseTransformation(player); yield 1; }
            case "faction" -> { diagnoseFaction(player); yield 1; }
            case "damage" -> { diagnoseDamage(player); yield 1; }
            case "daoengine" -> { diagnoseDaoEngine(player); yield 1; }
            default -> {
                send(player, "Unknown Module: " + module, ERR);
                send(player, "Available Modules: registry, aperture, combat, heavenwill, lifespan, clone, fortune, bloodline, oath, seclusion, daomarks, immortal, deduction, worldevent, dream, transformation, faction, damage, daoengine", INFO);
                yield 0;
            }
        };
    }

    private static void diagnoseRegistry(ServerPlayer player) {
        send(player, "[Registry]", HEAD);
        int guCount = GuRegistry.getAll().size();
        int moveCount = KillerMoveRegistry.getAll().size();
        int abilityCount = GuAbilityRegistry.getAll().size();
        int recipeCount = RefinementRecipe.getAllRecipes().size();
        int effectCount = 0;
        for (KillerMove m : KillerMoveRegistry.getAll()) {
            if (MoveEffectRegistry.hasEffect(m.id())) effectCount++;
        }
        int pathEffectCount = PathEffectComponent.getAll().size();

        status(player, "Gu Insect Types", guCount, guCount >= 148 ? OK : WARN, "(Expected>=148)");
        status(player, "Killer Move Types", moveCount, moveCount >= 61 ? OK : WARN, "(Expected>=61)");
        status(player, "Ability Types", abilityCount, abilityCount >= 146 ? OK : WARN, "(Expected>=146)");
        status(player, "Gu Refining Recipes", recipeCount, recipeCount >= 52 ? OK : WARN, "(Expected>=52)");
        status(player, "Killer Move Exclusive Effects", effectCount, effectCount > 0 ? OK : WARN, "/" + moveCount);
        status(player, "Path Effect Mapping", pathEffectCount, pathEffectCount >= 48 ? OK : WARN, "/48");

        Map<Integer, Integer> rankDist = new HashMap<>();
        Map<DaoPath, Integer> pathDist = new HashMap<>();
        for (GuType type : GuRegistry.getAll()) {
            rankDist.merge(type.rank(), 1, Integer::sum);
            pathDist.merge(type.path(), 1, Integer::sum);
        }
        StringBuilder rankStr = new StringBuilder("Gu Insect Rank Distribution: ");
        rankDist.entrySet().stream().sorted(Map.Entry.comparingByKey())
            .forEach(e -> rankStr.append(e.getKey()).append("Rank:").append(e.getValue()).append(" "));
        send(player, rankStr.toString(), INFO);

        int coveredPaths = 0;
        for (DaoPath p : DaoPath.values()) {
            if (pathDist.getOrDefault(p, 0) > 0) coveredPaths++;
        }
        status(player, "Path Coverage", coveredPaths, coveredPaths >= 48 ? OK : WARN, "/48");
    }

    private static void diagnoseAperture(ServerPlayer player) {
        send(player, "[Aperture System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        Aperture ap = data.getAperture();

        if (!ap.isOpened()) {
            send(player, "Status: Aperture Not Opened", WARN);
            send(player, "Verification: After using Hope Gu / command to open aperture, diagnose again.", INFO);
            return;
        }

        status(player, "Cultivation Rank", ap.getRank().getLevel() + "Rank·" + ap.getSubRank().getDisplayName(), OK, "");
        status(player, "Innate Talent", ap.getAptitude().getDisplayName(), OK, "");
        status(player, "Primeval Essence", String.format("%.0f/%.0f", ap.getCurrentEssence(), ap.getMaxEssence()),
            ap.getCurrentEssence() > 0 ? OK : WARN, "");
        status(player, "Thoughts", String.format("%.0f/%.0f", ap.getThoughts(), ap.getMaxThoughts()),
            ap.getThoughts() > 0 ? OK : WARN, "");

        List<GuInstance> guList = ap.getStoredGu();
        int alive = 0, dead = 0, damaged = 0, refined = 0;
        for (GuInstance gu : guList) {
            if (gu.isAlive()) alive++; else dead++;
            if (gu.isDamaged()) damaged++;
            if (gu.isRefined()) refined++;
        }
        status(player, "Total Gu Insects", guList.size(), guList.size() > 0 ? OK : WARN, "");
        status(player, "Alive / Dead", alive + "/" + dead, dead == 0 ? OK : WARN, "");
        status(player, "Injured", damaged, damaged == 0 ? OK : WARN, "");
        status(player, "Refined", refined, OK, "/" + guList.size());

        if (ap.getPrimaryPath() != null) {
            status(player, "Primary Path", ap.getPrimaryPath().getDisplayName(), OK, "");
        }
    }

    private static void diagnoseCombat(ServerPlayer player) {
        send(player, "[Combat System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        Aperture ap = data.getAperture();
        CombatState cs = data.getCombatState();
        GuBuffManager bm = data.getBuffManager();

        if (!ap.isOpened()) {
            send(player, "Aperture not opened, skipping combat diagnosis.", INFO);
            return;
        }

        int availableAbilities = 0;
        for (GuInstance gu : ap.getStoredGu()) {
            if (!gu.isActive()) continue;
            GuAbility ability = GuAbilityRegistry.get(gu.getTypeId());
            if (ability != null) availableAbilities++;
        }
        status(player, "Available Skills", availableAbilities, availableAbilities > 0 ? OK : WARN, "");

        List<ResourceLocation> moves = cs.getEquippedMoves();
        status(player, "Equipped Killer Moves", moves.size(), OK, "/2");
        for (ResourceLocation moveId : moves) {
            KillerMove move = KillerMoveRegistry.get(moveId);
            if (move != null) {
                boolean onCd = cs.isMoveCooldown(moveId);
                send(player, "  " + move.displayName() + " [" + move.primaryPath().getDisplayName() + "] " +
                    (onCd ? "On Cooldown" : "Ready"), onCd ? WARN : OK);
            }
        }

        int buffCount = bm.getActiveBuffs().size();
        status(player, "Active Buffs", buffCount, OK, "");

        boolean sealed = SealManager.isSealed(player);
        status(player, "Sealed Status", sealed ? "Sealed" : "Normal", sealed ? ERR : OK, "");
    }

    private static void diagnoseHeavenWill(ServerPlayer player) {
        send(player, "[Heaven's Will System]", HEAD);
        float attention = HeavenWillManager.getAttention(player);
        ChatFormatting color = attention < 25 ? OK : attention < 50 ? WARN : attention < 75 ? ChatFormatting.GOLD : ERR;
        status(player, "Heaven's Will Attention Level", String.format("%.1f", attention), color, "/100");

        String level;
        if (attention < 25) level = "Safe - Heaven's Will Not Watching";
        else if (attention < 50) level = "Caution - Primeval Essence Consumption Increased";
        else if (attention < 75) level = "Warning - Possible Lightning Strike";
        else if (attention < 90) level = "Danger - Attributes Suppressed";
        else level = "Critical - Imminent Heavenly Punishment";
        send(player, "  Threat Level: " + level, color);
    }

    private static void diagnoseLifespan(ServerPlayer player) {
        send(player, "[Lifespan System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        int lifespan = data.getLifespan();
        int rankLevel = data.getAperture().isOpened() ? data.getAperture().getRank().getLevel() : 0;
        int maxLifespan = LifespanManager.getMaxLifespan(rankLevel);

        if (lifespan == 0 && !data.getAperture().isOpened()) {
            send(player, "Aperture not opened, Lifespan System not activated.", INFO);
            return;
        }

        float pct = maxLifespan > 0 ? (float) lifespan / maxLifespan * 100 : 0;
        ChatFormatting color = pct > 50 ? OK : pct > 25 ? WARN : pct > 10 ? ChatFormatting.GOLD : ERR;
        status(player, "Lifespan", lifespan + "/" + maxLifespan, color, String.format("(%.0f%%)", pct));

        if (pct <= 10) send(player, "  [!] Lifespan is about to run out!", ERR);
    }

    private static void diagnoseClone(ServerPlayer player) {
        send(player, "[Avatar System]", HEAD);
        boolean active = CloneManager.isActive(player);
        status(player, "Avatar Status", active ? "Activated" : "Inactive", active ? OK : INFO, "");
        if (active) {
            send(player, "  Effect: 20% Dodge + 40% Bonus Damage + Speed / Attack Increase", OK);
        }
    }

    private static void diagnoseFortune(ServerPlayer player) {
        send(player, "[Fortune System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        float luck = data.getLuck();

        ChatFormatting color;
        String desc;
        if (luck > 1.2f) { color = OK; desc = "Extreme Fortune"; }
        else if (luck > 1.0f) { color = OK; desc = "Good Luck"; }
        else if (luck >= 1.0f) { color = INFO; desc = "Normal"; }
        else if (luck >= 0.7f) { color = WARN; desc = "Poor Luck"; }
        else if (luck >= 0.5f) { color = ERR; desc = "Plagued by Misfortune"; }
        else { color = ERR; desc = "Omen of Great Calamity"; }

        status(player, "Fortune Value", String.format("%.2f", luck), color, "(" + desc + ")");
        status(player, "Gu Refining Bonus", String.format("%.0f%%", FortunePlunderManager.getRefinementBonus(player) * 100), OK, "");
        status(player, "Drop Bonus", String.format("%.0f%%", FortunePlunderManager.getLootDropBonus(player) * 100), OK, "");
    }

    private static void diagnoseBloodline(ServerPlayer player) {
        send(player, "[Bloodline System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        int bloodlineId = data.getBloodlineId();

        if (bloodlineId == 0) {
            send(player, "Undeveloped Bloodline (Auto-assigned when aperture opens)", INFO);
            return;
        }

        BloodlineManager.Bloodline bl = BloodlineManager.getBloodline(player);
        if (bl != null) {
            status(player, "Bloodline", bl.displayName, OK, "(ID:" + bloodlineId + ")");
            DaoPath affinity = BloodlineManager.getAffinityPath(bl);
            if (affinity != null) {
                status(player, "Affinity Path", affinity.getDisplayName(), OK, "");
            }
        } else {
            status(player, "Bloodline ID", bloodlineId, WARN, "(Unidentified)");
        }
    }

    private static void diagnoseOath(ServerPlayer player) {
        send(player, "[Poison Oath System]", HEAD);
        boolean active = PoisonOathManager.hasActiveOath(player);
        status(player, "Poison Oath Status", active ? "Active" : "None", active ? WARN : INFO, "");
    }

    private static void diagnoseSeclusion(ServerPlayer player) {
        send(player, "[Closed-Door Cultivation System]", HEAD);
        boolean inSeclusion = SeclusionManager.isInSeclusion(player);
        status(player, "Closed-Door Cultivation State", inSeclusion ? "In Seclusion" : "Not in Seclusion", inSeclusion ? OK : INFO, "");
        if (inSeclusion) {
            send(player, "  Effect: Accelerated Primeval Essence Recovery + Periodic Dao Mark Gain", OK);
        }
    }

    private static void diagnoseDaoMarks(ServerPlayer player) {
        send(player, "[Dao Mark System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        int total = data.getTotalDaoMarks();
        status(player, "Total Dao Marks", total, total > 0 ? OK : INFO, "");

        Map<DaoPath, Integer> marks = data.getAllDaoMarks();
        List<Map.Entry<DaoPath, Integer>> sorted = new ArrayList<>(marks.entrySet());
        sorted.sort(Comparator.<Map.Entry<DaoPath, Integer>>comparingInt(Map.Entry::getValue).reversed());

        int shown = 0;
        for (Map.Entry<DaoPath, Integer> e : sorted) {
            if (e.getValue() <= 0) continue;
            send(player, "  " + e.getKey().getDisplayName() + "Path: " + e.getValue() +
                " (Bonus:" + String.format("%.0f%%", data.getDaoMarkBonus(e.getKey()) * 100) + ")", INFO);
            if (++shown >= 8) {
                send(player, "  ... Omitted " + (sorted.size() - 8) + " Entries", INFO);
                break;
            }
        }
    }

    private static void diagnoseImmortal(ServerPlayer player) {
        send(player, "[Immortal Aperture System]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        ImmortalAperture iap = data.getImmortalAperture();

        if (!iap.isFormed()) {
            send(player, "Immortal Aperture not established (Requires Rank 4 or above)", INFO);
            return;
        }

        status(player, "Grade", iap.getGrade().getDisplayName(), OK, "");
        status(player, "Integrity", String.format("%.1f%%", iap.getIntegrity()), iap.getIntegrity() > 50 ? OK : WARN, "");
        status(player, "Heaven Qi", String.format("%.0f", iap.getStoredHeavenQi()), OK, "/" + (int) iap.getMaxQi());
        status(player, "Earth Qi", String.format("%.0f", iap.getStoredEarthQi()), OK, "/" + (int) iap.getMaxQi());
        status(player, "Immortal Essence Stone", iap.getImmortalEssenceStones(), OK, "");
        status(player, "Development Level", iap.getDevelopmentLevel(), OK, "");
        status(player, "Flaw Count", iap.getBreachCount(), iap.getBreachCount() == 0 ? OK : WARN, "");

        boolean inCalamity = CalamityManager.isInCalamity(player.getUUID());
        if (inCalamity) {
            Calamity cal = CalamityManager.getActiveCalamity(player.getUUID());
            if (cal != null) {
                status(player, "Tribulations", cal.getType().getDisplayName(), ERR,
                    String.format("Progress:%.0f%%", cal.getProgress() * 100));
            }
        } else {
            status(player, "Calamities", "None", OK, "");
        }
    }

    private static void diagnoseDeduction(ServerPlayer player) {
        send(player, "[Deduction System]", HEAD);
        UUID uuid = player.getUUID();
        boolean deducting = DeductionManager.isDeducting(uuid);
        status(player, "Deduction Status", deducting ? "Deducing" : "Idle", deducting ? OK : INFO, "");

        if (deducting) {
            var session = DeductionManager.getSession(uuid);
            if (session != null) {
                send(player, "  Progress: " + String.format("%.0f%%", session.getProgress() * 100) +
                    "Success Rate: " + String.format("%.0f%%", session.getSuccessRate() * 100), OK);
            }
        }

        var improved = DeductionManager.getAllImprovedMoves(uuid);
        status(player, "Deduced Killer Moves", improved != null ? improved.size() : 0, OK, "");
    }

    private static void diagnoseWorldEvent(ServerPlayer player) {
        send(player, "[Heavenly Omen]", HEAD);
        var activeEvent = WorldEventManager.getActiveEvent(player.level());
        if (activeEvent != null) {
            status(player, "Active Omen", activeEvent.getType().name(), OK, "");
        } else {
            status(player, "Active Omen", "None", INFO, "");
        }
    }

    private static void diagnoseDream(ServerPlayer player) {
        send(player, "[Dream Realm System]", HEAD);
        boolean dreaming = DreamExplorationManager.isDreaming(player);
        status(player, "Dream Realm Status", dreaming ? "Entering Dream Realm" : "Awake", dreaming ? OK : INFO, "");
    }

    private static void diagnoseTransformation(ServerPlayer player) {
        send(player, "[Transformation System]", HEAD);
        boolean transformed = TransformationManager.isTransformed(player);
        status(player, "Transformation Status", transformed ? "Transformed" : "Not Transformed", transformed ? OK : INFO, "");
        if (transformed) {
            var form = TransformationManager.getCurrentForm(player.getUUID());
            if (form != null) {
                send(player, "  Current Form: " + form.name(), OK);
            }
        }
    }

    private static void diagnoseFaction(ServerPlayer player) {
        send(player, "[Faction Reputation]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        FactionReputation rep = data.getFactionReputation();
        send(player, "  Faction system has been integrated into Gu Master / merchant entities.", INFO);
    }

    private static void diagnoseDamage(ServerPlayer player) {
        send(player, "[Gu Insect Damage]", HEAD);
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        Aperture ap = data.getAperture();
        if (!ap.isOpened()) {
            send(player, "Aperture not opened", INFO);
            return;
        }

        int total = ap.getStoredGu().size();
        int damaged = 0;
        for (GuInstance gu : ap.getStoredGu()) {
            if (gu.isDamaged()) damaged++;
        }
        status(player, "Gu Insect Damage", damaged + "/" + total, damaged == 0 ? OK : WARN, "");
        if (damaged > 0) {
            for (GuInstance gu : ap.getStoredGu()) {
                if (gu.isDamaged()) {
                    GuType type = GuRegistry.get(gu.getTypeId());
                    send(player, "  [Damaged] " + (type != null ? type.displayName() : gu.getTypeId().toString()) +
                        " (Effect reduced to 50%)", WARN);
                }
            }
        }
    }

    private static void diagnoseDaoEngine(ServerPlayer player) {
        send(player, "[Path Combination Engine]", HEAD);
        int pathEffects = PathEffectComponent.getAll().size();
        status(player, "Path Effect Mapping", pathEffects, pathEffects >= 48 ? OK : WARN, "/48");
        send(player, "  Path Reaction System: PathReactionRegistry loaded.", OK);
        send(player, "  Path Stacking System: PathStackingRule loaded.", OK);

        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        Aperture ap = data.getAperture();
        if (ap.isOpened() && !ap.getStoredGu().isEmpty()) {
            Map<DaoPath, Integer> pathCounts = new EnumMap<>(DaoPath.class);
            for (GuInstance gu : ap.getStoredGu()) {
                if (!gu.isActive()) continue;
                GuType type = GuRegistry.get(gu.getTypeId());
                if (type != null) {
                    pathCounts.merge(type.path(), 1, Integer::sum);
                }
            }

            List<PathStackingRule.StackThreshold> stacks = PathStackingRule.check(pathCounts);
            if (!stacks.isEmpty()) {
                send(player, "  Current Gu insects can trigger stacking effects:", OK);
                for (var st : stacks) {
                    send(player, "    x" + st.requiredCount() +
                        " -> " + st.effect().name() + " (" + st.description() + ")", OK);
                }
            }

            List<DaoPath> paths = new ArrayList<>(pathCounts.keySet());
            if (paths.size() >= 2) {
                var reactions = PathReactionRegistry.findReactions(paths);
                if (!reactions.isEmpty()) {
                    send(player, "  Current Gu insects can trigger Path reactions:", OK);
                    for (var r : reactions) {
                        send(player, "    " + r.name() + " -> " + r.type().name(), OK);
                    }
                }
            }
        }
    }

    private static void send(ServerPlayer player, String msg, ChatFormatting... formats) {
        MutableComponent comp = Component.literal(msg);
        for (ChatFormatting f : formats) comp = comp.withStyle(f);
        player.sendSystemMessage(comp);
    }

    private static void status(ServerPlayer player, String label, Object value, ChatFormatting color, String suffix) {
        MutableComponent comp = Component.literal("  " + label + ": ")
            .withStyle(INFO)
            .append(Component.literal(String.valueOf(value)).withStyle(color));
        if (suffix != null && !suffix.isEmpty()) {
            comp = comp.append(Component.literal(" " + suffix).withStyle(INFO));
        }
        player.sendSystemMessage(comp);
    }
}
