package com.reverendinsanity.network;

import com.reverendinsanity.core.combat.CombatState;
import com.reverendinsanity.core.combat.KillerMove;
import com.reverendinsanity.core.combat.KillerMoveRegistry;
import com.reverendinsanity.core.combat.ability.GuAbility;
import com.reverendinsanity.core.combat.ability.GuAbilityRegistry;
import com.reverendinsanity.core.combat.killermove.KillerMoveExecutor;
import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.deduction.DeductionManager;
import com.reverendinsanity.core.deduction.MoveBlueprint;
import com.reverendinsanity.core.deduction.DeductionSession;
import com.reverendinsanity.core.aperture.ImmortalAperture;
import com.reverendinsanity.core.aperture.ApertureResourceManager;
import com.reverendinsanity.core.aperture.calamity.Calamity;
import com.reverendinsanity.core.aperture.calamity.CalamityManager;
import com.reverendinsanity.core.gu.GuInstance;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.core.gu.GuType;
import com.reverendinsanity.core.path.DaoPath;
import com.reverendinsanity.registry.ModAttachments;
import com.reverendinsanity.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.reverendinsanity.util.AdvancementHelper;
import com.reverendinsanity.client.gui.RadialMenuAction;
import com.reverendinsanity.core.cultivation.SeclusionManager;
import com.reverendinsanity.core.combat.ToggleMoveManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 服务端处理客户端请求（技能催动、杀招施展、空窍界面）
public class ServerPayloadHandler {

