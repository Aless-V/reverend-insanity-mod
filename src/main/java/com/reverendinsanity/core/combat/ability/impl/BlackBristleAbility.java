package com.reverendinsanity.core.combat.ability.impl;

import com.reverendinsanity.client.vfx.VfxHelper;
import com.reverendinsanity.client.vfx.VfxType;
import com.reverendinsanity.core.combat.ability.GuAbility;
import com.reverendinsanity.core.combat.buff.impl.BlackBristleBuff;
import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

// Black Mane Gu Skill: Strength Path Rank 2 — Mane Armor. +6 Armor +4 Toughness. Non-armor-piercing damage reduced by 15% (×0.85). Reflects 1.5 damage to melee attackers.
public class BlackBristleAbility extends GuAbility {

    public BlackBristleAbility() {
        super(GuRegistry.id("black_bristle_gu"), 15f, 500, AbilityType.BUFF);
    }

    @Override
    protected void onActivate(ServerPlayer player, Aperture aperture) {
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        data.getBuffManager().applyBuff(player, new BlackBristleBuff());

        VfxHelper.spawn(player, VfxType.AURA_RING,
            player.getX(), player.getY(), player.getZ(),
            0f, 1f, 0f,
            0xFF333333, 1.5f, 15);
    }
}
