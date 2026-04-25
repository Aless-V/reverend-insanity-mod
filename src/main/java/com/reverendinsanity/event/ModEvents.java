package com.reverendinsanity.event;

import com.reverendinsanity.ReverendInsanity;
import com.reverendinsanity.command.GuCommand;
import com.reverendinsanity.core.combat.CombatState;
import com.reverendinsanity.core.combat.FlashBlindManager;
import com.reverendinsanity.core.combat.RuntimeStateCleanup;
import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.gu.GuInstance;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.core.gu.GuType;
import com.reverendinsanity.core.combat.PoisonCloudManager;
import com.reverendinsanity.core.cultivation.TribulationManager;
import com.reverendinsanity.core.deduction.DeductionManager;
import com.reverendinsanity.core.dream.DreamExplorationManager;
import com.reverendinsanity.core.transformation.TransformationManager;
import com.reverendinsanity.world.dimension.ApertureTimeManager;
import com.reverendinsanity.core.aperture.calamity.CalamityManager;
import com.reverendinsanity.core.faction.Faction;
import com.reverendinsanity.core.faction.FactionReputation;
import com.reverendinsanity.network.ServerPlayerSyncDispatcher;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.ArrayList;

// 模组事件处理
@EventBusSubscriber(modid = ReverendInsanity.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        if (data.getPlayerUUID() == null) {
            data.setPlayerUUID(player.getUUID());
        }
        data.tick();
        data.getBuffManager().tick(player);
        DeductionManager.tick(player);
        if (data.getImmortalAperture().isFormed()) {
            data.getImmortalAperture().tick(player);
            CalamityManager.tick(player, data.getImmortalAperture());
        }
        ApertureTimeManager.tickTimeAcceleration(player);
        com.reverendinsanity.core.combat.ToggleMoveManager.tickPlayer(player);
        com.reverendinsanity.core.combat.IntelligenceManager.tickObservation(player);

        if (player.tickCount % 20 == 0) {
            ServerPlayerSyncDispatcher.syncHud(player, data);
            ServerPlayerSyncDispatcher.syncTargetIntel(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.setInvisible(false);
            com.reverendinsanity.core.cultivation.BloodlineManager.onPlayerLogin(player);
            com.reverendinsanity.core.tutorial.TutorialManager.onPlayerJoin(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TribulationManager.onPlayerDisconnect(player);
            DeductionManager.cancelDeduction(player.getUUID());
            DreamExplorationManager.onPlayerLogout(player);
            TransformationManager.onPlayerLogout(player);
            com.reverendinsanity.core.inheritance.InheritanceTrialManager.onPlayerLogout(player);
            com.reverendinsanity.core.heavenwill.HeavenWillManager.onPlayerLogout(player);
            com.reverendinsanity.core.cultivation.LifespanManager.onPlayerLogout(player);
            com.reverendinsanity.core.clone.CloneManager.onPlayerLogout(player);
            com.reverendinsanity.core.cultivation.FortunePlunderManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.SelfDestructManager.onPlayerLogout(player);
            com.reverendinsanity.core.oath.PoisonOathManager.onPlayerLogout(player);
            com.reverendinsanity.core.cultivation.BloodlineManager.onPlayerLogout(player);
            com.reverendinsanity.core.cultivation.SeclusionManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.LifeDeathGateManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.AmbushManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.SealManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.TrapManager.onPlayerLogout(player);
            com.reverendinsanity.core.cultivation.DaoInsightManager.onPlayerLogout(player);
            com.reverendinsanity.core.tutorial.TutorialManager.onPlayerLogout(player);
            com.reverendinsanity.command.WalkthroughTestRunner.onPlayerLogout(player);
            com.reverendinsanity.core.combat.ToggleMoveManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.MeritManager.onPlayerLogout(player);
            com.reverendinsanity.core.combat.IntelligenceManager.clearPlayer(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        GuCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onMobKilledByPlayer(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) return;

        GuMasterData data = killer.getData(ModAttachments.GU_MASTER_DATA.get());
        FactionReputation rep = data.getFactionReputation();

        if (event.getEntity() instanceof com.reverendinsanity.entity.AncientGuImmortalEntity) {
            rep.addReputation(Faction.RIGHTEOUS, 50);
            killer.displayClientMessage(
                Component.literal("Righteous Path Reputation +50").withStyle(net.minecraft.ChatFormatting.BLUE), true);
        } else if (event.getEntity() instanceof com.reverendinsanity.entity.GuMasterEntity guMaster) {
            Faction masterFaction = guMaster.getFaction();
            switch (masterFaction) {
                case RIGHTEOUS -> {
                    rep.addReputation(Faction.DEMONIC, 20);
                    rep.addReputation(Faction.RIGHTEOUS, -30);
                    killer.displayClientMessage(
                        Component.literal("Demonic Path Reputation +20, Righteous Path Reputation -30").withStyle(net.minecraft.ChatFormatting.RED), true);
                }
                case DEMONIC -> {
                    rep.addReputation(Faction.RIGHTEOUS, 20);
                    rep.addReputation(Faction.DEMONIC, -15);
                    killer.displayClientMessage(
                        Component.literal("Righteous Path Reputation +20, Demonic Path Reputation -15").withStyle(net.minecraft.ChatFormatting.BLUE), true);
                }
                case INDEPENDENT -> {
                    rep.addReputation(Faction.INDEPENDENT, -10);
                    killer.displayClientMessage(
                        Component.literal("Independent Cultivator Reputation -10").withStyle(net.minecraft.ChatFormatting.YELLOW), true);
                }
            }
        }
        com.reverendinsanity.core.cultivation.FortunePlunderManager.onKillEntity(killer, event.getEntity());
        com.reverendinsanity.core.oath.PoisonOathManager.onKillEntity(killer);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        Aperture aperture = data.getAperture();

        if (!aperture.isOpened()) return;

        TribulationManager.onPlayerDeath(player);
        TransformationManager.onPlayerDeath(player);
        DreamExplorationManager.onPlayerLogout(player);
        com.reverendinsanity.core.inheritance.InheritanceTrialManager.onPlayerDeath(player);
        com.reverendinsanity.core.heavenwill.HeavenWillManager.onPlayerDeath(player);
        com.reverendinsanity.core.clone.CloneManager.onPlayerDeath(player);
        com.reverendinsanity.core.oath.PoisonOathManager.onPlayerDeath(player);
        com.reverendinsanity.core.cultivation.SeclusionManager.onPlayerDeath(player);
        com.reverendinsanity.core.combat.LifeDeathGateManager.onPlayerDeath(player);
        com.reverendinsanity.core.gu.GuDamageManager.onPlayerDeath(player);
        com.reverendinsanity.core.cultivation.DaoInsightManager.onPlayerDeath(player);
        com.reverendinsanity.core.combat.DeathRecapManager.onDeath(player);
        com.reverendinsanity.core.combat.DefenseManager.remove(player.getUUID());

        ResourceLocation cicadaId = GuRegistry.id("spring_autumn_cicada");
        GuInstance cicada = null;
        for (GuInstance gu : aperture.getStoredGu()) {
            if (gu.getTypeId().equals(cicadaId)) {
                cicada = gu;
                break;
            }
        }

        if (cicada != null) {
            event.setCanceled(true);
            aperture.removeGu(cicada);
            player.setHealth(player.getMaxHealth());
            aperture.setCurrentEssence(aperture.getMaxEssence());
            aperture.setThoughts(aperture.getMaxThoughts());
            player.removeAllEffects();
            data.getBuffManager().clearAll(player);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
            player.displayClientMessage(
                Component.literal("Spring Autumn Cicada crumbles. The river of time reverses — You are reborn!").withStyle(net.minecraft.ChatFormatting.GOLD),
                false
            );
            return;
        }

        data.getBuffManager().clearAll(player);

        for (GuInstance gu : new ArrayList<>(aperture.getStoredGu())) {
            GuType type = GuRegistry.get(gu.getTypeId());
            if (type == null) continue;

            Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(gu.getTypeId());
            if (item != net.minecraft.world.item.Items.AIR) {
                ItemEntity itemEntity = new ItemEntity(
                    player.level(),
                    player.getX() + (player.getRandom().nextFloat() - 0.5) * 2,
                    player.getY() + 0.5,
                    player.getZ() + (player.getRandom().nextFloat() - 0.5) * 2,
                    new ItemStack(item)
                );
                player.level().addFreshEntity(itemEntity);
            }
        }

        aperture.clearGu();

        aperture.setCurrentEssence(aperture.getMaxEssence() * 0.5f);
        aperture.setThoughts(0);

        CombatState combatState = data.getCombatState();
        for (ResourceLocation moveId : new ArrayList<>(combatState.getEquippedMoves())) {
            combatState.unequipMove(moveId);
        }

        player.displayClientMessage(Component.literal("Gu insects scatter. Your cultivation is damaged..."), false);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        PoisonCloudManager.tick();
        com.reverendinsanity.core.combat.FrostManager.tick();
        com.reverendinsanity.core.combat.DotManager.tick();
        FlashBlindManager.tick(event.getServer());
        com.reverendinsanity.core.event.WorldEventManager.tickAllEvents(event.getServer());
        for (ServerLevel level : event.getServer().getAllLevels()) {
            com.reverendinsanity.core.formation.FormationArrayManager.tickFormations(level);
            com.reverendinsanity.core.combat.SealManager.tickServer(level);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        RuntimeStateCleanup.clearAll(event.getServer());
    }

    @SubscribeEvent
    public static void onEnderPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (TribulationManager.isInTribulation(player)) {
                event.setCanceled(true);
                player.displayClientMessage(
                    Component.literal("Cannot use Ender Pearls during a heavenly tribulation!")
                        .withStyle(net.minecraft.ChatFormatting.RED), true);
            }
        }
    }

}