    public static void handleActivateAbility(final ActivateAbilityPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();
            CombatState combatState = data.getCombatState();

            if (!aperture.isOpened()) {
                player.displayClientMessage(Component.literal("Aperture not opened. Cannot activate Gu insects"), true);
                return;
            }

            List<GuAbility> availableAbilities = new ArrayList<>();
            for (GuInstance gu : aperture.getStoredGu()) {
                if (!gu.isActive()) continue;
                GuAbility ability = GuAbilityRegistry.get(gu.getTypeId());
                if (ability != null && !availableAbilities.contains(ability)) {
                    availableAbilities.add(ability);
                }
            }

            int slotIndex = payload.slotIndex();
            if (slotIndex < 0 || slotIndex >= availableAbilities.size()) {
                player.displayClientMessage(Component.literal("No Gu insect in this skill slot"), true);
                return;
            }

            GuAbility ability = availableAbilities.get(slotIndex);
            if (ability.execute(player, aperture, combatState)) {
                GuType guType = GuRegistry.get(ability.getGuTypeId());
                String name = guType != null ? guType.displayName() : "Gu";
                player.displayClientMessage(Component.literal(name + " Activation successful"), true);
            } else {
                if (combatState.isAbilityOnCooldown(ability.getGuTypeId())) {
                    player.displayClientMessage(Component.literal("Skill on cooldown"), true);
                } else {
                    player.displayClientMessage(Component.literal("Insufficient Primeval Essence"), true);
                }
            }
        });
    }

    public static void handleUseKillerMove(final UseKillerMovePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();
            CombatState combatState = data.getCombatState();

            if (!aperture.isOpened()) {
                player.displayClientMessage(Component.literal("Aperture not opened"), true);
                return;
            }

            List<ResourceLocation> equipped = combatState.getEquippedMoves();
            int slot = payload.slotIndex();
            if (slot < 0 || slot >= equipped.size()) {
                player.displayClientMessage(Component.literal("Killer Move slot is empty"), true);
                return;
            }

            KillerMove move = KillerMoveRegistry.get(equipped.get(slot));
            if (move == null) {
                player.displayClientMessage(Component.literal("Killer Move does not exist"), true);
                return;
            }

            if (KillerMoveExecutor.execute(player, aperture, combatState, move)) {
                player.displayClientMessage(Component.literal("Killer Move「" + move.displayName() + "」successfully executed!"), false);
                AdvancementHelper.grant(player, "first_killer_move");
            } else {
                if (!move.canUse(aperture.getRank())) {
                    player.displayClientMessage(Component.literal("Insufficient cultivation rank, Cannot execute this Killer Move"), true);
                } else if (aperture.getCurrentEssence() < move.essenceCost()) {
                    player.displayClientMessage(Component.literal("Insufficient Primeval Essence"), true);
                } else if (aperture.getThoughts() < move.thoughtsCost()) {
                    player.displayClientMessage(Component.literal("Insufficient Thoughts, Cannot drive the Killer Move"), true);
                } else if (combatState.isMoveCooldown(move.id())) {
                    player.displayClientMessage(Component.literal("Killer Move on cooldown"), true);
                } else {
                    player.displayClientMessage(Component.literal("Missing required Gu insect"), true);
                }
            }
        });
    }

    public static void handleOpenAperture(final OpenAperturePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();
            CombatState combatState = data.getCombatState();

            List<SyncApertureContentsPayload.GuInfo> guInfoList = new ArrayList<>();
            for (GuInstance gu : aperture.getStoredGu()) {
                GuType type = GuRegistry.get(gu.getTypeId());
                if (type != null) {
                    guInfoList.add(new SyncApertureContentsPayload.GuInfo(
                        gu.getTypeId().toString(),
                        type.displayName(),
                        type.rank(),
                        type.path().getDisplayName(),
                        type.category().getDisplayName(),
                        gu.getHunger(),
                        gu.isRefined(),
                        gu.isAlive(),
                        gu.getProficiency()
                    ));
                }
            }

            List<SyncApertureContentsPayload.MoveInfo> equippedList = new ArrayList<>();
            for (ResourceLocation moveId : combatState.getEquippedMoves()) {
                KillerMove move = KillerMoveRegistry.get(moveId);
                if (move != null) {
                    equippedList.add(new SyncApertureContentsPayload.MoveInfo(
                        moveId.toString(), move.displayName(),
                        move.moveType().getDisplayName(), move.minRank(),
                        move.essenceCost(), move.thoughtsCost(),
                        buildMoveDescription(move)
                    ));
                }
            }

            List<SyncApertureContentsPayload.MoveInfo> availableList = new ArrayList<>();
            for (KillerMove move : KillerMoveRegistry.getAll()) {
                availableList.add(new SyncApertureContentsPayload.MoveInfo(
                    move.id().toString(), move.displayName(),
                    move.moveType().getDisplayName(), move.minRank(),
                    move.essenceCost(), move.thoughtsCost(),
                    buildMoveDescription(move)
                ));
            }

            PacketDistributor.sendToPlayer(player,
                new SyncApertureContentsPayload(guInfoList, equippedList, availableList));
        });
    }

    public static void handleEquipMove(final EquipMovePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            CombatState combatState = data.getCombatState();

            ResourceLocation moveId = ResourceLocation.parse(payload.moveId());
            if (payload.equip()) {
                combatState.equipMove(moveId);
                player.displayClientMessage(Component.literal("Killer Move equipped"), true);
            } else {
                combatState.unequipMove(moveId);
                player.displayClientMessage(Component.literal("Killer Move unequipped"), true);
            }

            handleOpenAperture(new OpenAperturePayload(), context);
        });
    }

    public static void handleFeedGu(final FeedGuPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();

            if (!aperture.isOpened()) {
                player.displayClientMessage(Component.literal("Aperture not opened"), true);
                return;
            }

            List<GuInstance> guList = aperture.getStoredGu();
            int idx = payload.guIndex();
            if (idx < 0 || idx >= guList.size()) {
                player.displayClientMessage(Component.literal("Invalid Gu insect index"), true);
                return;
            }

            GuInstance gu = guList.get(idx);
            if (!gu.isAlive()) {
                player.displayClientMessage(Component.literal("Gu insect is dead, Cannot feed"), true);
                return;
            }

            boolean hasPrimevalStone = false;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(ModItems.PRIMEVAL_STONE.get())) {
                    hasPrimevalStone = true;
                    stack.shrink(1);
                    break;
                }
            }

            if (!hasPrimevalStone) {
                player.displayClientMessage(Component.literal("Insufficient Essence Stones, Cannot feed"), true);
                return;
            }

            if (gu.feed()) {
                player.displayClientMessage(Component.literal("Feeding successful! Hunger level: " + (int)gu.getHunger() + "%"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.GENERIC_EAT, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.2f);
                AdvancementHelper.grant(player, "feed_gu");
            } else {
                player.displayClientMessage(Component.literal("Gu insect is full, No need to feed"), true);
            }

            handleOpenAperture(new OpenAperturePayload(), context);
        });
    }

    public static void handleDiscardGu(final DiscardGuPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();

            if (!aperture.isOpened()) return;

            int idx = payload.guIndex();
            List<GuInstance> guList = aperture.getStoredGu();
            if (idx < 0 || idx >= guList.size()) return;

            GuType type = GuRegistry.get(guList.get(idx).getTypeId());
            GuInstance removed = aperture.removeGuAt(idx);
            if (removed != null) {
                String name = type != null ? type.displayName() : "Gu";
                player.displayClientMessage(Component.literal(name + " Discarded from the aperture"), true);
            }

            handleOpenAperture(new OpenAperturePayload(), context);
        });
    }

    public static void handleOpenCodex(final OpenCodexPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Set<ResourceLocation> discovered = data.getDiscoveredGu();

            List<SyncCodexPayload.CodexEntry> entries = new ArrayList<>();
            for (GuType type : GuRegistry.getAll()) {
                boolean isDiscovered = discovered.contains(type.id());
                entries.add(new SyncCodexPayload.CodexEntry(
                    type.id().toString(),
                    type.displayName(),
                    type.rank(),
                    type.path().getDisplayName(),
                    type.category().getDisplayName(),
                    isDiscovered
                ));
            }

            PacketDistributor.sendToPlayer(player,
                new SyncCodexPayload(entries, discovered.size()));
        });
    }

    public static void handleOpenDeductionScreen(final OpenDeductionScreenPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();

            List<SyncDeductionScreenPayload.DeductionGuEntry> guEntries = new ArrayList<>();
            for (GuInstance gu : aperture.getStoredGu()) {
                if (!gu.isActive() || !gu.isAlive()) continue;
                GuType type = GuRegistry.get(gu.getTypeId());
                if (type != null) {
                    guEntries.add(new SyncDeductionScreenPayload.DeductionGuEntry(
                        gu.getTypeId().toString(), type.displayName(), type.rank(), type.path().getDisplayName()
                    ));
                }
            }

            boolean deducting = DeductionManager.isDeducting(player.getUUID());
            float progress = 0;
            float successRate = 0;
            if (deducting) {
                DeductionSession session = DeductionManager.getSession(player.getUUID());
                if (session != null) {
                    progress = session.getProgress();
                    successRate = session.getSuccessRate();
                }
            }

            PacketDistributor.sendToPlayer(player,
                new SyncDeductionScreenPayload(guEntries, deducting, progress, successRate));
        });
    }

    public static void handleStartDeduction(final StartDeductionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            ResourceLocation coreGuId = ResourceLocation.parse(payload.coreGuId());
            List<ResourceLocation> supportIds = new ArrayList<>();
            for (String s : payload.supportGuIds()) {
                if (s != null && !s.isEmpty()) {
                    supportIds.add(ResourceLocation.parse(s));
                }
            }

            DaoPath targetPath = null;
            try {
                targetPath = DaoPath.valueOf(payload.targetPath());
            } catch (Exception e) {
                player.displayClientMessage(Component.literal("Invalid Path"), true);
                return;
            }

            MoveBlueprint blueprint = new MoveBlueprint(coreGuId, supportIds, targetPath);
            if (DeductionManager.startDeduction(player, blueprint)) {
                player.displayClientMessage(Component.literal("Starting Killer Move deduction...").withStyle(net.minecraft.ChatFormatting.GOLD), false);
            }
        });
    }

    public static void handleCancelDeduction(final CancelDeductionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            DeductionManager.cancelDeduction(player.getUUID());
            player.displayClientMessage(Component.literal("Deduction canceled"), true);
        });
    }

    public static void handleEnterAperture(final EnterAperturePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            ImmortalAperture immortalAp = data.getImmortalAperture();

            if (!immortalAp.isFormed()) {
                if (!data.getAperture().getRank().isImmortal()) {
                    player.displayClientMessage(Component.literal("Insufficient cultivation rank, Cannot open the Immortal Aperture"), true);
                    return;
                }
                immortalAp.form(data.getAperture(), data);
                player.displayClientMessage(
                    Component.literal("Immortal Aperture successfully opened! " + immortalAp.getGrade().getDisplayName() + "！")
                        .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD), false);
                AdvancementHelper.grant(player, "form_immortal_aperture");
            }

            if (player.level().dimension().equals(com.reverendinsanity.world.dimension.ModDimensions.APERTURE_DIM)) {
                player.displayClientMessage(Component.literal("You are already inside the Immortal Aperture"), true);
                return;
            }

            com.reverendinsanity.world.dimension.ApertureDimensionManager.enterAperture(player);
            player.displayClientMessage(
                Component.literal("Entering Immortal Aperture · " + immortalAp.getGrade().getDisplayName())
                    .withStyle(net.minecraft.ChatFormatting.AQUA, net.minecraft.ChatFormatting.BOLD), false);
        });
    }

    public static void handleExitAperture(final ExitAperturePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            if (!player.level().dimension().equals(com.reverendinsanity.world.dimension.ModDimensions.APERTURE_DIM)) {
                player.displayClientMessage(Component.literal("You are not inside the Immortal Aperture"), true);
                return;
            }

            com.reverendinsanity.world.dimension.ApertureDimensionManager.exitAperture(player);
            player.displayClientMessage(
                Component.literal("Leaving the Immortal Aperture, Returning to your original location").withStyle(net.minecraft.ChatFormatting.GREEN), false);
        });
    }

    public static void handleResistCalamity(final ResistCalamityPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            ImmortalAperture immortalAp = data.getImmortalAperture();

            if (!CalamityManager.isInCalamity(player.getUUID())) {
                player.displayClientMessage(Component.literal("No active tribulations"), true);
                return;
            }

            float amount = Math.min(payload.amount(), 50.0f);
            if (immortalAp.consumeQi(amount)) {
                CalamityManager.resistCalamity(player.getUUID(), amount);
                player.displayClientMessage(
                    Component.literal("Consume Heaven and Earth Qi to resist tribulations and reduce damage " + String.format("%.1f", amount))
                        .withStyle(net.minecraft.ChatFormatting.GREEN), true);
            } else {
                player.displayClientMessage(Component.literal("Insufficient Heaven and Earth Qi"), true);
            }
        });
    }

    public static void handleOpenImmortalAperture(final OpenImmortalAperturePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            ImmortalAperture immortalAp = data.getImmortalAperture();

            if (!immortalAp.isFormed() && data.getAperture().getRank().isImmortal()) {
                immortalAp.form(data.getAperture(), data);
                player.displayClientMessage(
                    Component.literal("Immortal Aperture successfully opened!" + immortalAp.getGrade().getDisplayName() + "！")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
                AdvancementHelper.grant(player, "form_immortal_aperture");
            }

            syncImmortalApertureToClient(player, data);
        });
    }

    public static void handleExtractResource(final ExtractResourcePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            ImmortalAperture immortalAp = data.getImmortalAperture();
            if (!immortalAp.isFormed()) return;

            ApertureResourceManager.ResourceType[] types = ApertureResourceManager.ResourceType.values();
            int ord = payload.resourceOrdinal();
            if (ord < 0 || ord >= types.length) return;

            ApertureResourceManager.ResourceType resType = types[ord];
            int amount = Math.min(payload.amount(), 64);
            ApertureResourceManager resMgr = immortalAp.getResourceManager();

            int extracted = 0;
            for (int i = 0; i < amount; i++) {
                if (resMgr.consumeResource(resType, 1)) {
                    extracted++;
                } else break;
            }

            if (extracted > 0) {
                ItemStack stack = getItemForResource(resType, extracted);
                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                    player.displayClientMessage(
                        Component.literal("Extracted " + extracted + " " + resType.getDisplayName())
                            .withStyle(ChatFormatting.GREEN), true);
                }
            } else {
                player.displayClientMessage(Component.literal("Insufficient resources"), true);
            }

            syncImmortalApertureToClient(player, data);
        });
    }

    public static void handleRepairAperture(final RepairAperturePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            ImmortalAperture immortalAp = data.getImmortalAperture();
            if (!immortalAp.isFormed()) return;

            float before = immortalAp.getIntegrity();
            immortalAp.repair(Math.min(payload.amount(), 10.0f));
            float after = immortalAp.getIntegrity();

            if (after > before) {
                player.displayClientMessage(
                    Component.literal("Immortal Aperture Repair: " + String.format("%.1f", before) + "% → " + String.format("%.1f", after) + "%")
                        .withStyle(ChatFormatting.GREEN), true);
            } else {
                player.displayClientMessage(Component.literal("Insufficient Heaven and Earth Qi. Cannot repair."), true);
            }

            syncImmortalApertureToClient(player, data);
        });
    }

    public static void handleRepairBreach(final RepairBreachPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            ImmortalAperture ap = data.getImmortalAperture();
            if (!ap.isFormed()) return;

            if (ap.getBreachCount() > 0) {
                ap.repairBreach();
                player.displayClientMessage(
                    Component.literal("[Immortal Aperture] Repaired a flaw").withStyle(ChatFormatting.GREEN), false);
            } else {
                player.displayClientMessage(
                    Component.literal("[Immortal Aperture] No flaws need repair").withStyle(ChatFormatting.GRAY), true);
            }

            syncImmortalApertureToClient(player, data);
        });
    }

    public static void syncImmortalApertureToClient(ServerPlayer player, GuMasterData data) {
        ImmortalAperture ap = data.getImmortalAperture();

        List<SyncImmortalAperturePayload.ResourceEntry> resources = new ArrayList<>();
        if (ap.isFormed()) {
            ApertureResourceManager resMgr = ap.getResourceManager();
            for (ApertureResourceManager.ResourceType type : ApertureResourceManager.ResourceType.values()) {
                resources.add(new SyncImmortalAperturePayload.ResourceEntry(
                    type.ordinal(), type.getDisplayName(), resMgr.getResource(type)));
            }
        }

        List<SyncImmortalAperturePayload.DaoMarkEntry> topMarks = new ArrayList<>();
        if (ap.isFormed()) {
            List<Map.Entry<DaoPath, Integer>> sorted = new ArrayList<>();
            for (DaoPath path : DaoPath.values()) {
                int marks = ap.getDaoMark(path);
                if (marks > 0) {
                    sorted.add(Map.entry(path, marks));
                }
            }
            sorted.sort(Comparator.<Map.Entry<DaoPath, Integer>>comparingInt(Map.Entry::getValue).reversed());
            for (int i = 0; i < Math.min(sorted.size(), 8); i++) {
                Map.Entry<DaoPath, Integer> e = sorted.get(i);
                topMarks.add(new SyncImmortalAperturePayload.DaoMarkEntry(
                    e.getKey().getDisplayName(), e.getValue()));
            }
        }

        boolean calamityActive = CalamityManager.isInCalamity(player.getUUID());
        String calamityName = "";
        float calamityProgress = 0;
        if (calamityActive) {
            Calamity cal = CalamityManager.getActiveCalamity(player.getUUID());
            if (cal != null) {
                calamityName = cal.getType().getDisplayName();
                calamityProgress = cal.getProgress();
            }
        }

        PacketDistributor.sendToPlayer(player, new SyncImmortalAperturePayload(
            ap.isFormed(),
            ap.isFormed() ? ap.getGrade().getDisplayName() : "",
            ap.getIntegrity(),
            ap.getStoredHeavenQi(),
            ap.getStoredEarthQi(),
            ap.getMaxQi(),
            ap.getImmortalEssenceStones(),
            calamityActive,
            calamityName,
            calamityProgress,
            ap.getDaysSinceLastCalamity(),
            resources,
            topMarks,
            ap.getDevelopmentLevel(),
            ap.getBreachCount(),
            ap.getTotalCalamitiesSurvived(),
            ap.isFormed() ? ap.getGrade().getTimeFlowRate() : 1
        ));
    }

    private static ItemStack getItemForResource(ApertureResourceManager.ResourceType type, int count) {
        return switch (type) {
            case PRIMEVAL_STONE -> new ItemStack(ModItems.PRIMEVAL_STONE.get(), count);
            case MOON_PETAL -> new ItemStack(ModItems.MOON_ORCHID_PETAL.get(), count);
            case BEAST_BONE -> new ItemStack(ModItems.BEAST_BONE.get(), count);
            case BITTER_WINE -> new ItemStack(ModItems.BITTER_WINE.get(), count);
            case SPIDER_SILK -> new ItemStack(ModItems.SPIDER_SILK.get(), count);
            case JADE_BEAD -> new ItemStack(ModItems.JADE_EYE.get(), count);
            case IMMORTAL_ESSENCE -> new ItemStack(ModItems.PRIMEVAL_STONE.get(), count * 5);
        };
    }

    private static String buildMoveDescription(KillerMove move) {
        StringBuilder sb = new StringBuilder();
        sb.append(move.primaryPath().getDisplayName()).append("Path | ");
        sb.append("Power:").append(String.format("%.0f", move.power()));
        sb.append(" Cooldown:").append(move.cooldownTicks() / 20).append("s");
        GuType coreType = GuRegistry.get(move.coreGu());
        if (coreType != null) {
            sb.append(" | Core:").append(coreType.displayName());
        }
        if (!move.supportGu().isEmpty()) {
            sb.append("+");
            for (int i = 0; i < move.supportGu().size(); i++) {
                GuType supType = GuRegistry.get(move.supportGu().get(i));
                if (supType != null) {
                    if (i > 0) sb.append(",");
                    sb.append(supType.displayName());
                }
            }
        }
        return sb.toString();
    }

    public static void handleDefenseAction(final DefenseActionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            switch (payload.action()) {
                case DefenseActionPayload.SHIELD -> {
                    if (com.reverendinsanity.core.combat.DefenseManager.activateShield(player)) {
                        player.displayClientMessage(Component.literal("Primeval Essence shield activated!").withStyle(ChatFormatting.AQUA), true);
                    } else {
                        player.displayClientMessage(Component.literal("Cannot activate shield (Insufficient Primeval Essence or already active)"), true);
                    }
                }
                case DefenseActionPayload.DODGE -> {
                    if (com.reverendinsanity.core.combat.DefenseManager.activateDodge(player)) {
                        player.displayClientMessage(Component.literal("Emergency dodge!").withStyle(ChatFormatting.GREEN), true);
                    } else {
                        player.displayClientMessage(Component.literal("Dodge on cooldown or insufficient Primeval Essence"), true);
                    }
                }
            }
        });
    }

    public static void handleRadialMenu(final RadialMenuPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            switch (payload.actionType()) {
                case RadialMenuPayload.TYPE_SYSTEM -> {
                    RadialMenuAction action = RadialMenuAction.fromIndex(payload.actionIndex());
                    if (action == null) return;
                    switch (action) {
                        case APERTURE -> handleOpenAperture(new OpenAperturePayload(), context);
                        case IMMORTAL_APERTURE -> handleOpenImmortalAperture(new OpenImmortalAperturePayload(), context);
                        case DEDUCTION -> handleOpenDeductionScreen(new OpenDeductionScreenPayload(), context);
                        case CODEX -> handleOpenCodex(new OpenCodexPayload(), context);
                        case SECLUSION -> {
                            if (SeclusionManager.isInSeclusion(player)) {
                                player.displayClientMessage(Component.literal("Already in seclusion. Move to interrupt."), true);
                            } else if (SeclusionManager.enterSeclusion(player)) {
                                player.displayClientMessage(Component.literal("Entering seclusion state...").withStyle(ChatFormatting.AQUA), false);
                            } else {
                                player.displayClientMessage(Component.literal("Cannot enter seclusion."), true);
                            }
                        }
                    }
                }
                case RadialMenuPayload.TYPE_ABILITY -> handleActivateAbility(new ActivateAbilityPayload(payload.actionIndex()), context);
                case RadialMenuPayload.TYPE_MOVE -> {
                    GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
                    CombatState combatState = data.getCombatState();
                    List<ResourceLocation> equipped = combatState.getEquippedMoves();
                    int slot = payload.actionIndex();
                    if (slot < 0 || slot >= equipped.size()) return;
                    KillerMove move = KillerMoveRegistry.get(equipped.get(slot));
                    if (move == null) return;
                    if (ToggleMoveManager.isToggleable(move)) {
                        ToggleMoveManager.toggleMove(player, move);
                    } else {
                        handleUseKillerMove(new UseKillerMovePayload(slot), context);
                    }
                }
            }
        });
    }
}
