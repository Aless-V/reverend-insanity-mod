package com.reverendinsanity.core.combat.ability.impl;

import com.reverendinsanity.client.vfx.VfxHelper;
import com.reverendinsanity.client.vfx.VfxType;
import com.reverendinsanity.core.combat.ability.GuAbility;
import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.cultivation.PermanentStatApplier;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

// Brown Bear Innate Power Gu: Permanently increases strength by +0.6. Cap: 20.
public class BearAccumulationAbility extends GuAbility {

    public BearAccumulationAbility() {
        super(GuRegistry.id("brown_bear_gu"), 40f, 1800, AbilityType.INSTANT);
    }

    @Override
    protected void onActivate(ServerPlayer player, Aperture aperture) {
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        float current = data.getPermanentStat("strength");
        if (current >= 20.0f) {
            player.sendSystemMessage(Component.literal("§cBrown Bear's base strength has reached its limit."));
            return;
        }
        data.addPermanentStat("strength", 0.6f);
        PermanentStatApplier.refresh(player);
        float total = data.getPermanentStat("strength");
        player.sendSystemMessage(Component.literal("§6Brown Bear's innate power infused! Strength +0.6 (Current: " + String.format("%.1f", total) + ")"));

        VfxHelper.spawn(player, VfxType.AURA_RING,
            player.getX(), player.getY(), player.getZ(),
            0f, 1f, 0f,
            0xFF884400, 2.0f, 20);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.0f, 0.4f);
    }
}
